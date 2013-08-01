/* Secu3Droid - An open source, free manager for SECU-3 engine
 * control unit
 * Copyright (C) 2013 Maksim M. Levin. Russia, Voronezh
 * 
 * SECU-3  - An open source, free engine control unit
 * Copyright (C) 2007 Alexey A. Shabelnikov. Ukraine, Gorlovka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * contacts:
 *            http://secu-3.org
 *            email: mmlevin@mail.ru
*/

package org.secu3.android.api.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.secu3.android.R;
import org.secu3.android.api.utils.EncodingCP866;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class Secu3Manager {	
	private static final String LOG_TAG = "Secu3Manager";		
	private final static int PROGRESS_TOTAL_PARAMS = 19;
	
	public enum SECU3_STATE {SECU3_NORMAL, SECU3_BOOTLOADER};
	public enum SECU3_PACKET_SEARCH {SEARCH_START, SEARCH_END};
	public enum SECU3_TASK {SECU3_NONE,SECU3_READ_SENSORS,SECU3_RAW_SENSORS,SECU3_READ_PARAMS,SECU3_READ_ERRORS,SECU3_READ_SAVED_ERRORS,SECU3_READ_FW_INFO,SECU3_START_LOGGING,SECU3_STOP_LOGGING};
	
	private int progressCurrent = 0;
	private int progressTotal = 0;
	private int subprogress = 0;
	private int disableReason = 0;
	private int maxConnectionRetries = 0;
	private int nbRetriesRemaining = 0;
	
	private SECU3_TASK secu3Task = SECU3_TASK.SECU3_NONE;
	private SECU3_TASK prevSecu3Task = SECU3_TASK.SECU3_NONE;
	
	private boolean enabled = false;
	private boolean connected = false;

	private Service callingService = null;
	private BluetoothSocket secu3Socket = null;
	private String deviceAddress = null;
	
	private ExecutorService notificationPool = null;
	private ScheduledExecutorService connectionAndReadingPool = null; 
	private ConnectedSecu3 connectedSecu3 = null;	
	private Context appContext = null;
	private Secu3Logger logger = new Secu3Logger();	
	
	private Secu3ProtoWrapper wrapper = null;
	
	private Secu3Packet ChMode = null;
	
	
	private class ConnectedSecu3 extends Thread {
		public static final int STATUS_TIMEOUT = 10;

		public Queue<Secu3Packet> sendPackets = new LinkedList<Secu3Packet>();
		public Queue<SECU3_TASK> tasks = new LinkedList<Secu3Manager.SECU3_TASK>();
		
		private final BluetoothSocket socket;
		private final InputStream in;
		private final OutputStream out;					
		private Timer timer = new Timer();

		SECU3_STATE secu3State = SECU3_STATE.SECU3_NORMAL;

		SECU3_PACKET_SEARCH secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_START;
		OnlineTask onlineTask = new OnlineTask();
	
		private boolean ready = false;
		
		int packetBuffer[] = new int [Secu3Packet.MAX_PACKET_SIZE];		
		
		public ConnectedSecu3 (BluetoothSocket socket) {
			this.socket = socket;
			
			InputStream tmpIn = null;
			OutputStream tmpOut = null;		
						
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			}
			catch (IOException e) {
				Log.d(LOG_TAG, "Error while getting socket streams");
			}
			
			in = tmpIn;
			out = tmpOut;
		}
		
		class OnlineTask extends TimerTask {
			private int offline = 0;
			
			public OnlineTask() {
				offline = 0;
			}
			
			public void run () {
				if (offline++ >= STATUS_TIMEOUT) { 
					offline = STATUS_TIMEOUT;
					appContext.sendBroadcast(new Intent(Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE).putExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS, false));
				}
			}

			public synchronized void reset() {
				offline = 0;
			}
		}		
		
		@SuppressWarnings("unused")
		public boolean isReady() {
			return ready;
		}
		
		void updateProgress(int progress) {
			progressCurrent = progress;
			appContext.sendBroadcast(new Intent(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS)
												.putExtra(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS_CURRENT, progressCurrent)
												.putExtra(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS_TOTAL, progressTotal));
		}
		
		void updateTask() {
			if (!tasks.isEmpty()) {
				prevSecu3Task = SECU3_TASK.SECU3_NONE;
				secu3Task = tasks.poll();
			};
		}
		
		void parsePacket(String packet, BufferedReader reader, BufferedWriter writer) throws IOException {
			Secu3Packet ChangeMode = new Secu3Packet(ChMode);
			switch (secu3State) {
			case SECU3_NORMAL:
					getProtoWrapper().parse(packet);
					appContext.sendBroadcast(getProtoWrapper().getLastPacketIntent());
					
					if (secu3Task != prevSecu3Task) { // If task changed
						prevSecu3Task = secu3Task;
						switch (secu3Task) {
						// No task received
						case SECU3_NONE:
							break;
						case SECU3_START_LOGGING:
							logger.setPath(PreferenceManager.getDefaultSharedPreferences(appContext).getString(appContext.getString(R.string.pref_write_log_path), ""));
							logger.beginLogging();
							break;
						case SECU3_STOP_LOGGING:
							logger.endLogging();
							break;							
						// Task to read sensors
						case SECU3_READ_SENSORS:		
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.sensor_dat_title));
							writer.write(ChangeMode.pack());
							writer.flush();					
							break;
						case SECU3_RAW_SENSORS:
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.adcraw_dat_title));
							writer.write(ChangeMode.pack());
							writer.flush();					
							break;							
						// Task to read params
						case SECU3_READ_PARAMS:
							progressCurrent = 0;
							progressTotal = PROGRESS_TOTAL_PARAMS;
							subprogress = 0;
							getProtoWrapper().init();
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.starter_title));
							writer.write(ChangeMode.pack());
							writer.flush();					
							break;
						case SECU3_READ_ERRORS:
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.ce_err_codes_title));
							writer.write(ChangeMode.pack());							
							writer.flush();
							break;
						case SECU3_READ_SAVED_ERRORS:
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.ce_saved_err_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;
						case SECU3_READ_FW_INFO:
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.fwinfo_dat_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;
						}
					}

					logger.OnPacketReceived(getProtoWrapper().getLastPacket());
					
					switch (secu3Task) {
					case SECU3_NONE:
						updateTask();
						break;
					case SECU3_START_LOGGING:
						updateTask();
						break;
					case SECU3_STOP_LOGGING:
						updateTask();
						break;
					case SECU3_READ_PARAMS:
						switch (getProtoWrapper().getLastPacket().getPacketIdResId()) {
						case R.string.packet_type_startr_par:
							updateProgress(1 + subprogress);
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.angles_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;
						case R.string.packet_type_angles_par:
							updateProgress(2 + subprogress);
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.idling_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;					
						case R.string.packet_type_idlreg_par:
							updateProgress(3 + subprogress);
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.fnname_dat_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;
						case R.string.packet_type_fnname_dat:
							updateProgress(4 + subprogress);
							subprogress = wrapper.getFunsetNames().length;
							if (wrapper.funsetNamesValid()) {
								((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.functions_title));
								writer.write(ChangeMode.pack());
								writer.flush();
							}
							break;								
						case R.string.packet_type_funset_par:
							updateProgress(5 + subprogress);
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.temperature_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;
						case R.string.packet_type_temper_par:
							updateProgress(6 + subprogress);
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.carburetor_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;
						case R.string.packet_type_carbur_par:
							updateProgress(7 + subprogress);
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.adc_errors_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;
						case R.string.packet_type_adccor_par:
							updateProgress(8 + subprogress);
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.ckps_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;
						case R.string.packet_type_ckps_par:
							updateProgress(9 + subprogress);
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.miscellaneous_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;
						case R.string.packet_type_miscel_par:
							updateProgress(10 + subprogress);
							((ProtoFieldString) ChMode.findField(R.string.change_mode_data_title)).setValue(appContext.getString(R.string.choke_control_title));
							writer.write(ChangeMode.pack());
							writer.flush();
							break;	
						case R.string.packet_type_choke_par:
							updateProgress(11 + subprogress);
							updateTask();							
							break;
						}			
						break;
					case SECU3_READ_ERRORS:
						switch (getProtoWrapper().getLastPacket().getPacketIdResId()) {
						case R.string.packet_type_ce_err_codes:	
							updateTask();						
						}				
						break;						
					case SECU3_READ_SAVED_ERRORS:
						switch (getProtoWrapper().getLastPacket().getPacketIdResId()) {
						case R.string.packet_type_ce_saved_err:
							updateTask();
						}
						break;
					case SECU3_READ_FW_INFO:
						switch (getProtoWrapper().getLastPacket().getPacketIdResId()) {
						case R.string.packet_type_fwinfo_dat:
							updateTask();						
						}				
						break;						
					case SECU3_RAW_SENSORS:
						switch (getProtoWrapper().getLastPacket().getPacketIdResId()) {
						case R.string.packet_type_adcraw_dat:
							updateTask();
						}
						break;
					case SECU3_READ_SENSORS:
						switch (getProtoWrapper().getLastPacket().getPacketIdResId()) {
						case R.string.packet_type_sendor_dat:
							updateTask();
						}
						break;						
					}
					Log.d(LOG_TAG,getProtoWrapper().getLogString());							
				break;
			default:
				break;
			}											
		}		
			
		public void run() {
			timer.scheduleAtFixedRate(onlineTask, 0, 100);	
			int idx = 0;
			int ch;	
			String line;
			
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader (in,"ISO-8859-1"));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out,"ISO-8859-1"));				
				while (enabled) {
					if (!sendPackets.isEmpty()) {				
							Secu3Packet packet = sendPackets.poll(); 
							writer.append(packet.pack() + "\r\n");
							Log.d(LOG_TAG, "Send packet");
							updateProgress(++progressCurrent);
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
							}							
							writer.flush();
					}					
					if (reader.ready()) {
						ready = true;												
						if ((ch = (char)reader.read()) != -1) {							
							if (secu3packetSearch == SECU3_PACKET_SEARCH.SEARCH_START) {							
								if (ch == Secu3Packet.INPUT_PACKET) {
									secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_END;								
									idx = 0;
								}
							}
							packetBuffer [idx++] = ch;
							if (idx >= Secu3Packet.MAX_PACKET_SIZE) {
								secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_START;
								idx = 0;
							}												
							if ((secu3packetSearch == SECU3_PACKET_SEARCH.SEARCH_END) && ((char)ch == '\r')) {
								secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_START;
								EncodingCP866.Cp866ToUtf16(packetBuffer);
								line = new String(packetBuffer,0,idx-1);
								Log.d(LOG_TAG, "Recieved: " + line);
								parsePacket(line,reader,writer);
								onlineTask.reset();					
								appContext.sendBroadcast(new Intent(Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE).putExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS, true));
							}
						}											
					} else {
						Log.d(LOG_TAG, "Data: not ready "+System.currentTimeMillis());
						SystemClock.sleep(100);
					}
				}
			} catch (Exception e) {
				Log.d(LOG_TAG, "Error while getting data "+e.toString());		
				e.printStackTrace();
			} finally {
				this.close();
			}
		}
		
		public void close () {
			ready = false;
			
			sendPackets = null;
			
			try {
				Log.d(LOG_TAG, "Closing Bluetooth input stream");
				in.close();
			} catch (IOException e) {
				Log.d(LOG_TAG, "Error while closing Bluetooth input stream");
			} finally {
				try {
					Log.d(LOG_TAG, "Closing Bluetooth output stream");
					out.close();
				} catch (IOException e) {
					Log.d(LOG_TAG, "Error while closing Bluetooth output stream");
				}
			}
		}
	}
	
	public Secu3Manager(Service callingService, String deviceAddress, int maxRetries) {
		this.callingService = callingService;
		this.deviceAddress = deviceAddress;
		maxConnectionRetries = maxRetries;
		nbRetriesRemaining = maxRetries + 1;
		appContext = callingService.getApplicationContext();	
		wrapper = new Secu3ProtoWrapper(appContext);
		ChMode = wrapper.obtainPacketSkeleton(R.string.change_mode_title);
		try {
			getProtoWrapper().instantiateFromXml(R.xml.protocol);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void setDisableReason (int reasonID) {
		disableReason = reasonID;
	}
	
	public int getDisableReason () {
		return disableReason;
	}
	
	public synchronized boolean isEnabled () {
		return enabled;
	}
	
	public synchronized boolean enable() {
		Secu3Service.secu3Notification.notificationManager.cancel(R.string.service_closed_because_connection_problem_notification_title);
		if (! enabled){
        	Log.d(LOG_TAG, "enabling Secu3 bluetooth manager");
			final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	        if (bluetoothAdapter == null) {
	            // Device does not support Bluetooth
	        	Log.e(LOG_TAG, "Device does not support Bluetooth");
	        	disable(R.string.msg_bluetooth_unsupported);
	        } else if (!bluetoothAdapter.isEnabled()) {
	        	// Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        	// startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	        	Log.e(LOG_TAG, "Bluetooth is not enabled");
	        	disable(R.string.msg_bluetooth_disabled);
	        } else {
				final BluetoothDevice secu3Device = bluetoothAdapter.getRemoteDevice(deviceAddress);
				if (secu3Device == null){
					Log.e(LOG_TAG, "Secu3 device not found");       	    	
		        	disable(R.string.msg_bluetooth_secu3_unavaible);
				} else {
	    			Log.e(LOG_TAG, "current device: "+secu3Device.getName() + " -- " + secu3Device.getAddress());
					try {
						secu3Socket = secu3Device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
					} catch (IOException e) {
	    				Log.e(LOG_TAG, "Error during connection", e);
	    				secu3Socket = null;
					}
					if (secu3Socket == null){
	    				Log.e(LOG_TAG, "Error while establishing connection: no socket");
			        	disable(R.string.msg_bluetooth_secu3_unavaible);
					} else {
						Runnable connectThread = new Runnable() {							
							@Override
							public void run() {
								try {
									connected = false;
									Log.v(LOG_TAG, "current device: "+secu3Device.getName() + " -- " + secu3Device.getAddress());
									if ((bluetoothAdapter.isEnabled()) && (nbRetriesRemaining > 0 )){										
										try {
											if (connectedSecu3 != null){
												connectedSecu3.close();
											}
											if ((secu3Socket != null) && ((connectedSecu3 == null) || (connectedSecu3.socket != secu3Socket))){
												Log.d(LOG_TAG, "trying to close old socket");
												secu3Socket.close();
											}
										} catch (IOException e) {
											Log.e(LOG_TAG, "Error during disconnection", e);
										}
										try {
											secu3Socket = secu3Device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
										} catch (IOException e) {
											Log.e(LOG_TAG, "Error during connection", e);
						    				secu3Socket = null;
										}
										if (secu3Socket == null){
											Log.e(LOG_TAG, "Error while establishing connection: no socket");
								        	disable(R.string.msg_bluetooth_secu3_unavaible);
										} else {
											Log.v(LOG_TAG, "connecting to socket");
											secu3Socket.connect();
						        			Log.d(LOG_TAG, "connected to socket");
											connected = true;
											nbRetriesRemaining = 1+maxConnectionRetries ;
											Secu3Service.secu3Notification.notificationManager.cancel(R.string.connection_problem_notification_title);
						        			Log.v(LOG_TAG, "starting socket reading task");
											connectedSecu3 = new ConnectedSecu3(secu3Socket);
											connectionAndReadingPool.execute(connectedSecu3);
								        	Log.v(LOG_TAG, "socket reading thread started");
										}
									} else if (! bluetoothAdapter.isEnabled()) {
										setDisableReason(R.string.msg_bluetooth_disabled);
									}
								} catch (IOException connectException) {
									Log.e(LOG_TAG, "error while connecting to socket", connectException);									
									disable(R.string.msg_bluetooth_secu3_unavaible);
								} finally {
									nbRetriesRemaining--;
									if (! connected) {
										disableIfNeeded();
									}
								}
							}
						};
						this.enabled = true;
			        	Log.d(LOG_TAG, "Bluetooth Secu3 manager enabled");
			        	Log.v(LOG_TAG, "starting notification thread");
						notificationPool = Executors.newSingleThreadExecutor();
			        	Log.v(LOG_TAG, "starting connection and reading thread");
						connectionAndReadingPool = Executors.newSingleThreadScheduledExecutor();
			        	Log.v(LOG_TAG, "starting connection to socket task");
						connectionAndReadingPool.scheduleWithFixedDelay(connectThread, 5000, 60000, TimeUnit.MILLISECONDS);
					}
				}
			}
		}
		return this.enabled;
	}
	
	private synchronized void disableIfNeeded () {
		if (enabled) {
			if (nbRetriesRemaining > 0) {
				Log.e(LOG_TAG, "Unable to establish connection");
				Secu3Service.secu3Notification.notifyConnectionProblem(maxConnectionRetries,nbRetriesRemaining);
			} else {
				disable (R.string.msg_too_many_connection_problems);
			}
		}
	}
	
	public synchronized void disable(int reasonId) {
    	Log.d(LOG_TAG, "disabling Secu3 bluetooth manager reason: "+callingService.getString(reasonId));
		setDisableReason(reasonId);
    	disable();
	}	
	
	public synchronized void setTask (SECU3_TASK task) {
		if (connectedSecu3 != null) {
			connectedSecu3.tasks.add(task);
		}
	}
	
	public synchronized void appendPacket (Secu3Packet packet, int packets_counter) {
		if ((connectedSecu3 != null) && (connectedSecu3.sendPackets != null)) {
			connectedSecu3.sendPackets.add(packet);
		}
		if (packets_counter != 0) {
			progressTotal = packets_counter;
			progressCurrent = 0;
		}
	}
	
	public synchronized void disable() {
		Secu3Service.secu3Notification.notificationManager.cancel(R.string.connection_problem_notification_title);
		if (getDisableReason() != 0){
			Secu3Service.secu3Notification.notifyServiceStopped(getDisableReason());
		}
		if (enabled){
        	Log.d(LOG_TAG, "disabling Secu3 manager");
			enabled = false;
			logger.endLogging();
			connectionAndReadingPool.shutdown();
			Runnable closeAndShutdown = new Runnable() {				
				@Override
				public void run(){
					try {
						connectionAndReadingPool.awaitTermination(10, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!connectionAndReadingPool.isTerminated()){
						connectionAndReadingPool.shutdownNow();
						if (connectedSecu3 != null){
							connectedSecu3.close();
						}
						if ((secu3Socket != null) && ((connectedSecu3 == null) || (connectedSecu3.socket != secu3Socket))){
							try {
								Log.d(LOG_TAG, "closing Bluetooth socket");
								secu3Socket.close();
							} catch (IOException closeException) {
								Log.e(LOG_TAG, "error while closing socket", closeException);
							}
						}
					}
				}
			};
			notificationPool.execute(closeAndShutdown);
			notificationPool.shutdown();
			callingService.stopSelf();
        	Log.d(LOG_TAG, "Bluetooth Secu3 manager disabled");
		}
	}

	public Secu3ProtoWrapper getProtoWrapper() {
		return wrapper;
	}
}

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
import org.secu3.android.api.io.Secu3Dat.*;
import org.secu3.android.api.utils.EncodingCP866;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class Secu3Manager {
	
	private static final String LOG_TAG = "Secu3Manager";		
	
	public enum SECU3_STATE {SECU3_NORMAL, SECU3_BOOTLOADER};
	public enum SECU3_PACKET_SEARCH {SEARCH_START, SEARCH_END};
	public enum SECU3_TASK {SECU3_NONE,SECU3_READ_SENSORS,SECU3_RAW_SENSORS,SECU3_READ_PARAMS,SECU3_READ_ERRORS,SECU3_READ_SAVED_ERRORS,SECU3_READ_FW_INFO,SECU3_START_LOGGING,SECU3_STOP_LOGGING};
	
	private Service callingService;
	private BluetoothSocket secu3Socket;
	private String deviceAddress;
	private boolean enabled = false;
	private ExecutorService notificationPool;
	private ScheduledExecutorService connectionAndReadingPool; 
	private ConnectedSecu3 connectedSecu3;
	private int disableReason = 0;
	private Context appContext;
	private int maxConnectionRetries;
	private int nbRetriesRemaining;
	private boolean connected = false;
	private SECU3_TASK secu3Task = SECU3_TASK.SECU3_NONE;
	private SECU3_TASK prevSecu3Task = SECU3_TASK.SECU3_NONE;
	private Secu3Logger logger = new Secu3Logger();	
	
	private int progressCurrent = 0;
	private int progressTotal = 0;
	private int subprogress = 0;
	private final static int PROGRESS_TOTAL_PARAMS = 19;
	
	private class ConnectedSecu3 extends Thread {
		public static final int STATUS_TIMEOUT = 10;

		public Queue<Secu3Dat> sendPackets = new LinkedList<Secu3Dat>();
		public Queue<SECU3_TASK> tasks = new LinkedList<Secu3Manager.SECU3_TASK>();
		
		private final BluetoothSocket socket;
		private final InputStream in;
		private final OutputStream out;			
		private FnNameDat fnNameDat = null;		
		private Timer timer = new Timer();

		SECU3_STATE secu3State = SECU3_STATE.SECU3_NORMAL;

		SECU3_PACKET_SEARCH secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_START;
		OnlineTask onlineTask = new OnlineTask();
	
		private boolean ready = false;
		
		int packetBuffer[] = new int [Secu3Dat.MAX_PACKET_SIZE];
		Secu3Parser parser = new Secu3Parser();		
		
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
		
		void parsePacket(String packet, BufferedReader reader, BufferedWriter writer) throws Exception {
			
			switch (secu3State) {
			case SECU3_NORMAL:
					parser.parse(packet);
					appContext.sendBroadcast(parser.getLastPacketIntent());
					
					if (secu3Task != prevSecu3Task) { // If task changed
						prevSecu3Task = secu3Task;
						switch (secu3Task) {
						// No task received
						case SECU3_NONE:
							break;
						case SECU3_START_LOGGING:
							logger.BeginLogging();
							break;
						case SECU3_STOP_LOGGING:
							logger.EndLogging();
							break;							
						// Task to read sensors
						case SECU3_READ_SENSORS:					
							writer.write(ChangeMode.pack(Secu3Dat.SENSOR_DAT));
							writer.flush();					
							break;
						case SECU3_RAW_SENSORS:
							writer.write(ChangeMode.pack(Secu3Dat.ADCRAW_DAT));
							writer.flush();					
							break;							
						// Task to read params
						case SECU3_READ_PARAMS:
							progressCurrent = 0;
							progressTotal = PROGRESS_TOTAL_PARAMS;
							subprogress = 0;
							if (fnNameDat != null) fnNameDat.clear();
							writer.write(ChangeMode.pack(Secu3Dat.STARTR_PAR));
							writer.flush();					
							break;
						case SECU3_READ_ERRORS:
							writer.write(ChangeMode.pack(Secu3Dat.CE_ERR_CODES));							
							writer.flush();
							break;
						case SECU3_READ_SAVED_ERRORS:
							writer.write(ChangeMode.pack(Secu3Dat.CE_SAVED_ERR));
							writer.flush();
							break;
						case SECU3_READ_FW_INFO:
							writer.write(ChangeMode.pack(Secu3Dat.FWINFO_DAT));
							writer.flush();
							break;
						}
					}

					logger.OnPacketReceived(parser.getLastPacket());
					
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
						switch (parser.getLastPackedId()) {
						case Secu3Dat.STARTR_PAR:
							updateProgress(1 + subprogress);
							writer.write(ChangeMode.pack(Secu3Dat.ANGLES_PAR));
							writer.flush();
							break;
						case Secu3Dat.ANGLES_PAR:
							updateProgress(2 + subprogress);
							writer.write(ChangeMode.pack(Secu3Dat.IDLREG_PAR));
							writer.flush();
							break;					
						case Secu3Dat.IDLREG_PAR:
							updateProgress(3 + subprogress);
							writer.write(ChangeMode.pack(Secu3Dat.FNNAME_DAT));
							writer.flush();
							break;
						case Secu3Dat.FNNAME_DAT:
							updateProgress(4 + subprogress);
							subprogress = (fnNameDat == null)?0:fnNameDat.names_count();
							if (fnNameDat == null) {
								fnNameDat = new FnNameDat();
							}
							fnNameDat.parse(packet);
							if (fnNameDat.names_available()) {
								appContext.sendBroadcast(fnNameDat.getIntent());
								writer.write(ChangeMode.pack(Secu3Dat.FUNSET_PAR));
								writer.flush();
							}
							break;								
						case Secu3Dat.FUNSET_PAR:
							updateProgress(5 + subprogress);
							writer.write(ChangeMode.pack(Secu3Dat.TEMPER_PAR));
							writer.flush();
							break;
						case Secu3Dat.TEMPER_PAR:
							updateProgress(6 + subprogress);
							writer.write(ChangeMode.pack(Secu3Dat.CARBUR_PAR));
							writer.flush();
							break;
						case Secu3Dat.CARBUR_PAR:
							updateProgress(7 + subprogress);
							writer.write(ChangeMode.pack(Secu3Dat.ADCCOR_PAR));
							writer.flush();
							break;
						case Secu3Dat.ADCCOR_PAR:
							updateProgress(8 + subprogress);
							writer.write(ChangeMode.pack(Secu3Dat.CKPS_PAR));
							writer.flush();
							break;
						case Secu3Dat.CKPS_PAR:
							updateProgress(9 + subprogress);
							writer.write(ChangeMode.pack(Secu3Dat.MISCEL_PAR));
							writer.flush();
							break;
						case Secu3Dat.MISCEL_PAR:
							updateProgress(10 + subprogress);
							writer.write(ChangeMode.pack(Secu3Dat.CHOKE_PAR));
							writer.flush();
							break;	
						case Secu3Dat.CHOKE_PAR:
							updateProgress(11 + subprogress);
							updateTask();							
							break;
						}			
						break;
					case SECU3_READ_ERRORS:
						switch (parser.getLastPackedId()) {
						case Secu3Dat.CE_ERR_CODES:	
							updateTask();						
						}				
						break;						
					case SECU3_READ_SAVED_ERRORS:
						switch (parser.getLastPackedId()) {
						case Secu3Dat.CE_SAVED_ERR:
							updateTask();
						}
						break;
					case SECU3_READ_FW_INFO:
						switch (parser.getLastPackedId()) {
						case Secu3Dat.FWINFO_DAT:
							updateTask();						
						}				
						break;						
					case SECU3_RAW_SENSORS:
						switch (parser.getLastPackedId()) {
						case Secu3Dat.ADCRAW_DAT:
							updateTask();
						}
						break;
					case SECU3_READ_SENSORS:
						switch (parser.getLastPackedId()) {
						case Secu3Dat.SENSOR_DAT:
							updateTask();
						}
						break;						
					}
					Log.d(LOG_TAG,parser.getLogString());							
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
						try {
							Secu3Dat packet =sendPackets.poll(); 
							writer.append(packet.pack() + "\r\n");
							updateProgress(++progressCurrent);
							Thread.sleep(200);							
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							writer.flush();
						}
					}					
					if (reader.ready()) {
						ready = true;												
						if ((ch = (char)reader.read()) != -1) {							
							if (secu3packetSearch == SECU3_PACKET_SEARCH.SEARCH_START) {							
								if (ch == Secu3Dat.INPUT_PACKET) {
									secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_END;								
									idx = 0;
								}
							}
							packetBuffer [idx++] = ch;
							if (idx >= Secu3Dat.MAX_PACKET_SIZE) {
								secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_START;
								idx = 0;
							}												
							if ((secu3packetSearch == SECU3_PACKET_SEARCH.SEARCH_END) && ((char)ch == '\r')) {
								secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_START;
								EncodingCP866.Cp866ToUtf16(packetBuffer);
								line = new String(packetBuffer,0,idx-1);
								Log.d(LOG_TAG, "Recieved: " + line);
								try {
									parsePacket(line,reader,writer);
								} catch (Exception e) {
									Log.d(LOG_TAG, e.getMessage());
								}
								onlineTask.reset();					
								appContext.sendBroadcast(new Intent(Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE).putExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS, true));
							}
						}											
					} else {
						Log.d(LOG_TAG, "Data: not ready "+System.currentTimeMillis());
						SystemClock.sleep(100);
					}
				}
			} catch (IOException e) {
				Log.d(LOG_TAG, "Error while getting data "+e.toString());				
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
		this.maxConnectionRetries = maxRetries;
		this.nbRetriesRemaining =maxRetries + 1;
		this.appContext = callingService.getApplicationContext();		
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
        	Log.d(LOG_TAG, "enabling Bluetooth GPS manager");
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
			        	Log.d(LOG_TAG, "Bluetooth GPS manager enabled");
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
    	Log.d(LOG_TAG, "disabling Bluetooth GPS manager reason: "+callingService.getString(reasonId));
		setDisableReason(reasonId);
    	disable();
	}	
	
	public synchronized void setTask (SECU3_TASK task) {
		if (connectedSecu3 != null) {
			connectedSecu3.tasks.add(task);
		}
	}
	
	public synchronized void appendPacket (Secu3Dat packet, int packets_counter) {
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
			logger.EndLogging();
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
								Log.d(LOG_TAG, "closing Bluetooth GPS socket");
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
        	Log.d(LOG_TAG, "Bluetooth GPS manager disabled");
		}
	}	
}

package org.secu3.android.api.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat.ChangeMode;

import android.app.Notification;
import android.app.NotificationManager;
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
	
	enum SECU3_STATE {SECU3_NORMAL, SECU3_BOOTLOADER};
	enum SECU3_PACKET_SEARCH {SEARCH_START, SEARCH_END};
	
	private Service callingService;
	private BluetoothSocket secu3Socket;
	private String deviceAddress;
	private boolean enabled = false;
	private ExecutorService notificationPool;
	private ScheduledExecutorService connectionAndReadingPool; 
	private ConnectedSecu3 connectedSecu3;
	private int disableReason = 0;
	private Notification connectionProblemNotification;
	private Notification serviceStoppedNotification;
	private Context appContext;
	private NotificationManager notificationManager;
	private int maxConnectionRetries;
	private int nbRetriesRemaining;
	private boolean connected = false;
	
	private class ConnectedSecu3 extends Thread {
		
		private final BluetoothSocket socket;
		private final InputStream in;
		private final OutputStream out;
		Timer timer = new Timer();
		int offline = 0;
		public static final int STATUS_TIMEOUT = 10;

		SECU3_STATE secu3State = SECU3_STATE.SECU3_NORMAL;

		SECU3_PACKET_SEARCH secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_START;
		OnlineTask onlineTask = new OnlineTask();
	
		private boolean ready = false;
		
		char packetBuffer[] = new char [Secu3Dat.MAX_PACKET_SIZE];
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
			public void run () {
				if (offline++ >= STATUS_TIMEOUT) { 
					appContext.sendBroadcast(new Intent(Secu3Service.STATUS_ONLINE).putExtra(Secu3Service.STATUS, false));
				}
			}
		}		
		
		public boolean isReady() {
			return ready;
		}
		
		void parsePacket(String packet, BufferedReader reader, BufferedWriter writer) {
			switch (secu3State) {
			case SECU3_NORMAL:
				try {
					parser.parse(packet);
					appContext.sendBroadcast(parser.getLastPacketIntent());
					
					switch (parser.getLastPackedId()) {
					case Secu3Dat.SENSOR_DAT:						
						writer.write(ChangeMode.pack(Secu3Dat.ADCRAW_DAT));
						writer.flush();
						break;					
					case Secu3Dat.ADCRAW_DAT:
						writer.write(ChangeMode.pack(Secu3Dat.STARTR_PAR));
						writer.flush();
						break;						
					case Secu3Dat.STARTR_PAR:
						writer.write(ChangeMode.pack(Secu3Dat.ANGLES_PAR));
						writer.flush();
						break;
					case Secu3Dat.ANGLES_PAR:
						writer.write(ChangeMode.pack(Secu3Dat.IDLREG_PAR));
						writer.flush();
						break;					
					case Secu3Dat.IDLREG_PAR:
						writer.write(ChangeMode.pack(Secu3Dat.FUNSET_PAR));
						writer.flush();
						break;
					case Secu3Dat.FUNSET_PAR:
						writer.write(ChangeMode.pack(Secu3Dat.TEMPER_PAR));
						writer.flush();
						break;
					case Secu3Dat.TEMPER_PAR:
						writer.write(ChangeMode.pack(Secu3Dat.CARBUR_PAR));
						writer.flush();
						break;
					case Secu3Dat.CARBUR_PAR:
						writer.write(ChangeMode.pack(Secu3Dat.ADCCOR_PAR));
						writer.flush();
						break;
					case Secu3Dat.ADCCOR_PAR:
						writer.write(ChangeMode.pack(Secu3Dat.CKPS_PAR));
						writer.flush();
						break;
					case Secu3Dat.CKPS_PAR:
						writer.write(ChangeMode.pack(Secu3Dat.MISCEL_PAR));
						writer.flush();
						break;
					case Secu3Dat.MISCEL_PAR:
						writer.write(ChangeMode.pack(Secu3Dat.SENSOR_DAT));
						writer.flush();
						break;
						
					case Secu3Dat.CHANGEMODE:
						break;
					case Secu3Dat.BOOTLOADER:
						break;
					case Secu3Dat.FNNAME_DAT:
						break;
					case Secu3Dat.OP_COMP_NC:
						break;
					case Secu3Dat.CE_ERR_CODES:
						break;
					case Secu3Dat.KNOCK_PAR:
						break;
					case Secu3Dat.CE_SAVED_ERR:
						break;
					case Secu3Dat.FWINFO_DAT:
						break;
					case Secu3Dat.EDITAB_PAR:
						break;
					case Secu3Dat.ATTTAB_PAR:
						break;
					case Secu3Dat.DBGVAR_DAT:
						break;
					case Secu3Dat.DIAGINP_DAT:
						break;
					case Secu3Dat.DIAGOUT_DAT:
						break;
					}								
					Log.d(LOG_TAG,parser.getLogString());							
				} catch (Exception e) {
					Log.d(LOG_TAG,e.getMessage());
				}
				break;
				
			case SECU3_BOOTLOADER:
				break;
			}											
		}		
		
		public void run() {
			timer.scheduleAtFixedRate(onlineTask, 0, 100);	
			int idx = 0;
			char ch;	
			String line;
			
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader (in,"cp1251"));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out,"cp1251"));
				while (enabled) {
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
							if ((secu3packetSearch == SECU3_PACKET_SEARCH.SEARCH_END) && (ch == '\r')) {
								secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_START;
								line = String.valueOf(packetBuffer,0,idx-1);
								Log.d(LOG_TAG, "Recieved: " + line);
								parsePacket(line,reader,writer);
								if (offline > 0) offline=0;					
								appContext.sendBroadcast(new Intent(Secu3Service.STATUS_ONLINE).putExtra(Secu3Service.STATUS, true));
							}
						}											
					} else {
						Log.d(LOG_TAG, "Data: not ready "+System.currentTimeMillis());
						SystemClock.sleep(100);
					}
				}
			} catch (IOException e) {
				Log.d(LOG_TAG, "Error while getting data");				
			} finally {
				this.close();
			}
		}
		
		public void close () {
			ready = false;
			
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
	
	@SuppressWarnings("deprecation")
	public Secu3Manager(Service callingService, String deviceAddress, int maxRetries) {
		this.callingService = callingService;
		this.deviceAddress = deviceAddress;
		this.maxConnectionRetries = maxRetries;
		this.nbRetriesRemaining =maxRetries + 1;
		this.appContext = callingService.getApplicationContext();
		notificationManager = (NotificationManager)callingService.getSystemService(Context.NOTIFICATION_SERVICE);
		
		connectionProblemNotification = new Notification();
		serviceStoppedNotification = new Notification();
		serviceStoppedNotification.setLatestEventInfo(appContext,
				appContext.getString(R.string.service_closed_because_connection_problem_notification_title),
				appContext.getString(R.string.service_closed_because_connection_problem_notification),
				null);
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
		notificationManager.cancel(R.string.service_closed_because_connection_problem_notification_title);
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
											// Cancel discovery because it will slow down the connection
											// bluetoothAdapter.cancelDiscovery();
											// we increment the number of connection tries
											// Connect the device through the socket. This will block
											// until it succeeds or throws an exception
											Log.v(LOG_TAG, "connecting to socket");
											secu3Socket.connect();
						        			Log.d(LOG_TAG, "connected to socket");
											connected = true;
											// reset eventual disabling cause
//											setDisableReason(0);
											// connection obtained so reset the number of connection try
											nbRetriesRemaining = 1+maxConnectionRetries ;
											notificationManager.cancel(R.string.connection_problem_notification_title);
						        			Log.v(LOG_TAG, "starting socket reading task");
											connectedSecu3 = new ConnectedSecu3(secu3Socket);
											connectionAndReadingPool.execute(connectedSecu3);
								        	Log.v(LOG_TAG, "socket reading thread started");
										}
//									} else if (! bluetoothAdapter.isEnabled()) {
//										setDisableReason(R.string.msg_bluetooth_disabled);
									}
								} catch (IOException connectException) {
									// Unable to connect
									Log.e(LOG_TAG, "error while connecting to socket", connectException);									
									// disable(R.string.msg_bluetooth_gps_unavaible);
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
	
	@SuppressWarnings("deprecation")
	private synchronized void disableIfNeeded () {
		if (enabled) {
			if (nbRetriesRemaining > 0) {
				Log.e(LOG_TAG, "Unable to establish connection");
				connectionProblemNotification.when = System.currentTimeMillis();
				String pbMessage = appContext.getResources().getQuantityString(R.plurals.connection_problem_notification, nbRetriesRemaining, nbRetriesRemaining);
				connectionProblemNotification.setLatestEventInfo(appContext, 
						appContext.getString(R.string.connection_problem_notification_title), 
						pbMessage, 
						connectionProblemNotification.contentIntent);
				connectionProblemNotification.number = 1 + maxConnectionRetries - nbRetriesRemaining;
				notificationManager.notify(R.string.connection_problem_notification_title, connectionProblemNotification);				
			} else {
				//disable (R.string.msg_too_many_connection_problems);
			}
		}
	}
	
	public synchronized void disable(int reasonId) {
    	Log.d(LOG_TAG, "disabling Bluetooth GPS manager reason: "+callingService.getString(reasonId));
		setDisableReason(reasonId);
    	//disable();
	}	
	
	@SuppressWarnings("deprecation")
	public synchronized void disable() {
		notificationManager.cancel(R.string.connection_problem_notification_title);
		if (getDisableReason() != 0){
			serviceStoppedNotification.when = System.currentTimeMillis();
			serviceStoppedNotification.setLatestEventInfo(appContext, 
					appContext.getString(R.string.service_closed_because_connection_problem_notification_title), 
					appContext.getString(R.string.service_closed_because_connection_problem_notification, appContext.getString(getDisableReason())),
					serviceStoppedNotification.contentIntent);
			notificationManager.notify(R.string.service_closed_because_connection_problem_notification_title, serviceStoppedNotification);
		}
		if (enabled){
        	Log.d(LOG_TAG, "disabling Secu3 manager");
			enabled = false;
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

package org.secu3.android.api.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.secu3.android.MainActivity;
import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat.ChangeMode;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Secu3Service extends Service {
	
	NotificationManager notificationManager;
	BluetoothAdapter bluetoothAdapter;
	BluetoothSocket bluetoothSocket;
	Handler handler;
	Timer timer = new Timer();
	
	enum SECU3_STATE {SECU3_NORMAL, SECU3_BOOTLOADER};
	SECU3_STATE secu3State = SECU3_STATE.SECU3_NORMAL;
	enum SECU3_PACKET_SEARCH {SEARCH_START, SEARCH_END};
	SECU3_PACKET_SEARCH secu3packetSearch = SECU3_PACKET_SEARCH.SEARCH_START;
	int offline = 0;
	
	static final int NOTOFICATION_ID = 1000;
	public static final String STATUS_ONLINE = "org.secu3.android.STATUS_ONLINE";
	public static final String STATUS = "status";
	public static final int STATUS_TIMEOUT = 10;
	
	class OnlineTask extends TimerTask {
		public void run () {
			if (offline++ >= STATUS_TIMEOUT) { 
				sendBroadcast(new Intent(STATUS_ONLINE).putExtra(STATUS, false));
			}
		}
	}
	
	OnlineTask onlineTask = new OnlineTask();

	Thread secu3Thread = new Thread (new Runnable () {
		char packetBuffer[] = new char [Secu3Dat.MAX_PACKET_SIZE];
		Secu3Parser parser = new Secu3Parser();
		
		void parsePacket(String packet, BufferedReader reader, BufferedWriter writer) {
			switch (secu3State) {
			case SECU3_NORMAL:
				try {
					parser.parse(packet);
					sendBroadcast(parser.getLastPacketIntent());
					
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
					Log.d(getString(R.string.app_name),parser.getLogString());							
				} catch (Exception e) {
					Log.d(getString(R.string.app_name),e.getMessage());
				}
				break;
				
			case SECU3_BOOTLOADER:
				break;
			}											
		}
		
		@Override
		public void run() {
			String line;
			timer.scheduleAtFixedRate(onlineTask, 0, 100);
			
			
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(bluetoothSocket.getInputStream(),"cp1251"));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bluetoothSocket.getOutputStream(),"cp1251"));
				
				int idx = 0;
				char ch;					
				
				for (;;) {					
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
							Log.d(getString(R.string.app_name), "Recieved: " + line);
							parsePacket(line,reader,writer);
							if (offline > 0) offline=0;					
							sendBroadcast(new Intent(STATUS_ONLINE).putExtra(STATUS, true));
						}
					}
					else {
						Log.d(getString(R.string.app_name), "No read data");							
					}
				}															

			} catch (Exception e) {					
				Log.d(getString(R.string.app_name), "Run exception " + e.getMessage());
			}
		}			
	});

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		showNotification();		
		Log.d("Secu3Service", "Starting service");
		try {
			BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice("00:11:11:24:04:80");			
			bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
	    	bluetoothSocket.connect();
	    	secu3Thread.start();	    	
		} catch (Exception e) {			
			Log.d(getString(R.string.app_name),"Create socket exception " + e.getMessage());
		}		
		Log.d(getString(R.string.app_name), "Thread started");
		return START_STICKY;	
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void showNotification() {
		Intent intent = new Intent (this,MainActivity.class);	
		
		Notification notification = new NotificationCompat.Builder(this)
											.setContentTitle("Secu-3T Manager")
											.setSmallIcon(R.drawable.ic_launcher)											
											.setWhen(System.currentTimeMillis())
											.setOngoing(true)
											.setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
											.build();		
		notificationManager.notify(NOTOFICATION_ID, notification);
	}
	
	@Override
	public void onDestroy() {
		Log.d(getString(R.string.app_name), "Stopping service");
		if (bluetoothSocket != null) {
			try {
				bluetoothSocket.close();
			} catch (IOException e) {
				Log.d(getString(R.string.app_name), "Error closing socket");
			}
		}
		notificationManager.cancelAll();
		super.onDestroy();
	}
}

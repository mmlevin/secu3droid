package org.secu3.android.api.io;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Secu3Service extends Service {	
	private static final String LOG_TAG = "Secu3Service";	
	
	public static final String ACTION_SECU3_SERVICE_START = "org.secu3.android.intent.action.SECU3_SERVICE_START";	
	public static final String ACTION_SECU3_SERVICE_STOP = "org.secu3.android.intent.action.SECU3_SERVICE_STOP";
	public static final String ACTION_SECU3_SERVICE_SET_TASK = "org.secu3.android.intent.action.SECU3_SERVICE_SET_TASK";
	public static final String ACTION_SECU3_SERVICE_SET_TASK_PARAM = "org.secu3.android.intent.action.extra.SECU3_SERVICE_SET_TASK_PARAM";
	public static final String ACTION_SECU3_SERVICE_SEND_PACKET= "org.secu3.android.intent.action.SECU3_SERVICE_SEND_PACKET";	
	public static final String ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET= "org.secu3.android.intent.action.extra.SECU3_SERVICE_SEND_PACKET_PARAM_PACKET";	
	public static final String ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PROGRESS = "org.secu3.android.intent.action.extra.SECU3_SERVICE_SEND_PACKET_PARAM_PROGRESS";
	
	public static final String EVENT_SECU3_SERVICE_STATUS_ONLINE = "org.secu3.android.intent.action.STATUS_ONLINE";
	public static final String EVENT_SECU3_SERVICE_STATUS = "org.secu3.android.intent.action.extra.STATUS";
	public static final String EVENT_SECU3_SERVICE_PROGRESS = "org.secu3.android.intent.action.SECU3_SERVICE_PROGRESS";
	public static final String EVENT_SECU3_SERVICE_PROGRESS_CURRENT = "org.secu3.android.intent.action.extra.SECU3_SERVICE_PROGRESS_CURRENT";
	public static final String EVENT_SECU3_SERVICE_PROGRESS_TOTAL = "org.secu3.android.intent.action.extra.SECU3_SERVICE_PROGRESS_TOTAL";		
	
	NotificationManager notificationManager;
	private Secu3Manager secu3Manager = null;
	public static Secu3Notification secu3Notification;

	@SuppressLint("ShowToast")
	@Override
	public void onCreate() {
		super.onCreate();
		secu3Notification = new Secu3Notification(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String deviceAddress = sharedPreferences.getString(getString(R.string.pref_bluetooth_device_key), null);
		int maxConRetries = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_connection_retries_key), this.getString(R.string.defaultConnectionRetries)));
		Log.d(LOG_TAG, "prefs device addr: "+deviceAddress);
		if (ACTION_SECU3_SERVICE_START.equals(intent.getAction())){
			if (secu3Manager == null){
				if (BluetoothAdapter.checkBluetoothAddress(deviceAddress)){
					secu3Manager = new Secu3Manager(this, deviceAddress, maxConRetries);
					boolean enabled = secu3Manager.enable();
					if (enabled) {															
						startForeground(R.string.foreground_service_started_notification, secu3Notification.secu3Notification);
						Toast.makeText(this, R.string.msg_service_started,Toast.LENGTH_LONG).show();
					} else {
						stopSelf();
					}
				} else {
					stopSelf();
				}
			} else {
				Toast.makeText(this, R.string.msg_service_already_started, Toast.LENGTH_LONG).show();
				sendBroadcast(intent);
			}
		} else if (ACTION_SECU3_SERVICE_STOP.equals(intent.getAction())){
			stopSelf();
		} else if (ACTION_SECU3_SERVICE_SET_TASK.equals(intent.getAction())) {
			SECU3_TASK task = SECU3_TASK.values()[intent.getIntExtra(ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_NONE.ordinal())];
			if (secu3Manager != null) {
				secu3Manager.setTask(task);
				sendBroadcast(intent);
			}
		} else if (ACTION_SECU3_SERVICE_SEND_PACKET.equals(intent.getAction())) {
			if (secu3Manager != null) {
				Secu3Dat packet = intent.getParcelableExtra(ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET);
				int packets_counter = intent.getIntExtra(ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PROGRESS, 0); 
				secu3Manager.appendPacket (packet, packets_counter);
				sendBroadcast(intent);
			}
		}
				
		return super.onStartCommand(intent, flags, startId);	
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		Log.d(getString(R.string.app_name), "Stopping service");
		secu3Notification.notificationManager.cancelAll();
		Secu3Manager manager = secu3Manager;
		secu3Manager  = null;
		if (manager != null){
			if (manager.getDisableReason() != 0){
				Toast.makeText(this, getString(R.string.msg_service_stopped_by_problem, getString(manager.getDisableReason())),Toast.LENGTH_LONG).show();				
			} else {
				Toast.makeText(this, R.string.msg_service_stopped, Toast.LENGTH_LONG).show();
			}
			manager.disable();
		}
		super.onDestroy();
		
	}
}

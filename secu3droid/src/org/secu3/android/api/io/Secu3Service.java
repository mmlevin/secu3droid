package org.secu3.android.api.io;

import org.secu3.android.MainActivity;
import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class Secu3Service extends Service {	
	private static final String LOG_TAG = "Secu3Service";
	
	public static final String PREF_BLUETOOTH_DEVICE = "bluetoothDevice";
	public static final String PREF_CONNECTION_RETRIES = "connectionRetries";
	public static final String PREF_ABOUT = "about";
	
	public static final String ACTION_SECU3_SERVICE_START = "org.secu3.android.intent.action.SECU3_SERVICE_START";
	public static final String ACTION_SECU3_SERVICE_STOP = "org.secu3.android.intent.action.SECU3_SERVICE_STOP";
	public static final String ACTION_SECU3_SERVICE_READ_PARAMS = "org.secu3.android.intent.action.SECU3_SERVICE_READ_PARAMS";
	public static final String RECEIVE_SECU3_SERVICE_PROGRESS = "org.secu3.android.intent.action.SECU3_SERVICE_PROGRESS";
	public static final String SECU3_SERVICE_PROGRESS_CURRENT = "org.secu3.android.intent.action.extra.SECU3_SERVICE_PROGRESS_CURRENT";
	public static final String SECU3_SERVICE_PROGRESS_TOTAL = "org.secu3.android.intent.action.extra.SECU3_SERVICE_PROGRESS_TOTAL";
	public static final String ACTION_SECU3_SERVICE_READ_ERRORS = "org.secu3.android.intent.action.SECU3_SERVICE_READ_ERRORS";
	public static final String ACTION_SECU3_SERVICE_READ_SAVED_ERRORS = "org.secu3.android.intent.action.SECU3_SERVICE_READ_SAVED_ERRORS";
	public static final String SECU3_SERVICE_STATUS_ONLINE = "org.secu3.android.intent.action.STATUS_ONLINE";
	public static final String SECU3_SERVICE_STATUS = "org.secu3.android.intent.action.extra.STATUS";
	
	private Toast toast;
	
	NotificationManager notificationManager;
	private Secu3Manager secu3Manager = null;

	@SuppressLint("ShowToast")
	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String deviceAddress = sharedPreferences.getString(PREF_BLUETOOTH_DEVICE, null);
		int maxConRetries = Integer.parseInt(sharedPreferences.getString(PREF_CONNECTION_RETRIES, this.getString(R.string.defaultConnectionRetries)));
		Log.d(LOG_TAG, "prefs device addr: "+deviceAddress);
		if (ACTION_SECU3_SERVICE_START.equals(intent.getAction())){
			if (secu3Manager == null){
				if (BluetoothAdapter.checkBluetoothAddress(deviceAddress)){
					secu3Manager = new Secu3Manager(this, deviceAddress, maxConRetries);
					boolean enabled = secu3Manager.enable();
					if (enabled) {
						Notification notification = new NotificationCompat.Builder(this)
						.setContentTitle(this.getString(R.string.foreground_service_started_notification))
						.setSmallIcon(R.drawable.ic_launcher)											
						.setWhen(System.currentTimeMillis())
						.setOngoing(true)
						.setContentIntent(PendingIntent.getActivity(this, 0, new Intent (this,MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
						.build();																
						startForeground(R.string.foreground_service_started_notification, notification);
						toast.setText(this.getString(R.string.msg_service_started));
						toast.show();	
					} else {
						stopSelf();
					}
				} else {
					stopSelf();
				}
			} else {
				toast.setText(this.getString(R.string.msg_service_already_started));
				toast.show();				
				secu3Manager.setTask(SECU3_TASK.SECU3_READ_SENSORS);
				sendBroadcast(intent);
			}
		} else if (ACTION_SECU3_SERVICE_STOP.equals(intent.getAction())){
			stopSelf();
		} else if (ACTION_SECU3_SERVICE_READ_PARAMS.equals(intent.getAction())) {
			if (secu3Manager != null) {
				secu3Manager.setTask(SECU3_TASK.SECU3_READ_PARAMS);
				sendBroadcast(intent);
			}
		} else if (ACTION_SECU3_SERVICE_READ_ERRORS.equals(intent.getAction())) {
			if (secu3Manager != null) {
				secu3Manager.setTask(SECU3_TASK.SECU3_READ_ERRORS);
				sendBroadcast(intent);
			}
		} else if (ACTION_SECU3_SERVICE_READ_SAVED_ERRORS.equals(intent.getAction())) {
			if (secu3Manager != null) {
				secu3Manager.setTask(SECU3_TASK.SECU3_READ_SAVED_ERRORS);
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
		notificationManager.cancelAll();
		Secu3Manager manager = secu3Manager;
		secu3Manager  = null;
		if (manager != null){
			if (manager.getDisableReason() != 0){
				toast.setText(getString(R.string.msg_service_stopped_by_problem, getString(manager.getDisableReason())));
				toast.show();				
			} else {
				toast.setText(R.string.msg_service_stopped);
				toast.show();				
			}
			manager.disable();
		}
		super.onDestroy();
		
	}
}

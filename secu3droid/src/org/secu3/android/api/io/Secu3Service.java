package org.secu3.android.api.io;

import org.secu3.android.MainActivity;
import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat.ADCCorPar;
import org.secu3.android.api.io.Secu3Dat.AnglesPar;
import org.secu3.android.api.io.Secu3Dat.CKPSPar;
import org.secu3.android.api.io.Secu3Dat.CarburPar;
import org.secu3.android.api.io.Secu3Dat.FunSetPar;
import org.secu3.android.api.io.Secu3Dat.IdlRegPar;
import org.secu3.android.api.io.Secu3Dat.MiscelPar;
import org.secu3.android.api.io.Secu3Dat.StartrPar;
import org.secu3.android.api.io.Secu3Dat.TemperPar;
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;

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
import android.util.Config;
import android.util.Log;

@SuppressWarnings("deprecation")
public class Secu3Service extends Service {	
	private static final String LOG_TAG = "Secu3Service";
	
	public static final String PREF_BLUETOOTH_DEVICE = "bluetoothDevice";
	public static final String PREF_CONNECTION_RETRIES = "connectionRetries";
	public static final String ACTION_SECU3_SERVICE_START = "org.secu3.android.intent.action.SECU3_SERVICE_START";
	public static final String ACTION_SECU3_SERVICE_STOP = "org.secu3.android.intent.action.SECU3_SERVICE_STOP";
	public static final String ACTION_SECU3_SERVICE_READ_PARAMS = "org.secu3.android.intent.action.SECU3_SERVICE_READ_PARAMS";
	public static final String STATUS_ONLINE = "org.secu3.android.intent.action.STATUS_ONLINE";
	public static final String STATUS = "status";
	
	NotificationManager notificationManager;
	private Secu3Manager secu3Manager = null;
	
	public static class Secu3Params {
		public static boolean valid = false;
		public static StartrPar startrPar;
		public static AnglesPar anglesPar;
		public static IdlRegPar idlRegPar;
		public static FunSetPar funSetPar;
		public static TemperPar temperPar;
		public static CarburPar carburPar;
		public static ADCCorPar adcCorPar;
		public static CKPSPar ckpsPar;
		public static MiscelPar miscelPar;
		
		public static synchronized boolean getValid() {return valid;};
		public static synchronized StartrPar getStartrPar () {return startrPar;};
		public static synchronized AnglesPar getAnglesPar () {return anglesPar;};
		public static synchronized IdlRegPar getIdlRegPar () {return idlRegPar;};
		public static synchronized FunSetPar getFunSetPar () {return funSetPar;};
		public static synchronized TemperPar getTemperPar () {return temperPar;};
		public static synchronized CarburPar getCarburPar () {return carburPar;};
		public static synchronized ADCCorPar getADCCorPar () {return adcCorPar;};
		public static synchronized CKPSPar	  getCKPSPar   () {return ckpsPar;  };
		public static synchronized MiscelPar getMiscelPar () {return miscelPar;};
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String deviceAddress = sharedPreferences.getString(PREF_BLUETOOTH_DEVICE, "00:11:11:24:04:80");
		int maxConRetries = Integer.parseInt(sharedPreferences.getString(PREF_CONNECTION_RETRIES, this.getString(R.string.defaultConnectionRetries)));
		if (Config.LOGD){
			Log.d(LOG_TAG, "prefs device addr: "+deviceAddress);
		}
		if (ACTION_SECU3_SERVICE_START.equals(intent.getAction())){
			if (secu3Manager == null){
				if (BluetoothAdapter.checkBluetoothAddress(deviceAddress)){
					secu3Manager = new Secu3Manager(this, deviceAddress, maxConRetries);
					boolean enabled = secu3Manager.enable();
					if (enabled) {
						Intent myIntent = new Intent(this, MainActivity.class);
						Notification notification = new NotificationCompat.Builder(this)
						.setContentTitle(this.getString(R.string.foreground_service_started_notification))
						.setSmallIcon(R.drawable.ic_launcher)											
						.setWhen(System.currentTimeMillis())
						.setOngoing(true)
						.setContentIntent(PendingIntent.getActivity(this, 0, myIntent, 0))
						.build();						
						startForeground(R.string.foreground_service_started_notification, notification);
					} else {
						stopSelf();
					}
				} else {
					stopSelf();
				}
			}
		} else if (ACTION_SECU3_SERVICE_STOP.equals(intent.getAction())){
			stopSelf();
		} else if (ACTION_SECU3_SERVICE_READ_PARAMS.equals(intent.getAction())) {
			if (secu3Manager != null) {
				secu3Manager.setTask(SECU3_TASK.SECU3_READ_PARAMS);
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
			} else {
			}
			manager.disable();
		}
		super.onDestroy();
		
	}
}

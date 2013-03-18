package org.secu3.android.api.io;

import org.secu3.android.MainActivity;
import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat.ADCCorPar;
import org.secu3.android.api.io.Secu3Dat.AnglesPar;
import org.secu3.android.api.io.Secu3Dat.CKPSPar;
import org.secu3.android.api.io.Secu3Dat.CarburPar;
import org.secu3.android.api.io.Secu3Dat.FnNameDat;
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
	public static final String ACTION_SECU3_SERVICE_READ_ERRORS = "org.secu3.android.intent.action.SECU3_SERVICE_READ_ERRORS";
	public static final String ACTION_SECU3_SERVICE_READ_SAVED_ERRORS = "org.secu3.android.intent.action.SECU3_SERVICE_READ_SAVED_ERRORS";
	public static final String STATUS_ONLINE = "org.secu3.android.intent.action.STATUS_ONLINE";
	public static final String STATUS = "status";
	
	NotificationManager notificationManager;
	private Secu3Manager secu3Manager = null;
	
	public static class Secu3Params {
		private static boolean valid = false;
		private static StartrPar startrPar;
		private static AnglesPar anglesPar;
		private static IdlRegPar idlRegPar;
		private static FnNameDat fnNameDat;
		private static FunSetPar funSetPar;
		private static TemperPar temperPar;
		private static CarburPar carburPar;
		private static ADCCorPar adcCorPar;
		private static CKPSPar ckpsPar;
		private static MiscelPar miscelPar;
		
		public synchronized static boolean isValid() {
			return valid;
		}
		public synchronized static void setValid(boolean valid) {
			Secu3Params.valid = valid;
		}
		public synchronized static StartrPar getStartrPar() {
			return startrPar;
		}
		public synchronized static void setStartrPar(StartrPar startrPar) {
			Secu3Params.startrPar = startrPar;
		}
		public synchronized static AnglesPar getAnglesPar() {
			return anglesPar;
		}
		public synchronized static void setAnglesPar(AnglesPar anglesPar) {
			Secu3Params.anglesPar = anglesPar;
		}
		public synchronized static IdlRegPar getIdlRegPar() {
			return idlRegPar;
		}
		public synchronized static void setIdlRegPar(IdlRegPar idlRegPar) {
			Secu3Params.idlRegPar = idlRegPar;
		}
		public synchronized static FnNameDat getFnNameDat() {
			return fnNameDat;
		}
		public synchronized static void setFnNameDat(FnNameDat fnNameDat) {
			Secu3Params.fnNameDat = fnNameDat;
		}
		public synchronized static FunSetPar getFunSetPar() {
			return funSetPar;
		}
		public synchronized static void setFunSetPar(FunSetPar funSetPar) {
			Secu3Params.funSetPar = funSetPar;
		}
		public synchronized static TemperPar getTemperPar() {
			return temperPar;
		}
		public synchronized static void setTemperPar(TemperPar temperPar) {
			Secu3Params.temperPar = temperPar;
		}
		public synchronized static CarburPar getCarburPar() {
			return carburPar;
		}
		public synchronized static void setCarburPar(CarburPar carburPar) {
			Secu3Params.carburPar = carburPar;
		}
		public synchronized static ADCCorPar getAdcCorPar() {
			return adcCorPar;
		}
		public synchronized static void setAdcCorPar(ADCCorPar adcCorPar) {
			Secu3Params.adcCorPar = adcCorPar;
		}
		public synchronized static CKPSPar getCkpsPar() {
			return ckpsPar;
		}
		public synchronized static void setCkpsPar(CKPSPar ckpsPar) {
			Secu3Params.ckpsPar = ckpsPar;
		}
		public synchronized static MiscelPar getMiscelPar() {
			return miscelPar;
		}
		public synchronized static void setMiscelPar(MiscelPar miscelPar) {
			Secu3Params.miscelPar = miscelPar;
		}
		
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
			} else {
			}
			manager.disable();
		}
		super.onDestroy();
		
	}
}

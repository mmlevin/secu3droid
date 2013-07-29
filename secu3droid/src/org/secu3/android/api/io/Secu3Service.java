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

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class Secu3Service extends Service implements OnSharedPreferenceChangeListener {	
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
	public static final String EVENT_SECU3_SERVICE_RECEIVE_PACKET= "org.secu3.android.intent.action.SECU3_SERVICE_RECEIVE_PACKET";
	public static final String EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET= "org.secu3.android.intent.action.extra.SECU3_SERVICE_RECEIVE_PARAM_PACKET";
	
	
	NotificationManager notificationManager;
	private Secu3Manager secu3Manager = null;
	public static Secu3Notification secu3Notification = null;

	@Override
	public void onCreate() {
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		secu3Notification = new Secu3Notification(this);
		super.onCreate();		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String deviceAddress = sharedPref.getString(getString(R.string.pref_bluetooth_device_key), null);
		onSharedPreferenceChanged (sharedPref,getString(R.string.pref_write_log_key));
		int maxConRetries = Integer.parseInt(sharedPref.getString(getString(R.string.pref_connection_retries_key), this.getString(R.string.defaultConnectionRetries)));
		Log.d(LOG_TAG, "prefs device addr: "+deviceAddress);
		if (ACTION_SECU3_SERVICE_START.equals(intent.getAction())){
			if (BluetoothAdapter.getDefaultAdapter() != null) {
				if (secu3Manager == null){
					if (BluetoothAdapter.checkBluetoothAddress(deviceAddress)){
						secu3Manager = new Secu3Manager(this, deviceAddress, maxConRetries);
						boolean enabled = secu3Manager.enable();
						if (enabled) {															
							startForeground(R.string.foreground_service_started_notification, secu3Notification.secu3Notification);
							secu3Notification.toast(R.string.msg_service_started);
						} else {
							stopSelf();
						}
					} else {
						stopSelf();
					}
				} else {
					secu3Notification.toast(R.string.msg_service_already_started);
					sendBroadcast(intent);
				}
			} else {
				secu3Notification.toast(R.string.msg_bluetooth_unsupported);
				stopSelf();
			}
		} else if (ACTION_SECU3_SERVICE_STOP.equals(intent.getAction())){
			Log.d(LOG_TAG, "Stopping service");
			secu3Notification.notificationManager.cancelAll();
			Secu3Manager manager = secu3Manager;
			secu3Manager  = null;
			if (manager != null){
				if (manager.getDisableReason() != 0){
					secu3Notification.notifyServiceStopped(manager.getDisableReason());
					secu3Notification.toast(getString(R.string.msg_service_stopped_by_problem, getString(manager.getDisableReason())));				
				} else {
					secu3Notification.toast(R.string.msg_service_stopped);
				}
				manager.disable();
			}			
			stopSelf();
			System.exit(0);			
		} else if (ACTION_SECU3_SERVICE_SET_TASK.equals(intent.getAction())) {
			SECU3_TASK task = SECU3_TASK.values()[intent.getIntExtra(ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_NONE.ordinal())];
			if (secu3Manager != null) {
				secu3Manager.setTask(task);
				sendBroadcast(intent);
			}
		} else if (ACTION_SECU3_SERVICE_SEND_PACKET.equals(intent.getAction())) {
			if (secu3Manager != null) {
				Secu3Packet packet = intent.getParcelableExtra(ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET);
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
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (getString(R.string.pref_write_log_key).equals(key)) {
			boolean writeLog = sharedPreferences.getBoolean(key, false);
			if (secu3Manager != null) {
				secu3Manager.setTask(writeLog?SECU3_TASK.SECU3_START_LOGGING:SECU3_TASK.SECU3_STOP_LOGGING);
			}
		}		
	}	
}

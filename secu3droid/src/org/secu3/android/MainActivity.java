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

package org.secu3.android;

import java.util.Locale;

import org.andengine.AndEngine;
import org.secu3.android.api.io.ProtoFieldFloat;
import org.secu3.android.api.io.ProtoFieldInteger;
import org.secu3.android.api.io.ProtoFieldString;
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;
import org.secu3.android.api.io.Secu3Packet;
import org.secu3.android.api.io.Secu3Service;
import org.secu3.android.api.utils.PacketUtils;

import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String RAW_SENSORS = "RAW_SENSORS";
	private static final String STATUS = "status";
	private static final String DATA = "data";	

	private String sensorsFormat = "";
	private String speedFormat = "";
	private String sensorsRawFormat = "";
	private boolean isOnline;
	private boolean errors = false;
	private boolean rawSensors = false;
	private int protocol_version = SettingsActivity.PROTOCOL_UNKNOWN;
	private LinearLayout logButtonLayout = null;
	private PowerManager.WakeLock wakelock = null;
	
	private PacketUtils packetUtils = null;
	
	ReceiveMessages receiver = null;
	TextView textViewData = null;
	TextView textViewDataExt = null;
	TextView textViewStatus = null;
	TextView textFWInfo = null;	
	int fwOptions = Integer.MIN_VALUE;
	
	public class ReceiveMessages extends BroadcastReceiver 
	{
		public IntentFilter intentFilter;
		
		public ReceiveMessages() {
			intentFilter = new IntentFilter();
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET);			
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE);
		}
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{    
			update (intent); 
		}
	}
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		setTheme(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_night_mode_key), false)?R.style.AppBaseTheme:R.style.AppBaseTheme_Light);	
		setContentView(R.layout.activity_main);		

		packetUtils = new PacketUtils(this);
		
		sensorsFormat = getString(R.string.sensors_format);
		speedFormat = getString(R.string.speed_format);
		sensorsRawFormat = getString(R.string.sensors_raw_format);
		textViewData = (TextView)findViewById(R.id.textViewData);
		textViewDataExt = (TextView)findViewById(R.id.textViewDataExt);
		textViewStatus = (TextView)findViewById(R.id.mainTextViewStatus);
		textFWInfo = (TextView)findViewById(R.id.mainTextFWInfo);
		
		receiver = new ReceiveMessages();		
		
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Secu3Droid wakelock");
		
		if (savedInstanceState != null) {
			textViewData.setText(savedInstanceState.getString(DATA));
			textViewStatus.setText(savedInstanceState.getString(STATUS));
			rawSensors =  savedInstanceState.getBoolean(RAW_SENSORS);
		}
		
		setRawMode(rawSensors);
				
		logButtonLayout = (LinearLayout)findViewById(R.id.mainLogButtonLayout);
				
		View.OnClickListener logButtonListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.mainLogButton1:
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,v.getContext(),Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_SET_LOG_MARKER_1.ordinal()));
					break;
				case R.id.mainLogButton2:startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,v.getContext(),Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_SET_LOG_MARKER_2.ordinal()));
					break;
				case R.id.mainLogButton3:
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,v.getContext(),Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_SET_LOG_MARKER_3.ordinal()));
					break;
					default:break;
				}
				
			}
		};
		
		Button b = (Button) findViewById(R.id.mainLogButton1);
		b.setOnClickListener(logButtonListener);
		b= (Button) findViewById(R.id.mainLogButton2);
		b.setOnClickListener(logButtonListener);
		b= (Button) findViewById(R.id.mainLogButton3);
		b.setOnClickListener(logButtonListener);
		
		
		super.onCreate(savedInstanceState);		
	}
	
	@Override
	protected void onDestroy() {
		if ((wakelock != null)&&(wakelock.isHeld())) {
			wakelock.release();
		}
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {						
		outState.putString(DATA, textViewData.getText().toString());
		outState.putString(STATUS, textViewStatus.getText().toString());
		outState.putBoolean(RAW_SENSORS, rawSensors);
		super.onSaveInstanceState(outState);		
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem m = menu.findItem(R.id.menu_errors);		
		m.setIcon(errors?R.drawable.ic_menu_errors_highlighted:R.drawable.ic_menu_errors);
		m = menu.findItem(R.id.menu_raw_sensors);
		m.setChecked(rawSensors);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_dashboard:
			if(!AndEngine.isDeviceSupported(this)) {
				new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_device_not_supported_title)
				.setMessage(R.string.dialog_device_not_supported_title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.ok, null)
				.create().show();
			} else startActivity(new Intent(this,DashBoardActivity.class));
			return true;
		case R.id.menu_preferences:
			startActivity(new Intent(this,SettingsActivity.class));
			return true;
		case R.id.menu_params:			
			startActivity(new Intent(this,ParamActivity.class));
			return true;
		case R.id.menu_errors:
			startActivity(new Intent(this,ErrorsActivity.class));
			return true;
		case R.id.menu_exit:
			Secu3Service.secu3Notification.notificationManager.cancelAll();
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_STOP,Uri.EMPTY,this,Secu3Service.class));
			return true;
		case R.id.menu_diagnostics:
			new AlertDialog.Builder(this)
				.setTitle(android.R.string.dialog_alert_title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(R.string.menu_diagnostics_warning_title)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						if ((fwOptions == Integer.MIN_VALUE) || ((fwOptions & (1 << Secu3Packet.COPT_DIAGNOSTICS)) == 0)) {
							Toast.makeText(getApplicationContext(), R.string.diagnostics_not_supported_title, Toast.LENGTH_LONG).show();
						}
						startActivity(new Intent (getApplicationContext(),DiagnosticsActivity.class));
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create()
				.show();
			return true;
		case R.id.menu_raw_sensors:			
			item.setChecked(rawSensors = !item.isChecked());
			setRawMode(rawSensors);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}		
	}
	
	
	@Override
	protected void onResume() {
		if (SettingsActivity.isKeepScreenAliveActive(this)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		if ((wakelock != null)&&(SettingsActivity.isWakeLockEnabled(this))) {
			wakelock.acquire();
		} else if ((wakelock != null)&&(wakelock.isHeld())) {
			wakelock.release();
		}
		registerReceiver(receiver, receiver.intentFilter);
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_START,Uri.EMPTY,this,Secu3Service.class));
		protocol_version = SettingsActivity.getProtocolVersion(getBaseContext());
		logButtonLayout.setVisibility(((protocol_version < SettingsActivity.PROTOCOL_14012014_WINTER_RELEASE) || (!SettingsActivity.isSensorLoggerEnabled(this)))?View.GONE:View.VISIBLE);
		setRawMode(rawSensors);
		invalidateOptionsMenu();
		super.onResume();		
	}
	
	@Override
	protected void onPause() {	
		if (SettingsActivity.isKeepScreenAliveActive(this)) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		unregisterReceiver(receiver);
		super.onPause();		
	}
	
	private void setRawMode(boolean raw) {
		SECU3_TASK task = raw?SECU3_TASK.SECU3_RAW_SENSORS:SECU3_TASK.SECU3_READ_SENSORS;
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, task.ordinal()));
	}	
	
	void update(Intent intent) {
		if (Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE.equals(intent.getAction())) {			
			boolean isOnline = intent.getBooleanExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS,false); 
			String s = isOnline?getString(R.string.status_online):getString(R.string.status_offline);
			textViewStatus.setText(s);		
			if (isOnline && !this.isOnline) {
				this.isOnline = true;						
				startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_READ_FW_INFO.ordinal()));
				setRawMode(rawSensors);				
			}						
		} else if (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET.equals(intent.getAction()))
		{
			Secu3Packet packet = intent.getParcelableExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET);
			if (packet != null) {
				switch (packet.getPacketIdResId()) {
				case R.string.packet_type_sensor_dat:
					boolean errors = ((ProtoFieldInteger) packet.getField(R.string.sensor_dat_errors_title)).getValue() != 0;
					if (errors != this.errors) {
						this.errors = errors;
						invalidateOptionsMenu();
					}
					if (!rawSensors) {
						int bitfield = ((ProtoFieldInteger) packet.getField(R.string.sensor_dat_bitfield_title)).getValue();
						textViewData.setText(String.format(Locale.US,sensorsFormat,
								((ProtoFieldInteger) packet.getField(R.string.sensor_dat_rpm_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.sensor_dat_map_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.sensor_dat_voltage_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.sensor_dat_temperature_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.sensor_dat_angle_correction_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.sensor_dat_knock_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.sensor_dat_knock_retard_title)).getValue(),
								((ProtoFieldInteger) packet.getField(R.string.sensor_dat_air_flow_title)).getValue(),
								Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_EPHH_VALVE),
								Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_CARB),
								Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_GAS),
								Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_EPM_VALVE),
								Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_COOL_FAN),
								Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_ST_BLOCK),
								((ProtoFieldFloat) packet.getField(R.string.sensor_dat_addi1_voltage_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.sensor_dat_addi2_voltage_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.sensor_dat_tps_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.sensor_dat_choke_position_title)).getValue()));						
						
						if (protocol_version >= SettingsActivity.PROTOCOL_28082013_SUMMER_RELEASE) {
							textViewDataExt.setText(String.format(Locale.US,speedFormat,
									packetUtils.calcSpeed(((ProtoFieldInteger) packet.getField(R.string.sensor_dat_speed_title)).getValue()),
									packetUtils.calcDistance(((ProtoFieldInteger) packet.getField(R.string.sensor_dat_distance_title)).getValue())));
						}
					}			
					break;
				case R.string.packet_type_adcraw_dat:
					if (rawSensors) {
						textViewDataExt.setText(null);
						textViewData.setText(String.format(Locale.US,sensorsRawFormat,
								((ProtoFieldFloat) packet.getField(R.string.adcraw_map_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.adcraw_voltage_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.adcraw_temperature_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.adcraw_knock_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.adcraw_tps_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.adcraw_addi1_title)).getValue(),
								((ProtoFieldFloat) packet.getField(R.string.adcraw_addi2_title)).getValue()));
					}
					break;
				case R.string.packet_type_fwinfo_dat:
					textFWInfo.setText(((ProtoFieldString) packet.findField(R.string.fwinfo_dat_data_title)).getValue());
					fwOptions = ((ProtoFieldInteger) packet.findField(R.string.fwinfo_dat_options_title)).getValue();
					break;
				default:
					break;
				}
			}
		}		
	}
}

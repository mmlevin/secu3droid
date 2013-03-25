package org.secu3.android;

import java.util.Locale;

import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.ADCRawDat;
import org.secu3.android.api.io.Secu3Dat.FWInfoDat;
import org.secu3.android.api.io.Secu3Dat.SensorDat;
import org.secu3.android.api.io.Secu3Service;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public class ReceiveMessages extends BroadcastReceiver 
	{
	@Override
	   public void onReceive(Context context, Intent intent) 
	   {    
	       String action = intent.getAction();
	       Log.d(getString(R.string.app_name), action);
	       if(action.equalsIgnoreCase(Secu3Service.SECU3_SERVICE_STATUS_ONLINE)) {
	    	   updateStatus(intent);
	       } else {
	    	   updateData(intent);
	       }
	   }
	}
	
	ReceiveMessages receiver = null;
	TextView textViewData = null;
	TextView textViewStatus = null;
	CheckBox checkBox = null;
	final static String LOG_TAG = "Main activity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		receiver = new ReceiveMessages();
		setContentView(R.layout.activity_main);
		textViewData = (TextView)findViewById(R.id.textViewData);
		textViewStatus = (TextView)findViewById(R.id.mainTextViewStatus);
		checkBox = (CheckBox)findViewById(R.id.anglesZeroAngleCheckBox);
		
		if (savedInstanceState != null) {
			textViewData.setText(savedInstanceState.getString("data"));
			textViewStatus.setText(savedInstanceState.getString("status"));
			checkBox.setChecked(savedInstanceState.getBoolean("checkbox"));
		}
		Log.d(getString(R.string.app_name), "onCreate");
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("data", textViewData.getText().toString());
		outState.putString("status", textViewStatus.getText().toString());
		outState.putBoolean("status", checkBox.isChecked());
		super.onSaveInstanceState(outState);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this,SettingsActivity.class));
			return true;
		case R.id.menu_params:			
			startActivity(new Intent(this,ParamActivity.class));
			return true;
		case R.id.menu_errors:
			startActivity(new Intent(this,ErrorsActivity.class));
			return true;
		case R.id.menu_exit:
			stopService(new Intent (this,Secu3Service.class));
			System.exit(0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}		
	}
	
	
	@Override
	protected void onResume() {
		try {
			IntentFilter infil = new IntentFilter();
			infil.addAction(Secu3Dat.RECEIVE_SENSOR_DAT);
			infil.addAction(Secu3Dat.RECEIVE_ADCRAW_DAT);
			infil.addAction(Secu3Dat.RECEIVE_FWINFO_DAT);
			infil.addAction(Secu3Service.SECU3_SERVICE_STATUS_ONLINE);
			registerReceiver(receiver, infil);
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_START,Uri.EMPTY,this,Secu3Service.class));
			startService(new Intent (checkBox.isChecked()?Secu3Service.ACTION_SECU3_SERVICE_READ_RAW_SENSORS:Secu3Service.ACTION_SECU3_SERVICE_READ_SENSORS,Uri.EMPTY,this,Secu3Service.class));			
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_READ_FW_INFO,Uri.EMPTY,this,Secu3Service.class));			
			
		} catch (Exception e) {
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
			
		}
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		try {
				unregisterReceiver(receiver);
		} catch (Exception e) {
			
		}
		super.onDestroy();
	}

	public void onClick (View v) {
		if (v == checkBox) {
			startService(new Intent (checkBox.isChecked()?Secu3Service.ACTION_SECU3_SERVICE_READ_RAW_SENSORS:Secu3Service.ACTION_SECU3_SERVICE_READ_SENSORS,Uri.EMPTY,this,Secu3Service.class));			
		}
	}
	
	void updateData(Intent intent) {
		String s = "";
		if (!checkBox.isChecked() && intent.getAction().equalsIgnoreCase(Secu3Dat.RECEIVE_SENSOR_DAT)) {
			SensorDat sd = (SensorDat)intent.getParcelableExtra(SensorDat.class.getCanonicalName());
			if (sd != null) {
				s = String.format(Locale.getDefault(),"RPM: %d min-1\r\nPressure: %f kPa\r\n"
						+ "Voltage: %f V\r\nTemperature: %f °C\r\n"
						+ "Angle: %f °\r\nKnock level: %f V\r\n"
						+ "Knock retard: %f °\r\nAir flow: %d\r\n"
						+ "EPHH valve: %d\r\nCarb sensor: %d\r\n"
						+ "Gas valve: %d\r\nEPM Valve: %d\r\nCE State: %d",
						sd.frequen, sd.pressure, sd.voltage, sd.temperat, sd.adv_angle,
						sd.knock_k, sd.knock_retard, sd.air_flow, sd.ephh_valve,
						sd.carb, sd.gas, sd.epm_valve, sd.ce_state
				);
				textViewData.setText(s);				
			}
		}
		else if (checkBox.isChecked() && intent.getAction().equalsIgnoreCase(Secu3Dat.RECEIVE_ADCRAW_DAT)) {
			ADCRawDat ad = (ADCRawDat)intent.getParcelableExtra(ADCRawDat.class.getCanonicalName());
			if (ad != null) {
				s = String.format(Locale.getDefault(),"MAP Sensor data: %fV\r\nVoltage Sensor data: %fV\r\nTemperature Sensor data: %fV\r\nKnock Sensor data: %fV",ad.map_value,ad.ubat_value,ad.temp_value,ad.knock_value);
				textViewData.setText(s);
			}
		} else if (intent.getAction().equals(Secu3Dat.RECEIVE_FWINFO_DAT)) {
			FWInfoDat packet = (FWInfoDat) intent.getParcelableExtra(FWInfoDat.class.getCanonicalName());
		}
	}
	
	void updateStatus(Intent intent) {
		String s = intent.getBooleanExtra(Secu3Service.SECU3_SERVICE_STATUS,false)?"Online":"Offline";
		textViewStatus.setText(s);
	}
}

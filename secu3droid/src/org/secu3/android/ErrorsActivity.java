package org.secu3.android;

import org.secu3.android.MainActivity.ReceiveMessages;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.CEErrCodes;
import org.secu3.android.api.io.Secu3Service;
import org.secu3.android.api.io.Secu3Dat.CESavedErr;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ErrorsActivity extends Activity {
	TextView textViewStatus = null;
	
	CheckBox CKPSCheckBox;
	CheckBox EepromCrcError;
	CheckBox FirmwareCrcError;
	CheckBox DetonationProcessorError;
	CheckBox DetonationDetectedError;
	CheckBox PressureSensorError;
	CheckBox TemperatureSensorError;
	CheckBox VoltageError;
	CheckBox DwellControlError;
	CheckBox PhaseSensorError;
	CheckBox RealtimeError;
	CheckBox ReadingInertion;

	void setRealtime (boolean realtime) {
		startService(new Intent (realtime?Secu3Service.ACTION_SECU3_SERVICE_READ_ERRORS:Secu3Service.ACTION_SECU3_SERVICE_READ_SAVED_ERRORS,Uri.EMPTY,this,Secu3Service.class));		
	}
	
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
	
	ReceiveMessages receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_errors);
		
		receiver = new ReceiveMessages();		
		
		textViewStatus = (TextView)findViewById(R.id.errorsTextViewStatus);
		CKPSCheckBox = (CheckBox)findViewById(R.id.errorsCKPSCheckBox);
		EepromCrcError = (CheckBox)findViewById(R.id.errorsEepromCrcCheckBox);
		FirmwareCrcError = (CheckBox)findViewById(R.id.errorsFirmwareCrcCheckBox);
		DetonationProcessorError = (CheckBox)findViewById(R.id.errorsDetonationProcessorCheckBox);
		DetonationDetectedError = (CheckBox)findViewById(R.id.errorsDetonationDetectedCheckBox);
		PressureSensorError = (CheckBox)findViewById(R.id.errorsPressureSensorCheckBox);
		TemperatureSensorError = (CheckBox)findViewById(R.id.errorsTemperatureSensorError);
		VoltageError = (CheckBox)findViewById(R.id.errorsVoltageErrorCheckBox);
		DwellControlError = (CheckBox)findViewById(R.id.errorsDwellControlErrorCheckBox);
		PhaseSensorError = (CheckBox)findViewById(R.id.errorsPhaseSensorErrorCheckBox);
		RealtimeError = (CheckBox)findViewById(R.id.errorsRealtimeErrorsCheckBox);
		ReadingInertion = (CheckBox)findViewById(R.id.errorsInertionCheckBox);
		
		RealtimeError.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (buttonView == RealtimeError) {
					setRealtime(RealtimeError.isChecked());
				}				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_errors, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		try {
			IntentFilter infil = new IntentFilter();
			infil.addAction(Secu3Dat.RECEIVE_CE_ERR_CODES);
			infil.addAction(Secu3Dat.RECEIVE_CE_SAVED_ERR);
			infil.addAction(Secu3Service.SECU3_SERVICE_STATUS_ONLINE);
			registerReceiver(receiver, infil);
			
		} catch (Exception e) {
		}
		finally {
			setRealtime(RealtimeError.isChecked());
			super.onResume();
		}
	}
	
	@Override
	protected void onPause() {
		try {
			unregisterReceiver(receiver);
		}
		catch (Exception e) {
			
		}
		finally {
			super.onPause();
		}
	}
	
	void updateFlags (int flags) {
		CKPSCheckBox.setChecked(((flags >> Secu3Dat.ECUERROR_CKPS_MALFUNCTION) & 1) != 0);
		EepromCrcError.setChecked(((flags >> Secu3Dat.ECUERROR_EEPROM_PARAM_BROKEN) & 1)  != 0);
		FirmwareCrcError.setChecked(((flags >> Secu3Dat.ECUERROR_PROGRAM_CODE_BROKEN) & 1)  != 0);
		DetonationProcessorError.setChecked(((flags >> Secu3Dat.ECUERROR_KSP_CHIP_FAILED) & 1)  != 0);
		DetonationDetectedError.setChecked(((flags >> Secu3Dat.ECUERROR_KNOCK_DETECTED) & 1)  != 0);
		PressureSensorError.setChecked(((flags >> Secu3Dat.ECUERROR_MAP_SENSOR_FAIL) & 1)  != 0);
		TemperatureSensorError.setChecked(((flags >> Secu3Dat.ECUERROR_TEMP_SENSOR_FAIL) & 1)  != 0);
		VoltageError.setChecked(((flags >> Secu3Dat.ECUERROR_VOLT_SENSOR_FAIL) & 1)  != 0);
		DwellControlError.setChecked(((flags >> Secu3Dat.ECUERROR_DWELL_CONTROL) & 1)  != 0);
		PhaseSensorError.setChecked(((flags >> Secu3Dat.ECUERROR_CAMS_MALFUNCTION) & 1)  != 0);		
	}
	
	void updateData (Intent intent) {
		String action = intent.getAction();		
		if (Secu3Dat.RECEIVE_CE_SAVED_ERR.equals(action)) {
			CESavedErr packet = intent.getParcelableExtra(CESavedErr.class.getCanonicalName());
			updateFlags(packet.flags);		
		} else if (Secu3Dat.RECEIVE_CE_ERR_CODES.equals(action)) {
			CEErrCodes packet = intent.getParcelableExtra(CEErrCodes.class.getCanonicalName());
			updateFlags(packet.flags);									
		}
	}
	
	void updateStatus(Intent intent) {
		String s = intent.getBooleanExtra(Secu3Service.SECU3_SERVICE_STATUS,false)?"Online":"Offline";
		textViewStatus.setText(s);
	}
	
}

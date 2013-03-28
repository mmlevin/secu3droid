package org.secu3.android;

import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.CEErrCodes;
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;
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
	public final String LOG_TAG = "ErrorsActivity";
	
	boolean isOnline = false;
	
	TextView errorsTextViewStatus = null;	
	CheckBox CKPSCheckBox = null;
	CheckBox EepromCrcError = null;
	CheckBox FirmwareCrcError = null;
	CheckBox DetonationProcessorError = null;
	CheckBox DetonationDetectedError = null;
	CheckBox PressureSensorError = null;
	CheckBox TemperatureSensorError = null;
	CheckBox VoltageError = null;
	CheckBox DwellControlError = null;
	CheckBox PhaseSensorError = null;
	CheckBox RealtimeError = null;
	CheckBox ReadingInertion = null;

	private void setRealtime (boolean realtime) {
		SECU3_TASK task = realtime?SECU3_TASK.SECU3_READ_ERRORS:SECU3_TASK.SECU3_READ_SAVED_ERRORS;
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, task.ordinal()));		
	}
	
	public class ReceiveMessages extends BroadcastReceiver 
	{
		public IntentFilter intentFilter = null;
		
		public ReceiveMessages() {
			intentFilter = new IntentFilter();
			intentFilter.addAction(Secu3Dat.RECEIVE_CE_ERR_CODES);
			intentFilter.addAction(Secu3Dat.RECEIVE_CE_SAVED_ERR);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE);
		}
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{    
			String action = intent.getAction();
			Log.d(LOG_TAG, action);
			update(intent);
		}
	}
	
	ReceiveMessages receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(LOG_TAG, "onCreate");
		
		setContentView(R.layout.activity_errors);
		
		receiver = new ReceiveMessages();		
		
		errorsTextViewStatus = (TextView)findViewById(R.id.errorsTextViewStatus);
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
		getMenuInflater().inflate(R.menu.activity_errors, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Log.d(LOG_TAG, "onResume");
		
		isOnline = false;
		
		try {
			registerReceiver(receiver, receiver.intentFilter);			
		} catch (Exception e) {
		}
		finally {
			
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Log.d(LOG_TAG, "onPause");
		
		try {
			unregisterReceiver(receiver);
		}
		catch (Exception e) {		
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
	
	void update (Intent intent) {		
		if (Secu3Dat.RECEIVE_CE_SAVED_ERR.equals(intent.getAction())) {
			CESavedErr packet = intent.getParcelableExtra(CESavedErr.class.getCanonicalName());
			updateFlags(packet.flags);		
		} else if (Secu3Dat.RECEIVE_CE_ERR_CODES.equals(intent.getAction())) {
			CEErrCodes packet = intent.getParcelableExtra(CEErrCodes.class.getCanonicalName());
			updateFlags(packet.flags);									
		} else if (Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE.equals(intent.getAction())) {
			boolean isOnline = intent.getBooleanExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS,false);
			if (isOnline && !this.isOnline) {
				this.isOnline = true;
				setRealtime(RealtimeError.isChecked());
			}			
			errorsTextViewStatus.setText(isOnline?"Online":"Offline");
		}
	}		
}

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
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ErrorsActivity extends Activity {
	public final String LOG_TAG = "ErrorsActivity";
	
	boolean isOnline = false;
	
	TextView errorsTextViewStatus = null;	
	CheckBox RealtimeError = null;
	CheckBox ReadingInertion = null;
		
	private CheckBox errors[] = null;

	private void setRealtime (boolean realtime) {
		SECU3_TASK task = realtime?SECU3_TASK.SECU3_READ_ERRORS:SECU3_TASK.SECU3_READ_SAVED_ERRORS;
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, task.ordinal()));		
	}
	
	private int getErrors () {
		int res = 0;
		for (int i = 0; i != Secu3Dat.SECU3_ECU_ERRORS_COUNT; ++i) {
			if (errors [i].isChecked()) res |= 0x01 << i; 
		}
		return res;
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
		
		errors = new CheckBox[Secu3Dat.SECU3_ECU_ERRORS_COUNT];
		String errorNames[] = getResources().getStringArray(R.array.errors_ecu_errors_names);
		String errorBCs[] = getResources().getStringArray(R.array.errors_ecu_errors_blink_codes);
		LinearLayout l = (LinearLayout)findViewById(R.id.errorsLinearLayout);
		
		for (int i = 0; i != Secu3Dat.SECU3_ECU_ERRORS_COUNT; ++i) {
			errors[i] = new CheckBox(this);
			errors[i].setText(String.format("(%s) - %s",errorBCs[i],errorNames[i]));
			errors[i].setTextAppearance(this, R.style.secu3TextAppearance);
			l.addView(errors[i]);			
		}		
		
		errorsTextViewStatus = (TextView)findViewById(R.id.errorsStatusTextView);
		RealtimeError = (CheckBox)findViewById(R.id.errorsRealtimeErrorsCheckBox);
		ReadingInertion = (CheckBox)findViewById(R.id.errorsInertionCheckBox);
		
		RealtimeError.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (buttonView == RealtimeError) {
					setRealtime(RealtimeError.isChecked());
					ReadingInertion.setEnabled (RealtimeError.isChecked());
					ActivityCompat.invalidateOptionsMenu(ErrorsActivity.this);
				}				
			}
		});
		RealtimeError.setChecked(false);
		ReadingInertion.setEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_errors, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_errors_write:
			CESavedErr err = new CESavedErr();
			err.flags = getErrors();
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, err));			
			return true;
		case R.id.menu_errors_read:
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_READ_SAVED_ERRORS.ordinal()));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_errors_read).setEnabled(!RealtimeError.isChecked());
		menu.findItem(R.id.menu_errors_write).setEnabled(!RealtimeError.isChecked());
		return super.onPrepareOptionsMenu(menu);
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
		for (int i = 0; i != Secu3Dat.SECU3_ECU_ERRORS_COUNT; ++i) {
			errors[i].setChecked(((flags & 0x01) != 0)?true:false);
			flags >>= 1; 
		}
	}
	
	void update (Intent intent) {		
		if (Secu3Dat.RECEIVE_CE_SAVED_ERR.equals(intent.getAction())) {
			CESavedErr packet = intent.getParcelableExtra(CESavedErr.class.getCanonicalName());
			updateFlags(packet.flags);		
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_READ_SENSORS.ordinal()));
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

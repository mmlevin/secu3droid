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

import java.util.ArrayList;

import org.secu3.android.api.io.ProtoFieldInteger;
import org.secu3.android.api.io.Secu3Packet;
import org.secu3.android.api.io.Secu3Service;
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;
import org.secu3.android.parameters.ParamItemsAdapter;
import org.secu3.android.parameters.items.BaseParamItem;
import org.secu3.android.parameters.items.ParamItemBoolean;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class ErrorsActivity extends Activity {
	private static final String INERTNESS = "inertness";
	private static final String REALTIME = "realtime";
	private static final String ERRORS = "errors";
	public final int INERTNESS_COUNT = 10;
	
	private Secu3Packet CeSavedError = null;
	private Secu3Packet OpCompNc = null;
	
	boolean isOnline = false;
	boolean realtime;
	private boolean inertness;
	
	ParamItemsAdapter adapter;
	TextView errorsTextViewStatus = null;	
	CheckBox RealtimeError = null;
	CheckBox ReadingInertion = null;
		
	private ArrayList<BaseParamItem> errors = null;
	private int errorsInertness[] = null;
	

	private void setRealtime (boolean realtime) {
		for (int i = 0; i != Secu3Packet.SECU3_ECU_ERRORS_COUNT; i++) {
			errors.get(i).setEnabled(!realtime);
		}		
		adapter.notifyDataSetChanged();
		ReadingInertion.setEnabled (realtime);
		ActivityCompat.invalidateOptionsMenu(ErrorsActivity.this);		
		SECU3_TASK task = realtime?SECU3_TASK.SECU3_READ_ERRORS:SECU3_TASK.SECU3_READ_SENSORS;
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, task.ordinal()));	
	}
	
	public boolean getInertness() {
		return inertness;
	}

	public void setInertness(boolean inertness) {
		this.inertness = inertness;
	}			
		
	public class ReceiveMessages extends BroadcastReceiver 
	{
		public IntentFilter intentFilter = null;
		
		public ReceiveMessages() {
			intentFilter = new IntentFilter();
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_SKELETON_PACKET);
		}
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{    
			update(intent);
		}
	}
	
	ReceiveMessages receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_night_mode_key), false)?R.style.AppBaseTheme:R.style.AppBaseTheme_Light);
		
		
		
		setContentView(R.layout.activity_errors);
		
		receiver = new ReceiveMessages();				
				
		errorsTextViewStatus = (TextView)findViewById(R.id.errorsStatusTextView);
		RealtimeError = (CheckBox)findViewById(R.id.errorsRealtimeErrorsCheckBox);
		ReadingInertion = (CheckBox)findViewById(R.id.errorsInertionCheckBox);
		
		boolean realtime = RealtimeError.isChecked();
		errors = new ArrayList<BaseParamItem>();
		errorsInertness = new int [INERTNESS_COUNT]; 
		String errorNames[] = getResources().getStringArray(R.array.errors_ecu_errors_names);
		String errorBCs[] = getResources().getStringArray(R.array.errors_ecu_errors_blink_codes);
		for (int i = 0; i != Secu3Packet.SECU3_ECU_ERRORS_COUNT; i++) {
			errors.add(new ParamItemBoolean(this, errorNames[i], getString (R.string.errors_code,errorBCs[i]), false));
			errors.get(i).setEnabled(!realtime);
		}
		adapter = new ParamItemsAdapter(errors);		
		
		RealtimeError.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (buttonView == RealtimeError) {
					setRealtime(RealtimeError.isChecked());
				}				
			}
		});
		ReadingInertion.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (buttonView == ReadingInertion) {
					setInertness (ReadingInertion.isChecked());
				}
			}
		});
		RealtimeError.setChecked(false);
		ReadingInertion.setEnabled(false);
		
		ListView l = (ListView)findViewById(R.id.errorsListView);
		l.setAdapter(adapter);
		l.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
				adapter = (ParamItemsAdapter) parent.getAdapter();
				BaseParamItem i = (BaseParamItem) adapter.getItem(position);
				adapter.setValue(String.valueOf(!((ParamItemBoolean) i).getValue()), position);				
			}
			
		});
		
		if (savedInstanceState != null) {
			setErrors(savedInstanceState.getInt(ERRORS));
			RealtimeError.setChecked(savedInstanceState.getBoolean(REALTIME));
			ReadingInertion.setChecked(savedInstanceState.getBoolean(INERTNESS));
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(ERRORS, getErrors());
		outState.putBoolean(REALTIME, RealtimeError.isChecked());
		outState.putBoolean(INERTNESS, ReadingInertion.isChecked());
		super.onSaveInstanceState(outState);
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
			if (CeSavedError != null) {
				((ProtoFieldInteger) CeSavedError.findField(R.string.ce_saved_err_data_title)).setValue(getErrors());
				startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, CeSavedError));				
			}			
			return true;
		case R.id.menu_errors_read:
			if (OpCompNc != null) {
				((ProtoFieldInteger) OpCompNc.findField(R.string.op_comp_nc_operation_title)).setValue (Secu3Packet.OPCODE_CE_SAVE_ERRORS);
				startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, OpCompNc));
			}
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
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON_PARAM, R.string.ce_saved_err_title));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON_PARAM, R.string.op_comp_nc_title));
		registerReceiver(receiver, receiver.intentFilter);
		super.onResume();		
	}
	
	@Override
	protected void onPause() {		
		unregisterReceiver(receiver);
		super.onPause();			
	}
	
	void updateFlags (int flags) {
		for (int i = 0; i != INERTNESS_COUNT-1; i++) {
			errorsInertness[i] = errorsInertness[i+1];
		}
		errorsInertness [INERTNESS_COUNT - 1] = flags;
		
		if (getInertness()) {
			flags = 0;
			for (int i = 0; i != INERTNESS_COUNT; i++) {
				flags |= errorsInertness[i];
			}
		}
		
		for (int i = 0; i != Secu3Packet.SECU3_ECU_ERRORS_COUNT; ++i) {
			((ParamItemBoolean) errors.get(i)).setValue(((flags & 0x01) != 0)?true:false);
			flags >>= 1; 
		}
		adapter.notifyDataSetChanged();
	}
	
	private int getErrors () {
		int res = 0;
		for (int i = 0; i != Secu3Packet.SECU3_ECU_ERRORS_COUNT; ++i) {
			if (((ParamItemBoolean) errors.get(i)).getValue()) res |= 0x01 << i; 
		}
		return res;
	}	
	
	private void setErrors(int errors) {
		for (int i = 0; i != Secu3Packet.SECU3_ECU_ERRORS_COUNT; ++i) {
			((ParamItemBoolean) this.errors.get(i)).setValue(((errors & 0x01)!=0)?true:false);
			errors >>= 1;
		}		
	}
	
	void update (Intent intent) {		
		if (Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE.equals(intent.getAction())) {
			boolean isOnline = intent.getBooleanExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS,false);
			if (isOnline && !this.isOnline) {
				this.isOnline = true;
				setRealtime(RealtimeError.isChecked());
			}			
			String s = isOnline?getString(R.string.status_online):getString(R.string.status_offline);
			errorsTextViewStatus.setText(s);
		} else if (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_SKELETON_PACKET.equals(intent.getAction())) {
			Secu3Packet packet = intent.getParcelableExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_SKELETON_PACKET);
			if (packet != null) {
				if (packet.getNameId() == R.string.ce_saved_err_title) CeSavedError = packet;
				else if (packet.getNameId() == R.string.op_comp_nc_title) OpCompNc = packet;
			}
		} else if (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET.equals(intent.getAction())) {
			Secu3Packet packet = intent.getParcelableExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET);
			if (packet != null) {
				if (packet.getNameId() == R.string.ce_saved_err_title) {
					updateFlags(((ProtoFieldInteger) packet.getField(R.string.ce_saved_err_data_title)).getValue());		
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_READ_SENSORS.ordinal()));
				} else
				if (packet.getNameId() == R.string.ce_err_codes_title) {				
					updateFlags(((ProtoFieldInteger) packet.getField(R.string.ce_err_codes_data_title)).getValue());
				}
				if (packet.getNameId() == R.string.op_comp_nc_title) {
					if (((ProtoFieldInteger) packet.getField(R.string.op_comp_nc_operation_title)).getValue() == Secu3Packet.OPCODE_CE_SAVE_ERRORS) {				
						Toast.makeText(this, String.format(getString(R.string.params_saved_error_code), ((ProtoFieldInteger) packet.getField(R.string.op_comp_nc_operation_code_title)).getValue()), Toast.LENGTH_LONG).show();
					}
				}
			}
		} 
	}
}

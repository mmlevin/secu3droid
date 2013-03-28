package org.secu3.android.fragments;

import java.text.NumberFormat;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.TemperPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class TemperFragment extends Fragment implements ISecu3Fragment{
	TemperPar packet;
	
	EditText fanOn;
	EditText fanOff;
	CheckBox useTempSensor;
	CheckBox usePWM;
	CheckBox useTable;

	private class CustomTextWatcher implements TextWatcher {
		EditText e = null;
		
		public CustomTextWatcher(EditText e) {
			this.e =e;  
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			float f = 0;
			try {
				NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
				Number number = format.parse(s.toString());				
				f = number.floatValue();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (packet != null) {
					switch (e.getId()){
						case R.id.temperFanOnEditText:
							packet.vent_on = f; 
							break;
						case R.id.temperFanOffEditText:
							packet.vent_off = f;
							break;
					}
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {			
		}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) return null;
		
		return inflater.inflate(R.layout.temper_params, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fanOn = (EditText)getView().findViewById(R.id.temperFanOnEditText);
		fanOff = (EditText)getView().findViewById(R.id.temperFanOffEditText);
		useTempSensor = (CheckBox)getView().findViewById(R.id.temperUseTempSensorCheckBox);
		usePWM = (CheckBox)getView().findViewById(R.id.temperUsePWMCheckBox);
		useTable = (CheckBox)getView().findViewById(R.id.temperUseTableCheckBox);	
				
		fanOn.addTextChangedListener(new CustomTextWatcher(fanOn));
		fanOff.addTextChangedListener(new CustomTextWatcher(fanOff));
		
		OnCheckedChangeListener checkedListener = new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (packet != null) {
					if (buttonView == useTempSensor) {	
						packet.tmp_use = isChecked?1:0;						
					} else if (buttonView == usePWM) {	
						packet.vent_pwm = isChecked?1:0;						
					} else if (buttonView == useTable) {
						packet.cts_use_map = isChecked?1:0;
					}
				}
			}
		};
		
		useTempSensor.setOnCheckedChangeListener(checkedListener);
		usePWM.setOnCheckedChangeListener(checkedListener);
		useTable.setOnCheckedChangeListener(checkedListener);
	}
	
	@Override
	public void onResume() {
		updateData();		
		super.onResume();
	}

	@Override
	public void updateData() {
		if (packet != null) {		
			fanOn.setText(String.format("%.2f",((TemperPar)packet).vent_on));
			fanOff.setText(String.format("%.2f",((TemperPar)packet).vent_off));
			useTempSensor.setChecked(((TemperPar)packet).tmp_use != 0);
			usePWM.setChecked(((TemperPar)packet).vent_pwm != 0);
			useTable.setChecked(((TemperPar)packet).cts_use_map != 0);
		}
	}

	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (TemperPar) packet;		
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

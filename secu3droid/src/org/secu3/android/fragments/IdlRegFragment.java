package org.secu3.android.fragments;

import java.text.NumberFormat;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.IdlRegPar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class IdlRegFragment extends Secu3Fragment implements ISecu3Fragment{
	IdlRegPar packet;
	
	EditText ifac1;
	EditText ifac2;
	EditText minimalAngle;
	EditText maximalAngle;
	EditText targetRPM;
	EditText rpmSensitivity;
	EditText turnOnTemp;
	CheckBox useIdleReg;
	
	private class CustomTextWatcher implements TextWatcher {
		EditText e = null;
		
		public CustomTextWatcher(EditText e) {
			this.e =e;  
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			float f = 0;
			int i = 0;
			try {
				NumberFormat format = NumberFormat.getInstance(Locale.US);
				Number number = format.parse(s.toString());				
				f = number.floatValue();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (packet != null) {
					switch (e.getId()){
					case R.id.idlregIfac1EditText:
						packet.ifac1 = f;
						break;
					case R.id.idlregIfac2EditText:
						packet.ifac2 = f;
						break;					
					case R.id.idlregMaximalAngleEditText:
						packet.max_angle = f;
						break;
					case R.id.idlregMinimalAngleEditText:
						packet.min_angle = f;
						break;
					case R.id.idlregTurnOnTempEditText:
						packet.turn_on_temp = f;
						break;
					}
					dataChanged(packet);
				}
			}
			try {
				NumberFormat format = NumberFormat.getInstance(Locale.US);
				Number number = format.parse(s.toString());				
				i = number.intValue();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (packet != null) {
					switch (e.getId()){
					case R.id.idlregTargetRPMEditText:
						packet.idling_rpm = i; 
						break;
					case R.id.idlregRPMSensitivityEditText:
						packet.MINEFR = i;
						break;
					}
					dataChanged(packet);
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
		
		return inflater.inflate(R.layout.idlreg_params, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ifac1 = (EditText)getView().findViewById(R.id.idlregIfac1EditText);
		ifac2 = (EditText)getView().findViewById(R.id.idlregIfac2EditText); 
		minimalAngle = (EditText)getView().findViewById(R.id.idlregMinimalAngleEditText);
		maximalAngle = (EditText)getView().findViewById(R.id.idlregMaximalAngleEditText);
		targetRPM = (EditText)getView().findViewById(R.id.idlregTargetRPMEditText);
		rpmSensitivity = (EditText)getView().findViewById(R.id.idlregRPMSensitivityEditText);
		useIdleReg = (CheckBox)getView().findViewById(R.id.idlregUseIdleRegulatorCheckBox);
		turnOnTemp = (EditText)getView().findViewById(R.id.idlregTurnOnTempEditText);
		
		ifac1.addTextChangedListener(new CustomTextWatcher(ifac1));
		ifac2.addTextChangedListener(new CustomTextWatcher(ifac2));
		minimalAngle.addTextChangedListener(new CustomTextWatcher(minimalAngle));
		maximalAngle.addTextChangedListener(new CustomTextWatcher(maximalAngle));
		targetRPM.addTextChangedListener(new CustomTextWatcher(targetRPM));
		rpmSensitivity.addTextChangedListener(new CustomTextWatcher(rpmSensitivity));
		turnOnTemp.addTextChangedListener(new CustomTextWatcher(turnOnTemp));
		
		useIdleReg.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (packet != null) {
					packet.idl_regul = isChecked?1:0;
					dataChanged(packet);
				}
			}
		});
	}

	@Override
	public void onResume() {
		updateData();		
		super.onResume();		
	}

	@Override
	public void updateData() {
		if (packet != null) {		
			ifac1.setText(String.format(Locale.US,"%.2f",((IdlRegPar)packet).ifac1));
			ifac2.setText(String.format(Locale.US,"%.2f",((IdlRegPar)packet).ifac2));
			minimalAngle.setText(String.format(Locale.US,"%.2f",((IdlRegPar)packet).min_angle));
			maximalAngle.setText(String.format(Locale.US,"%.2f",((IdlRegPar)packet).max_angle));
			targetRPM.setText(String.format(Locale.US,"%d",((IdlRegPar)packet).idling_rpm));
			rpmSensitivity.setText(String.format(Locale.US,"%d",((IdlRegPar)packet).MINEFR));
			turnOnTemp.setText(String.format(Locale.US, "%.2f",((IdlRegPar)packet).turn_on_temp));
			useIdleReg.setChecked(((IdlRegPar)packet).idl_regul != 0);
		}
	}
	
	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (IdlRegPar) packet;
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

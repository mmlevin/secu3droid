package org.secu3.android.fragments;

import java.text.NumberFormat;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.CarburPar;

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

public class CarburFragment extends Fragment implements ISecu3Fragment {
	CarburPar packet;
	
	EditText carburEPHHLowThreshholdGasoline;
	EditText carburEPHHHighThreshholdGasoline;
	EditText carburEPHHLowThreshholdGas;
	EditText carburEPHHHighThreshholdGas;
	EditText carburOverrunDelay;
	CheckBox carburSensorInverse;
	EditText carburEPMValveOnPressure;
	EditText carburTpsThreshold;

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
					case R.id.carburOverrunValveDelayEditText:
						packet.shutoff_delay = f;
						break;
					case R.id.carburEPMValveOnPressureEditText:
						packet.epm_ont = f;
						break;	
					case R.id.carburTPSThresholdEditText:
						packet.tps_threshold = f;
						break;
					}
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
					case R.id.carburOverrunLowThreshholdGasolineEditText:
						packet.ephh_lot = i; 
						break;
					case R.id.carburOverrunHighThreshholdGasolineEditText:
						packet.ephh_hit = i;
						break;
					case R.id.carburOverrunLowThreshholdGasEditText:
						packet.ephh_lot_g = i; 
						break;
					case R.id.carburOverrunHighThreshholdGasEditText:
						packet.ephh_hit_g = i;
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
		
		return inflater.inflate(R.layout.carbur_params, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		carburEPHHLowThreshholdGasoline = (EditText)getView().findViewById(R.id.carburOverrunLowThreshholdGasolineEditText);
		carburEPHHHighThreshholdGasoline = (EditText)getView().findViewById(R.id.carburOverrunHighThreshholdGasolineEditText);
		carburEPHHLowThreshholdGas = (EditText)getView().findViewById(R.id.carburOverrunLowThreshholdGasEditText);
		carburEPHHHighThreshholdGas = (EditText)getView().findViewById(R.id.carburOverrunHighThreshholdGasEditText);
		carburOverrunDelay = (EditText)getView().findViewById(R.id.carburOverrunValveDelayEditText);
		carburEPMValveOnPressure = (EditText)getView().findViewById(R.id.carburEPMValveOnPressureEditText);
		carburSensorInverse = (CheckBox)getView().findViewById(R.id.carburSensorInverseCheckBox);
		carburTpsThreshold =  (EditText)getView().findViewById(R.id.carburTPSThresholdEditText);
		
		carburEPHHLowThreshholdGasoline.addTextChangedListener(new CustomTextWatcher(carburEPHHLowThreshholdGasoline));
		carburEPHHHighThreshholdGasoline.addTextChangedListener(new CustomTextWatcher(carburEPHHHighThreshholdGasoline));
		carburEPHHLowThreshholdGas.addTextChangedListener(new CustomTextWatcher(carburEPHHLowThreshholdGas));
		carburEPHHHighThreshholdGas.addTextChangedListener(new CustomTextWatcher(carburEPHHHighThreshholdGas));
		carburOverrunDelay.addTextChangedListener(new CustomTextWatcher(carburOverrunDelay));
		carburTpsThreshold.addTextChangedListener(new CustomTextWatcher(carburTpsThreshold));
		carburEPMValveOnPressure.addTextChangedListener(new CustomTextWatcher(carburEPMValveOnPressure));
		
		carburSensorInverse.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (packet != null) packet.carb_invers = isChecked?1:0;				
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
			carburEPHHLowThreshholdGasoline.setText(String.format(Locale.US,"%d",((CarburPar)packet).ephh_lot));
			carburEPHHHighThreshholdGasoline.setText(String.format(Locale.US,"%d",((CarburPar)packet).ephh_hit));
			carburEPHHLowThreshholdGas.setText(String.format(Locale.US,"%d",((CarburPar)packet).ephh_lot_g));
			carburEPHHHighThreshholdGas.setText(String.format(Locale.US,"%d",((CarburPar)packet).ephh_hit_g));
			carburOverrunDelay.setText(String.format(Locale.US,"%.2f",((CarburPar)packet).shutoff_delay));
			carburEPMValveOnPressure.setText(String.format(Locale.US,"%.2f",((CarburPar)packet).epm_ont));
			carburTpsThreshold.setText(String.format(Locale.US,"%.1f",((CarburPar)packet).tps_threshold));
			carburSensorInverse.setChecked(((CarburPar)packet).carb_invers != 0);
		}
	}

	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (CarburPar) packet;		
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

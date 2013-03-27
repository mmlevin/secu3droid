package org.secu3.android.fragments;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.ADCCorPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ADCCorFragment extends Fragment implements ISecu3Fragment {
	ADCCorPar packet;
	
	EditText adccorMapSensorCoefficient;
	EditText adccorMapSensorCorrection;
	EditText adccorVoltageSensorCoefficient;
	EditText adccorVoltageSensorCorrection;
	EditText adccorTemperatureSensorCoefficient;
	EditText adccorTemperatureSensorCorrection;

	private class CustomTextWatcher implements TextWatcher {
		EditText e = null;
		
		public CustomTextWatcher(EditText e) {
			this.e =e;  
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			float f = 0;
			try {
				f = Float.valueOf(s.toString());
			} catch (NumberFormatException e) {				
			} finally {
				if (packet != null) {
					switch (e.getId()){
						case R.id.adccorMAPSensorCoefficientEditText:
							packet.map_adc_factor = f; 
							break;
						case R.id.adccorMAPSensorCorrectionEditText:
							packet.map_adc_correction = f;
							break;
						case R.id.adccorTemperatureSensorCoefficientEditText:
							packet.temp_adc_factor = f;
							break;
						case R.id.adccorTemperatureSensorCorrectionEditText:
							packet.temp_adc_correction = f;
							break;
						case R.id.adccorVoltageSensorCoefficientEditText:
							packet.ubat_adc_factor = f;
							break;
						case R.id.adccorVoltageSensorCorrectionEditText:
							packet.ubat_adc_correction = f;
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
		
		return inflater.inflate(R.layout.adccor_params, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adccorMapSensorCoefficient = (EditText)getView().findViewById(R.id.adccorMAPSensorCoefficientEditText);
		adccorMapSensorCorrection = (EditText)getView().findViewById(R.id.adccorMAPSensorCorrectionEditText);
		adccorVoltageSensorCoefficient = (EditText)getView().findViewById(R.id.adccorVoltageSensorCoefficientEditText);
		adccorVoltageSensorCorrection = (EditText)getView().findViewById(R.id.adccorVoltageSensorCorrectionEditText);
		adccorTemperatureSensorCoefficient = (EditText)getView().findViewById(R.id.adccorTemperatureSensorCoefficientEditText);
		adccorTemperatureSensorCorrection = (EditText)getView().findViewById(R.id.adccorTemperatureSensorCorrectionEditText);		
		
		adccorMapSensorCoefficient.addTextChangedListener(new CustomTextWatcher(adccorMapSensorCoefficient));
		adccorMapSensorCorrection.addTextChangedListener(new CustomTextWatcher(adccorMapSensorCorrection));
		adccorVoltageSensorCoefficient.addTextChangedListener(new CustomTextWatcher(adccorVoltageSensorCoefficient));
		adccorVoltageSensorCorrection.addTextChangedListener(new CustomTextWatcher(adccorVoltageSensorCorrection));
		adccorTemperatureSensorCoefficient.addTextChangedListener(new CustomTextWatcher(adccorTemperatureSensorCoefficient));
		adccorTemperatureSensorCorrection.addTextChangedListener(new CustomTextWatcher(adccorTemperatureSensorCorrection));
	}
	
	@Override
	public void onResume() {
		updateData();		
		super.onResume();
	}

	@Override
	public void updateData() {
		if (packet != null) {			
			adccorMapSensorCoefficient.setText(String.format("%.2f",((ADCCorPar)packet).map_adc_factor));
			adccorMapSensorCorrection.setText(String.format("%.2f",((ADCCorPar)packet).map_adc_correction));
			adccorVoltageSensorCoefficient.setText(String.format("%.2f",((ADCCorPar)packet).ubat_adc_factor));
			adccorVoltageSensorCorrection.setText(String.format("%.2f",((ADCCorPar)packet).ubat_adc_correction));
			adccorTemperatureSensorCoefficient.setText(String.format("%.2f",((ADCCorPar)packet).temp_adc_factor));
			adccorTemperatureSensorCorrection.setText(String.format("%.2f",((ADCCorPar)packet).temp_adc_correction));
		}
	}
	
	@Override	
	public void setData (Secu3Dat packet) {
		this.packet = (ADCCorPar) packet;		
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

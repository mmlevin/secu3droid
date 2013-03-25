package org.secu3.android.fragments;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.ADCCorPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	}
	
	@Override	
	public void setData (Secu3Dat packet) {
		this.packet = (ADCCorPar) packet;
		
		if (packet != null && isAdded()) {			
			adccorMapSensorCoefficient.setText(String.valueOf(((ADCCorPar)packet).map_adc_factor));
			adccorMapSensorCorrection.setText(String.valueOf(((ADCCorPar)packet).map_adc_correction));
			adccorVoltageSensorCoefficient.setText(String.valueOf(((ADCCorPar)packet).ubat_adc_factor));
			adccorVoltageSensorCorrection.setText(String.valueOf(((ADCCorPar)packet).ubat_adc_correction));
			adccorTemperatureSensorCoefficient.setText(String.valueOf(((ADCCorPar)packet).temp_adc_factor));
			adccorTemperatureSensorCorrection.setText(String.valueOf(((ADCCorPar)packet).temp_adc_correction));
		}
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

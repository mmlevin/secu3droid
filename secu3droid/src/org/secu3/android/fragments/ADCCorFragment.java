package org.secu3.android.fragments;

import java.text.NumberFormat;
import java.util.Locale;

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
	EditText adccorTpsSensorCoefficient;
	EditText adccorTpsSensorCorrection;
	EditText adccorADDIO1SensorCoefficient;
	EditText adccorADDIO1SensorCorrection;
	EditText adccorADDIO2SensorCoefficient;
	EditText adccorADDIO2SensorCorrection;

	private class CustomTextWatcher implements TextWatcher {
		EditText e = null;
		
		public CustomTextWatcher(EditText e) {
			this.e =e;  
		}
		
		@Override
		public void afterTextChanged(Editable s) {				
			float f = 0;
			try {
				NumberFormat format = NumberFormat.getInstance(Locale.US);
				Number number = format.parse(s.toString());
				f = number.floatValue();
			} catch (Exception e) {				
				e.printStackTrace();
			}
			finally {
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
						case R.id.adccorTPSSensorCoefficientEditText:
							packet.tps_adc_factor = f;
							break;
						case R.id.adccorTPSSensorCorrectionEditText:
							packet.tps_adc_correction = f;
							break;
						case R.id.adccorADDIO1SensorCoefficientEditText:
							packet.add_i1_factor = f;
							break;
						case R.id.adccorADDIO1SensorCorrectionEditText:
							packet.add_i1_correction = f;
							break;
						case R.id.adccorADDIO2SensorCoefficientEditText:
							packet.add_i2_factor = f;
							break;
						case R.id.adccorADDIO2SensorCorrectionEditText:
							packet.add_i2_correction = f;
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
		adccorTpsSensorCoefficient = (EditText)getView().findViewById(R.id.adccorTPSSensorCoefficientEditText);
		adccorTpsSensorCorrection = (EditText)getView().findViewById(R.id.adccorTPSSensorCorrectionEditText);
		adccorADDIO1SensorCoefficient = (EditText)getView().findViewById(R.id.adccorADDIO1SensorCoefficientEditText);
		adccorADDIO1SensorCorrection = (EditText)getView().findViewById(R.id.adccorADDIO1SensorCorrectionEditText);
		adccorADDIO2SensorCoefficient = (EditText)getView().findViewById(R.id.adccorADDIO2SensorCoefficientEditText);
		adccorADDIO2SensorCorrection = (EditText)getView().findViewById(R.id.adccorADDIO2SensorCorrectionEditText);
		
		adccorMapSensorCoefficient.addTextChangedListener(new CustomTextWatcher(adccorMapSensorCoefficient));
		adccorMapSensorCorrection.addTextChangedListener(new CustomTextWatcher(adccorMapSensorCorrection));
		adccorVoltageSensorCoefficient.addTextChangedListener(new CustomTextWatcher(adccorVoltageSensorCoefficient));
		adccorVoltageSensorCorrection.addTextChangedListener(new CustomTextWatcher(adccorVoltageSensorCorrection));
		adccorTemperatureSensorCoefficient.addTextChangedListener(new CustomTextWatcher(adccorTemperatureSensorCoefficient));
		adccorTemperatureSensorCorrection.addTextChangedListener(new CustomTextWatcher(adccorTemperatureSensorCorrection));
		adccorTpsSensorCoefficient.addTextChangedListener(new CustomTextWatcher(adccorTpsSensorCoefficient));
		adccorTpsSensorCorrection.addTextChangedListener(new CustomTextWatcher(adccorTpsSensorCorrection));
		adccorADDIO1SensorCoefficient.addTextChangedListener(new CustomTextWatcher(adccorADDIO1SensorCoefficient));
		adccorADDIO1SensorCorrection.addTextChangedListener(new CustomTextWatcher(adccorADDIO1SensorCorrection));
		adccorADDIO2SensorCoefficient.addTextChangedListener(new CustomTextWatcher(adccorADDIO2SensorCoefficient));
		adccorADDIO2SensorCorrection.addTextChangedListener(new CustomTextWatcher(adccorADDIO2SensorCorrection));
	}
	
	@Override
	public void onResume() {
		updateData();		
		super.onResume();
	}

	@Override
	public void updateData() {
		if (packet != null) {			
			adccorMapSensorCoefficient.setText(String.format(Locale.US,"%.3f",((ADCCorPar)packet).map_adc_factor));
			adccorMapSensorCorrection.setText(String.format(Locale.US,"%.4f",((ADCCorPar)packet).map_adc_correction));
			adccorVoltageSensorCoefficient.setText(String.format(Locale.US,"%.3f",((ADCCorPar)packet).ubat_adc_factor));
			adccorVoltageSensorCorrection.setText(String.format(Locale.US,"%.4f",((ADCCorPar)packet).ubat_adc_correction));
			adccorTemperatureSensorCoefficient.setText(String.format(Locale.US,"%.3f",((ADCCorPar)packet).temp_adc_factor));
			adccorTemperatureSensorCorrection.setText(String.format(Locale.US,"%.4f",((ADCCorPar)packet).temp_adc_correction));
			adccorTpsSensorCoefficient.setText(String.format(Locale.US,"%.3f",((ADCCorPar)packet).tps_adc_factor));
			adccorTpsSensorCorrection.setText(String.format(Locale.US,"%.4f",((ADCCorPar)packet).tps_adc_correction));
			adccorADDIO1SensorCoefficient.setText(String.format(Locale.US,"%.3f",((ADCCorPar)packet).add_i1_factor));
			adccorADDIO1SensorCorrection.setText(String.format(Locale.US,"%.4f",((ADCCorPar)packet).add_i1_correction));
			adccorADDIO2SensorCoefficient.setText(String.format(Locale.US,"%.3f",((ADCCorPar)packet).add_i2_factor));
			adccorADDIO2SensorCorrection.setText(String.format(Locale.US,"%.4f",((ADCCorPar)packet).add_i2_correction));
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

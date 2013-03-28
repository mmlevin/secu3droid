package org.secu3.android.fragments;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.FnNameDat;
import org.secu3.android.api.io.Secu3Dat.FunSetPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class FunsetFragment extends Fragment implements ISecu3Fragment{
	FunSetPar packet;
	FnNameDat extraPacket;
	
	EditText lowerPressure;
	EditText upperPressure;
	EditText sensorOffset;
	EditText sensorGradient;
	Spinner gasolineTable;
	Spinner gasTable;
	String tableNames[] = null;

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
			} finally {
				if (packet != null) {
					switch (e.getId()){
						case R.id.funsetLowerPressureEditText:
							packet.map_lower_pressure = f; 
							break;
						case R.id.funsetUpperPressureEditText:
							packet.map_upper_pressure = f;
							break;
						case R.id.funsetMAPSensorOffsetEditText:
							packet.map_curve_offset = f;
							break;
						case R.id.funsetMAPSensorGradientEditText:
							packet.map_curve_gradient = f;
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
			
		return inflater.inflate(R.layout.funset_params, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lowerPressure = (EditText)getView().findViewById(R.id.funsetLowerPressureEditText);
		upperPressure = (EditText)getView().findViewById(R.id.funsetUpperPressureEditText);
		sensorOffset = (EditText)getView().findViewById(R.id.funsetMAPSensorOffsetEditText);
		sensorGradient = (EditText)getView().findViewById(R.id.funsetMAPSensorGradientEditText);
		
		gasolineTable = (Spinner)getView().findViewById(R.id.funsetGasolineTableSpinner);
		gasTable  = (Spinner)getView().findViewById(R.id.funsetGasTableSpinner);
		gasolineTable = (Spinner)getView().findViewById(R.id.funsetGasolineTableSpinner);
		gasTable = (Spinner)getView().findViewById(R.id.funsetGasTableSpinner);		
		
		lowerPressure.addTextChangedListener(new CustomTextWatcher(lowerPressure));
		upperPressure.addTextChangedListener(new CustomTextWatcher(upperPressure));
		sensorOffset.addTextChangedListener(new CustomTextWatcher(sensorOffset));
		sensorGradient.addTextChangedListener(new CustomTextWatcher(sensorGradient));
		
		OnItemSelectedListener spinerListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				if ((packet != null) && (v != null)) {
					if (v.getParent() == gasolineTable) {
						packet.fn_benzin = position;
					} else if (v.getParent() == gasTable) {
						packet.fn_gas = position;
					}					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {				
			}
		};
		gasolineTable.setOnItemSelectedListener(spinerListener);
		gasTable.setOnItemSelectedListener(spinerListener);
	}
	
	@Override
	public void onResume() {
		updateData();		
		super.onResume();
	}

	@Override
	public void updateData() {
		if (packet != null) {				
			lowerPressure.setText (String.format(Locale.US,"%.2f",packet.map_lower_pressure));
			upperPressure.setText (String.format(Locale.US,"%.2f",packet.map_upper_pressure));
			sensorOffset.setText (String.format(Locale.US,"%.3f",packet.map_curve_offset));
			sensorGradient.setText (String.format(Locale.US,"%.3f",packet.map_curve_gradient));
			gasolineTable.setSelection(packet.fn_benzin);
			gasTable.setSelection(packet.fn_gas);
		}
		if ((extraPacket != null) && (extraPacket.names_available())) {					
			tableNames = Arrays.copyOf(extraPacket.names,extraPacket.names.length);
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item,tableNames);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			gasolineTable.setAdapter(adapter);
			
			adapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item,tableNames);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			gasTable.setAdapter(adapter);
			
			if (packet != null) {
				gasolineTable.setSelection(packet.fn_benzin);
				gasTable.setSelection(packet.fn_gas);				
			}
		}
	}

	@Override
	public void setData(Secu3Dat packet) {
		if (packet instanceof FunSetPar) {
			this.packet = (FunSetPar) packet;
		}
		if (packet instanceof FnNameDat) {
			this.extraPacket = (FnNameDat) packet;
		}
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
	
	public Secu3Dat getExtraData() {
		return extraPacket;
	}
}

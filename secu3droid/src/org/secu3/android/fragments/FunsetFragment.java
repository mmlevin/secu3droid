package org.secu3.android.fragments;

import java.util.Arrays;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.FnNameDat;
import org.secu3.android.api.io.Secu3Dat.FunSetPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class FunsetFragment extends Fragment implements ISecu3Fragment{
	FunSetPar packet;
	
	EditText lowerPressure;
	EditText upperPressure;
	EditText sensorOffset;
	EditText sensorGradient;
	Spinner gasolineTable;
	Spinner gasTable;
	String tableNames[] = null;
	
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
	}

	@Override
	public void setData(Secu3Dat packet) {
		if (packet instanceof FunSetPar) {
			this.packet = (FunSetPar) packet;
		}
		if (packet != null && isAdded()) {
			if (packet instanceof FunSetPar) {				
				lowerPressure.setText (String.valueOf(((FunSetPar)packet).map_lower_pressure));
				upperPressure.setText (String.valueOf(((FunSetPar)packet).map_upper_pressure));
				sensorOffset.setText (String.valueOf(((FunSetPar)packet).map_curve_offset));
				sensorGradient.setText (String.valueOf(((FunSetPar)packet).map_curve_gradient));
				gasolineTable.setSelection(((FunSetPar)packet).fn_benzin);
				gasTable.setSelection(((FunSetPar)packet).fn_gas);
			}
			if (packet instanceof FnNameDat) {
				gasolineTable = (Spinner)getView().findViewById(R.id.funsetGasolineTableSpinner);
				gasTable = (Spinner)getView().findViewById(R.id.funsetGasTableSpinner);
				
				if (((FnNameDat)packet).names_available()) {					
					tableNames = Arrays.copyOf(((FnNameDat)packet).names,((FnNameDat)packet).names.length);
					
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item,tableNames);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					gasolineTable.setAdapter(adapter);
					
					adapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item,tableNames);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					gasTable.setAdapter(adapter);
				}
			}
		}		
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

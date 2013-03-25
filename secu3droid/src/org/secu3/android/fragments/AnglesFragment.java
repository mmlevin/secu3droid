package org.secu3.android.fragments;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.AnglesPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

public class AnglesFragment extends Fragment implements ISecu3Fragment{
	AnglesPar packet;
	
	EditText minimalAngle;
	EditText maximalAngle;
	EditText angleDecrementStep;
	EditText angleIncrementStep;
	CheckBox zeroAngle;
	EditText currentAngle;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) return null;
		
		return inflater.inflate(R.layout.angles_params, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		minimalAngle = (EditText)getView().findViewById(R.id.anglesMinimalAngleEditText);
		maximalAngle = (EditText)getView().findViewById(R.id.anglesMaximalAngleEditText);
		angleDecrementStep = (EditText)getView().findViewById(R.id.anglesDecrementStepEditText);
		angleIncrementStep = (EditText)getView().findViewById(R.id.anglesIncrementStepEditText);
		zeroAngle = (CheckBox)getView().findViewById(R.id.anglesZeroAngleCheckBox);
		currentAngle = (EditText)getView().findViewById(R.id.anglesCurrentAngleEditText);		
	}

	@Override
	public void onResume() {
		super.onResume();
		updateData();		
	}

	@Override
	public void updateData() {
		if (packet != null) {			
			minimalAngle.setText(String.valueOf(((AnglesPar)packet).min_angle));
			maximalAngle.setText(String.valueOf(((AnglesPar)packet).max_angle));
			angleDecrementStep.setText(String.valueOf(((AnglesPar)packet).dec_spead));
			angleIncrementStep.setText(String.valueOf(((AnglesPar)packet).inc_spead));
			zeroAngle.setChecked(((AnglesPar)packet).zero_adv_ang != 0);
			currentAngle.setText(String.valueOf(((AnglesPar)packet).angle_corr));
		}
	}
	
	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (AnglesPar) packet;			
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

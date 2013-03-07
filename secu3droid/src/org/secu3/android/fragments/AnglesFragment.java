package org.secu3.android.fragments;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.AnglesPar;
import org.secu3.android.api.io.Secu3Dat.StartrPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

public class AnglesFragment extends Fragment implements ISecu3Fragment{
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
	public void setData(Secu3Dat packet) {
		if (packet != null && isAdded()) {
			minimalAngle = (EditText)getView().findViewById(R.id.anglesMinimalAngleEditText);
			maximalAngle = (EditText)getView().findViewById(R.id.anglesMaximalAngleEditText);
			angleDecrementStep = (EditText)getView().findViewById(R.id.anglesDecrementStepEditText);
			angleIncrementStep = (EditText)getView().findViewById(R.id.anglesIncrementStepEditText);
			zeroAngle = (CheckBox)getView().findViewById(R.id.anglesZeroAngleCheckBox);
			currentAngle = (EditText)getView().findViewById(R.id.anglesCurrentAngleEditText);
			
			minimalAngle.setText(String.valueOf(((AnglesPar)packet).min_angle));
			maximalAngle.setText(String.valueOf(((AnglesPar)packet).max_angle));
			angleDecrementStep.setText(String.valueOf(((AnglesPar)packet).angle_corr));
			angleIncrementStep.setText(String.valueOf(((AnglesPar)packet).inc_spead));
			zeroAngle.setChecked(((AnglesPar)packet).zero_adv_ang != 0);
			currentAngle.setText(String.valueOf(((AnglesPar)packet).angle_corr));
		}		
	}
}

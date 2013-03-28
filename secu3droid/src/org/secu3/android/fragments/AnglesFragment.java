package org.secu3.android.fragments;

import java.text.NumberFormat;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.AnglesPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class AnglesFragment extends Fragment implements ISecu3Fragment{
	AnglesPar packet;
	
	EditText minimalAngle;
	EditText maximalAngle;
	EditText angleDecrementStep;
	EditText angleIncrementStep;
	CheckBox zeroAngle;
	EditText currentAngle;
	
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
						case R.id.anglesMinimalAngleEditText:
							packet.min_angle = f; 
							break;
						case R.id.anglesMaximalAngleEditText:
							packet.max_angle = f;
							break;
						case R.id.anglesDecrementStepEditText:
							packet.dec_spead = f;
							break;
						case R.id.anglesIncrementStepEditText:
							packet.inc_spead = f;
							break;
						case R.id.anglesCorrectionAngleEditText:
							packet.angle_corr = f;
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
		
		return inflater.inflate(R.layout.angles_params, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		minimalAngle = (EditText)getView().findViewById(R.id.anglesMinimalAngleEditText);
		maximalAngle = (EditText)getView().findViewById(R.id.anglesMaximalAngleEditText);
		angleDecrementStep = (EditText)getView().findViewById(R.id.anglesDecrementStepEditText);
		angleIncrementStep = (EditText)getView().findViewById(R.id.anglesIncrementStepEditText);
		zeroAngle = (CheckBox)getView().findViewById(R.id.showRawDataCheckBox);
		currentAngle = (EditText)getView().findViewById(R.id.anglesCorrectionAngleEditText);		
		
		minimalAngle.addTextChangedListener(new CustomTextWatcher(minimalAngle));
		maximalAngle.addTextChangedListener(new CustomTextWatcher(maximalAngle));
		angleDecrementStep.addTextChangedListener(new CustomTextWatcher(angleDecrementStep));
		angleIncrementStep.addTextChangedListener(new CustomTextWatcher(angleIncrementStep));
		currentAngle.addTextChangedListener(new CustomTextWatcher(currentAngle));
		
		zeroAngle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (packet != null) packet.zero_adv_ang = isChecked?1:0;			
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		updateData();		
	}

	@Override
	public void updateData() {
		if (packet != null) {			
			minimalAngle.setText(String.format(Locale.US,"%.2f",((AnglesPar)packet).min_angle));
			maximalAngle.setText(String.format(Locale.US,"%.2f",((AnglesPar)packet).max_angle));
			angleDecrementStep.setText(String.format(Locale.US,"%.2f",((AnglesPar)packet).dec_spead));
			angleIncrementStep.setText(String.format(Locale.US,"%.2f",((AnglesPar)packet).inc_spead));
			zeroAngle.setChecked(((AnglesPar)packet).zero_adv_ang != 0);
			currentAngle.setText(String.format(Locale.US,"%.2f",((AnglesPar)packet).angle_corr));
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

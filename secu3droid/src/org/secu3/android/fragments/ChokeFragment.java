package org.secu3.android.fragments;

import java.text.NumberFormat;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.ChokePar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ChokeFragment extends Fragment implements ISecu3Fragment {
	
	ChokePar packet = null;
	EditText chokeSteps;
	
	private class CustomTextWatcher implements TextWatcher {
		EditText e = null;
		
		public CustomTextWatcher(EditText e) {
			this.e =e;  
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			int i = 0;
			try {
				NumberFormat format = NumberFormat.getInstance(Locale.US);
				Number number = format.parse(s.toString());				
				i = number.intValue();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (packet != null) {
					switch (e.getId()){
						case R.id.chokeStepsEditText:
							packet.steps = i;
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
		return inflater.inflate(R.layout.starter_params, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		chokeSteps = (EditText)getView().findViewById(R.id.chokeStepsEditText);
		chokeSteps.addTextChangedListener(new CustomTextWatcher(chokeSteps));
	}
	
	@Override
	public void onResume() {
		updateData();
		super.onStart();
	}
	
	@Override
	public void updateData() {
		if (packet != null) {
			chokeSteps.setText(String.format(Locale.US,"%d",((ChokePar)packet).steps));
		}
	}
	
	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (ChokePar)packet;				
	}
	
	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

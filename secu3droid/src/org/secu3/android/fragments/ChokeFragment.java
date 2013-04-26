package org.secu3.android.fragments;

import java.text.NumberFormat;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.ChokePar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

public class ChokeFragment extends Secu3Fragment implements ISecu3Fragment {
	
	ChokePar packet = null;
	EditText chokeSteps;
	ToggleButton chokeTest;
	
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
					dataChanged(packet);
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
		return inflater.inflate(R.layout.choke_params, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		chokeSteps = (EditText)getView().findViewById(R.id.chokeStepsEditText);
		chokeSteps.addTextChangedListener(new CustomTextWatcher(chokeSteps));
		
		chokeTest = (ToggleButton)getView().findViewById(R.id.chokeTestButton);	
		chokeTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (packet != null) {
					packet.testing = isChecked?1:0;
					dataChanged(packet);						
				}		
			}
		});
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

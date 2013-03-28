package org.secu3.android.fragments;

import java.text.NumberFormat;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.MiscelPar;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

public class MiscelFragment extends Fragment implements ISecu3Fragment {
	MiscelPar packet;
	
	Spinner baudrate;
	EditText period;
	CheckBox enableIgnitionCutoff;
	EditText ignitionCutoffRPM;
	EditText hallOutputStart;
	EditText hallOutputDelay;
	
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
						case R.id.miscelHallOutputStartEditText:						
							packet.hop_start_cogs = i;
							break;
						case R.id.miscelHallOutputDelayEditText:													
							packet.hop_durat_cogs = i;
							break;
						case R.id.miscelIgnitionCutoffRPMEditText:
							packet.ign_cutoff_thrd = i;
							break;
						case R.id.miscelPeriodEditText:
							packet.period_ms = i;
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
		
		return inflater.inflate(R.layout.miscel_params, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		period = (EditText)getView().findViewById(R.id.miscelPeriodEditText);
		enableIgnitionCutoff = (CheckBox)getView().findViewById(R.id.miscelEnableIgnitionCutoffCheckBox);
		ignitionCutoffRPM = (EditText)getView().findViewById(R.id.miscelIgnitionCutoffRPMEditText);
		hallOutputStart = (EditText)getView().findViewById(R.id.miscelHallOutputStartEditText);
		hallOutputDelay = (EditText)getView().findViewById(R.id.miscelHallOutputDelayEditText);
		baudrate = (Spinner)getView().findViewById(R.id.miscelBaudrateSpinner);
		
		period.addTextChangedListener(new CustomTextWatcher(period));
		ignitionCutoffRPM.addTextChangedListener(new CustomTextWatcher(ignitionCutoffRPM));
		hallOutputStart.addTextChangedListener(new CustomTextWatcher(hallOutputStart));
		hallOutputDelay.addTextChangedListener(new CustomTextWatcher(hallOutputDelay));
		
		baudrate.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				if (packet != null) {
					packet.baud_rate = Secu3Dat.BAUD_RATE[position];
					packet.baud_rate_index = Secu3Dat.BAUD_RATE_INDEX[position];
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {								
			}			
		});
		
		enableIgnitionCutoff.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (packet != null) packet.ign_cutoff = isChecked?1:0;				
			}
		});
	}
	
	@Override
	public void onResume() {		
		Integer[] arr = new Integer[Secu3Dat.BAUD_RATE.length];		
		int j = 0;
		for (int i : Secu3Dat.BAUD_RATE) {
			arr[j++] = i;
		}
		
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item,arr);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
		baudrate.setAdapter(adapter);

		updateData();		
		super.onResume();
	}

	@Override
	public void updateData() {
		if (packet != null) {			
			baudrate.setSelection(Secu3Dat.indexOf (Secu3Dat.BAUD_RATE_INDEX,((MiscelPar)packet).baud_rate_index));			
			period.setText(String.format(Locale.US,"%d",((MiscelPar)packet).period_ms));
			enableIgnitionCutoff.setChecked(((MiscelPar)packet).ign_cutoff != 0);
			ignitionCutoffRPM.setText(String.format(Locale.US,"%d",((MiscelPar)packet).ign_cutoff_thrd));
			hallOutputStart.setText(String.format(Locale.US,"%d",((MiscelPar)packet).hop_start_cogs));
			hallOutputDelay.setText(String.format(Locale.US,"%d",((MiscelPar)packet).hop_durat_cogs));
		}
	}

	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (MiscelPar) packet; 				
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

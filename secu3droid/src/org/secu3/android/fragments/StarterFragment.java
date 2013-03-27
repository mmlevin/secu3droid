package org.secu3.android.fragments;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.StartrPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class StarterFragment extends Fragment implements ISecu3Fragment {
	
	StartrPar packet = null;
	EditText starterRPM;
	EditText starterMap;
	
	private class CustomTextWatcher implements TextWatcher {
		EditText e = null;
		
		public CustomTextWatcher(EditText e) {
			this.e =e;  
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			int i = 0;
			try {
				i = Integer.valueOf(s.toString());
			} catch (NumberFormatException e) {				
			} finally {
				if (packet != null) {
					switch (e.getId()){
						case R.id.starterOffEditText:
							packet.starter_off = i;
							break;
						case R.id.starterMapAbandonEditText:
							packet.smap_abandon = i;
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
		starterRPM = (EditText)getView().findViewById(R.id.starterOffEditText);
		starterMap = (EditText)getView().findViewById(R.id.starterMapAbandonEditText);	
		starterMap.addTextChangedListener(new CustomTextWatcher(starterMap));
		starterRPM.addTextChangedListener(new CustomTextWatcher(starterRPM));
	}
	
	@Override
	public void onResume() {
		updateData();
		super.onStart();
	}
	
	@Override
	public void updateData() {
		if (packet != null) {
			starterRPM.setText(String.format("%d",((StartrPar)packet).starter_off));
			starterMap.setText(String.format("%d",((StartrPar)packet).smap_abandon));
		}
	}
	
	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (StartrPar)packet;				
	}
	
	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

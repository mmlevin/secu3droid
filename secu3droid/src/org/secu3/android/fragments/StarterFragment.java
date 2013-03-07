package org.secu3.android.fragments;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.StartrPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class StarterFragment extends Fragment implements ISecu3Fragment {
	EditText starterRPM;
	EditText starterMap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) return null;
		return inflater.inflate(R.layout.starter_params, null);
	}

	@Override
	public void setData(Secu3Dat packet) {
		if (packet != null && isAdded()) {
			starterRPM = (EditText)getView().findViewById(R.id.starterOffEditText);
			starterMap = (EditText)getView().findViewById(R.id.starterMapAbandonEditText);
			
			starterRPM.setText(String.valueOf(((StartrPar)packet).starter_off));
			starterMap.setText(String.valueOf(((StartrPar)packet).smap_abandon));
		}
	}
}

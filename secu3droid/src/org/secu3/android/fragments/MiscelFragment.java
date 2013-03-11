package org.secu3.android.fragments;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.MiscelPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class MiscelFragment extends Fragment implements ISecu3Fragment {
	Spinner baudrate;
	EditText period;
	CheckBox enableIgnitionCutoff;
	EditText ignitionCutoffRPM;
	EditText hallOutputStart;
	EditText hallOutputDelay;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) return null;
		
		return inflater.inflate(R.layout.miscel_params, null);
	}

	@Override
	public void setData(Secu3Dat packet) {
		if (packet != null && isAdded()) {
			baudrate = (Spinner)getView().findViewById(R.id.miscelBaudrateSpinner);
			period = (EditText)getView().findViewById(R.id.miscelPeriodEditText);
			enableIgnitionCutoff = (CheckBox)getView().findViewById(R.id.miscelEnableIgnitionCutoffCheckBox);
			ignitionCutoffRPM = (EditText)getView().findViewById(R.id.miscelIgnitionCutoffRPMEditText);
			hallOutputStart = (EditText)getView().findViewById(R.id.miscelHallOutputStartEditText);
			hallOutputDelay = (EditText)getView().findViewById(R.id.miscelHallOutputDelayEditText);
			
			period.setText(String.valueOf(((MiscelPar)packet).period_ms));
			enableIgnitionCutoff.setChecked(((MiscelPar)packet).ign_cutoff != 0);
			ignitionCutoffRPM.setText(String.valueOf(((MiscelPar)packet).ign_cutoff_thrd));
			hallOutputStart.setText(String.valueOf(((MiscelPar)packet).hop_start_cogs));
			hallOutputDelay.setText(String.valueOf(((MiscelPar)packet).hop_durat_cogs));
		}
		
	}
}

package org.secu3.android.fragments;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ADCCorFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) return null;
		
		return inflater.inflate(R.layout.adccor_params, null);
	}
	
	public void setData (Secu3Dat packet) {
		
	}
}

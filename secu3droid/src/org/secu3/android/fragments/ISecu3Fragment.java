package org.secu3.android.fragments;

import org.secu3.android.api.io.Secu3Dat;

import android.support.v4.app.Fragment;

public interface ISecu3Fragment {

	public void setData (Secu3Dat packet);
	public Secu3Dat getData();
	public void updateData();
	
	public interface OnDataChangedListener {
		public void onDataChanged(Fragment fragment, Secu3Dat packet);
	}
	
	public void setOnDataChangedListener (OnDataChangedListener listener);
}

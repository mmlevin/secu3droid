package org.secu3.android.fragments;

import org.secu3.android.api.io.Secu3Dat;

public interface ISecu3Fragment {

	public void setData (Secu3Dat packet);
	public Secu3Dat getData();
	public void updateData();
}

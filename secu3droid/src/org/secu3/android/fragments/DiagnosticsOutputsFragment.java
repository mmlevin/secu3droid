/* Secu3Droid - An open source, free manager for SECU-3 engine
 * control unit
 * Copyright (C) 2013 Maksim M. Levin. Russia, Voronezh
 * 
 * SECU-3  - An open source, free engine control unit
 * Copyright (C) 2007 Alexey A. Shabelnikov. Ukraine, Gorlovka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * contacts:
 *            http://secu-3.org
 *            email: mmlevin@mail.ru
*/

package org.secu3.android.fragments;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.DiagOutDat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

public class DiagnosticsOutputsFragment extends Secu3Fragment implements ISecu3Fragment {
	DiagOutDat packet = new DiagOutDat();	
	
	CheckBox outputs[] = null;

	private int getOutputs () {
		int res = 0;
		for (int i = 0; i != outputs.length; ++i) {
			if (outputs [i].isChecked()) res |= 0x01 << i; 
		}
		return res;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) return null;
		
		return inflater.inflate(R.layout.diagnostics_outputs, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		String outputNames[] = getResources().getStringArray(R.array.diagnostics_output_names);		
		outputs = new CheckBox[outputNames.length];

		LinearLayout l = (LinearLayout)getView().findViewById(R.id.diagnosticsOutputsLinearLayout);
		
		CompoundButton.OnCheckedChangeListener listener = new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int i = getOutputs();
				if (packet != null) {
					packet.setOutputs(i);
					dataChanged(packet);
				}				
			}
		};
		
		for (int i = 0; i != outputs.length; ++i) {
			outputs[i] = new CheckBox(getActivity());
			outputs[i].setText(outputNames[i]);
			outputs[i].setTextAppearance(getActivity(), R.style.secu3TextAppearance);
			outputs[i].setOnCheckedChangeListener(listener);
			l.addView(outputs[i]);			
		}			
	}
	
	@Override
	public void onResume() {
		updateData();		
		super.onResume();
	}

	@Override
	public void updateData() {
		if (packet != null) {			
		}
	}

	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (DiagOutDat) packet;		
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

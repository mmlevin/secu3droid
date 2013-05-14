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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class ChokeFragment extends Secu3Fragment implements ISecu3Fragment {
	
	ChokePar packet = null;
	EditText chokeSteps;
	ToggleButton chokeTest;
	Button chokeManualStepUpButton;
	Button chokeManualStepDownButton;	
	
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
							dataChanged(packet);							
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
		
		chokeManualStepUpButton = (Button)getView().findViewById(R.id.chokeManualStepUp);
		chokeManualStepDownButton = (Button)getView().findViewById(R.id.chokeManualStepDown);
		
		chokeManualStepUpButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (packet != null)	{
					packet.manual_delta = 127;
					dataChanged(packet);	
				}
			}
		});

		chokeManualStepDownButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (packet != null)	{
					packet.manual_delta = -127;
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

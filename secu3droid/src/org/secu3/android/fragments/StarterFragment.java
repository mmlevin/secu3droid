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

import java.util.ArrayList;
import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.StartrPar;
import org.secu3.android.api.utils.BaseParamItem;
import org.secu3.android.api.utils.CustomNumberPickerDialog;
import org.secu3.android.api.utils.CustomNumberPickerDialog.OnCustomNumberPickerAcceptListener;
import org.secu3.android.api.utils.CustomNumberPickerFloatDialog;
import org.secu3.android.api.utils.CustomNumberPickerIntegerDialog;
import org.secu3.android.api.utils.ParamItemBoolean;
import org.secu3.android.api.utils.ParamItemFloat;
import org.secu3.android.api.utils.ParamItemInteger;
import org.secu3.android.api.utils.ParamItemsAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class StarterFragment extends Secu3Fragment implements ISecu3Fragment,OnItemClickListener,OnCustomNumberPickerAcceptListener  {
	
	StartrPar packet = null;
	ParamItemsAdapter adapter = null;
	CustomNumberPickerDialog dialog = null;	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) return null;
		return inflater.inflate(R.layout.starter_params, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		ListView lv = (ListView)getView().findViewById(R.id.starterParamsListView);				
		
        ArrayList<BaseParamItem> items = new ArrayList<BaseParamItem>();
        items.add(new ParamItemInteger(getActivity(), "RPM when starter will be turned off", "It will turn off starter after engine starts", "min-1", 600, 200, 2000, 10));
        items.add(new ParamItemInteger(getActivity(), "RPM when switching from start map", "It will switch ignition map from start to working", "min-1", 650, 200, 2000, 10));
        items.add(new ParamItemBoolean(getActivity(), "Zero advance angle", "If checked, advance angle will set to 0", true));        
        items.add(new ParamItemFloat(getActivity(), "Minimal advance angle", "Minimal working value of ignition advance angle", "°", -10.0f , -20.0f, 40.0f, 0.25f));
        items.add(new ParamItemFloat(getActivity(), "Maximal advance angle", "Maximal working value of ignition advance angle", "°", -10.0f , -20.0f, 40.0f, 0.25f));
        
        adapter = new ParamItemsAdapter(items);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);		
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		BaseParamItem i = (BaseParamItem) adapter.getItem(position);
		if (i instanceof ParamItemInteger) {
			dialog = new CustomNumberPickerIntegerDialog();
	        ((CustomNumberPickerIntegerDialog) dialog).setRange(((ParamItemInteger) i).getValue(), ((ParamItemInteger) i).getMinValue(), ((ParamItemInteger) i).getMaxValue(), ((ParamItemInteger) i).getStepValue());
	        dialog.setOnCustomNumberPickerAcceprListener(this);
	        dialog.show(getActivity().getSupportFragmentManager(), i.getName());			        
		} else if (i instanceof ParamItemFloat) {
			dialog = new CustomNumberPickerFloatDialog();
			((CustomNumberPickerFloatDialog) dialog).setRange(((ParamItemFloat) i).getValue(), ((ParamItemFloat) i).getMinValue(), ((ParamItemFloat) i).getMaxValue(), ((ParamItemFloat) i).getStepValue());
			dialog.setOnCustomNumberPickerAcceprListener(this);
	        dialog.show(getActivity().getSupportFragmentManager(), i.getName());				
		} else if (i instanceof ParamItemBoolean) {
			adapter.setValue(String.valueOf(!((ParamItemBoolean) i).getValue()), position);
		}
	}
	
	@Override
	public void onResume() {
		updateData();
		super.onStart();
	}
	
	@Override
	public void updateData() {
		if (packet != null) {
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

	@Override
	public void setValue(String value, int position) {
		adapter.setValue(value, position);		
	}
}

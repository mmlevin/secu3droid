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

package org.secu3.android.parameters.items;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.secu3.android.R;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ParamItemSpinner extends BaseParamItem {
	private String value = null;
	private int index = 0;
	Spinner paramSpinner = null;
	
	class CustomOnItemSelectedListener implements OnItemSelectedListener {
		ParamItemSpinner item;
		
		public CustomOnItemSelectedListener(ParamItemSpinner item) {
			this.item = item;
		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (item.getIndex() != position) {
				item.setIndex(position);
				if (listener != null) listener.onParamItemChange(item);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {			
		}
		
	}
	
	public ParamItemSpinner(Context context, String name, String summary, String value, String index) throws ParseException {
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		
		this.setContext(context);
		this.setName(name);
		this.setSummary(summary);
		this.setValue(value);
		
		if (index != null) this.setIndex(format.parse(index).intValue());
	}
		
	public ParamItemSpinner(Context context, int nameID, int summaryID, int valueID, int index) {
		this.setNameId(nameID);
		this.setSummaryId(summaryID);
		this.setUnitsId(summaryID);
		this.setContext(context);
		if (nameID != 0) this.setName(context.getString(nameID));
		if (summaryID != 0) this.setSummary(context.getString(summaryID));
		if (valueID != 0) this.setValue(context.getString(valueID));
		this.setIndex(index);
	}
	
	@Override
	public View getView() {	
		View v = super.getView(R.layout.params_list_item_spinner);
		
		TextView paramName = (TextView) v.findViewById(R.id.param_name);
		TextView paramSummary = (TextView) v.findViewById(R.id.param_summary);
		paramSpinner = (Spinner) v.findViewById(R.id.param_spinner);
		paramSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener(this));
        
		paramName.setText(this.getName());
		paramSummary.setText(this.getSummary());			
		
		setValue(value);
		setIndex(index);
		return v;
	}

	public String getValue() {
		return value;
	}

	@SuppressWarnings("unchecked")
	public void setValue(String value) {
		if (paramSpinner != null) {
			if (value == null) {
				paramSpinner.setEnabled(false);
			} else {
				String values[] = value.split("[|]");
				paramSpinner.setEnabled(true);
				paramSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,values));
				((ArrayAdapter<String>) paramSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			}
		}
		this.value = value;
	}

	public int getIndex() {
		/*if (paramSpinner != null)
			index = paramSpinner.getSelectedItemPosition();*/
		return index;
	}

	public void setIndex(int index) {
		if (paramSpinner != null)
			paramSpinner.setSelection(index);		
		this.index = index;
	}
}
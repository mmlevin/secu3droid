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

import org.secu3.android.R;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ParamItemBoolean extends BaseParamItem {
	private boolean value = false;
	
	class CustomOnCheckChangeListener implements CompoundButton.OnCheckedChangeListener {
		final ParamItemBoolean item;
		
		public CustomOnCheckChangeListener(ParamItemBoolean item) {
			this.item = item;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (item.value != isChecked) {
				item.setValue(isChecked);
				if (listener != null) listener.onParamItemChange(item);
			}
		}
		
	}
	
	public ParamItemBoolean(Context context, String name, String summary, boolean value) {
		this.setContext(context);
		this.setName(name);
		this.setSummary(summary);
		this.value = value;
	}
	
	public ParamItemBoolean(Context context, String name, String summary, String value) {
		this.setContext(context);
		this.setName(name);
		this.setSummary(summary);
		if (value != null) this.value = Boolean.parseBoolean(value);
	}	
	
	public ParamItemBoolean(Context context, int nameID, int summaryID, boolean value) {
		this.setNameId(nameID);
		this.setSummaryId(summaryID);
		this.setUnitsId(summaryID);
		this.setContext(context);
		if (nameID != 0) this.setName(context.getString(nameID));
		if (summaryID != 0) this.setSummary(context.getString(summaryID));
		this.value = value;
	}
	
	public ParamItemBoolean(Context context, int nameID, int summaryID, boolean value, boolean enabled) {
		this.setNameId(nameID);
		this.setSummaryId(summaryID);
		this.setUnitsId(summaryID);
		this.setContext(context);
		if (nameID != 0) this.setName(context.getString(nameID));
		if (summaryID != 0) this.setSummary(context.getString(summaryID));		
		this.value = value;
		setEnabled(enabled);
	}
	
	@Override
	public View getView() {	
		View v = super.getView(R.layout.params_list_item_boolean);
		
		TextView paramName = (TextView) v.findViewById(R.id.param_name);
		TextView paramSummary = (TextView) v.findViewById(R.id.param_summary);
		 CheckBox paramValueBoolean = (CheckBox) v.findViewById(R.id.param_value_boolean);
		
		paramValueBoolean.setOnCheckedChangeListener(new CustomOnCheckChangeListener(this));
		paramValueBoolean.setClickable(isEnabled());
        
		paramName.setText(this.getName());
		paramSummary.setText(this.getSummary());
        paramValueBoolean.setChecked(this.getValue());			
		return v;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
	
}
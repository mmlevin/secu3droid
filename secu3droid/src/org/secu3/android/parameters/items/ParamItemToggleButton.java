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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ParamItemToggleButton extends BaseParamItem {
	private boolean value = false;
	
	class CustomOnCheckChangeListener implements CompoundButton.OnCheckedChangeListener {
		ParamItemToggleButton item;
		
		public CustomOnCheckChangeListener(ParamItemToggleButton item) {
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
	
	public ParamItemToggleButton(Context context, String name, String summary) {
		this.setContext(context);
		this.setName(name);
		this.setSummary(summary);
	}
		
	public ParamItemToggleButton(Context context, int nameID, int summaryID) {
		this.setNameId(nameID);
		this.setSummaryId(summaryID);
		this.setUnitsId(summaryID);
		this.setContext(context);
		this.setName(context.getString(nameID));
		this.setSummary(context.getString(summaryID));
	}
	
	@Override
	public View getView() {	
		View v = super.getView(R.layout.params_list_item_toggle_button);
		
		TextView paramName = (TextView) v.findViewById(R.id.param_name);
		TextView paramSummary = (TextView) v.findViewById(R.id.param_summary);
		ToggleButton paramButton = (ToggleButton) v.findViewById(R.id.param_button);
		paramButton.setOnCheckedChangeListener(new CustomOnCheckChangeListener (this));
        
		paramName.setText(this.getName());
		paramSummary.setText(this.getSummary());			
		return v;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
}
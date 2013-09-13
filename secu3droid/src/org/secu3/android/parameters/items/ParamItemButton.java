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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ParamItemButton extends BaseParamItem {
	
	class CustomClickListener implements OnClickListener {
		ParamItemButton item;
		
		public CustomClickListener(ParamItemButton item) {
			this.item = item;
		}		
		
		@Override
		public void onClick(View v) {
			if (listener != null) listener.onParamItemChange(item);			
		}		
	}
	
	public ParamItemButton(Context context, String name, String summary, String units) {
		this.setContext(context);
		this.setName(name);
		this.setSummary(summary);
		this.setUnits(units);
	}
		
	public ParamItemButton(Context context, int nameID, int summaryID, int unitsID) {
		this.setNameId(nameID);
		this.setSummaryId(summaryID);
		this.setUnitsId(unitsID);
		this.setContext(context);
		if (nameID != 0) this.setName(context.getString(nameID));
		if (summaryID!= 0) this.setSummary(context.getString(summaryID));
		if (unitsID!= 0) this.setSummary(context.getString(unitsID));
	}
	
	@Override
	public View getView() {	
		View v = super.getView(R.layout.params_list_item_button);
		
		TextView paramName = (TextView) v.findViewById(R.id.param_name);
		TextView paramSummary = (TextView) v.findViewById(R.id.param_summary);
		Button paramButton =(Button) v.findViewById(R.id.param_button);
		paramButton.setText(getUnits());
		paramButton.setOnClickListener(new CustomClickListener(this));
		paramButton.setEnabled(isEnabled());
        
		paramName.setText(this.getName());
		paramSummary.setText(this.getSummary());			
		return v;
	}	
}
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
import android.widget.TextView;

public class ParamItemInteger extends BaseParamItem {
	private int value = 0;
	private int minValue = 0;		
	private int maxValue = 0;
	private int stepValue = 0;
	private String format = "%d";
	
	public ParamItemInteger(Context context, String name, String summary, String units, int value, int minValue, int maxValue, int stepValue) {
		this.setContext(context);
		this.setName(name);
		this.setSummary(summary);
		this.setUnits(units);
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.stepValue = stepValue;
	}
	
	public ParamItemInteger(Context context, String name, String summary, String units, String value, String minValue, String maxValue, String stepValue) throws ParseException {
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		
		this.setContext(context);
		this.setName(name);
		this.setSummary(summary);
		this.setUnits(units);
			
		if (value != null) this.value = format.parse(value).intValue();
		if (minValue != null) this.minValue = format.parse(minValue).intValue();
		if (maxValue != null) this.maxValue = format.parse(maxValue).intValue();
		if (stepValue != null) this.stepValue = format.parse(stepValue).intValue();
	}	
	
	public ParamItemInteger(Context context, int nameID, int summaryID, int unitsID, int value, int minValue, int maxValue, int stepValue) {
		this.setNameId(nameID);
		this.setSummaryId(summaryID);
		this.setUnitsId(summaryID);
		this.setContext(context);
		if (nameID != 0) this.setName(context.getString(nameID));
		if (summaryID != 0) this.setSummary(context.getString(summaryID));
		if (unitsID != 0) this.setUnits(context.getString(unitsID));
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.stepValue = stepValue;
	}		
	
	@Override
	public View getView() {
		View v = super.getView(R.layout.params_list_item_number);

		TextView paramName = (TextView) v.findViewById(R.id.param_name);
		TextView paramSummary = (TextView) v.findViewById(R.id.param_summary);
		TextView paramValueNum = (TextView) v.findViewById(R.id.param_value_number);
		TextView paramUnits = (TextView) v.findViewById(R.id.param_units);
        
		paramName.setText(this.getName());
		paramSummary.setText(this.getSummary());    		
		paramUnits.setText(this.getUnits());
        paramValueNum.setText(String.format(Locale.US, format, value));			
		
		return v;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		if ((value <= maxValue) && (value >= minValue))
		this.value = value;
	}
	
	public int getMinValue() {
		return minValue;
	}
	
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	
	public int getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	
	public int getStepValue() {
		return stepValue;
	}
	
	public void setStepValue(int stepValue) {
		this.stepValue = stepValue;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
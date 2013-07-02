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

public class ParamItemFloat extends BaseParamItem {
	private float value = 0;
	private float minValue = 0;		
	private float maxValue = 0;
	private float stepValue = 0;
	private String format = "%.02f";
	
	public ParamItemFloat(Context context, String name, String summary, String units, float value, float minValue, float maxValue, float stepValue) {
		this.setContext(context);
		this.setName(name);
		this.setSummary(summary);
		this.setUnits(units);
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.stepValue = stepValue;
	}
	
	public ParamItemFloat(Context context, String name, String summary, String units, String value, String minValue, String maxValue, String stepValue) throws ParseException {
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		
		this.setContext(context);
		this.setName(name);
		this.setSummary(summary);
		this.setUnits(units);
			
		if (value != null) this.value = format.parse(value).floatValue();
		if (minValue != null) this.minValue = format.parse(minValue).floatValue();
		if (maxValue != null) this.maxValue = format.parse(maxValue).floatValue();
		if (stepValue != null) this.stepValue = format.parse(stepValue).floatValue();
	}	
	
	public ParamItemFloat(Context context, int nameID, int summaryID, int unitsID, float value, float minValue, float maxValue, float stepValue) {
		this.setContext(context);
		this.setName(context.getString(nameID));
		this.setSummary(context.getString(summaryID));
		this.setUnits(context.getString(unitsID));
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
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public float getMinValue() {
		return minValue;
	}
	
	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}
	
	public float getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}
	
	public float getStepValue() {
		return stepValue;
	}
	
	public void setStepValue(float stepValue) {
		this.stepValue = stepValue;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
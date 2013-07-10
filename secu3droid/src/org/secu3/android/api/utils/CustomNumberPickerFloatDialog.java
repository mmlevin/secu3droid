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

package org.secu3.android.api.utils;

import java.util.Locale;
import net.simonvt.numberpicker.NumberPicker;

public class CustomNumberPickerFloatDialog extends CustomNumberPickerDialog {

	private static final int SHORT_STYLE_THRESHOLD = 1000;
	private float value = 0;
	private float minValue = 0;
	private float maxValue = 0;
	private float stepValue = 0;
	private String format = "%.02f";
	private int stepsCount;	
	
	@Override
	protected void setMainNumberPickerDisplayedValues(NumberPicker numberPicker) {
		if (numberPicker != null) {	
			if (isShortMode()) {
				int stepsCount = Math.round((maxValue - minValue) / stepValue);
				int index = Math.round((value - minValue) / stepValue);
				
				float mMinValue = value;
				while ((mMinValue - stepValue) >=  minValue) {
					mMinValue -= stepValue;
				}			
				
				numberPicker.setMinValue(0);
				numberPicker.setMaxValue(stepsCount);
				numberPicker.setValue(index);
				
				String values[] = new String[stepsCount+1];
				for (int i = 0; i != stepsCount+1; i++) {					
					String value = String.format(Locale.US, getFormat(), mMinValue + stepValue * i);
					values[i] = value;
				}
				
				numberPicker.setDisplayedValues(values);
			}
			else {
				int stepsCount = Math.round(maxValue - minValue);
				int index = Math.round(value - minValue);
				
				int mMinValue = Math.round(minValue);		
				
				numberPicker.setMinValue(0);
				numberPicker.setMaxValue(stepsCount);
				numberPicker.setValue(index);
				
				String values[] = new String[stepsCount+1];
				for (int i = 0; i != stepsCount+1; i++) {					
					String value = String.format(Locale.US, "%d", mMinValue + i);
					values[i] = value;
				}
				
				numberPicker.setDisplayedValues(values);				
			}
		}
	}
		
	int truncLength (float d) {
		return String.format(Locale.US, getFormat(),d).length()-2;
	}
	
	int multiplier (float d) {
		return (int) Math.round(Math.pow(10, truncLength(d)));		
	}
	
	@Override
	protected void setAdditionalNumberPickerDisplayedValues(NumberPicker numberPicker) {		
		if (numberPicker != null) {	
			if (!isShortMode()) {
				int stepsCount = Math.round(1 / stepValue);
				int index = (int) Math.round((value-Math.floor(value)) / stepValue);		
				
				float mMinValue = (float) (value - Math.floor(value));
				while ((mMinValue - stepValue) >=  0) {
					mMinValue -= stepValue;
				}				
				
				numberPicker.setMinValue(0);
				numberPicker.setMaxValue(stepsCount-1);
				numberPicker.setValue(index);				
				
				String values[] = new String[stepsCount];
				for (int i = 0; i != stepsCount; i++) {					
					String value = String.format(Locale.US, getFormat(), mMinValue + stepValue * i);					
					values[i] = value.substring(value.indexOf('.'));;
				}
				
				numberPicker.setDisplayedValues(values);				
			}
		}
	}	
	
	public CustomNumberPickerFloatDialog setRange (float value, float minValue, float maxValue, float stepValue) {				
		if (stepValue < 0) throw new IllegalArgumentException("stepValue cannot be less to zero");
		else if (((stepValue == 0) && (minValue == 0) && (maxValue == 0))||((value < minValue) && (stepValue != 0) && (minValue != maxValue))) {
			minValue = value;
			maxValue = value;
			stepValue = 1;
		} else {
			if (value < minValue) throw new IllegalArgumentException("value could not be less than minValue");
			if (value > maxValue) throw new IllegalArgumentException("value could not be greater than minValue");
		};
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.stepValue = stepValue;
		
		stepsCount = Math.round((maxValue - minValue) / stepValue);
		
		setShortMode(stepsCount <= SHORT_STYLE_THRESHOLD);
		return this;
	}

	public String getFormat() {
		return format;
	}

	public CustomNumberPickerFloatDialog setFormat(String format) {
		this.format = format;
		return this;
	}

	@Override
	public String getValue() {
		if (isShortMode()) {
			return numberPickerMain.getDisplayedValues()[numberPickerMain.getValue()];
		} else {
			float whole = Float.valueOf(numberPickerMain.getDisplayedValues()[numberPickerMain.getValue()]);
			float trunc = Float.valueOf("0"+numberPickerAdditional.getDisplayedValues()[numberPickerAdditional.getValue()]);
			return String.format(Locale.US,getFormat(),whole+Math.signum(whole)*trunc);
		}
	}	
}
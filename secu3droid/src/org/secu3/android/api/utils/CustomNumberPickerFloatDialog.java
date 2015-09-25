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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.simonvt.numberpicker.NumberPicker;

public class CustomNumberPickerFloatDialog extends CustomNumberPickerDialog {

	private static final int SHORT_STYLE_THRESHOLD = 1000;
	private float value = 0;
	private float minValue = 0;
	private float maxValue = 0;
	private float stepValue = 0;
	private String format = "%.02f";

//TODO Sometimes edited value is not exact as initial
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (!isShortMode()) {
			String formatString = String.format(Locale.US, " (%1$s...%1$s)",getFormat());
			getDialog().setTitle(getTag() + String.format(Locale.US, formatString, minValue, maxValue));
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
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
				int stepsCount = Math.round(Math.max(Math.abs(maxValue),Math.abs(minValue)));
				int index = Math.round(Math.abs(value));
				
				int mMinValue;
				boolean signFlag = false;
				
				if ((minValue < 0) && (maxValue > 0)) {
					mMinValue = 0;
					signFlag = true;
				} else mMinValue = Math.round(minValue);
				
				if (signFlag) stepsCount ++;
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
				int index = Math.round((Math.abs(value) - Float.valueOf(Math.abs(value)).intValue()) / stepValue);
				
				numberPicker.setMinValue(0);
				numberPicker.setMaxValue(stepsCount-1);
				numberPicker.setValue(index);				
				
				String values[] = new String[stepsCount];
				for (int i = 0; i != stepsCount; i++) {					
					String value = String.format(Locale.US, getFormat(), stepValue * i);					
					values[i] = value.substring(value.indexOf('.'));
				}
				
				numberPicker.setDisplayedValues(values);				
			}
		}
	}	
	
	@Override
	protected void setSignNumberPickerDisplayedValues(NumberPicker numberPicker) {
		if (numberPicker != null) {
			if (!isShortMode()) {
				numberPicker.setMinValue(0);
				if ((minValue < 0) && (maxValue >= 0)) {
					String values[] = new String[2];
					values [0] = "-"; values [1] ="+";
					numberPicker.setMaxValue(1);
					numberPicker.setValue((value < 0)?0:1);
					numberPicker.setDisplayedValues(values);
				} else if ((minValue < 0) && (maxValue < 0)) {
					String values[] = new String[1];
					values [0] = "-";
					numberPicker.setDisplayedValues(values);
				} else if ((minValue >= 0) && (maxValue >= 0)) {
					String values[] = new String[1];
					values [0] = "+";
					numberPicker.setDisplayedValues(values);
					numberPicker.setVisibility(View.GONE);
				}
			}
		}
		
	}	
	
	public CustomNumberPickerFloatDialog setRange (float value, float minValue, float maxValue, float stepValue) {				
		if (stepValue < 0) throw new IllegalArgumentException("stepValue cannot be less to zero");
		else if (!isValid()) {
			minValue = value;
			maxValue = value;
			stepValue = 1;
		} else {
			if (value < minValue) throw new IllegalArgumentException("value could not be less than minValue");
			if (value > maxValue) throw new IllegalArgumentException("value could not be greater than minValue");
		}

		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.stepValue = stepValue;
		
		int stepsCount = Math.round((maxValue - minValue) / stepValue);
				
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
			String signChar = numberPickerSign.getDisplayedValues()[numberPickerSign.getValue()];
			float sign = (signChar.equals("-"))?-1.0f:1.0f;
			return String.format(Locale.US,getFormat(),sign*(whole+trunc));
		}
	}
}
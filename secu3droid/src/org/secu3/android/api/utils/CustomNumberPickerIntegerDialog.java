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

public class CustomNumberPickerIntegerDialog extends CustomNumberPickerDialog {	
	NumberPicker numberPicker = null;
	
	int value = 0;
	int minValue = 0;
	int maxValue = 0;
	int stepValue = 0;			
	
	@Override		
	protected void setNumberPickerDisplayedValues(NumberPicker numberPicker) {
		if (numberPicker != null) {
			int count = (maxValue - minValue) / stepValue;	
			int index = (value - minValue) / stepValue;
			int currentValue = minValue;
			numberPicker.setMinValue(0);
			numberPicker.setMaxValue(count);
			numberPicker.setValue(index);
			
			String values[] = new String[count+1];
			for (int i = 0; i != count+1; i++, currentValue += stepValue) {					
				String value = String.format(Locale.US, "%d", currentValue);
				values[i] = value;
			}
			
			numberPicker.setDisplayedValues(values);
		}
	}
	
	public CustomNumberPickerIntegerDialog setRange (int value, int minValue, int maxValue, int stepValue) {
		if (stepValue <= 0) throw new IllegalArgumentException("stepValue cannot be less or equals to zero");
		if ((value % stepValue) != 0) throw new IllegalArgumentException("value should be a miltiple of stepValue");
		if (value < minValue) throw new IllegalArgumentException("value counld not be less than minValue");
		if (value > maxValue) throw new IllegalArgumentException("value counld not be greater than minValue");
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.stepValue = stepValue;
		return this;
	}
}
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

package org.secu3.android.parameters;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import org.secu3.android.parameters.items.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ParamItemsAdapter extends BaseAdapter {	
	private ArrayList <BaseParamItem> items = null;

	public ParamItemsAdapter(ArrayList<BaseParamItem> items) {
		this.items = items;        
	}
	
	public BaseParamItem findItemByNameId (int Id) {
		if (items != null) {
			int size = items.size();
			for (int i = 0; i != size; i++) {
				if (items.get(i).getNameId() == Id) return items.get(i); 
			}
		}
		return null;
	}	
	
	@Override
	public int getCount() {
		if (items != null)
			return items.size();
		else return 0;
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        BaseParamItem i = items.get(position);	        	            
        return i.getView();
	}
  		
	public void setValue(String value, int position) {
		BaseParamItem i = (BaseParamItem)items.get(position);
		if (i instanceof ParamItemInteger) {
				NumberFormat format = NumberFormat.getInstance(Locale.US);
				Number number;
				try {
					number = format.parse(value);						
					((ParamItemInteger) i).setValue(number.intValue());
				} catch (ParseException e) {
					e.printStackTrace();
				} 					
		} else if (i instanceof ParamItemFloat) {
			NumberFormat format = NumberFormat.getInstance(Locale.US);
			Number number;
			try {
				number = format.parse(value);
				((ParamItemFloat) i).setValue(number.floatValue());						
			} catch (ParseException e) {
				e.printStackTrace();
			} 													
		} else if (i instanceof ParamItemBoolean) {
			((ParamItemBoolean) i).setValue(Boolean.parseBoolean(value));
		} else if (i instanceof ParamItemString) {
			((ParamItemString) i).setValue(value);
		}
		notifyDataSetChanged();
	}
	
	public void setIntegerItem (int id, int value) {
		BaseParamItem item;
		if ((item = findItemByNameId(id)) != null ) ((ParamItemInteger)item).setValue (value);
	}
	
	public void setFloatItem (int id, float value) {
		BaseParamItem item;
		if ((item = findItemByNameId(id)) != null ) ((ParamItemFloat)item).setValue (value);
	}
	
	public void setBooleanItem (int id, boolean value) {
		BaseParamItem item;
		if ((item = findItemByNameId(id)) != null ) ((ParamItemBoolean)item).setValue (value);
	}
	
	public void setSpinnerItemIndex (int id, int index) {
		BaseParamItem item;
		if ((item = findItemByNameId(id)) != null ) ((ParamItemSpinner)item).setIndex(index);
	}

	public void setSpinnerItemValue (int id, String value) {
		BaseParamItem item;
		if ((item = findItemByNameId(id)) != null ) ((ParamItemSpinner)item).setValue(value);
	}		
}
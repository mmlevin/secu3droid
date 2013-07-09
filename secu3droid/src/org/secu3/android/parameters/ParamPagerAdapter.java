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

import java.util.ArrayList;

import org.secu3.android.parameters.items.BaseParamItem;
import org.secu3.android.parameters.items.ParamItemBoolean;
import org.secu3.android.parameters.items.ParamItemFloat;
import org.secu3.android.parameters.items.ParamItemInteger;
import org.secu3.android.parameters.items.ParamItemSpinner;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ParamPagerAdapter extends FragmentPagerAdapter{
	private ArrayList<ParamsPage> pages = null;
	private Context context;
	
	public BaseParamItem findItemByNameId (int Id) {
		if (pages != null) {
			int size = pages.size();
			for (int i = 0; i != size; i++) {
				ParamsPage page = pages.get(i);
				ArrayList<BaseParamItem> items = page.getItems();
				int size2 = page.getItems().size();
				for (int j = 0; j != size2; j++) {
					if (items.get(j).getNameId() == Id) return items.get(j); 
				}
			}
		}
		return null;
	}	
			
	public ParamPagerAdapter(FragmentManager fm, Context context, ArrayList<ParamsPage> pages) {
		super(fm);		
		this.pages = pages;
		this.context = context;
	}	
		
	@Override
	public ParamsPageFragment getItem (int position) {		
		ParamsPageFragment fragment = ParamsPageFragment.newInstance(position);
		return fragment;					
	}	
	
	@Override
	public int getCount() {
		return pages.size();
	}				
	
	@Override
	public CharSequence getPageTitle(int position) {
		return context.getString(pages.get(position).getNameId());
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
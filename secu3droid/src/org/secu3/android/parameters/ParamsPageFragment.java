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

import org.secu3.android.R;
import org.secu3.android.parameters.items.BaseParamItem;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ParamsPageFragment extends Fragment {
	private OnItemClickListener listener = null;	
	ListView lv = null;
	
	ArrayList<BaseParamItem> items;
	
    static ParamsPageFragment newInstance(ArrayList<BaseParamItem> items) {
    	ParamsPageFragment f = new ParamsPageFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("items", items);
        f.setArguments(args);

        return f;
    }	
    
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	items = (ArrayList<BaseParamItem>) (getArguments() != null ? getArguments().getSerializable("items") : 1);    	
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) return null;
		return inflater.inflate(R.layout.params_page, null);
	}
	
	@Override
	public void onAttach(Activity activity) {		
		super.onAttach(activity);
		listener = (OnItemClickListener) activity;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lv = (ListView) getView().findViewById(R.id.params_page_listview);
		if (items != null) {
			lv.setAdapter(new ParamItemsAdapter(items));
			lv.setOnItemClickListener(listener);
		}
	}					
}
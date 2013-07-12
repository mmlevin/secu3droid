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

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ParamsPageFragment extends ListFragment {
	private OnItemClickListener listener = null;
	
    static ParamsPageFragment newInstance(int num) {
    	ParamsPageFragment f = new ParamsPageFragment();

        Bundle args = new Bundle();
        args.putInt("number", num);
        f.setArguments(args);

        return f;
    }    	
    	
	public int getNum() {
		return getArguments().getInt("number", 0);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (listener != null) {
			listener.onItemClick(l, v, position, id);
		}
	}
	
	public void setOnItemClickListener (OnItemClickListener listener) {
		this.listener = listener;
	}
}
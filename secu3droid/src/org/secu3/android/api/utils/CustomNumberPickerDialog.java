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

import org.secu3.android.R;

import net.simonvt.numberpicker.NumberPicker;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public abstract class CustomNumberPickerDialog extends DialogFragment {			

		public interface OnNumberPickerDialogAcceptListener {
			void onNumberPickerDialogAccept (int itemId);
		}		
		
		protected NumberPicker numberPickerMain = null;
		protected NumberPicker numberPickerAdditional = null;
		private int numberPickerIndexMain = Integer.MAX_VALUE;
		private int numberPickerIndexAdditional = Integer.MAX_VALUE;
		private int numberPickerId = 0;
		private OnNumberPickerDialogAcceptListener listener = null;
		private boolean shortMode = true;
		private boolean valid = false;
			
		public CustomNumberPickerDialog setOnCustomNumberPickerAcceptListener (OnNumberPickerDialogAcceptListener listener) {
			this.listener = listener;
			return this;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			setRetainInstance(true);			
			
		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle(getTag());
		    builder.setCancelable(true);
		    LayoutInflater inflater = this.getActivity().getLayoutInflater();

		    View v = inflater.inflate(R.layout.custom_number_picker_integer,null);
		    numberPickerMain = (NumberPicker) v.findViewById(R.id.numberPickerMain);
		    numberPickerAdditional = (NumberPicker) v.findViewById(R.id.numberPickerAdditional);
		    		
		    if (numberPickerMain != null) {		    	
		    	setMainNumberPickerDisplayedValues(numberPickerMain);
		    	if (numberPickerIndexMain != Integer.MAX_VALUE)
		    		numberPickerMain.setValue(numberPickerIndexMain);		    	
		    	numberPickerMain.setFocusable(true);
		    	numberPickerMain.setFocusableInTouchMode(true);
		    }		    		
		    
		    if (numberPickerAdditional != null) {		    	
		    	setAdditionalNumberPickerDisplayedValues(numberPickerAdditional);
		    	if (numberPickerIndexAdditional != Integer.MAX_VALUE)
		    		numberPickerAdditional.setValue(numberPickerIndexAdditional);		    	
		    	numberPickerAdditional.setFocusable(true);
		    	numberPickerAdditional.setFocusableInTouchMode(true);
		    }			    
		    
		    setShortMode(shortMode);
		    
		    builder.setView(v)
		           .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		            	   if (listener != null) listener.onNumberPickerDialogAccept(numberPickerId);
		               }
		           })
		           .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		                   dismiss();
		               }
		           });    
		    return builder.create();
		}			
		
		public CustomNumberPickerDialog setId (int id) {
			this.numberPickerId = id;
			return this;
		}
		
		// http://code.google.com/p/android/issues/detail?id=17423
		@Override
		public void onDestroyView() {
		    if (getDialog() != null && getRetainInstance())
		        getDialog().setOnDismissListener(null);
		       
		    super.onDestroyView();
		}		

		@Override
		public void onSaveInstanceState(Bundle arg0) {
			if (numberPickerMain != null) {
				numberPickerIndexMain = numberPickerMain.getValue();
			}
			super.onSaveInstanceState(arg0);
		}
		
		protected abstract void setMainNumberPickerDisplayedValues(NumberPicker numberPicker);
		protected abstract void setAdditionalNumberPickerDisplayedValues(NumberPicker numberPicker);
		public abstract String getValue();

		public boolean isShortMode() {
			return shortMode;
		}

		public void setShortMode(boolean shortMode) {
			this.shortMode = shortMode;
			if (numberPickerAdditional != null)
				numberPickerAdditional.setVisibility(shortMode?View.GONE:View.VISIBLE);
		}

		public boolean isValid() {
			return valid;
		}

		public void setValid(boolean valid) {
			this.valid = valid;
		}
	}
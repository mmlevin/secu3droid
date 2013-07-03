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

package org.secu3.android;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import org.secu3.android.api.io.*;
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;
import org.secu3.android.api.io.Secu3Dat.*;
import org.secu3.android.api.utils.CustomNumberPickerDialog;
import org.secu3.android.api.utils.CustomNumberPickerDialog.OnCustomNumberPickerAcceptListener;
import org.secu3.android.api.utils.CustomNumberPickerFloatDialog;
import org.secu3.android.api.utils.CustomNumberPickerIntegerDialog;
import org.secu3.android.api.utils.ResourcesUtils;
import org.secu3.android.fragments.*;
import org.secu3.android.fragments.ISecu3Fragment.OnDataChangedListener;
import org.secu3.android.parameters.ParamItemToggleButton;
import org.secu3.android.parameters.ParamItemsAdapter;
import org.secu3.android.parameters.ParamPagerAdapter;
import org.secu3.android.parameters.ParamsPage;
import org.secu3.android.parameters.ParamsPageFragment;
import org.secu3.android.parameters.items.BaseParamItem;
import org.secu3.android.parameters.items.ParamItemBoolean;
import org.secu3.android.parameters.items.ParamItemButton;
import org.secu3.android.parameters.items.ParamItemFloat;
import org.secu3.android.parameters.items.ParamItemInteger;
import org.secu3.android.parameters.items.ParamItemLabel;
import org.secu3.android.parameters.items.ParamItemSpinner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ParamActivity extends FragmentActivity implements OnDataChangedListener, OnItemClickListener,OnCustomNumberPickerAcceptListener {
	public static final String LOG_TAG = "ParamActivity";	
	
	public static final int PARAMS_NUMBER = 9;
	
	MiscelFragment miscelParPage = null;
	ChokeFragment chokeParPage = null;
	
	ProgressBar progressBar = null;
		
	ArrayList<ParamsPage> pages;
	TextView textViewStatus = null;
	TextView textView = null;
	ViewPager pager = null;
    ParamPagerAdapter paramAdapter = null;
    ParamItemsAdapter adapter = null;
    int position = Integer.MAX_VALUE;
	CustomNumberPickerDialog dialog = null;

    SharedPreferences sharedPref = null;
	private boolean isOnline = false;
	private boolean uploadImmediatelly;
		
	    
    private void readParams() {    	
    	startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_READ_PARAMS.ordinal()));
    	startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_READ_SENSORS.ordinal()));
    }
    
	public class ReceiveMessages extends BroadcastReceiver 
	{
		public IntentFilter intentFilter = null;
		
		public ReceiveMessages() {
			intentFilter = new IntentFilter();
			intentFilter.addAction(Secu3Dat.RECEIVE_STARTER_PAR);
			intentFilter.addAction(Secu3Dat.RECEIVE_ANGLES_PAR);
			intentFilter.addAction(Secu3Dat.RECEIVE_IDLREG_PAR);
			intentFilter.addAction(Secu3Dat.RECEIVE_FNNAME_DAT);
			intentFilter.addAction(Secu3Dat.RECEIVE_FUNSET_PAR);
			intentFilter.addAction(Secu3Dat.RECEIVE_TEMPER_PAR);
			intentFilter.addAction(Secu3Dat.RECEIVE_CARBUR_PAR);
			intentFilter.addAction(Secu3Dat.RECEIVE_ADCCOR_PAR);
			intentFilter.addAction(Secu3Dat.RECEIVE_CKPS_PAR);
			intentFilter.addAction(Secu3Dat.RECEIVE_MISCEL_PAR);
			intentFilter.addAction(Secu3Dat.RECEIVE_CHOKE_PAR);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS);
			intentFilter.addAction(Secu3Dat.RECEIVE_OP_COMP_NC);
		}
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{    
			String action = intent.getAction();
			Log.d(LOG_TAG, action);
			update(intent);	   	    
		}
	}
	
	ReceiveMessages receiver;

	public void createFormFromXml (int id) throws InvalidAlgorithmParameterException {
		String name;
		ParamsPage page = null;
		BaseParamItem item = null;

		int parameterType = 0;		
		String parameterName = null;
		String parameterSummary = null;
		String parameterUnits = null;
		String parameterStrValue = null;
		String parameterValue = null;
		String parameterMinValue = null;
		String parameterMaxValue = null;
		String parameterStepValue = null;
		String parameterIndex = null;
		String parameterMasFormat = null;
		String attr = null;
		String attrValue = null;		
		
		try {
			XmlPullParser xpp = getResources().getXml(id);
			pages = new ArrayList<ParamsPage>();
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					name = xpp.getName();
					if (name.equalsIgnoreCase("Parameters")) {
						if (pages.size() != 0) throw new InvalidAlgorithmParameterException("Pages adapter is non empty, probably nested Parameter element"); 
					} else					
					// Found new page element
					if (name.equalsIgnoreCase("Page")) {
						if (page != null) {
							throw new InvalidAlgorithmParameterException("Pages can't be nested");
						}
						int count = xpp.getAttributeCount(); 
						if (count > 0) {
							for (int i = 0; i != count; i++) {
								attr  = xpp.getAttributeName(i);
								attrValue = xpp.getAttributeValue(i);
								if (attr.equals("name")) {
									if (ResourcesUtils.isResource(attrValue)) {
										page = new ParamsPage(ResourcesUtils.referenceToInt(attrValue));
									} else throw new InvalidAlgorithmParameterException("Page name must be a string reference");									
									pages.add(page);
								}
							}
						}						
					} else
					// Found new parameter element
					if (name.equalsIgnoreCase("Parameter")){
						if (item != null) {
							throw new InvalidAlgorithmParameterException("Parameters can't be nested");
						}
						int count = xpp.getAttributeCount(); 
						parameterName = null;
						parameterSummary = null;
						parameterUnits = null;
						parameterStrValue = null;
						parameterValue = null;
						parameterMinValue = null;
						parameterMaxValue = null;
						parameterStepValue = null;
						parameterIndex = null;
						parameterMasFormat = null;
						if (count > 0) {
							for (int i = 0; i != count; i++) {
								attr  = xpp.getAttributeName(i);
								attrValue = xpp.getAttributeValue(i);
								if (attr.equalsIgnoreCase("name")) {
									if (ResourcesUtils.isResource(attrValue)) {
										parameterName = attrValue;
									} else throw new InvalidAlgorithmParameterException("Parameter name must be a string reference");									
								} else if (attr.equalsIgnoreCase("summary")) {
									parameterSummary = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								} else									
								if (attr.equalsIgnoreCase("units")) {
									parameterUnits = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								} else
								if (attr.equalsIgnoreCase("type")) {
									if (ResourcesUtils.isResource(attrValue)) {
										parameterType = ResourcesUtils.referenceToInt(attrValue);
									} else throw new InvalidAlgorithmParameterException("Parameter type must be a reference");									
								} else 
								if (attr.equalsIgnoreCase("value")) {
									parameterValue = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								} else
								if (attr.equalsIgnoreCase("str_value")) {
									parameterStrValue = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								} else	
								if (attr.equalsIgnoreCase("minValue")) {
									parameterMinValue = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								} else 
								if (attr.equalsIgnoreCase("maxValue")) {
									parameterMaxValue = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								} else 
								if (attr.equalsIgnoreCase("stepValue")) {
									parameterStepValue = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								} else 
								if (attr.equalsIgnoreCase("format")) {
									parameterMasFormat = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								}
								else 
								if (attr.equalsIgnoreCase("index")) {
									parameterIndex = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								}
							}
						}											
					}
					break;
				case XmlPullParser.END_TAG:
					name = xpp.getName();
					if (name.equalsIgnoreCase("Parameters")) {
						if (pages.size() == 0) throw new InvalidAlgorithmParameterException("Parameters closed, but not opened");
					} else				
					if (name.equalsIgnoreCase("Page")) {
						if (page == null) throw new InvalidAlgorithmParameterException("Page closed, but not opened");
						else page = null;
					} else
					if (name.equalsIgnoreCase("Parameter")) {
						if ((parameterName == null) || (parameterType == 0) || (TextUtils.isEmpty(parameterName))) throw new InvalidAlgorithmParameterException("Parameter element is invalid");
						else {
							switch (parameterType) {
							case R.id.parameter_type_boolean:
								item = new ParamItemBoolean(this, ResourcesUtils.getReferenceString(this, parameterName), parameterSummary, parameterValue);
								item.setNameId(ResourcesUtils.referenceToInt(parameterName));
								break;
							case R.id.parameter_type_integer:
								try {
									item = new ParamItemInteger(this, ResourcesUtils.getReferenceString(this, parameterName), parameterSummary,
											parameterUnits, parameterValue, parameterMinValue, parameterMaxValue, parameterStepValue);
									item.setNameId(ResourcesUtils.referenceToInt(parameterName));									
								} catch (ParseException e) {
									throw new InvalidAlgorithmParameterException("Wrong integer parameter attributes");
								}
								break;
							case R.id.parameter_type_float:
								try {
									item = new ParamItemFloat(this, ResourcesUtils.getReferenceString(this, parameterName), parameterSummary,
											parameterUnits, parameterValue, parameterMinValue, parameterMaxValue, parameterStepValue);
									item.setNameId(ResourcesUtils.referenceToInt(parameterName));	
									((ParamItemFloat) item).setFormat (parameterMasFormat);
								} catch (ParseException e) {
									throw new InvalidAlgorithmParameterException("Wrong integer parameter attributes");
								}								
								break;
							case R.id.parameter_type_label:
								item = new ParamItemLabel(this, ResourcesUtils.getReferenceString(this, parameterName), parameterSummary);
								item.setNameId(ResourcesUtils.referenceToInt(parameterName));
								break;
							case R.id.parameter_type_button:
								item = new ParamItemButton(this, ResourcesUtils.getReferenceString(this, parameterName), parameterSummary);
								item.setNameId(ResourcesUtils.referenceToInt(parameterName));
								break;
							case R.id.parameter_type_toggle_button:
								item = new ParamItemToggleButton(this, ResourcesUtils.getReferenceString(this, parameterName), parameterSummary);
								item.setNameId(ResourcesUtils.referenceToInt(parameterName));
								break;
							case R.id.parameter_type_spinner:
								try {
									item = new ParamItemSpinner(this, ResourcesUtils.getReferenceString(this, parameterName), parameterSummary,parameterStrValue,parameterIndex);
									item.setNameId(ResourcesUtils.referenceToInt(parameterName));									
								} catch (ParseException e) {
									throw new InvalidAlgorithmParameterException("Wrong spinner parameter attributes");
								}								
								break;
							default: throw new InvalidAlgorithmParameterException("Unknown parameter type");
							}
							if (item != null) {
								page.addParamItem(item);
								item = null;
							}
						}
					}
					break;
				case XmlPullParser.TEXT:					
					break;
				default:
					break;
				}
				xpp.next();
			}
	    	} catch (XmlPullParserException e) {
	    		e.printStackTrace();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
		
	}
	
	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		if (fragment instanceof ParamsPageFragment) {
			((ParamsPageFragment) fragment).setListAdapter(new ParamItemsAdapter(pages.get(((ParamsPageFragment) fragment).getNum()).getItems()));
			((ParamsPageFragment) fragment).setOnItemClickListener(this);
		}
	}	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		Log.d(LOG_TAG, "onCreate");
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		uploadImmediatelly = sharedPref.getBoolean(getString(R.string.pref_upload_immediately), false);
		setContentView(R.layout.activity_param);
		
		try {
			createFormFromXml(R.xml.parameters);
		} catch (InvalidAlgorithmParameterException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
					
		paramAdapter = new ParamPagerAdapter(getSupportFragmentManager(),this,pages);
		progressBar = (ProgressBar)findViewById(R.id.paramsProgressBar);
		readParams();
				
		receiver = new ReceiveMessages();
		textViewStatus = (TextView) findViewById(R.id.paramsTextViewStatus);
		pager = (ViewPager)findViewById(R.id.paramsPager);
		pager.setAdapter(paramAdapter);
		
		super.onCreate(savedInstanceState);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_param, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_params_download:
	    	progressBar.setIndeterminate(true);
	    	progressBar.setVisibility(ProgressBar.VISIBLE);	    	
			readParams();
			return true;
		case R.id.menu_params_upload:
			/*if (isValid()) {
			if (false) {
				try {
			    	progressBar.setIndeterminate(true);
			    	progressBar.setVisibility(ProgressBar.VISIBLE);					
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PROGRESS, PARAMS_NUMBER));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, starterParPage.getData()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, anglesParPage.getData()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, idlRegParPage.getData()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, funsetParPage.getData()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, temperParPage.getData()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, carburParPage.getData()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, adcCorParPage.getData()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, ckpsParPage.getData()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, miscelParPage.getData()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, chokeParPage.getData()));
				} catch (Exception e) {
					Log.d (LOG_TAG, e.toString());
				}
			}*/
			return true;
		case R.id.menu_params_save_eeprom:
			try {
		    	progressBar.setIndeterminate(true);
		    	progressBar.setVisibility(ProgressBar.VISIBLE);				
				startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, new OpCompNc(Secu3Dat.OPCODE_EEPROM_PARAM_SAVE,0)));
			} catch (Exception e) {
				Log.d (LOG_TAG, e.toString());
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}        
    }

	@Override
	protected void onPause() {
		super.onPause();		
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
			
		}		
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		
		Log.d(LOG_TAG, "onResume");
		
		pager = (ViewPager)findViewById(R.id.paramsPager);
		pager.setAdapter(paramAdapter);
		paramAdapter.notifyDataSetChanged();
		
		dialog = (CustomNumberPickerDialog)getLastCustomNonConfigurationInstance();
		if (dialog != null) {
			dialog.setOnCustomNumberPickerAcceprListener(this);
		}			
		
		isOnline = false;
		
		try {
			registerReceiver(receiver, receiver.intentFilter);
			
		} catch (Exception e) {
		}		
	}	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		adapter = (ParamItemsAdapter) parent.getAdapter();
		if (adapter != null) {
			this.position = position;
			BaseParamItem i = (BaseParamItem) adapter.getItem(position);
			if (i instanceof ParamItemInteger) {
				dialog = new CustomNumberPickerIntegerDialog();
		        ((CustomNumberPickerIntegerDialog) dialog).setRange(((ParamItemInteger) i).getValue(), ((ParamItemInteger) i).getMinValue(), ((ParamItemInteger) i).getMaxValue(), ((ParamItemInteger) i).getStepValue());
		        dialog.setOnCustomNumberPickerAcceprListener(this);
		        dialog.show(getSupportFragmentManager(), i.getName());			        
			} else if (i instanceof ParamItemFloat) {
				dialog = new CustomNumberPickerFloatDialog();
				((CustomNumberPickerFloatDialog) dialog).setRange(((ParamItemFloat) i).getValue(), ((ParamItemFloat) i).getMinValue(), ((ParamItemFloat) i).getMaxValue(), ((ParamItemFloat) i).getStepValue());
				((CustomNumberPickerFloatDialog) dialog).setFormat (((ParamItemFloat) i).getFormat());
				dialog.setOnCustomNumberPickerAcceprListener(this);
		        dialog.show(getSupportFragmentManager(), i.getName());				
			} else if (i instanceof ParamItemBoolean) {
				adapter.setValue(String.valueOf(!((ParamItemBoolean) i).getValue()), position);
			}
		}
	}
	
	@Override
	public void setValue(String value, int position) {
		if (adapter != null) adapter.setValue(value, this.position);
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return dialog;
	}	
	
	void setIntegerItem (int id, int value) {
		BaseParamItem item;
		if ((item = paramAdapter.findItemByNameId(id)) != null ) ((ParamItemInteger)item).setValue (value);
	}
	
	void setFloatItem (int id, float value) {
		BaseParamItem item;
		if ((item = paramAdapter.findItemByNameId(id)) != null ) ((ParamItemFloat)item).setValue (value);
	}
	
	void setBooleanItem (int id, boolean value) {
		BaseParamItem item;
		if ((item = paramAdapter.findItemByNameId(id)) != null ) ((ParamItemBoolean)item).setValue (value);
	}
	
	void setSpinnerItemIndex (int id, int index) {
		BaseParamItem item;
		if ((item = paramAdapter.findItemByNameId(id)) != null ) ((ParamItemSpinner)item).setIndex(index);
	}

	void setSpinnerItemValue (int id, String value) {
		BaseParamItem item;
		if ((item = paramAdapter.findItemByNameId(id)) != null ) ((ParamItemSpinner)item).setValue(value);
	}	

	void update (Intent intent) {
		if (Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE.equals(intent.getAction())) {
			boolean isOnline = intent.getBooleanExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS,false);
			if (isOnline && !this.isOnline) {
				this.isOnline = true;
				readParams();
			}
			textViewStatus.setText(isOnline?R.string.status_online:R.string.status_offline);
		} else if (Secu3Dat.RECEIVE_STARTER_PAR.equals(intent.getAction())) {
			StartrPar packet = intent.getParcelableExtra(StartrPar.class.getCanonicalName());
			setIntegerItem(R.string.starter_off_title, packet.starter_off);
			setIntegerItem(R.string.starter_map_abandon_title, packet.smap_abandon);
		} else if (Secu3Dat.RECEIVE_ANGLES_PAR.equals(intent.getAction())) {			
			AnglesPar packet = intent.getParcelableExtra(AnglesPar.class.getCanonicalName());
			setFloatItem (R.string.angles_min_angle_title,packet.min_angle);
			setFloatItem (R.string.angles_max_angle_title,packet.max_angle);
			setFloatItem (R.string.angles_angle_decrement_step_title,packet.dec_spead);
			setFloatItem (R.string.angles_angle_increment_step_title,packet.inc_spead);
			setBooleanItem(R.string.angles_zero_angle_title,packet.zero_adv_ang == 1);
			setFloatItem (R.string.angles_octane_correction_title,packet.angle_corr);
		} else if (Secu3Dat.RECEIVE_IDLREG_PAR.equals(intent.getAction())) {
			IdlRegPar packet = intent.getParcelableExtra(IdlRegPar.class.getCanonicalName());
			setFloatItem (R.string.idlreg_ifac1_title,packet.ifac1);
			setFloatItem (R.string.idlreg_ifac2_title,packet.ifac2);
			setFloatItem (R.string.idlreg_minimal_angle_title,packet.min_angle);
			setFloatItem (R.string.idlreg_maximal_angle_title,packet.max_angle);
			setIntegerItem (R.string.idlreg_target_rpm_title,packet.idling_rpm);
			setIntegerItem (R.string.idlreg_rpm_sensitivity_title,packet.MINEFR);
			setFloatItem (R.string.idlreg_turn_on_temp_title,packet.turn_on_temp);
			setBooleanItem(R.string.idlreg_use_idle_regulator_title,packet.idl_regul == 1);
		} else if (Secu3Dat.RECEIVE_FNNAME_DAT.equals(intent.getAction())) {
			FnNameDat packet = intent.getParcelableExtra(FnNameDat.class.getCanonicalName());
			if (packet.names_available()) {
				String[] tableNames = Arrays.copyOf(packet.names,packet.names.length);
				String data = "";
				for (int i=0; i != tableNames.length-1; i++) {
					data += tableNames[i] + "|";
				}
				data += tableNames[tableNames.length - 1];
				setSpinnerItemValue(R.string.funset_maps_set_gasoline_title, data);
				setSpinnerItemValue(R.string.funset_maps_set_gas_title, data);
			}			
		}else if (Secu3Dat.RECEIVE_FUNSET_PAR.equals(intent.getAction())) {
			FunSetPar packet = intent.getParcelableExtra(FunSetPar.class.getCanonicalName());
			setSpinnerItemIndex(R.string.funset_maps_set_gasoline_title, packet.fn_benzin);
			setSpinnerItemIndex(R.string.funset_maps_set_gas_title, packet.fn_gas);
			setFloatItem(R.string.funset_lower_pressure_title, packet.map_lower_pressure);
			setFloatItem(R.string.funset_upper_pressure_title, packet.map_upper_pressure);
			setFloatItem(R.string.funset_map_sensor_offset_title, packet.map_curve_offset);
			setFloatItem(R.string.funset_map_sensor_gradient_title, packet.map_curve_gradient);
			setFloatItem(R.string.funset_tps_curve_offset_title, packet.tps_curve_offset);
			setFloatItem(R.string.funset_tps_curve_gradient_title, packet.tps_curve_gradient);
		} else if (Secu3Dat.RECEIVE_TEMPER_PAR.equals(intent.getAction())) {
			TemperPar packet = intent.getParcelableExtra(TemperPar.class.getCanonicalName());
			setFloatItem(R.string.temper_fan_on_title, packet.vent_on);
			setFloatItem(R.string.temper_fan_off_title, packet.vent_off);
			setBooleanItem(R.string.temper_use_temp_sensor_title, packet.tmp_use == 1);
			setBooleanItem(R.string.temper_use_pwm_title, packet.vent_pwm == 1);
			setBooleanItem(R.string.temper_use_table, packet.cts_use_map == 1);
		} else if (Secu3Dat.RECEIVE_CARBUR_PAR.equals(intent.getAction())) {
			CarburPar packet = intent.getParcelableExtra(CarburPar.class.getCanonicalName());
			setIntegerItem(R.string.carbur_overrun_lower_threshold_gasoline_title, packet.ephh_lot);
			setIntegerItem(R.string.carbur_overrun_upper_threshold_gasoline_title, packet.ephh_hit);
			setIntegerItem(R.string.carbur_overrun_lower_threshold_gas_title, packet.ephh_lot_g);
			setIntegerItem(R.string.carbur_overrun_upper_threshold_gas_title, packet.ephh_hit_g);
			setFloatItem(R.string.carbur_overrun_valve_delay, packet.shutoff_delay);
			setBooleanItem(R.string.carbur_sensor_inverse_title, packet.carb_invers == 1);
			setFloatItem(R.string.carbur_epm_valve_on_pressure_title, packet.epm_ont);
			setFloatItem(R.string.carbur_tps_threshold_title, packet.tps_threshold);
		} else if (Secu3Dat.RECEIVE_ADCCOR_PAR.equals(intent.getAction())) {
			ADCCorPar packet = intent.getParcelableExtra(ADCCorPar.class.getCanonicalName());
			setFloatItem(R.string.adccor_map_sensor_factor_title, packet.map_adc_factor);
			setFloatItem(R.string.adccor_map_sensor_correction_title, packet.map_adc_correction);
			setFloatItem(R.string.adccor_voltage_sensor_factor_title, packet.ubat_adc_factor);
			setFloatItem(R.string.adccor_voltage_sensor_correction_title, packet.ubat_adc_correction);
			setFloatItem(R.string.adccor_temper_sensor_factor_title, packet.temp_adc_factor);
			setFloatItem(R.string.adccor_temper_sensor_correction, packet.temp_adc_correction);
			setFloatItem(R.string.adccor_tps_sensor_factor_title, packet.tps_adc_factor);
			setFloatItem(R.string.adccor_tps_sensor_correction_title, packet.tps_adc_correction);
			setFloatItem(R.string.adccor_addi1_sensor_factor_title, packet.add_i1_factor);
			setFloatItem(R.string.adccor_addi1_sensor_correction_title, packet.add_i1_correction);
			setFloatItem(R.string.adccor_addi2_sensor_factor_title, packet.add_i2_factor);
			setFloatItem(R.string.adccor_addi2_sensor_correction_title, packet.add_i2_correction);
		} else if (Secu3Dat.RECEIVE_CKPS_PAR.equals(intent.getAction())) {
			CKPSPar packet = intent.getParcelableExtra(CKPSPar.class.getCanonicalName());
			setSpinnerItemIndex(R.string.ckps_ckp_edge_title, packet.ckps_edge_type);
			setSpinnerItemIndex(R.string.ckps_ref_s_edge_title, packet.ref_s_edge_type);
			setBooleanItem(R.string.ckps_merge_outputs, packet.ckps_merge_ign_outs == 1);
			setIntegerItem(R.string.ckps_cogs_number_title, packet.ckps_cogs_num);
			setIntegerItem(R.string.ckps_missing_cogs_number_title, packet.ckps_miss_num);
			setIntegerItem(R.string.ckps_cogs_before_tdc_title, packet.ckps_cogs_btdc);
			setIntegerItem(R.string.ckps_engine_cylynders_title, packet.ckps_engine_cyl);
			setIntegerItem(R.string.ckps_ignition_pulse_delay_title, packet.ckps_ignit_cogs);
		} else if (Secu3Dat.RECEIVE_MISCEL_PAR.equals(intent.getAction())) {
			MiscelPar packet = intent.getParcelableExtra(MiscelPar.class.getCanonicalName());
			setSpinnerItemIndex(R.string.miscel_baudrate_title, Secu3Dat.indexOf (Secu3Dat.BAUD_RATE_INDEX,((MiscelPar)packet).baud_rate_index));
			setIntegerItem(R.string.miscel_period_title, packet.period_ms);
			setBooleanItem(R.string.miscel_ignition_cutoff_title, packet.ign_cutoff == 1);
			setIntegerItem(R.string.miscel_ignition_cutoff_rpm_title, packet.ign_cutoff_thrd);
			setIntegerItem(R.string.miscel_hall_output_start_title, packet.hop_start_cogs);
			setIntegerItem(R.string.miscel_hall_output_delay_title, packet.hop_durat_cogs);
		} else if (Secu3Dat.RECEIVE_CHOKE_PAR.equals(intent.getAction())) {
			ChokePar packet = intent.getParcelableExtra(ChokePar.class.getCanonicalName());
			setIntegerItem(R.string.choke_steps_title, packet.steps);
		}
		else if (Secu3Service.EVENT_SECU3_SERVICE_PROGRESS.equals(intent.getAction())) {
			int current = intent.getIntExtra(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS_CURRENT,0);
			int total = intent.getIntExtra(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS_TOTAL,0);
			if (current == total) {
				progressBar.setVisibility(ProgressBar.GONE);
			}
			progressBar.setIndeterminate(current==0);
			progressBar.setMax(total);
			progressBar.setProgress(current);
		} else if (Secu3Dat.RECEIVE_OP_COMP_NC.equals(intent.getAction())) {
			OpCompNc packet = intent.getParcelableExtra(OpCompNc.class.getCanonicalName());
			if ((packet != null) && (packet.opcode == Secu3Dat.OPCODE_EEPROM_PARAM_SAVE)) {
				progressBar.setVisibility(ProgressBar.GONE);				
				Toast.makeText(this, String.format("Params saved: error code %d", packet.opdata), Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onDataChanged(Fragment fragment, Secu3Dat packet) {
		if ((packet != null) && (uploadImmediatelly || (fragment == chokeParPage))) {
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packet));
		}
	}
	
	@Override
	// This is bugfix of http://stackoverflow.com/questions/13910826/viewpager-fragmentstatepageradapter-orientation-change
	protected void onSaveInstanceState(Bundle outState) {
	//	super.onSaveInstanceState(outState);
	}
}

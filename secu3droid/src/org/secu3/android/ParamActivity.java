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
import java.text.ParseException;
import java.util.ArrayList;

import org.secu3.android.api.io.*;
import org.secu3.android.api.io.Secu3Dat.ChokePar;
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;
import org.secu3.android.api.io.Secu3Dat.*;
import org.secu3.android.api.utils.CustomNumberPickerDialog;
import org.secu3.android.api.utils.CustomNumberPickerDialog.OnNumberPickerDialogAcceptListener;
import org.secu3.android.api.utils.CustomNumberPickerFloatDialog;
import org.secu3.android.api.utils.CustomNumberPickerIntegerDialog;
import org.secu3.android.api.utils.PacketUtils;
import org.secu3.android.api.utils.ResourcesUtils;
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
import org.secu3.android.parameters.items.ParamItemToggleButton;
import org.secu3.android.parameters.items.BaseParamItem.OnParamItemChangeListener;
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

public class ParamActivity extends FragmentActivity implements OnItemClickListener,OnNumberPickerDialogAcceptListener,OnParamItemChangeListener {
	public static final String LOG_TAG = "ParamActivity";	
	
	public static final int PARAMS_NUMBER = 9;	
	
	ProgressBar progressBar = null;
		
	ArrayList<ParamsPage> pages;
	TextView textViewStatus = null;
	TextView textView = null;
	ViewPager pager = null;
    ParamPagerAdapter paramAdapter = null;
	ParamItemsAdapter adapter = null;
    int position = Integer.MAX_VALUE;
	CustomNumberPickerDialog dialog = null;
	boolean isValid = false;

    SharedPreferences sharedPref = null;
	private boolean isOnline = false;
	private boolean uploadImmediatelly;
		
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return dialog;
	}		
	    
    private void paramsRead() {
    	isValid = false;
    	startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_READ_PARAMS.ordinal()));
    	startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_READ_SENSORS.ordinal()));
    }
    
	public class ReceiveMessages extends BroadcastReceiver 
	{
		public IntentFilter intentFilter = null;
		
		public ReceiveMessages() {
			intentFilter = new IntentFilter();
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET);
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

	public void createFormFromXml (int id){
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
						if (pages.size() != 0) throw new IllegalArgumentException("Pages adapter is non empty, probably nested Parameter element"); 
					} else					
					// Found new page element
					if (name.equalsIgnoreCase("Page")) {
						if (page != null) {
							throw new IllegalArgumentException("Pages can't be nested");
						}
						int count = xpp.getAttributeCount(); 
						if (count > 0) {
							for (int i = 0; i != count; i++) {
								attr  = xpp.getAttributeName(i);
								attrValue = xpp.getAttributeValue(i);
								if (attr.equals("name")) {
									if (ResourcesUtils.isResource(attrValue)) {
										page = new ParamsPage(ResourcesUtils.referenceToInt(attrValue));
									} else throw new IllegalArgumentException("Page name must be a string reference");									
									pages.add(page);
								}
							}
						}						
					} else
					// Found new parameter element
					if (name.equalsIgnoreCase("Parameter")){
						if (item != null) {
							throw new IllegalArgumentException("Parameters can't be nested");
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
									} else throw new IllegalArgumentException("Parameter name must be a string reference");									
								} else if (attr.equalsIgnoreCase("summary")) {
									parameterSummary = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								} else									
								if (attr.equalsIgnoreCase("units")) {
									parameterUnits = (ResourcesUtils.isResource(attrValue))?ResourcesUtils.getReferenceString(this,attrValue):attrValue;
								} else
								if (attr.equalsIgnoreCase("type")) {
									if (ResourcesUtils.isResource(attrValue)) {
										parameterType = ResourcesUtils.referenceToInt(attrValue);
									} else throw new IllegalArgumentException("Parameter type must be a reference");									
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
						if (pages.size() == 0) throw new IllegalArgumentException("Parameters closed, but not opened");
					} else				
					if (name.equalsIgnoreCase("Page")) {
						if (page == null) throw new IllegalArgumentException("Page closed, but not opened");
						else page = null;
					} else
					if (name.equalsIgnoreCase("Parameter")) {
						if ((parameterName == null) || (parameterType == 0) || (TextUtils.isEmpty(parameterName))) throw new IllegalArgumentException("Parameter element is invalid");
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
									throw new IllegalArgumentException("Wrong integer parameter attributes");
								}
								break;
							case R.id.parameter_type_float:
								try {
									item = new ParamItemFloat(this, ResourcesUtils.getReferenceString(this, parameterName), parameterSummary,
											parameterUnits, parameterValue, parameterMinValue, parameterMaxValue, parameterStepValue);
									item.setNameId(ResourcesUtils.referenceToInt(parameterName));	
									((ParamItemFloat) item).setFormat (parameterMasFormat);
								} catch (ParseException e) {
									throw new IllegalArgumentException("Wrong integer parameter attributes");
								}								
								break;
							case R.id.parameter_type_label:
								item = new ParamItemLabel(this, ResourcesUtils.getReferenceString(this, parameterName), parameterSummary);
								item.setNameId(ResourcesUtils.referenceToInt(parameterName));
								break;
							case R.id.parameter_type_button:
								item = new ParamItemButton(this, ResourcesUtils.getReferenceString(this, parameterName), parameterSummary,parameterUnits);
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
									throw new IllegalArgumentException("Wrong spinner parameter attributes");
								}								
								break;
							default: throw new IllegalArgumentException("Unknown parameter type");
							}
							if (item != null) {
								item.setOnParamItemChangeListener(this);								
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
		
		createFormFromXml(R.xml.parameters);
				
		paramAdapter = new ParamPagerAdapter(getSupportFragmentManager(),this,pages);
		progressBar = (ProgressBar)findViewById(R.id.paramsProgressBar);
		paramsRead();
				
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
			paramsRead();
			return true;
		case R.id.menu_params_upload:
			if (isValid) {
				paramsUpload();
			}
			return true;
		case R.id.menu_params_save_eeprom:
	    	progressBar.setIndeterminate(true);
	    	progressBar.setVisibility(ProgressBar.VISIBLE);				
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, new OpCompNc(Secu3Dat.OPCODE_EEPROM_PARAM_SAVE,0)));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}        
    }

	private void paramsUpload() {
		progressBar.setIndeterminate(true);
		progressBar.setVisibility(ProgressBar.VISIBLE);					
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PROGRESS, PARAMS_NUMBER));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, Secu3Dat.STARTR_PAR)));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, Secu3Dat.ANGLES_PAR)));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, Secu3Dat.IDLREG_PAR)));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, Secu3Dat.FUNSET_PAR)));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, Secu3Dat.TEMPER_PAR)));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, Secu3Dat.CARBUR_PAR)));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, Secu3Dat.ADCCOR_PAR)));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, Secu3Dat.CKPS_PAR)));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, Secu3Dat.MISCEL_PAR)));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, Secu3Dat.CHOKE_PAR)));
	}

	@Override
	protected void onPause() {
		super.onPause();		
		unregisterReceiver(receiver);
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
			dialog.setOnCustomNumberPickerAcceptListener(this);
		}			
		
		isOnline = false;
		
		registerReceiver(receiver, receiver.intentFilter);
	}	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		adapter = (ParamItemsAdapter) parent.getAdapter();
		if (adapter != null) {
			this.position = position;
			BaseParamItem i = (BaseParamItem) adapter.getItem(position);
			if (i instanceof ParamItemInteger) {
				dialog = new CustomNumberPickerIntegerDialog();
		        ((CustomNumberPickerIntegerDialog) dialog
		        .setId(i.getNameId()))
		        .setRange(((ParamItemInteger) i).getValue(), ((ParamItemInteger) i).getMinValue(), ((ParamItemInteger) i).getMaxValue(), ((ParamItemInteger) i).getStepValue())
		        .setOnCustomNumberPickerAcceptListener(this)
		        .show(getSupportFragmentManager(), i.getName());			        
			} else if (i instanceof ParamItemFloat) {
				dialog = new CustomNumberPickerFloatDialog();
				((CustomNumberPickerFloatDialog) dialog
				.setId(i.getNameId()))
				.setRange(((ParamItemFloat) i).getValue(), ((ParamItemFloat) i).getMinValue(), ((ParamItemFloat) i).getMaxValue(), ((ParamItemFloat) i).getStepValue())
				.setFormat (((ParamItemFloat) i).getFormat())
				.setOnCustomNumberPickerAcceptListener(this)				
		        .show(getSupportFragmentManager(), i.getName());				
			} else if (i instanceof ParamItemBoolean) {
				adapter.setValue(String.valueOf(!((ParamItemBoolean) i).getValue()), position);
				onItemChange(i.getNameId());
			}
		}
	}	
	
	@Override
	public void onNumberPickerDialogAccept(int itemId) {
		adapter.setValue(dialog.getValue(),position);
		onItemChange(itemId);
	}

	public void onItemChange(int itemId) {
		if (PacketUtils.isParamFromPage(itemId, R.string.choke_control_title)) {
			ChokePar packet = (ChokePar) PacketUtils.build(paramAdapter, Secu3Dat.CHOKE_PAR);
			switch (itemId) {
			case R.string.choke_manual_step_down:
				packet.manual_delta = -127;
				break;
			case R.string.choke_manual_step_up:
				packet.manual_delta = 127;
				break;
			case R.string.choke_testing_title:
				packet.testing = ((ParamItemToggleButton) paramAdapter.findItemByNameId(itemId)).getValue()?1:0;
				break;				
			default:
				break;
			}
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packet));			
		} else if (uploadImmediatelly) {
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, PacketUtils.build(paramAdapter, itemId)));			
		}
	}
	
	@Override
	public void onParamItemChange(BaseParamItem item) {
		if (item != null) onItemChange(item.getNameId());		
	}	
			
	void update (Intent intent) {
		if (Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE.equals(intent.getAction())) {
			boolean isOnline = intent.getBooleanExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS,false);
			if (isOnline && !this.isOnline) {
				this.isOnline = true;
				paramsRead();
			}
			textViewStatus.setText(isOnline?R.string.status_online:R.string.status_offline);
		} else if (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET.equals(intent.getAction())) {
			Secu3Dat packet = intent.getParcelableExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET);
			if (packet instanceof OpCompNc) {
				if (((OpCompNc)packet).opcode == Secu3Dat.OPCODE_EEPROM_PARAM_SAVE) {
					progressBar.setVisibility(ProgressBar.GONE);				
					Toast.makeText(this, String.format("Params saved: error code %d", ((OpCompNc)packet).opdata), Toast.LENGTH_LONG).show();
				}
			} else PacketUtils.setDataFromPacket(paramAdapter, packet);
		} else if (Secu3Service.EVENT_SECU3_SERVICE_PROGRESS.equals(intent.getAction())) {
			int current = intent.getIntExtra(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS_CURRENT,0);
			int total = intent.getIntExtra(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS_TOTAL,0);
			progressBar.setIndeterminate(current==0);
			progressBar.setMax(total);
			progressBar.setProgress(current);
			if (current == total) {
				progressBar.setVisibility(ProgressBar.GONE);
				isValid = true;
			}			
		}
	}

	
	@Override
	// This is bugfix of http://stackoverflow.com/questions/13910826/viewpager-fragmentstatepageradapter-orientation-change
	protected void onSaveInstanceState(Bundle outState) {
	//	super.onSaveInstanceState(outState);
	}
}

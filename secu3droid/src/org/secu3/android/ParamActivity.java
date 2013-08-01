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
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;
import org.secu3.android.api.utils.*;
import org.secu3.android.api.utils.CustomNumberPickerDialog.OnNumberPickerDialogAcceptListener;
import org.secu3.android.parameters.*;
import org.secu3.android.parameters.items.*;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ParamActivity extends FragmentActivity implements OnItemClickListener,OnNumberPickerDialogAcceptListener,OnParamItemChangeListener {
	public static final int PARAMS_NUMBER = 9;
	
	private static final String PAGE = "page";
	
    private int position = Integer.MAX_VALUE;    
	private boolean isOnline = false;
	private boolean uploadImmediatelly = false;
	private boolean isValid = false;
	
	private SharedPreferences sharedPref = null;
	private ArrayList<ParamsPage> pages = null;
	private ProgressBar progressBar = null;	
	private TextView textViewStatus = null;
	private ViewPager pager = null;
    private ParamPagerAdapter paramAdapter = null;
	private ParamItemsAdapter adapter = null;
	private CustomNumberPickerDialog dialog = null;	
	private ReceiveMessages receiver = null;
	
	private Secu3Packet OpCompNcSkeleton = null;
	private Secu3Packet ChokeControlSkeleton = null;
	private PacketUtils packetUtils = null;
		    
	public class ReceiveMessages extends BroadcastReceiver 
	{
		public IntentFilter intentFilter = null;
		
		public ReceiveMessages() {
			intentFilter = new IntentFilter();
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAMETER);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET);
		}
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{    
			update(intent);	   	    
		}
	}

    private void paramsRead() {
    	progressBar.setVisibility(ProgressBar.VISIBLE);
    	startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_READ_PARAMS.ordinal()));
    	startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SET_TASK,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SET_TASK_PARAM, SECU3_TASK.SECU3_READ_SENSORS.ordinal()));
    }
    
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return dialog;
	}	
	
	public void createFormFromXml (int xmlId){
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
			XmlPullParser xpp = getResources().getXml(xmlId);
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
								item.setPageId(page.getNameId());
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
		if (fragment instanceof ParamsPageFragment) {
			((ParamsPageFragment) fragment).setListAdapter(new ParamItemsAdapter(pages.get(((ParamsPageFragment) fragment).getNum()).getItems()));
			((ParamsPageFragment) fragment).setOnItemClickListener(this);
		}
		super.onAttachFragment(fragment);		
	}	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {				
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		uploadImmediatelly = sharedPref.getBoolean(getString(R.string.pref_upload_immediately_key), false);
		setTheme(sharedPref.getBoolean(getString(R.string.pref_night_mode_key), false)?R.style.AppBaseTheme:R.style.AppBaseTheme_Light);
		setContentView(R.layout.activity_param);
		
		createFormFromXml(R.xml.parameters);
				
		packetUtils = new PacketUtils();
		paramAdapter = new ParamPagerAdapter(getSupportFragmentManager(),this,pages);
		progressBar = (ProgressBar)findViewById(R.id.paramsProgressBar);
		paramsRead();
				
		receiver = new ReceiveMessages();
		textViewStatus = (TextView) findViewById(R.id.paramsTextViewStatus);
		pager = (ViewPager)findViewById(R.id.paramsPager);
		pager.setAdapter(paramAdapter);
		
		if (savedInstanceState != null) {
			pager.setCurrentItem(savedInstanceState.getInt(PAGE));
		}
		
    	isValid = false;
		
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
	    	Secu3Packet packet = new Secu3Packet(OpCompNcSkeleton);
	    	((ProtoFieldInteger) packet.findField(R.string.op_comp_nc_operation_title)).setValue (Secu3Packet.OPCODE_EEPROM_PARAM_SAVE);
	    	((ProtoFieldInteger) packet.findField(R.string.op_comp_nc_operation_title)).setValue (0);
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packet));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}        
    }

	private void paramsUpload() {
		progressBar.setIndeterminate(true);
		progressBar.setVisibility(ProgressBar.VISIBLE);					
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PROGRESS, PARAMS_NUMBER));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(R.string.starter_title))));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(R.string.angles_title))));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(R.string.idling_title))));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(R.string.functions_title))));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(R.string.temperature_title))));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(R.string.carburetor_title))));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(R.string.adc_errors_title))));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(R.string.ckps_title))));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(R.string.miscellaneous_title))));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(R.string.choke_control_title))));
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();				
	}
		
	@Override
	protected void onResume() {
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON_PARAM, R.string.op_comp_nc_title));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON_PARAM, R.string.choke_control_title));
		dialog = (CustomNumberPickerDialog)getLastCustomNonConfigurationInstance();
		if (dialog != null) {
			dialog.setOnCustomNumberPickerAcceptListener(this);
		}							
		registerReceiver(receiver, receiver.intentFilter);
		super.onResume();		
	}	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		adapter = (ParamItemsAdapter) parent.getAdapter();
		if (adapter != null) {
			this.position = position;
			BaseParamItem i = (BaseParamItem) adapter.getItem(position);
			if (i instanceof ParamItemInteger) {
				dialog = new CustomNumberPickerIntegerDialog();
				dialog.setValid(isValid);
		        ((CustomNumberPickerIntegerDialog) dialog
		        .setId(i.getNameId()))
		        .setRange(((ParamItemInteger) i).getValue(), ((ParamItemInteger) i).getMinValue(), ((ParamItemInteger) i).getMaxValue(), ((ParamItemInteger) i).getStepValue())
		        .setOnCustomNumberPickerAcceptListener(this)
		        .show(getSupportFragmentManager(), i.getName());			        
			} else if (i instanceof ParamItemFloat) {
				dialog = new CustomNumberPickerFloatDialog();
				dialog.setValid(isValid);
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
		BaseParamItem item = paramAdapter.findItemByNameId(itemId);
		if ((item != null) && (item.getPageId() == R.string.choke_control_title)) {
			Secu3Packet packet = new Secu3Packet (ChokeControlSkeleton); 
			switch (itemId) {
			case R.string.choke_manual_step_down_title:
				((ProtoFieldInteger) packet.findField(R.string.choke_steps_title)).setValue (-127);
				break;
			case R.string.choke_manual_step_up_title:
				((ProtoFieldInteger) packet.findField(R.string.choke_steps_title)).setValue (127);
				break;
			case R.string.choke_testing_title:
				((ProtoFieldInteger) packet.findField(R.string.choke_testing_title)).setValue (((ParamItemToggleButton) item).getValue()?1:0);
				break;				
			default:
				break;
			}
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packet));			
		} else if (uploadImmediatelly) {
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packetUtils.buildPacket(paramAdapter, getString(item.getPageId()))));
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
			if (!isOnline) {
				isValid = false;
			}
			String s = isOnline?getString(R.string.status_online):getString(R.string.status_offline);
			textViewStatus.setText(s);
		} else if (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET.equals(intent.getAction())) {
			Secu3Packet packet = intent.getParcelableExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET);
			if (packet.getNameId() == R.string.op_comp_nc_title) {
				if (((ProtoFieldInteger) packet.getField(R.string.op_comp_nc_operation_title)).getValue() == Secu3Packet.OPCODE_EEPROM_PARAM_SAVE) {
					progressBar.setVisibility(ProgressBar.GONE);				
					Toast.makeText(this, String.format(getString(R.string.params_saved_error_code), ((ProtoFieldInteger) packet.getField(R.string.op_comp_nc_operation_code_title)).getValue()), Toast.LENGTH_LONG).show();
				}
			} else {
				packetUtils.setParamFromPacket(paramAdapter, packet);
			}
		} else if (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_SKELETON_PACKET.equals(intent.getAction())) {
			Secu3Packet packet = intent.getParcelableExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET);
			if (packet.getNameId() == R.string.op_comp_nc_title) OpCompNcSkeleton = packet;
			else if (packet.getNameId() == R.string.choke_control_title) ChokeControlSkeleton = packet;
		} else if (Secu3Service.EVENT_SECU3_SERVICE_PROGRESS.equals(intent.getAction())) {
			int current = intent.getIntExtra(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS_CURRENT,0);
			int total = intent.getIntExtra(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS_TOTAL,0);
			progressBar.setIndeterminate(current==0);
			progressBar.setMax(total);
			progressBar.setProgress(current);
			if (current == total) {
				startService(new Intent(Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PARAMETER,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PARAMETER_ID, Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PARAMETER_FUNSET_NAMES));
			}			
		} else if (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAMETER.equals(intent.getAction())) {
			String funsetNames[] = intent.getStringArrayExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAMETER_FUNSET_NAMES);
			if (funsetNames != null) {
				progressBar.setVisibility(ProgressBar.GONE);				
				PacketUtils.setFunsetNames(paramAdapter, funsetNames);
				paramAdapter.notifyDataSetChanged();				
				isValid = true;				
			}
		}
	}

	
	@Override
	// This is bugfix of http://stackoverflow.com/questions/13910826/viewpager-fragmentstatepageradapter-orientation-change
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(PAGE, pager.getCurrentItem());
	//	super.onSaveInstanceState(outState);
	}
}

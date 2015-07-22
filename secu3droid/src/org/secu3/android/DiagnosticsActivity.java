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

import java.util.ArrayList;
import java.util.List;

import org.secu3.android.api.io.ProtoFieldInteger;
import org.secu3.android.api.io.Secu3Packet;
import org.secu3.android.api.io.Secu3Service;
import org.secu3.android.api.utils.PacketUtils;
import org.secu3.android.parameters.ParamItemsAdapter;
import org.secu3.android.parameters.items.*;
import org.secu3.android.parameters.items.BaseParamItem.OnParamItemChangeListener;

import android.support.v4.app.ListFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

public class DiagnosticsActivity extends FragmentActivity implements OnItemClickListener, OnParamItemChangeListener{
	private static final String BLDEENABLED = "BLDEENABLED";
	private static final String PAGE = "page";
	private static final String OUTPUTS = "outputs";	

	private boolean isOnline = false;
	
	private ListFragment inputFragment = null;	
	private int protocolversion = SettingsActivity.PROTOCOL_UNKNOWN;
	private ArrayList<BaseParamItem> outputItems = null;
	private ViewPager pager = null;
	private ReceiveMessages receiver = null;
	private TextView textViewStatus = null;
	
	private Secu3Packet OpCompNc = null;
	private Secu3Packet DiagOutDat = null;
	private boolean BlDeDiagEnabled = false;	
	
	public static class OutputDiagListFragment extends ListFragment {
		OnItemClickListener listener;		
		
		public void setOnItemClickListener (OnItemClickListener listener) {
			this.listener = listener;
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			if (listener != null) listener.onItemClick(l, v, position, id);
			super.onListItemClick(l, v, position, id);
		}		
	}
		
	private class DiagnosticsPagerAdapter extends FragmentPagerAdapter{
		private List<Fragment> fragments = null;
		private final String titles[];
		
		public DiagnosticsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
			titles = getBaseContext().getResources().getStringArray(R.array.diagnostics_fragments_title);
		}
		
		@Override
		public Fragment getItem (int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}		

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}
	}
	
	public class ReceiveMessages extends BroadcastReceiver 
	{
		public IntentFilter intentFilter = null;
		
		public ReceiveMessages() {
			intentFilter = new IntentFilter();
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE);
			intentFilter.addAction(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_SKELETON_PACKET);
		}
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{    
			update(intent);	   	    
		}
	}
	
	private void setOutputs (int outputs) {
		for (int i = 0; i != outputItems.size()-(this.protocolversion < SettingsActivity.PROTOCOL_14012014_WINTER_RELEASE?0:2); i++) {
			((ParamItemBoolean) outputItems.get(i)).setValue((outputs & 0x01)!=0);
			outputs >>= 1;
		}
		if ((protocolversion >= SettingsActivity.PROTOCOL_14012014_WINTER_RELEASE) && (BlDeDiagEnabled)){
			((ParamItemBoolean) outputItems.get(outputItems.size()-3)).setValue(((outputs >> 11) == 0) &&((outputs >> 12)==1)); // BL
			((ParamItemBoolean) outputItems.get(outputItems.size()-1)).setValue(((outputs >> 13) == 0) &&((outputs >> 14)==1)); // DE state
		}
	}
	
	private int getOutputs () {
		int res = 0;
		for (int i = 0; i != outputItems.size()-(this.protocolversion < SettingsActivity.PROTOCOL_14012014_WINTER_RELEASE?0:2); i++) {
			if (((ParamItemBoolean) outputItems.get(i)).getValue()) res |= 0x01 << i; 
		}		
		if ((protocolversion >= SettingsActivity.PROTOCOL_14012014_WINTER_RELEASE)){
			res &= ~(0x0F << 11);
			if (BlDeDiagEnabled) {				
				res |= ((((ParamItemBoolean) outputItems.get(outputItems.size()-2)).getValue())?1:2)  << 11; // BL							
				res |= ((((ParamItemBoolean) outputItems.get(outputItems.size()-1)).getValue())?1:2)  << 13; // DE			
			}
		}
		return res;
	}
	
	private void createOutputs() {
		String outputNames[] = getResources().getStringArray(R.array.diagnostics_output_names);
		for (int i = 0; i != outputNames.length-(this.protocolversion < SettingsActivity.PROTOCOL_14012014_WINTER_RELEASE?2:0); i++) { // In 3 protocol version BL & DE testing added
			outputItems.add(new ParamItemBoolean(this, outputNames[i], null, false));
			outputItems.get(i).setOnParamItemChangeListener(this);
		}
	}	
	
	private void setBlDeEnabled(boolean BlDeEnabled) {
		this.BlDeDiagEnabled = BlDeEnabled; 
		if (protocolversion >= SettingsActivity.PROTOCOL_14012014_WINTER_RELEASE) {
			(outputItems.get(outputItems.size()-2)).setEnabled(BlDeEnabled);
			(outputItems.get(outputItems.size()-1)).setEnabled(BlDeEnabled);
			onParamItemChange (null);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_night_mode_key), false) ? R.style.AppBaseTheme : R.style.AppBaseTheme_Light);
		setContentView(R.layout.activity_diagnostics);
		
		protocolversion = SettingsActivity.getProtocolVersion(this);
		List<Fragment> pages = new ArrayList<>();
		ArrayList<BaseParamItem> inputItems = new ArrayList<>();
		outputItems = new ArrayList<>();
		
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_voltage_title, 0, R.string.units_volts,0,0,100,0).setFormat("%.3f"));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_map_s, 0, R.string.units_volts,0,0,100,0).setFormat("%.3f"));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_temp, 0, R.string.units_volts,0,0,100,0).setFormat("%.3f"));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_add_io1, 0, R.string.units_volts,0,0,100,0).setFormat("%.3f"));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_add_io2, 0, R.string.units_volts,0,0,100,0).setFormat("%.3f"));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_carb_title, 0, R.string.units_volts,0,0,100,0).setFormat("%.3f"));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_gas_v, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_ckps, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_ref_s, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_ps, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_bl, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_de, 0, false,false));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_ks1_title, 0, R.string.units_volts,0,0,100,0).setFormat("%.3f"));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_ks2_title, 0, R.string.units_volts,0,0,100,0).setFormat("%.3f"));

		createOutputs();
		
		inputFragment = new ListFragment();
		inputFragment.setListAdapter(new ParamItemsAdapter(inputItems));
		OutputDiagListFragment outputsFragment = new OutputDiagListFragment();
		outputsFragment.setOnItemClickListener(this);
		outputsFragment.setListAdapter(new ParamItemsAdapter(outputItems));
		pages.add(inputFragment);
		pages.add(outputsFragment);

		DiagnosticsPagerAdapter diagnosticsAdapter = new DiagnosticsPagerAdapter(getSupportFragmentManager(),pages);
		receiver = new ReceiveMessages();
		textViewStatus = (TextView) findViewById(R.id.diagnosticsStatusTextView);		
		pager = (ViewPager)findViewById(R.id.diagnosticsPager);
		pager.setAdapter(diagnosticsAdapter);	
		
		if (savedInstanceState != null) {
			int outputs = savedInstanceState.getInt(OUTPUTS);
			setOutputs(outputs);
			boolean blde = savedInstanceState.getBoolean(BLDEENABLED);
			setBlDeEnabled(blde);
			int page = savedInstanceState.getInt(PAGE);
			pager.setCurrentItem(page);
		}
		
		CheckBox checkBoxEnableBlDeDiagnostics = (CheckBox) findViewById(R.id.diagnosticsEnableBlDe);
		checkBoxEnableBlDeDiagnostics.setVisibility((protocolversion >= SettingsActivity.PROTOCOL_14012014_WINTER_RELEASE)?View.VISIBLE:View.GONE);
		checkBoxEnableBlDeDiagnostics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {				
				setBlDeEnabled(isChecked);
			}
		});
		
		super.onCreate(savedInstanceState);		
	}

	@Override
	protected void onPause() {
		if (OpCompNc != null) {
			((ProtoFieldInteger) OpCompNc.findField(R.string.op_comp_nc_operation_title)).setValue (Secu3Packet.OPCODE_DIAGNOST_LEAVE);
			((ProtoFieldInteger) OpCompNc.findField(R.string.op_comp_nc_operation_code_title)).setValue (0);
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, OpCompNc));
		}
		unregisterReceiver(receiver);
		super.onPause();		
	}
	
	@Override
	// This is bugfix of http://stackoverflow.com/questions/13910826/viewpager-fragmentstatepageradapter-orientation-change
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(OUTPUTS, getOutputs());
		outState.putInt(PAGE, pager.getCurrentItem());
		outState.putBoolean(BLDEENABLED, BlDeDiagEnabled);
		//super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onResume() {					
		OpCompNc = null;
		DiagOutDat = null;
		registerReceiver(receiver, receiver.intentFilter);
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON_PARAM, R.string.op_comp_nc_title));
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_OBTAIN_PACKET_SKELETON_PARAM, R.string.diagout_dat_title));
		super.onResume();		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.diagnostics, menu);
		return false;		
	}

	private void update (Intent intent) {
		if (Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE.equals(intent.getAction())) {
			boolean isOnline = intent.getBooleanExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS,false);
			if (isOnline && !this.isOnline) {
				this.isOnline = true;
			}
			String s = isOnline?getString(R.string.status_online):getString(R.string.status_offline);
			textViewStatus.setText(s);
		} else if (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET.equals(intent.getAction()))
		{
			Secu3Packet packet = intent.getParcelableExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET);
			if (packet != null) {
				if (packet.getNameId() == R.string.diaginp_dat_title) {	
					PacketUtils.setDiagInpFromPacket((ParamItemsAdapter) inputFragment.getListAdapter(), packet);
					((ParamItemsAdapter) inputFragment.getListAdapter()).notifyDataSetChanged();
				} else
				if ((OpCompNc != null)&&(DiagOutDat != null)) {
					((ProtoFieldInteger) OpCompNc.findField(R.string.op_comp_nc_operation_title)).setValue(Secu3Packet.OPCODE_DIAGNOST_ENTER);
					((ProtoFieldInteger) OpCompNc.findField(R.string.op_comp_nc_operation_code_title)).setValue (0);
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, OpCompNc));
					onParamItemChange(null);
				}				
			}
		} else if (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_SKELETON_PACKET.equals(intent.getAction())) {
			Secu3Packet packet = intent.getParcelableExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_SKELETON_PACKET);
			if (packet != null) {
				if (packet.getNameId() == R.string.op_comp_nc_title) {
					OpCompNc = packet;
				}
				else if (packet.getNameId() == R.string.diagout_dat_title) {
					DiagOutDat = packet;
				}
			}
		}		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		ParamItemsAdapter adapter = (ParamItemsAdapter) parent.getAdapter();
		BaseParamItem i = (BaseParamItem) adapter.getItem(position);
		adapter.setValue(String.valueOf(!((ParamItemBoolean) i).getValue()), position);	
		onParamItemChange(i);
	}

	@Override
	public void onParamItemChange(BaseParamItem item) {
		if (DiagOutDat != null) {
			((ProtoFieldInteger) DiagOutDat.findField(R.string.diagout_dat_bitfield_title)).setValue(getOutputs());
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, DiagOutDat));
		}
	}
}

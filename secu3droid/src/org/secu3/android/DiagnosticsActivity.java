package org.secu3.android;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.ListView;
import android.widget.TextView;

public class DiagnosticsActivity extends FragmentActivity implements OnItemClickListener, OnParamItemChangeListener{
	private static final String PAGE = "page";
	private static final String OUTPUTS = "outputs";	

	private boolean isOnline = false;
	
	private ListFragment inputFragment = null;	
	private OutputDiagListFragment outputsFragment = null;
	private List<Fragment> pages = null;		
	private ArrayList<BaseParamItem> inputItems = null;
	private ArrayList<BaseParamItem> outputItems = null;		
	private DiagnosticsPagerAdapter diagnosticsAdapter = null;
	private ViewPager pager = null;
	private ReceiveMessages receiver = null;
	private TextView textViewStatus = null;	
	
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
		private String titles[];
		
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
		}
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{    
			update(intent);	   	    
		}
	}
	
	private void setOutputs (int outputs) {
		for (int i = 0; i != outputItems.size(); ++i) {
			((ParamItemBoolean) outputItems.get(i)).setValue(((outputs & 0x01)!=0)?true:false); 
			outputs >>= 1;
		}		
	}
	
	private int getOutputs () {
		int res = 0;
		for (int i = 0; i != outputItems.size(); ++i) {
			if (((ParamItemBoolean) outputItems.get(i)).getValue()) res |= 0x01 << i; 
		}
		return res;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_night_mode_key), false)?R.style.AppBaseTheme:R.style.AppBaseTheme_Light);
		setContentView(R.layout.activity_diagnostics);
		
		pages = new ArrayList<Fragment>();
		inputItems = new ArrayList<BaseParamItem>();
		outputItems = new ArrayList<BaseParamItem>();
		
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_voltage, 0, R.string.units_volts,0));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_map_s, 0, R.string.units_volts,0));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_temp, 0, R.string.units_volts,0));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_add_io1, 0, R.string.units_volts,0));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_add_io2, 0, R.string.units_volts,0));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_carb, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_gas_v, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_ckps, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_ref_s, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_ps, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_bl, 0, false,false));
		inputItems.add(new ParamItemBoolean(this, R.string.diag_input_de, 0, false,false));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_ks1, 0, R.string.units_volts,0));
		inputItems.add(new ParamItemFloat(this, R.string.diag_input_ks2, 0, R.string.units_volts,0));

		String outputNames[] = getResources().getStringArray(R.array.diagnostics_output_names);
		for (int i = 0; i != outputNames.length; i++) {
			outputItems.add(new ParamItemBoolean(this, outputNames[i], null, false));
			outputItems.get(i).setOnParamItemChangeListener(this);
		}
		
		inputFragment = new ListFragment();
		inputFragment.setListAdapter(new ParamItemsAdapter(inputItems));
		outputsFragment = new OutputDiagListFragment();
		outputsFragment.setOnItemClickListener(this);
		outputsFragment.setListAdapter(new ParamItemsAdapter(outputItems));
		pages.add(inputFragment);
		pages.add(outputsFragment); 
		
		diagnosticsAdapter = new DiagnosticsPagerAdapter(getSupportFragmentManager(),pages);
		receiver = new ReceiveMessages();
		textViewStatus = (TextView) findViewById(R.id.diagnosticsStatusTextView);		
		pager = (ViewPager)findViewById(R.id.diagnosticsPager);
		pager.setAdapter(diagnosticsAdapter);	
		
		if (savedInstanceState != null) {
			int outputs = savedInstanceState.getInt(OUTPUTS);
			setOutputs(outputs);
			int page = savedInstanceState.getInt(PAGE);
			pager.setCurrentItem(page);
		}
		super.onCreate(savedInstanceState);		
	}


	@Override
	protected void onPause() {	
		//startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, new OpCompNc(Secu3Packet.OPCODE_DIAGNOST_LEAVE,0)));
		// TODO
		unregisterReceiver(receiver);
		super.onPause();		
	}
	
	@Override
	// This is bugfix of http://stackoverflow.com/questions/13910826/viewpager-fragmentstatepageradapter-orientation-change
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(OUTPUTS, getOutputs());
		outState.putInt(PAGE, pager.getCurrentItem());
		//super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onResume() {							
		registerReceiver(receiver, receiver.intentFilter);
		//startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, new OpCompNc(Secu3Packet.OPCODE_DIAGNOST_ENTER,0)));
		// TODO
		super.onResume();		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.diagnostics, menu);
		return false;		
	}

	void update (Intent intent) {
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
			if (packet.getNameId() == R.string.diaginp_dat_title) {	
				PacketUtils.setDiagInpFromPacket((ParamItemsAdapter) inputFragment.getListAdapter(), packet);
				((ParamItemsAdapter) inputFragment.getListAdapter()).notifyDataSetChanged();
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
		// TODO
		//DiagOutDat packet = new DiagOutDat();
		//packet.setOutputs(getOutputs());
		//startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packet));				
	}
}

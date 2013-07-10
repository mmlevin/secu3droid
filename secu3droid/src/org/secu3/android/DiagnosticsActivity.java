package org.secu3.android;

import java.util.ArrayList;
import java.util.List;

import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.DiagInpDat;
import org.secu3.android.api.io.Secu3Service;
import org.secu3.android.api.io.Secu3Dat.OpCompNc;
import org.secu3.android.fragments.DiagnosticsChartFragment;
import org.secu3.android.fragments.DiagnosticsInputsFragment;
import org.secu3.android.fragments.DiagnosticsOutputsFragment;
import org.secu3.android.fragments.ISecu3Fragment.OnDataChangedListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class DiagnosticsActivity extends FragmentActivity implements OnDataChangedListener {
	public static final String LOG_TAG = "FragmentActivity";	
	
	DiagnosticsInputsFragment inputFragment = null;
	DiagnosticsOutputsFragment outputsFragment = null;
	DiagnosticsChartFragment chartFragment = null;
	
	List<Fragment> pages = new ArrayList<Fragment>();
	DiagnosticsPagerAdapter diagnosticsAdapter = null;
	ViewPager pager = null;
	ReceiveMessages receiver;
	boolean isOnline = false;
	TextView textViewStatus;
	
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
			String action = intent.getAction();
			Log.d(LOG_TAG, action);
			update(intent);	   	    
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diagnostics);
		
		pages.add(inputFragment = new DiagnosticsInputsFragment());
		pages.add(outputsFragment = new DiagnosticsOutputsFragment());
		//pages.add(chartFragment = new DiagnosticsChartFragment()); 
		
		diagnosticsAdapter = new DiagnosticsPagerAdapter(getSupportFragmentManager(),pages);
		receiver = new ReceiveMessages();
		textViewStatus = (TextView) findViewById(R.id.diagnosticsStatusTextView);		
		pager = (ViewPager)findViewById(R.id.diagnosticsPager);
		pager.setAdapter(diagnosticsAdapter);		
		
		outputsFragment.setOnDataChangedListener(this);
	}


	@Override
	protected void onPause() {
		super.onPause();	
		startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, new OpCompNc(Secu3Dat.OPCODE_DIAGNOST_LEAVE,0)));
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
			
		}	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Log.d(LOG_TAG, "onResume");
		
		isOnline = false;
			
		try {
			registerReceiver(receiver, receiver.intentFilter);
			
		} catch (Exception e) {
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.diagnostics, menu);
		return false;		
	}

	@Override
	public void onDataChanged(Fragment fragment, Secu3Dat packet) {
		if (packet != null) {
			startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, packet));
		}		
	}

	void update (Intent intent) {
		if (Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE.equals(intent.getAction())) {
			boolean isOnline = intent.getBooleanExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS,false);
			if (isOnline && !this.isOnline) {
				this.isOnline = true;
				startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, new OpCompNc(Secu3Dat.OPCODE_DIAGNOST_ENTER,0)));
			}
			textViewStatus.setText(isOnline?"Online":"Offline");
		} else if (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET.equals(intent.getAction()))
		{
			Secu3Dat packet = intent.getParcelableExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET);			
			if (packet instanceof DiagInpDat) {
				DiagnosticsInputsFragment page = inputFragment;
				if (page != null) {
					page.setData(packet);
					if (page.isVisible()) page.updateData();
				}
				
				DiagnosticsChartFragment page1 = chartFragment;
				if (page1 != null) {
					page1.setData(packet);
					if (page1.isVisible()) page1.updateData();
				}				
			}
		}				
	}
}

package org.secu3.android;

import java.util.ArrayList;
import java.util.List;

import org.secu3.android.api.io.*;
import org.secu3.android.api.io.Secu3Manager.SECU3_TASK;
import org.secu3.android.api.io.Secu3Dat.*;
import org.secu3.android.fragments.*;

import android.net.Uri;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ParamActivity extends FragmentActivity{
	public static final String LOG_TAG = "ParamActivity";	
	
	public static final int PARAMS_NUMBER = 9;
	
	StarterFragment starterParPage = null;
	AnglesFragment anglesParPage = null;
	IdlRegFragment idlRegParPage = null;
	FunsetFragment funsetParPage = null;
	TemperFragment temperParPage = null;
	CarburFragment carburParPage = null;
	ADCCorFragment adcCorParPage = null;
	CKPSFragment ckpsParPage = null;
	MiscelFragment miscelParPage = null;
	
	ProgressBar progressBar = null;
		
	List<Fragment> pages = new ArrayList<Fragment>();
	TextView textViewStatus = null;
	TextView textView = null;
	ViewPager pager = null;
    ParamPagerAdapter awesomeAdapter = null;
    
	private boolean isOnline = false;
	
    private boolean isValid() {
    	return (starterParPage.getData() != null) &&
    		   (anglesParPage.getData() != null) &&
    		   (idlRegParPage.getData() != null) &&
    		   (funsetParPage.getData() != null) &&
    		   (funsetParPage.getExtraData() != null) &&
    		   (((FnNameDat)funsetParPage.getExtraData()).names_available()) &&
    		   (temperParPage.getData() != null) &&
    		   (carburParPage.getData() != null) &&
    		   (adcCorParPage.getData() != null) &&
    		   (ckpsParPage.getData() != null) &&
    		   (miscelParPage.getData() != null);
    }
    
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
	
	private class ParamPagerAdapter extends FragmentPagerAdapter{
		private List<Fragment> fragments = null;
		private String titles[];
		
		public ParamPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
			titles = getBaseContext().getResources().getStringArray(R.array.fragment_titles);
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

	ReceiveMessages receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		Log.d(LOG_TAG, "onCreate");
		
		setContentView(R.layout.activity_param);
		
		pages.add(starterParPage = new StarterFragment());
		pages.add(anglesParPage = new AnglesFragment());
		pages.add(idlRegParPage = new IdlRegFragment());
		pages.add(funsetParPage = new FunsetFragment());
		pages.add(temperParPage = new TemperFragment());
		pages.add(carburParPage = new CarburFragment());
		pages.add(adcCorParPage = new ADCCorFragment());
		pages.add(ckpsParPage = new CKPSFragment());
		pages.add(miscelParPage = new MiscelFragment());
			
		awesomeAdapter = new ParamPagerAdapter(getSupportFragmentManager(),pages);
		progressBar = (ProgressBar)findViewById(R.id.paramsProgressBar);
		readParams();
				
		receiver = new ReceiveMessages();
		textViewStatus = (TextView) findViewById(R.id.paramsTextViewStatus);
		pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(awesomeAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_param, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_download:
	    	progressBar.setIndeterminate(true);
	    	progressBar.setVisibility(ProgressBar.VISIBLE);
			readParams();
			return true;
		case R.id.menu_upload:
			if (isValid()) {
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
				} catch (Exception e) {
					Log.d (LOG_TAG, e.toString());
				}
			}
			return true;
		case R.id.menu_save_eeprom:
			try {
		    	progressBar.setIndeterminate(true);
		    	progressBar.setVisibility(ProgressBar.VISIBLE);				
				startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET_PARAM_PACKET, new OPCompNc(Secu3Dat.OPCODE_EEPROM_PARAM_SAVE,0)));
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
		
		isOnline = false;
		
		try {
			registerReceiver(receiver, receiver.intentFilter);
			
		} catch (Exception e) {
		}		
	}	
	
	void update (Intent intent) {
		if (Secu3Service.EVENT_SECU3_SERVICE_STATUS_ONLINE.equals(intent.getAction())) {
			boolean isOnline = intent.getBooleanExtra(Secu3Service.EVENT_SECU3_SERVICE_STATUS,false);
			if (isOnline && !this.isOnline) {
				this.isOnline = true;
				readParams();
			}
			textViewStatus.setText(isOnline?"Online":"Offline");
		} else if (Secu3Dat.RECEIVE_STARTER_PAR.equals(intent.getAction())) {
			StartrPar packet = intent.getParcelableExtra(StartrPar.class.getCanonicalName());
			StarterFragment page = starterParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (Secu3Dat.RECEIVE_ANGLES_PAR.equals(intent.getAction())) {
			AnglesPar packet = intent.getParcelableExtra(AnglesPar.class.getCanonicalName());
			AnglesFragment page = anglesParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (Secu3Dat.RECEIVE_IDLREG_PAR.equals(intent.getAction())) {
			IdlRegPar packet = intent.getParcelableExtra(IdlRegPar.class.getCanonicalName());
			IdlRegFragment page = idlRegParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (Secu3Dat.RECEIVE_FNNAME_DAT.equals(intent.getAction())) {
			FnNameDat packet = intent.getParcelableExtra(FnNameDat.class.getCanonicalName());
			FunsetFragment page = funsetParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		}else if (Secu3Dat.RECEIVE_FUNSET_PAR.equals(intent.getAction())) {
			FunSetPar packet = intent.getParcelableExtra(FunSetPar.class.getCanonicalName());
			FunsetFragment page = funsetParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (Secu3Dat.RECEIVE_TEMPER_PAR.equals(intent.getAction())) {
			TemperPar packet = intent.getParcelableExtra(TemperPar.class.getCanonicalName());
			TemperFragment page = temperParPage;
			page.setData(packet);			
			if (page.isVisible()) page.updateData();
		} else if (Secu3Dat.RECEIVE_CARBUR_PAR.equals(intent.getAction())) {
			CarburPar packet = intent.getParcelableExtra(CarburPar.class.getCanonicalName());
			CarburFragment page = carburParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (Secu3Dat.RECEIVE_ADCCOR_PAR.equals(intent.getAction())) {
			ADCCorPar packet = intent.getParcelableExtra(ADCCorPar.class.getCanonicalName());
			ADCCorFragment page = adcCorParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (Secu3Dat.RECEIVE_CKPS_PAR.equals(intent.getAction())) {
			CKPSPar packet = intent.getParcelableExtra(CKPSPar.class.getCanonicalName());
			CKPSFragment page = ckpsParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (Secu3Dat.RECEIVE_MISCEL_PAR.equals(intent.getAction())) {
			MiscelPar packet = intent.getParcelableExtra(MiscelPar.class.getCanonicalName());
			MiscelFragment page = miscelParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (Secu3Service.EVENT_SECU3_SERVICE_PROGRESS.equals(intent.getAction())) {
			int current = intent.getIntExtra(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS_CURRENT,0);
			int total = intent.getIntExtra(Secu3Service.EVENT_SECU3_SERVICE_PROGRESS_TOTAL,0);
			if (current == total) progressBar.setVisibility(ProgressBar.GONE);
			progressBar.setIndeterminate(current==0);
			progressBar.setMax(total);
			progressBar.setProgress(current);
		} else if (Secu3Dat.RECEIVE_OP_COMP_NC.equals(intent.getAction())) {
			OPCompNc packet = intent.getParcelableExtra(OPCompNc.class.getCanonicalName());
			if ((packet != null) && (packet.opcode == Secu3Dat.OPCODE_EEPROM_PARAM_SAVE)) {
				progressBar.setVisibility(ProgressBar.GONE);				
				Toast.makeText(this, String.format("Params saved: error code %d", packet.opdata), Toast.LENGTH_LONG).show();
			}
		}
	}
}

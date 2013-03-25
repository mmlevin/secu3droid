package org.secu3.android;

import java.util.ArrayList;
import java.util.List;

import org.secu3.android.api.io.*;
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
	public static final String LOG_TAG = "Param Activity";	
	
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
    	progressBar.setIndeterminate(true);
    	progressBar.setVisibility(ProgressBar.VISIBLE);
    	startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_READ_PARAMS,Uri.EMPTY,this,Secu3Service.class));
    }
    
	public class ReceiveMessages extends BroadcastReceiver 
	{
	@Override
	   public void onReceive(Context context, Intent intent) 
	   {    
	       String action = intent.getAction();
	       Log.d(getString(R.string.app_name), action);
	       if(action.equalsIgnoreCase(Secu3Service.SECU3_SERVICE_STATUS_ONLINE)) {
	    	   updateStatus(intent);
	       } else {
	    	   updateData(intent);	   
	       }	    
	   }
	}
	
	private class ParamPagerAdapter extends FragmentPagerAdapter{
		List<Fragment> fragments = null;
		String titles[];
		
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
		setContentView(R.layout.activity_param);
		progressBar = (ProgressBar)findViewById(R.id.paramsProgressBar);
		readParams();
				
		receiver = new ReceiveMessages();
		textViewStatus = (TextView) findViewById(R.id.paramsTextViewStatus);
		pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(awesomeAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_param, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_download:
			readParams();
			return true;
		case R.id.menu_upload:
			if (isValid()) {
				try {
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.SECU3_SERVICE_PACKET, starterParPage.getData().pack()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.SECU3_SERVICE_PACKET, anglesParPage.getData().pack()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.SECU3_SERVICE_PACKET, idlRegParPage.getData().pack()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.SECU3_SERVICE_PACKET, funsetParPage.getData().pack()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.SECU3_SERVICE_PACKET, temperParPage.getData().pack()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.SECU3_SERVICE_PACKET, carburParPage.getData().pack()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.SECU3_SERVICE_PACKET, adcCorParPage.getData().pack()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.SECU3_SERVICE_PACKET, ckpsParPage.getData().pack()));
					startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.SECU3_SERVICE_PACKET, miscelParPage.getData().pack()));
				} catch (Exception e) {
					Log.d (LOG_TAG, e.toString());
				}
			}
			return true;
		case R.id.menu_save_eeprom:
			try {
		    	progressBar.setIndeterminate(true);
		    	progressBar.setVisibility(ProgressBar.VISIBLE);				
				startService(new Intent (Secu3Service.ACTION_SECU3_SERVICE_SEND_PACKET,Uri.EMPTY,this,Secu3Service.class).putExtra(Secu3Service.SECU3_SERVICE_PACKET, OPCompNc.pack(Secu3Dat.OPCODE_EEPROM_PARAM_SAVE,0)));
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
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
			
		}
		finally {
			super.onPause();
		}
	}
		
	@Override
	protected void onResume() {
		try {
			IntentFilter infil = new IntentFilter();
			infil.addAction(Secu3Dat.RECEIVE_STARTER_PAR);
			infil.addAction(Secu3Dat.RECEIVE_ANGLES_PAR);
			infil.addAction(Secu3Dat.RECEIVE_IDLREG_PAR);
			infil.addAction(Secu3Dat.RECEIVE_FNNAME_DAT);
			infil.addAction(Secu3Dat.RECEIVE_FUNSET_PAR);
			infil.addAction(Secu3Dat.RECEIVE_TEMPER_PAR);
			infil.addAction(Secu3Dat.RECEIVE_CARBUR_PAR);
			infil.addAction(Secu3Dat.RECEIVE_ADCCOR_PAR);
			infil.addAction(Secu3Dat.RECEIVE_CKPS_PAR);
			infil.addAction(Secu3Dat.RECEIVE_MISCEL_PAR);
			infil.addAction(Secu3Service.SECU3_SERVICE_STATUS_ONLINE);
			infil.addAction(Secu3Service.RECEIVE_SECU3_SERVICE_PROGRESS);
			infil.addAction(Secu3Dat.RECEIVE_OP_COMP_NC);
			registerReceiver(receiver, infil);
			
		} catch (Exception e) {
		}
		finally {
			super.onResume();
		}		
	}	
	
	void updateData (Intent intent) {
		String action = intent.getAction();
		if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_STARTER_PAR)) {
			StartrPar packet = intent.getParcelableExtra(StartrPar.class.getCanonicalName());
			StarterFragment page = starterParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_ANGLES_PAR)) {
			AnglesPar packet = intent.getParcelableExtra(AnglesPar.class.getCanonicalName());
			AnglesFragment page = anglesParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_IDLREG_PAR)) {
			IdlRegPar packet = intent.getParcelableExtra(IdlRegPar.class.getCanonicalName());
			IdlRegFragment page = idlRegParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_FNNAME_DAT)) {
			FnNameDat packet = intent.getParcelableExtra(FnNameDat.class.getCanonicalName());
			FunsetFragment page = funsetParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		}else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_FUNSET_PAR)) {
			FunSetPar packet = intent.getParcelableExtra(FunSetPar.class.getCanonicalName());
			FunsetFragment page = funsetParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_TEMPER_PAR)) {
			TemperPar packet = intent.getParcelableExtra(TemperPar.class.getCanonicalName());
			TemperFragment page = temperParPage;
			page.setData(packet);			
			if (page.isVisible()) page.updateData();
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_CARBUR_PAR)) {
			CarburPar packet = intent.getParcelableExtra(CarburPar.class.getCanonicalName());
			CarburFragment page = carburParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_ADCCOR_PAR)) {
			ADCCorPar packet = intent.getParcelableExtra(ADCCorPar.class.getCanonicalName());
			ADCCorFragment page = adcCorParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_CKPS_PAR)) {
			CKPSPar packet = intent.getParcelableExtra(CKPSPar.class.getCanonicalName());
			CKPSFragment page = ckpsParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_MISCEL_PAR)) {
			MiscelPar packet = intent.getParcelableExtra(MiscelPar.class.getCanonicalName());
			MiscelFragment page = miscelParPage;
			page.setData(packet);
			if (page.isVisible()) page.updateData();
		} else if (action.equalsIgnoreCase(Secu3Service.RECEIVE_SECU3_SERVICE_PROGRESS)) {
			int current = intent.getIntExtra(Secu3Service.SECU3_SERVICE_PROGRESS_CURRENT,0);
			int total = intent.getIntExtra(Secu3Service.SECU3_SERVICE_PROGRESS_TOTAL,0);
			if (current == total) progressBar.setVisibility(ProgressBar.GONE);
			progressBar.setIndeterminate(current==0);
			progressBar.setMax(total);
			progressBar.setProgress(current);
		} else if (action.equals(Secu3Dat.RECEIVE_OP_COMP_NC)) {
			OPCompNc packet = intent.getParcelableExtra(OPCompNc.class.getCanonicalName());
			if ((packet != null) && (packet.opcode == Secu3Dat.OPCODE_EEPROM_PARAM_SAVE)) {
				progressBar.setVisibility(ProgressBar.GONE);				
				Toast.makeText(this, String.format("Params saved: error code %d", packet.opdata), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	void updateStatus (Intent intent) {
		String s = intent.getBooleanExtra(Secu3Service.SECU3_SERVICE_STATUS,false)?"Online":"Offline";
		textViewStatus.setText(s);
	}
}

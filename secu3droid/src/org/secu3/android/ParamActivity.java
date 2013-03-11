package org.secu3.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.secu3.android.api.io.*;
import org.secu3.android.api.io.Secu3Dat.*;
import org.secu3.android.fragments.*;

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
import android.widget.TextView;

public class ParamActivity extends FragmentActivity {
	
	StarterFragment starterParPage;
	AnglesFragment anglesParPage;
	IdlRegFragment idlRegParPage;
	FunsetFragment funsetParPage;
	TemperFragment temperParPage;
	CarburFragment carburParPage;
	ADCCorFragment adcCorParPage;
	CKPSFragment ckpsParPage;
	MiscelFragment miscelParPage;
		
	List<Fragment> pages = new ArrayList<Fragment>();
	TextView textViewStatus;
	TextView textView;
	ViewPager pager;
    Context cxt;
    ParamPagerAdapter awesomeAdapter;
	
	public class ReceiveMessages extends BroadcastReceiver 
	{
	@Override
	   public void onReceive(Context context, Intent intent) 
	   {    
	       String action = intent.getAction();
	       Log.d(getString(R.string.app_name), action);
	       if(action.equalsIgnoreCase(Secu3Service.STATUS_ONLINE)) {
	    	   updateStatus(intent);
	       } else {
	    	   updateUI(intent);	   
	       }	    
	   }
	}
	
	private class ParamPagerAdapter extends FragmentPagerAdapter{
		List<Fragment> fragments = null;
		
		public ParamPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}


		@Override
		public Fragment getItem (int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
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
		cxt = this;
				
		receiver = new ReceiveMessages();
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
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
        if (item.getItemId() == R.id.menu_preferences) {
            startActivity(new Intent(getBaseContext(), Preferences.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onPause() {
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
			
		}
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
		}
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		try {
			IntentFilter infil = new IntentFilter();
			infil.addAction(Secu3Dat.RECEIVE_STARTER_PAR);
			infil.addAction(Secu3Dat.RECEIVE_ANGLES_PAR);
			infil.addAction(Secu3Dat.RECEIVE_IDLREG_PAR);
			infil.addAction(Secu3Dat.RECEIVE_FUNSET_PAR);
			infil.addAction(Secu3Dat.RECEIVE_TEMPER_PAR);
			infil.addAction(Secu3Dat.RECEIVE_CARBUR_PAR);
			infil.addAction(Secu3Dat.RECEIVE_ADCCOR_PAR);
			infil.addAction(Secu3Dat.RECEIVE_CKPS_PAR);
			infil.addAction(Secu3Dat.RECEIVE_MISCEL_PAR);
			infil.addAction(Secu3Service.STATUS_ONLINE);
			registerReceiver(receiver, infil);
			
		} catch (Exception e) {
		}
		super.onResume();
	}
	
	void updateUI (Intent intent) {
		String action = intent.getAction();
		if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_STARTER_PAR)) {
			StartrPar packet = intent.getParcelableExtra(StartrPar.class.getCanonicalName());
			starterParPage.setData(packet);
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_ANGLES_PAR)) {
			AnglesPar packet = intent.getParcelableExtra(AnglesPar.class.getCanonicalName());
			anglesParPage.setData(packet);
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_IDLREG_PAR)) {
			IdlRegPar packet = intent.getParcelableExtra(IdlRegPar.class.getCanonicalName());
			idlRegParPage.setData(packet);
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_FUNSET_PAR)) {
			FunSetPar packet = intent.getParcelableExtra(FunSetPar.class.getCanonicalName());
			funsetParPage.setData(packet);
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_TEMPER_PAR)) {
			TemperPar packet = intent.getParcelableExtra(TemperPar.class.getCanonicalName());
			temperParPage.setData(packet);			
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_CARBUR_PAR)) {
			CarburPar packet = intent.getParcelableExtra(CarburPar.class.getCanonicalName());
			carburParPage.setData(packet);
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_ADCCOR_PAR)) {
			ADCCorPar packet = intent.getParcelableExtra(ADCCorPar.class.getCanonicalName());
			adcCorParPage.setData(packet);
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_CKPS_PAR)) {
			CKPSPar packet = intent.getParcelableExtra(CKPSPar.class.getCanonicalName());
			ckpsParPage.setData(packet);
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_MISCEL_PAR)) {
			MiscelPar packet = intent.getParcelableExtra(MiscelPar.class.getCanonicalName());
			miscelParPage.setData(packet);
		}		
	}
	
	void updateStatus (Intent intent) {
		String s = intent.getBooleanExtra(Secu3Service.STATUS,false)?"Online":"Offline";
		textViewStatus.setText(s);
	}
}

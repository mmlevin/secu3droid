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
    AwesomePagerAdapter awesomeAdapter;
	
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
	
	private class AwesomePagerAdapter extends FragmentPagerAdapter{
		List<Fragment> fragments = null;
		
		public AwesomePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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
		
		cxt = this;
		
		pages.add(starterParPage = new StarterFragment());
		pages.add(anglesParPage = new AnglesFragment());
		pages.add(idlRegParPage = new IdlRegFragment());
		pages.add(funsetParPage = new FunsetFragment());
		pages.add(temperParPage = new TemperFragment());
		pages.add(carburParPage = new CarburFragment());
		pages.add(adcCorParPage = new ADCCorFragment());
		pages.add(ckpsParPage = new CKPSFragment());
		pages.add(miscelParPage = new MiscelFragment());
			
		awesomeAdapter = new AwesomePagerAdapter(getSupportFragmentManager(),pages);		
		setContentView(R.layout.activity_param);
				
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
		String s = "";
		TextView v = null;
		v = textView;
		if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_STARTER_PAR)) {
			StartrPar packet = intent.getParcelableExtra(StartrPar.class.getCanonicalName());
			if (packet != null && starterParPage.isAdded()) {
				s = String.format(Locale.getDefault(),"Starter off RPM: %d min-1\r\nMap abandon RPM: %d min-1", packet.starter_off,packet.smap_abandon);
				v = (TextView)starterParPage.getView().findViewById(R.id.textViewData);
			}
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_ANGLES_PAR)) {
			AnglesPar packet = intent.getParcelableExtra(AnglesPar.class.getCanonicalName());
			if (packet != null && anglesParPage.isAdded()) {
				s = String.format(Locale.getDefault(),"Minimal angle: %f°\r\nMaximal angle: %f°\r\nAngle decrement step: %f°\r\nAngle increment step: %f°\r\nZero angle: %d\r\nCurrent angle: %f°", packet.min_angle,packet.max_angle,packet.dec_spead,packet.inc_spead,packet.zero_adv_ang,packet.angle_corr);
				v = (TextView)anglesParPage.getView().findViewById(R.id.textViewData);
			}
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_IDLREG_PAR)) {
			IdlRegPar packet = intent.getParcelableExtra(IdlRegPar.class.getCanonicalName());
			if (packet != null && idlRegParPage.isAdded()) {
				s = String.format(Locale.getDefault(),"Coeff. 1: %f\r\nCoeff. 2:%f\r\nMinimal angle: %f°\r\nMaximal angle: %f°\r\nTarget RPM: %d min-1\r\nRPM sensitivity: %d min-1\r\nUse idle regulator: %d", packet.ifac1,packet.ifac2,packet.min_angle,packet.max_angle,packet.idling_rpm,packet.MINEFR,packet.idl_regul);
				v = (TextView)idlRegParPage.getView().findViewById(R.id.textViewData);
			}
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_FUNSET_PAR)) {
			FunSetPar packet = intent.getParcelableExtra(FunSetPar.class.getCanonicalName());
			if (packet != null && funsetParPage.isAdded()) {
				s = String.format(Locale.getDefault(),"Lower pressure: %f kPa\r\nUpper pressure: %f kPa\r\nMAP Sensor offset: %f V\r\nMAP Sensor gradient: %f kPa/V\r\nGasoline tables: %d\r\nGas tables: %d\r\n", packet.map_lower_pressure,packet.map_upper_pressure,packet.map_curve_offset,packet.map_curve_gradient,packet.fn_benzin,packet.fn_gas);
				v = (TextView)funsetParPage.getView().findViewById(R.id.textViewData);
			}
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_TEMPER_PAR)) {
			TemperPar packet = intent.getParcelableExtra(TemperPar.class.getCanonicalName());
			if (packet != null && temperParPage.isAdded()) {
				s = String.format(Locale.getDefault(),"Fan on: %f °C\r\nFan off: %f °C\r\nUse temperature sensor: %d\r\nUse PWM control: %d\r\nUse temperature sensor table: %d", packet.vent_on,packet.vent_off,packet.tmp_use,packet.vent_pwm,packet.cts_use_map);
				v = (TextView)temperParPage.getView().findViewById(R.id.textViewData);
			}
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_CARBUR_PAR)) {
			CarburPar packet = intent.getParcelableExtra(CarburPar.class.getCanonicalName());
			if (packet != null && carburParPage.isAdded()) {
				s = String.format(Locale.getDefault(),"EPHH low threshhold on gasoline: %d RPM\r\nEPHH high threshhold on gasoline: %d RPM\r\nEPHH low threshhold on gas: %d RPM\r\nEPHH high threshhold on gas: %d RPM\r\nEPHH valve delay: %f s\r\nCarburator sensor inverse: %d\r\nEPM valve on pressure: %f kPa", packet.ephh_lot,packet.ephh_hit,packet.ephh_lot_g,packet.ephh_hit_g,packet.shutoff_delay,packet.carb_invers,packet.epm_ont);
				v = (TextView)carburParPage.getView().findViewById(R.id.textViewData);
			}
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_ADCCOR_PAR)) {
			ADCCorPar packet = intent.getParcelableExtra(ADCCorPar.class.getCanonicalName());
			if (packet != null && adcCorParPage.isAdded()) {
				s = String.format(Locale.getDefault(),"MAP Sensor factor: %f\r\nMAP Sensor correction: %f V\r\nVoltage Sensor factor: %f\r\nVoltage Sensor correction: %f V\r\nTemperature Sensor factor: %f\r\nTemperature Sensor correction: %f V", packet.map_adc_factor,packet.map_adc_correction,packet.ubat_adc_factor,packet.ubat_adc_correction,packet.temp_adc_factor,packet.temp_adc_correction);
				v = (TextView)adcCorParPage.getView().findViewById(R.id.textViewData);
			}
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_CKPS_PAR)) {
			CKPSPar packet = intent.getParcelableExtra(CKPSPar.class.getCanonicalName());
			if (packet != null && ckpsParPage.isAdded()) {
				s = String.format(Locale.getDefault(),"CKPS Edge: %d\r\nReference edge: %d\r\nMerge outputs: %d\r\nTotal cogs number: %d\r\nMissing cogs number: %d\r\nCogs number before top dead center: %d\r\nCylinders: %d\r\nIgnitor pulse delay: %d cogs", packet.ckps_edge_type,packet.ref_s_edge_type,packet.ckps_merge_ign_outs,packet.ckps_cogs_num,packet.ckps_miss_num,packet.ckps_cogs_btdc,packet.ckps_engine_cyl,packet.ckps_ignit_cogs);
				v = (TextView)ckpsParPage.getView().findViewById(R.id.textViewData);
			}
		} else if (action.equalsIgnoreCase(Secu3Dat.RECEIVE_MISCEL_PAR)) {
			MiscelPar packet = intent.getParcelableExtra(MiscelPar.class.getCanonicalName());
			if (packet != null && miscelParPage.isAdded()) {
				s = String.format(Locale.getDefault(),"Baudrate: %d bps\r\nData transmit interval: %d ms\r\nUse ignition cutoff: %d\r\nIgnition cutoff threshold: %d RPM\r\nHall sensor start cog: %d\r\nHall sensor pulse delay: %d cogs", packet.baud_rate,packet.period_ms,packet.ign_cutoff,packet.ign_cutoff_thrd,packet.hop_start_cogs,packet.hop_durat_cogs);
				v = (TextView)miscelParPage.getView().findViewById(R.id.textViewData);
			}
		}		
		if (v != null ) v.setText(s);
	}
	
	void updateStatus (Intent intent) {
		String s = intent.getBooleanExtra(Secu3Service.STATUS,false)?"Online":"Offline";
		textViewStatus.setText(s);
	}
}

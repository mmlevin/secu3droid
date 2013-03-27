package org.secu3.android;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import org.secu3.android.api.io.Secu3Service;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private SharedPreferences sharedPref ;
	private BluetoothAdapter bluetoothAdapter = null;
	private String LOG_TAG = "Settings activity";
		
	@SuppressWarnings("deprecation")
	private void updateDevicePreferenceSummary(){
        // update bluetooth device summary
		String deviceName = "";
        ListPreference prefDevices = (ListPreference)findPreference(Secu3Service.PREF_BLUETOOTH_DEVICE);
        String deviceAddress = sharedPref.getString(Secu3Service.PREF_BLUETOOTH_DEVICE, null);
        if (BluetoothAdapter.checkBluetoothAddress(deviceAddress)){
        	deviceName = bluetoothAdapter.getRemoteDevice(deviceAddress).getName();
        }
        prefDevices.setSummary(getString(R.string.pref_bluetooth_device_summary, deviceName));
    }   

	@SuppressWarnings("deprecation")
	private void updateDevicePreferenceList(){
        // update bluetooth device summary
		updateDevicePreferenceSummary();
		// update bluetooth device list
        ListPreference prefDevices = (ListPreference)findPreference(Secu3Service.PREF_BLUETOOTH_DEVICE);
        Set<BluetoothDevice> pairedDevices = new HashSet<BluetoothDevice>();
        if (bluetoothAdapter != null){
        	pairedDevices = bluetoothAdapter.getBondedDevices();  
        }
        String[] entryValues = new String[pairedDevices.size()];
        String[] entries = new String[pairedDevices.size()];
        int i = 0;
    	    // Loop through paired devices
        for (BluetoothDevice device : pairedDevices) {
        	// Add the name and address to the ListPreference enties and entyValues
        	Log.v(LOG_TAG, "device: "+device.getName() + " -- " + device.getAddress());
        	entryValues[i] = device.getAddress();
            entries[i] = device.getName();
            i++;
        }
        prefDevices.setEntryValues(entryValues);
        prefDevices.setEntries(entries);
        Preference pref;        
        pref = (Preference)findPreference(Secu3Service.PREF_CONNECTION_RETRIES);
        String maxConnRetries = sharedPref.getString(Secu3Service.PREF_CONNECTION_RETRIES, getString(R.string.defaultConnectionRetries));
        pref.setSummary(getString(R.string.pref_connection_retries_summary,maxConnRetries));
        this.onContentChanged();
    }
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		addPreferencesFromResource(R.xml.preferences);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();	
        Preference pref = findPreference(Secu3Service.PREF_ABOUT);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {		
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SettingsActivity.this.displayAboutDialog();
				return true;
			}
		});
        
	}	
	
	@Override
	protected void onResume() {
        sharedPref.registerOnSharedPreferenceChangeListener(this);
		this.updateDevicePreferenceList();		
		super.onResume();		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		sharedPref.unregisterOnSharedPreferenceChangeListener(this);		
	}	

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (Secu3Service.PREF_BLUETOOTH_DEVICE.equals(key))	updateDevicePreferenceSummary();	
		updateDevicePreferenceList();
	}
	
	private void displayAboutDialog(){
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        // we need this to enable html links
        TextView textView = (TextView) messageView.findViewById(R.id.about_license);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);
        textView = (TextView) messageView.findViewById(R.id.about_sources);
        textView.setTextColor(defaultColor);
        
        textView = (TextView)messageView.findViewById(R.id.about_version_name);
        try {
        	PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        	textView.setText(pInfo.versionName);
        } catch (Exception e) {        	
        }
       
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.about_title);
		builder.setIcon(R.drawable.gplv3_icon);
        builder.setView(messageView);
		builder.show();
	}	
}

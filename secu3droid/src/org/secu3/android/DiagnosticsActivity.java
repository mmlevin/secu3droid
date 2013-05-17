package org.secu3.android;

import java.util.ArrayList;
import java.util.List;

import org.secu3.android.fragments.DiagnosticsInputsFragment;
import org.secu3.android.fragments.DiagnosticsOutputsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class DiagnosticsActivity extends FragmentActivity {
	public static final String LOG_TAG = "FragmentActivity";	
	
	DiagnosticsInputsFragment inputFragment = null;
	DiagnosticsOutputsFragment outputsFragment = null;
	List<Fragment> pages = new ArrayList<Fragment>();
	DiagnosticsPagerAdapter diagnosticsAdapter = null;
	ViewPager pager = null;
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diagnostics);
		
		pages.add(inputFragment = new DiagnosticsInputsFragment());
		pages.add(outputsFragment = new DiagnosticsOutputsFragment());
		
		diagnosticsAdapter = new DiagnosticsPagerAdapter(getSupportFragmentManager(),pages);
		pager = (ViewPager)findViewById(R.id.diagnosticsPager);
		pager.setAdapter(diagnosticsAdapter);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.diagnostics, menu);
		return true;
	}

}

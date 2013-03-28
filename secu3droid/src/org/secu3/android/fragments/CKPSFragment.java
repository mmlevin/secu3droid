package org.secu3.android.fragments;

import java.text.NumberFormat;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.CKPSPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;

public class CKPSFragment extends Fragment implements ISecu3Fragment{
	CKPSPar packet;
	
	RadioButton ckpsFallingEdge;
	RadioButton ckpsRaisingEdge;
	RadioButton refSFallingEdge;
	RadioButton refSRaisingEdge;
	CheckBox mergeOutputs;
	EditText cogsNumber;
	EditText missingCogsNumber;
	EditText cogsBeforeTDC;
	EditText engineCylinders;
	EditText ignitionPulseDelay;
	
	private class CustomTextWatcher implements TextWatcher {
		EditText e = null;
		
		public CustomTextWatcher(EditText e) {
			this.e =e;  
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			int i = 0;
			try {
				NumberFormat format = NumberFormat.getInstance(Locale.US);
				Number number = format.parse(s.toString());				
				i = number.intValue();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (packet != null) {
					switch (e.getId()){
						case R.id.ckpsCogsNumberEditText:
							packet.ckps_cogs_num = i;
							break;
						case R.id.ckpsMissingCogsNumberEditText:
							packet.ckps_miss_num = i;
							break;
						case R.id.ckpsCogsBeforeTDCEditText:
							packet.ckps_cogs_btdc = i;
							break;
						case R.id.ckpsEngineCylyndersEditText:
							packet.ckps_engine_cyl = i;
							break;
						case R.id.ckpsIgnitionPulseDelayEditText:
							packet.ckps_ignit_cogs = i;
							break;
					}
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {			
		}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) return null;
		
		return inflater.inflate(R.layout.ckps_params, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ckpsFallingEdge = (RadioButton)getView().findViewById(R.id.ckpsCKPSFallingEdgeRadioButton);
		ckpsRaisingEdge = (RadioButton)getView().findViewById(R.id.ckpsCKPSRisingEdgeRadioButton);
		refSFallingEdge = (RadioButton)getView().findViewById(R.id.ckpsRefSFallingEdgeRadioButton);
		refSRaisingEdge = (RadioButton)getView().findViewById(R.id.ckpsRefSRisingEdgeRadioButton);
		mergeOutputs = (CheckBox)getView().findViewById(R.id.ckpsMergeOutputsCheckBox);
		cogsNumber = (EditText)getView().findViewById(R.id.ckpsCogsNumberEditText);
		missingCogsNumber = (EditText)getView().findViewById(R.id.ckpsMissingCogsNumberEditText);
		cogsBeforeTDC = (EditText)getView().findViewById(R.id.ckpsCogsBeforeTDCEditText);
		engineCylinders = (EditText)getView().findViewById(R.id.ckpsEngineCylyndersEditText);
		ignitionPulseDelay = (EditText)getView().findViewById(R.id.ckpsIgnitionPulseDelayEditText);		
		
		OnCheckedChangeListener radioListener = new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (packet != null) {
					if (buttonView == ckpsFallingEdge) {
						packet.ckps_edge_type = isChecked?0:1;
					} else if (buttonView == ckpsRaisingEdge) {
						packet.ckps_edge_type = isChecked?1:0;						
					} else if (buttonView == refSFallingEdge) {
						packet.ref_s_edge_type = isChecked?0:1;						
					} else if (buttonView == refSRaisingEdge) {
						packet.ref_s_edge_type = isChecked?1:0;
					}					
				}				
			}
		};		
		ckpsFallingEdge.setOnCheckedChangeListener(radioListener);
		ckpsRaisingEdge.setOnCheckedChangeListener(radioListener);
		refSFallingEdge.setOnCheckedChangeListener(radioListener);
		refSRaisingEdge.setOnCheckedChangeListener(radioListener);
		
		mergeOutputs.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (packet != null) packet.ckps_merge_ign_outs = isChecked?1:0;
			}
		});
		
		cogsNumber.addTextChangedListener(new CustomTextWatcher(cogsNumber));
		missingCogsNumber.addTextChangedListener(new CustomTextWatcher(missingCogsNumber));
		cogsBeforeTDC.addTextChangedListener(new CustomTextWatcher(cogsBeforeTDC));
		engineCylinders.addTextChangedListener(new CustomTextWatcher(engineCylinders));
		ignitionPulseDelay.addTextChangedListener(new CustomTextWatcher(ignitionPulseDelay));
	}
	
	@Override
	public void onResume() {
		updateData();		
		super.onResume();		
	}

	@Override
	public void updateData() {
		if (packet != null) {			
			ckpsFallingEdge.setChecked(((CKPSPar)packet).ckps_edge_type == 0);
			ckpsRaisingEdge.setChecked(((CKPSPar)packet).ckps_edge_type != 0);
			refSFallingEdge.setChecked(((CKPSPar)packet).ref_s_edge_type == 0);
			refSRaisingEdge.setChecked(((CKPSPar)packet).ref_s_edge_type != 0);
			mergeOutputs.setChecked(((CKPSPar)packet).ckps_merge_ign_outs != 0);
			cogsNumber.setText(String.format(Locale.US,"%d",((CKPSPar)packet).ckps_cogs_num));
			missingCogsNumber.setText(String.format(Locale.US,"%d",((CKPSPar)packet).ckps_miss_num));
			cogsBeforeTDC.setText(String.format(Locale.US,"%d",((CKPSPar)packet).ckps_cogs_btdc));
			engineCylinders.setText(String.format(Locale.US,"%d",((CKPSPar)packet).ckps_engine_cyl));
			ignitionPulseDelay.setText(String.format(Locale.US,"%d",((CKPSPar)packet).ckps_ignit_cogs));
		}
	}

	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (CKPSPar) packet;				
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}

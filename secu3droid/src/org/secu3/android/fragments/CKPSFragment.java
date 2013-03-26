package org.secu3.android.fragments;

import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.CKPSPar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
			cogsNumber.setText(String.format("%d",((CKPSPar)packet).ckps_cogs_num));
			missingCogsNumber.setText(String.format("%d",((CKPSPar)packet).ckps_miss_num));
			cogsBeforeTDC.setText(String.format("%d",((CKPSPar)packet).ckps_cogs_btdc));
			engineCylinders.setText(String.format("%d",((CKPSPar)packet).ckps_engine_cyl));
			ignitionPulseDelay.setText(String.format("%d",((CKPSPar)packet).ckps_ignit_cogs));
		}
	}

	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (CKPSPar) packet;				
	}

	@Override
	public Secu3Dat getData() {
		if (packet == null) return null;
		packet.ckps_edge_type = ckpsFallingEdge.isChecked()?0:1;
		packet.ref_s_edge_type = refSFallingEdge.isChecked()?0:1;
		packet.ckps_merge_ign_outs = mergeOutputs.isChecked()?1:0;
		packet.ckps_cogs_num = Integer.valueOf(cogsNumber.getText().toString());
		packet.ckps_miss_num = Integer.valueOf(missingCogsNumber.getText().toString());
		packet.ckps_cogs_btdc = Integer.valueOf(cogsBeforeTDC.getText().toString());;
		packet.ckps_engine_cyl = Integer.valueOf(engineCylinders.getText().toString());
		packet.ckps_ignit_cogs = Integer.valueOf(ignitionPulseDelay.getText().toString());
		return packet;
	}
}

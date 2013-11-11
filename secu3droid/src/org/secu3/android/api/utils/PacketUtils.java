/* Secu3Droid - An open source, free manager for SECU-3 engine
 * control unit
 * Copyright (C) 2013 Maksim M. Levin. Russia, Voronezh
 * 
 * SECU-3  - An open source, free engine control unit
 * Copyright (C) 2007 Alexey A. Shabelnikov. Ukraine, Gorlovka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * contacts:
 *            http://secu-3.org
 *            email: mmlevin@mail.ru
*/

package org.secu3.android.api.utils;

import org.secu3.android.R;
import org.secu3.android.api.io.BaseProtoField;
import org.secu3.android.api.io.ProtoFieldFloat;
import org.secu3.android.api.io.ProtoFieldInteger;
import org.secu3.android.api.io.ProtoFieldString;
import org.secu3.android.api.io.Secu3Packet;
import org.secu3.android.parameters.ParamItemsAdapter;
import org.secu3.android.parameters.ParamPagerAdapter;
import org.secu3.android.parameters.items.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PacketUtils {
	
	float m_period_distance = 0f;
	
	public PacketUtils(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String pulses = sharedPreferences.getString(context.getString(R.string.pref_speed_pulse_key), context.getString(R.string.defaultSpeedPulse));
		m_period_distance = 1000.0f / (float)Integer.parseInt(pulses);
	}
	
	public Secu3Packet buildPacket (Secu3Packet packetSkeleton, ParamPagerAdapter paramAdapter, int packetId) {
		if ((packetSkeleton != null) && (packetSkeleton.getFields() != null) && (packetSkeleton.getFields().size() > 0)) {
			BaseProtoField field = null;
			for (int i = 0; i != packetSkeleton.getFields().size(); i++) {
				field = packetSkeleton.getFields().get(i);
				if (field.getNameId() == R.string.secur_par_flags_title) {
					int flags = 0;					
					ParamItemBoolean item = (ParamItemBoolean) paramAdapter.findItemByNameId(R.string.secur_par_use_immobilizer_title);
					if (item.getValue()) flags |= Secu3Packet.SECUR_USE_IMMO_FLAG;
					item = (ParamItemBoolean) paramAdapter.findItemByNameId(R.string.secur_par_use_bluetooth_title);
					if (item.getValue()) flags |= Secu3Packet.SECUR_USE_BT_FLAG;
					((ProtoFieldInteger) field).setValue (flags);
				}
				else {
					BaseParamItem item = paramAdapter.findItemByNameId(field.getNameId()); 
					if (item != null) {
						switch (item.getNameId()) {
							case R.string.miscel_baudrate_title:
								int baud_rate = Secu3Packet.BAUD_RATE_INDEX[((ParamItemSpinner) item).getIndex()];
								((ProtoFieldInteger) field).setValue (baud_rate);
								break;
							case R.string.adccor_map_sensor_correction_title:
							case R.string.adccor_voltage_sensor_correction_title:
							case R.string.adccor_temper_sensor_correction_title:
							case R.string.adccor_tps_sensor_correction_title:
							case R.string.adccor_addi1_sensor_correction_title:
							case R.string.adccor_addi2_sensor_correction_title:
							buildCorrection(paramAdapter, item);
								((ProtoFieldFloat) field).setValue (buildCorrection(paramAdapter, item));
								break;
							default:
								if (item instanceof ParamItemInteger) ((ProtoFieldInteger) field).setValue (((ParamItemInteger) item).getValue());
								else if (item instanceof ParamItemFloat) ((ProtoFieldFloat) field).setValue (((ParamItemFloat) item).getValue());
								else if (item instanceof ParamItemString) {
									String value = ((ParamItemString) item).getValue();
									if (value == null) value = "";
									((ProtoFieldString) field).setValue (value);
								}
								else if (item instanceof ParamItemBoolean) ((ProtoFieldInteger) field).setValue (((ParamItemBoolean) item).getValue()?1:0);
								else if (item instanceof ParamItemSpinner) ((ProtoFieldInteger) field).setValue (((ParamItemSpinner) item).getIndex());
								break;
						}
					}
				}
			} 
			return packetSkeleton;
		}
		return null;
	}

	private float buildCorrection(ParamPagerAdapter paramAdapter, BaseParamItem item) {
		int correctionId = item.getNameId();
		int factorId = 0;
		switch (correctionId) {
		case R.string.adccor_map_sensor_correction_title:
			factorId = R.string.adccor_map_sensor_factor_title;
			break;
		case R.string.adccor_voltage_sensor_correction_title:
			factorId = R.string.adccor_voltage_sensor_factor_title;
			break;
		case R.string.adccor_temper_sensor_correction_title:
			factorId = R.string.adccor_temper_sensor_factor_title;
			break;
		case R.string.adccor_tps_sensor_correction_title:
			factorId = R.string.adccor_tps_sensor_factor_title;
			break;
		case R.string.adccor_addi1_sensor_correction_title:
			factorId = R.string.adccor_addi1_sensor_factor_title;
			break;
		case R.string.adccor_addi2_sensor_correction_title:
			factorId = R.string.adccor_addi2_sensor_factor_title;
			break;
		}
		ParamItemFloat factorItem = (ParamItemFloat) paramAdapter.findItemByNameId(factorId);
		if (factorItem != null) {
			float factorValue = factorItem.getValue();
			float correctionValue = ((ParamItemFloat) item).getValue();
			correctionValue = correctionValue * 400 * factorValue;
			return correctionValue;
		}
		return 0;
	}
			
	public void setParamFromPacket (ParamPagerAdapter paramAdapter, Secu3Packet packet)
	{
		if ((packet != null) && (packet.getFields() != null) && (packet.getFields().size() > 0)) {
			BaseProtoField field = null;
			for (int i = 0; i != packet.getFields().size(); i++) {
				field = packet.getFields().get(i);
				if (field.getNameId() == R.string.secur_par_flags_title) {
					int flags = ((ProtoFieldInteger)field).getValue();
					((ParamItemBoolean)paramAdapter.findItemByNameId(R.string.secur_par_use_bluetooth_title)).setValue((flags & Secu3Packet.SECUR_USE_BT_FLAG) != 0);
					((ParamItemBoolean)paramAdapter.findItemByNameId(R.string.secur_par_use_immobilizer_title)).setValue((flags & Secu3Packet.SECUR_USE_IMMO_FLAG) != 0);
				} else {
					BaseParamItem item = paramAdapter.findItemByNameId(field.getNameId());					
					if (item != null) {
						switch (item.getNameId()) {
							case R.string.miscel_baudrate_title:
								int baud_rate_index = Secu3Packet.indexOf(Secu3Packet.BAUD_RATE_INDEX,((ProtoFieldInteger) field).getValue());
								((ParamItemSpinner) item).setIndex(baud_rate_index);
								break;
							case R.string.adccor_map_sensor_correction_title:
							case R.string.adccor_voltage_sensor_correction_title:
							case R.string.adccor_temper_sensor_correction_title:
							case R.string.adccor_tps_sensor_correction_title:
							case R.string.adccor_addi1_sensor_correction_title:
							case R.string.adccor_addi2_sensor_correction_title:
								((ParamItemFloat) item).setValue (calculateFactor(packet, field));
								break;
							default:
								if (item instanceof ParamItemInteger) ((ParamItemInteger) item).setValue(((ProtoFieldInteger) field).getValue());
								else if (item instanceof ParamItemFloat) ((ParamItemFloat) item).setValue(((ProtoFieldFloat) field).getValue());
								else if (item instanceof ParamItemBoolean) ((ParamItemBoolean) item).setValue((((ProtoFieldInteger) field).getValue()==1)?true:false);
								else if (item instanceof ParamItemSpinner) ((ParamItemSpinner) item).setIndex(((ProtoFieldInteger)field).getValue());
								break;
						}
					}		
				}
			}						
		}
	}

	private float calculateFactor(Secu3Packet packet, BaseProtoField field) {
		if ((packet != null) && (field != null)) {
			ProtoFieldFloat factorField;
			float correctionValue,factorValue;
			int correctionId = field.getNameId();
			int factorId = 0;
			switch (correctionId) {
				case R.string.adccor_map_sensor_correction_title:
					factorId = R.string.adccor_map_sensor_factor_title;
					break;
				case R.string.adccor_voltage_sensor_correction_title:
					factorId = R.string.adccor_voltage_sensor_factor_title;
					break;
				case R.string.adccor_temper_sensor_correction_title:
					factorId = R.string.adccor_temper_sensor_factor_title;
					break;
				case R.string.adccor_tps_sensor_correction_title:
					factorId = R.string.adccor_tps_sensor_factor_title;
					break;
				case R.string.adccor_addi1_sensor_correction_title:
					factorId = R.string.adccor_addi1_sensor_factor_title;
					break;
				case R.string.adccor_addi2_sensor_correction_title:
					factorId = R.string.adccor_addi2_sensor_factor_title;
					break;
			}
			correctionValue = ((ProtoFieldFloat) field).getValue();
			factorField = ((ProtoFieldFloat) packet.findField(factorId));
			if (factorField != null) {
				factorValue = factorField.getValue();
				if (factorValue != 0) {
					correctionValue = correctionValue / factorValue / 400;
				} else return 0;
				return correctionValue;
			}
		}
		return 0;
	}

	public static void setFunsetNames(ParamPagerAdapter paramAdapter, String[] funsetNames) {
		if (funsetNames != null) {			
			String data = "";
			for (int i=0; i != funsetNames.length-1; i++) {
				data += funsetNames[i] + "|";
			}
			data += funsetNames[funsetNames.length - 1];
			paramAdapter.setSpinnerItemValue(R.string.funset_maps_set_gasoline_title, data);
			paramAdapter.setSpinnerItemValue(R.string.funset_maps_set_gas_title, data);
		}
	}

	public static void setDiagInpFromPacket (ParamItemsAdapter adapter, Secu3Packet packet) {
		if (packet != null) {
			if (packet.getNameId() ==R.string.diaginp_dat_title) {
				if (adapter != null) {
					adapter.setFloatItem(R.string.diag_input_voltage_title, (((ProtoFieldFloat) packet.findField(R.string.diag_input_voltage_title)).getValue()));
					adapter.setFloatItem(R.string.diag_input_map_s, (((ProtoFieldFloat) packet.findField(R.string.diag_input_map_s)).getValue()));
					adapter.setFloatItem(R.string.diag_input_temp, (((ProtoFieldFloat) packet.findField(R.string.diag_input_temp)).getValue()));
					adapter.setFloatItem(R.string.diag_input_add_io1, (((ProtoFieldFloat) packet.findField(R.string.diag_input_add_io1)).getValue()));
					adapter.setFloatItem(R.string.diag_input_add_io2, (((ProtoFieldFloat) packet.findField(R.string.diag_input_add_io2)).getValue()));
					adapter.setFloatItem(R.string.diag_input_ks1_title, (((ProtoFieldFloat) packet.findField(R.string.diag_input_ks1_title)).getValue()));
					adapter.setFloatItem(R.string.diag_input_ks2_title, (((ProtoFieldFloat) packet.findField(R.string.diag_input_ks2_title)).getValue()));
					adapter.setBooleanItem(R.string.diag_input_carb_title, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_carb_title)).getValue())==1)?true:false);
					adapter.setBooleanItem(R.string.diag_input_gas_v, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield_title)).getValue() & (1 << 0)) != 0));
					adapter.setBooleanItem(R.string.diag_input_ckps, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield_title)).getValue() & (1 << 1)) != 0));
					adapter.setBooleanItem(R.string.diag_input_ref_s, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield_title)).getValue() & (1 << 2)) != 0));
					adapter.setBooleanItem(R.string.diag_input_ps, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield_title)).getValue() & (1 << 3)) != 0));
					adapter.setBooleanItem(R.string.diag_input_bl, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield_title)).getValue() & (1 << 4)) != 0));
					adapter.setBooleanItem(R.string.diag_input_de, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield_title)).getValue() & (1 << 5)) != 0));
				}
			}
		}
		
	}
	
	public float calcSpeed (int rawSpeed) {
		if ((rawSpeed != 0) && (rawSpeed != 65535)) {
			float period_s = (float)rawSpeed/250000.0f;
			float speed = (m_period_distance / period_s) * 3600.0f / 1000.0f;
			if (speed >= 999.9f) speed = 999.9f;
		}
		return 0;
	}
	
	public float calcDistance (int rawDistance) {
		float distance = m_period_distance * rawDistance / 1000.0f;
		if (distance > 9999.99f) distance = 9999.99f;
		return distance;
	}
}

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
import org.secu3.android.api.io.Secu3Packet;
import org.secu3.android.parameters.ParamItemsAdapter;
import org.secu3.android.parameters.ParamPagerAdapter;
import org.secu3.android.parameters.items.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.SparseArray;

public class PacketUtils {
	SparseArray<Secu3Packet> packetInputSkeletons = null;
	SparseArray<Secu3Packet> packetOutputSkeletons = null;
	
	float m_period_distance = 0f;
	
	public PacketUtils(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String pulses = sharedPreferences.getString(context.getString(R.string.pref_speed_pulse_key), context.getString(R.string.defaultSpeedPulse));
		m_period_distance = 1000.0f / (float)Integer.parseInt(pulses);
		packetInputSkeletons = new SparseArray<Secu3Packet>();
		packetOutputSkeletons = new SparseArray<Secu3Packet>();
	}
	
	public Secu3Packet buildPacket (ParamPagerAdapter paramAdapter, int packetId, int packetDir) {
		Secu3Packet packet = null;
		if ((packetDir == 0) || (packetDir == Secu3Packet.INPUT_TYPE)) {
			packet = packetInputSkeletons.get(packetId);
			if (packet == null) packet = packetOutputSkeletons.get(packetId);
		}
		else {
			packet = packetOutputSkeletons.get(packetId);
			if (packet == null) packet = packetInputSkeletons.get(packetId);
		}
		if ((packet != null) && (packet.getFields() != null) && (packet.getFields().size() > 0)) {
			BaseProtoField field = null;
			for (int i = 0; i != packet.getFields().size(); i++) {
				field = packet.getFields().get(i);
				if (field.getNameId() == R.string.secur_par_flags) {
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
						if (item.getNameId() == R.string.miscel_baudrate_title) {
							int baud_rate = Secu3Packet.BAUD_RATE_INDEX[((ParamItemSpinner) item).getIndex()];
							((ProtoFieldInteger) field).setValue (baud_rate);
						} else {
							if (item instanceof ParamItemInteger) ((ProtoFieldInteger) field).setValue (((ParamItemInteger) item).getValue());
							else if (item instanceof ParamItemFloat) ((ProtoFieldFloat) field).setValue (((ParamItemFloat) item).getValue());
							else if (item instanceof ParamItemBoolean) ((ProtoFieldInteger) field).setValue (((ParamItemBoolean) item).getValue()?1:0);
							else if (item instanceof ParamItemSpinner) ((ProtoFieldInteger) field).setValue (((ParamItemSpinner) item).getIndex());
						}
					}
				}
			} 
			return packet;
		}
		return null;
	}
			
	public void setParamFromPacket (ParamPagerAdapter paramAdapter, Secu3Packet packet)
	{
		if ((packet != null) && (packet.getFields() != null) && (packet.getFields().size() > 0)) {
			SparseArray<Secu3Packet> packetSkeletons = null;			
			if ((packet.getPacketDirResId() == 0) || (packet.getPacketDirResId() == Secu3Packet.INPUT_TYPE))
				packetSkeletons = packetInputSkeletons;
			else
				packetSkeletons = packetOutputSkeletons;
			if (packetSkeletons.indexOfKey(packet.getNameId()) < 0) {
				Secu3Packet packetSkeleton = new Secu3Packet(packet);
				packetSkeleton.reset();
				packetSkeletons.put(packet.getNameId(), packetSkeleton);
			}
			BaseProtoField field = null;
			for (int i = 0; i != packet.getFields().size(); i++) {
				field = packet.getFields().get(i);
				if (field.getNameId() == R.string.secur_par_flags) {
					int flags = ((ProtoFieldInteger)field).getValue();
					((ParamItemBoolean)paramAdapter.findItemByNameId(R.string.secur_par_use_bluetooth_title)).setValue((flags & Secu3Packet.SECUR_USE_BT_FLAG) != 0);
					((ParamItemBoolean)paramAdapter.findItemByNameId(R.string.secur_par_use_immobilizer_title)).setValue((flags & Secu3Packet.SECUR_USE_IMMO_FLAG) != 0);
				} else {
					BaseParamItem item = paramAdapter.findItemByNameId(field.getNameId());					
					if (item != null) {
						if (item.getNameId() == R.string.miscel_baudrate_title) {
							int baud_rate_index = Secu3Packet.indexOf(Secu3Packet.BAUD_RATE_INDEX,((ProtoFieldInteger) field).getValue());
							((ParamItemSpinner) item).setIndex(baud_rate_index);
						}
						else {
							if (item instanceof ParamItemInteger) ((ParamItemInteger) item).setValue(((ProtoFieldInteger) field).getValue());
							else if (item instanceof ParamItemFloat) ((ParamItemFloat) item).setValue(((ProtoFieldFloat) field).getValue());
							else if (item instanceof ParamItemBoolean) ((ParamItemBoolean) item).setValue((((ProtoFieldInteger) field).getValue()==1)?true:false);
							else if (item instanceof ParamItemSpinner) ((ParamItemSpinner) item).setIndex(((ProtoFieldInteger)field).getValue());
						}
					}		
				}
			}						
		}
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
					adapter.setFloatItem(R.string.diag_input_voltage, (((ProtoFieldFloat) packet.findField(R.string.diag_input_voltage)).getValue()));
					adapter.setFloatItem(R.string.diag_input_map_s, (((ProtoFieldFloat) packet.findField(R.string.diag_input_map_s)).getValue()));
					adapter.setFloatItem(R.string.diag_input_temp, (((ProtoFieldFloat) packet.findField(R.string.diag_input_temp)).getValue()));
					adapter.setFloatItem(R.string.diag_input_add_io1, (((ProtoFieldFloat) packet.findField(R.string.diag_input_add_io1)).getValue()));
					adapter.setFloatItem(R.string.diag_input_add_io2, (((ProtoFieldFloat) packet.findField(R.string.diag_input_add_io2)).getValue()));
					adapter.setFloatItem(R.string.diag_input_ks1, (((ProtoFieldFloat) packet.findField(R.string.diag_input_ks1)).getValue()));
					adapter.setFloatItem(R.string.diag_input_ks2, (((ProtoFieldFloat) packet.findField(R.string.diag_input_ks2)).getValue()));
					adapter.setBooleanItem(R.string.diag_input_carb, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_carb)).getValue())==1)?true:false);
					adapter.setBooleanItem(R.string.diag_input_gas_v, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield)).getValue() & (1 << 0)) != 0));
					adapter.setBooleanItem(R.string.diag_input_ckps, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield)).getValue() & (1 << 1)) != 0));
					adapter.setBooleanItem(R.string.diag_input_ref_s, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield)).getValue() & (1 << 2)) != 0));
					adapter.setBooleanItem(R.string.diag_input_ps, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield)).getValue() & (1 << 3)) != 0));
					adapter.setBooleanItem(R.string.diag_input_bl, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield)).getValue() & (1 << 4)) != 0));
					adapter.setBooleanItem(R.string.diag_input_de, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield)).getValue() & (1 << 5)) != 0));
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

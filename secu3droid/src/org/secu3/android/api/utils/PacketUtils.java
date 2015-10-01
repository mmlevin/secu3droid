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
import org.secu3.android.SettingsActivity;
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
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Locale;

public class PacketUtils {
	
	private float m_period_distance = 0f;
	private float engine_displacement = 1.0f;

	public static final float FUEL_DENSITY = 0.71f;
	public static final int MAX_FUEL_CONSTANT = 131072;

	private Context context;

	public UnioutUtils uniout;
	
	public PacketUtils(Context context) {
		this.context = context;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String pulses = sharedPreferences.getString(context.getString(R.string.pref_speed_pulse_key), context.getString(R.string.defaultSpeedPulse));
		float f_pulses = (float)Integer.parseInt(pulses);
		m_period_distance = 1000.0f / f_pulses;
		uniout = new UnioutUtils(context, 20000000,f_pulses);
	}
	
	public Secu3Packet buildPacket (Secu3Packet packetSkeleton, ParamPagerAdapter paramAdapter) {
		if ((packetSkeleton != null) && (packetSkeleton.getFields() != null) && (packetSkeleton.getFields().size() > 0)) {
			BaseProtoField field;
			BaseParamItem item;
			int fieldId;
			int flags = 0;
			for (int i = 0; i != packetSkeleton.getFields().size(); i++) {
				field = packetSkeleton.getFields().get(i);
				fieldId = field.getNameId();
				switch (fieldId) {
					case R.string.secur_par_flags_title:
						ParamItemBoolean item_secur = (ParamItemBoolean) paramAdapter.findItemByNameId(R.string.secur_par_use_immobilizer_title);
						if (item_secur.getValue()) flags |= Secu3Packet.SECUR_USE_IMMO_FLAG;
						item_secur = (ParamItemBoolean) paramAdapter.findItemByNameId(R.string.secur_par_use_bluetooth_title);
						if (item_secur.getValue()) flags |= Secu3Packet.SECUR_USE_BT_FLAG;
						((ProtoFieldInteger) field).setValue (flags);
						break;
					case R.string.miscel_baudrate_title:
						int baud_rate = Secu3Packet.BAUD_RATE_INDEX[((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.miscel_baudrate_title)).getIndex()];
						((ProtoFieldInteger) field).setValue (baud_rate);
						break;
					case R.string.injctr_par_injector_config_title:
						int config = ((ParamItemSpinner)paramAdapter.findItemByNameId(R.string.injctr_par_injector_config_title)).getIndex() << 4;
						config |= Secu3Packet.INJECTOR_SQIRTS_PER_CYCLE[((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.injctr_par_number_of_squirts_per_cycle_title)).getIndex()];
						((ProtoFieldInteger)field).setValue(config);
						break;
					case R.string.injctr_par_injector_cyl_disp_title:
						int engine_cylinders = ((ProtoFieldInteger)packetSkeleton.findField(R.string.injctr_par_cyl_num_title)).getValue();
						if (engine_cylinders != 0) {
							engine_displacement = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.injctr_par_enjine_displacement_title)).getValue();
							engine_displacement /= engine_cylinders;
						}
						((ProtoFieldFloat) field).setValue(engine_displacement);
						break;
					case R.string.adccor_map_sensor_correction_title:
					case R.string.adccor_voltage_sensor_correction_title:
					case R.string.adccor_temper_sensor_correction_title:
					case R.string.adccor_tps_sensor_correction_title:
					case R.string.adccor_addi1_sensor_correction_title:
					case R.string.adccor_addi2_sensor_correction_title:
						buildCorrection(paramAdapter, paramAdapter.findItemByNameId(field.getNameId()));
						((ProtoFieldFloat) field).setValue (buildCorrection(paramAdapter, paramAdapter.findItemByNameId(field.getNameId())));
						break;
					case R.string.temper_fan_pwm_freq_title:
						((ProtoFieldInteger) field).setValue((int) Math.round(524288.0/((ParamItemInteger)paramAdapter.findItemByNameId(R.string.temper_fan_pwm_freq_title)).getValue()));
						break;
					case R.string.uniout_par_unioutput_1_flags_title:
						flags = ((ParamItemSpinner)paramAdapter.findItemByNameId(R.string.unioutput1_logical_functions_title)).getIndex();
						if (flags >= UnioutUtils.UNIOUT_LF_COUNT -1) flags = UnioutUtils.UNIOUT_LF_NONE;
						flags <<= 4;
						flags |= ((ParamItemBoolean)paramAdapter.findItemByNameId(R.string.unioutput1_condition_1_inverse_title)).getValue()?0x01:0x00;
						flags |= ((ParamItemBoolean)paramAdapter.findItemByNameId(R.string.unioutput1_condition_2_inverse_title)).getValue()?0x02:0x00;
						((ProtoFieldInteger)field).setValue(flags);
						break;
					case R.string.uniout_par_unioutput_2_flags_title:
						flags = ((ParamItemSpinner)paramAdapter.findItemByNameId(R.string.unioutput2_logical_functions_title)).getIndex();
						if (flags >= UnioutUtils.UNIOUT_LF_COUNT -1) flags = UnioutUtils.UNIOUT_LF_NONE;
						flags <<= 4;
						flags |= ((ParamItemBoolean)paramAdapter.findItemByNameId(R.string.unioutput2_condition_1_inverse_title)).getValue()?0x01:0x00;
						flags |= ((ParamItemBoolean)paramAdapter.findItemByNameId(R.string.unioutput2_condition_2_inverse_title)).getValue()?0x02:0x00;
						((ProtoFieldInteger)field).setValue(flags);
						break;
					case R.string.uniout_par_unioutput_3_flags_title:
						flags = ((ParamItemSpinner)paramAdapter.findItemByNameId(R.string.unioutput3_logical_functions_title)).getIndex();
						if (flags >= UnioutUtils.UNIOUT_LF_COUNT -1) flags = UnioutUtils.UNIOUT_LF_NONE;
						flags <<= 4;
						flags |= ((ParamItemBoolean)paramAdapter.findItemByNameId(R.string.unioutput3_condition_1_inverse_title)).getValue()?0x01:0x00;
						flags |= ((ParamItemBoolean)paramAdapter.findItemByNameId(R.string.unioutput3_condition_2_inverse_title)).getValue()?0x02:0x00;
						((ProtoFieldInteger)field).setValue(flags);
						break;
					case R.string.uniout_par_logic_function_1_2_title:
						flags = ((ParamItemSpinner)paramAdapter.findItemByNameId(R.string.uniout_par_logic_function_1_2_title)).getIndex();
						if (flags >= UnioutUtils.UNIOUT_LF_COUNT -1) flags = UnioutUtils.UNIOUT_LF_NONE;
						((ProtoFieldInteger)field).setValue(flags);
						break;
					case R.string.unioutput1_condition1_on_value_title:
					case R.string.unioutput1_condition1_off_value_title:
					case R.string.unioutput1_condition2_on_value_title:
					case R.string.unioutput1_condition2_off_value_title:
					case R.string.unioutput2_condition1_on_value_title:
					case R.string.unioutput2_condition1_off_value_title:
					case R.string.unioutput2_condition2_on_value_title:
					case R.string.unioutput2_condition2_off_value_title:
					case R.string.unioutput3_condition1_on_value_title:
					case R.string.unioutput3_condition1_off_value_title:
					case R.string.unioutput3_condition2_on_value_title:
					case R.string.unioutput3_condition2_off_value_title:
						int uniout_condition = ((ProtoFieldInteger)packetSkeleton.findField(uniout.getConditionFieldIdForValue(fieldId))).getValue();
						 item = paramAdapter.findItemByNameId(fieldId);
						((ProtoFieldInteger)field).setValue(uniout.unioutEncodeCondVal(((ParamItemFloat) item).getValue(), uniout_condition));
						break;
					default:
						if ((item = paramAdapter.findItemByNameId(field.getNameId())) != null){
							if (item instanceof ParamItemInteger)
								((ProtoFieldInteger) field).setValue(((ParamItemInteger) item).getValue());
							else if (item instanceof ParamItemFloat)
								((ProtoFieldFloat) field).setValue(((ParamItemFloat) item).getValue());
							else if (item instanceof ParamItemString) {
								String value = ((ParamItemString) item).getValue();
								if (value == null) value = "";
								((ProtoFieldString) field).setValue(value);
							} else if (item instanceof ParamItemBoolean)
								((ProtoFieldInteger) field).setValue(((ParamItemBoolean) item).getValue() ? 1 : 0);
							else if (item instanceof ParamItemSpinner)
								((ProtoFieldInteger) field).setValue(((ParamItemSpinner) item).getIndex());
						}
						break;
				}
			}
			return packetSkeleton;
		}
		return null;
	}

	private float buildCorrection(ParamPagerAdapter paramAdapter, BaseParamItem item) {
		int factorId = 0;
		switch (item.getNameId()) {
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
		int uniout_flags,uniout_condition, fieldId;
		BaseParamItem item;

		if ((packet != null) && (packet.getFields() != null) && (packet.getFields().size() > 0)) {
			BaseProtoField field;
			for (int i = 0; i != packet.getFields().size(); i++) {
				field = packet.getFields().get(i);
				fieldId = field.getNameId();
				switch (fieldId) {
					case R.string.secur_par_flags_title: {
						int flags = ((ProtoFieldInteger) field).getValue();
						((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.secur_par_use_bluetooth_title)).setValue((flags & Secu3Packet.SECUR_USE_BT_FLAG) != 0);
						((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.secur_par_use_immobilizer_title)).setValue((flags & Secu3Packet.SECUR_USE_IMMO_FLAG) != 0);
					}
					break;
					case R.string.injctr_par_injector_config_title:
						int config = ((ProtoFieldInteger) field).getValue();
						((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.injctr_par_injector_config_title)).setIndex(config >> 4);
						((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.injctr_par_number_of_squirts_per_cycle_title)).setIndex(
								Secu3Packet.indexOf(Secu3Packet.INJECTOR_SQIRTS_PER_CYCLE, config & 0x0F));
						break;
					case R.string.injctr_par_injector_cyl_disp_title:
						engine_displacement = ((ProtoFieldFloat) field).getValue();
						break;
					case R.string.injctr_par_cyl_num_title:
						int engine_cylinders = ((ProtoFieldInteger) field).getValue();
						engine_displacement *= engine_cylinders;
						((ParamItemFloat) paramAdapter.findItemByNameId(R.string.injctr_par_enjine_displacement_title)).setValue(engine_displacement);
						break;
					case R.string.injctr_par_injector_sd_igl_const_title:
						int fuel_const = ((ProtoFieldInteger) field).getValue();
						Log.d("secu3", String.format("Get fuel constant %d", fuel_const));
						break;
					case R.string.miscel_baudrate_title:
						int baud_rate_index = Secu3Packet.indexOf(Secu3Packet.BAUD_RATE_INDEX, ((ProtoFieldInteger) field).getValue());
						((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.miscel_baudrate_title)).setIndex(baud_rate_index);
						break;
					case R.string.temper_fan_pwm_freq_title:
						((ParamItemInteger) paramAdapter.findItemByNameId(R.string.temper_fan_pwm_freq_title)).setValue((int) Math.round(524288.0 / ((ProtoFieldInteger) field).getValue()));
						break;
					case R.string.adccor_map_sensor_correction_title:
					case R.string.adccor_voltage_sensor_correction_title:
					case R.string.adccor_temper_sensor_correction_title:
					case R.string.adccor_tps_sensor_correction_title:
					case R.string.adccor_addi1_sensor_correction_title:
					case R.string.adccor_addi2_sensor_correction_title:
						((ParamItemFloat) paramAdapter.findItemByNameId(fieldId)).setValue(calculateFactor(packet, field));
						break;
					case R.string.unioutput1_condition_1_title:
					case R.string.unioutput2_condition_1_title:
					case R.string.unioutput3_condition_1_title:
						uniout_condition = ((ProtoFieldInteger)packet.findField(fieldId)).getValue();
						if (uniout_condition > UnioutUtils.UNIOUT_COND_TMR) uniout_condition--;
						((ParamItemSpinner) paramAdapter.findItemByNameId(fieldId)).setIndex(uniout_condition);
						break;
					case R.string.uniout_par_logic_function_1_2_title:
						uniout_flags = ((ProtoFieldInteger) field).getValue();
						if (uniout_flags == uniout.UNIOUT_LF_NONE) uniout_flags = uniout.UNIOUT_LF_COUNT - 1;
						((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.uniout_par_logic_function_1_2_title)).setIndex(uniout_flags);
						break;
					case R.string.uniout_par_unioutput_1_flags_title:
						uniout_flags = ((ProtoFieldInteger) field).getValue();
						((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.unioutput1_condition_1_inverse_title)).setValue((uniout_flags & 0x01) != 0);
						((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.unioutput1_condition_2_inverse_title)).setValue((uniout_flags & 0x02) != 0);
						if ((uniout_flags >>= 4) == uniout.UNIOUT_LF_NONE) uniout_flags = uniout.UNIOUT_LF_COUNT - 1;
						paramAdapter.findItemByNameId(R.string.unioutput1_condition_2_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT -1);
						paramAdapter.findItemByNameId(R.string.unioutput1_condition_2_inverse_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT -1);
						paramAdapter.findItemByNameId(R.string.unioutput1_condition2_on_value_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT -1);
						paramAdapter.findItemByNameId(R.string.unioutput1_condition2_off_value_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT -1);
 						((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.unioutput1_logical_functions_title)).setIndex(uniout_flags);
						break;
					case R.string.uniout_par_unioutput_2_flags_title:
						uniout_flags = ((ProtoFieldInteger) field).getValue();
						((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.unioutput2_condition_1_inverse_title)).setValue((uniout_flags & 0x01) != 0);
						((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.unioutput2_condition_2_inverse_title)).setValue((uniout_flags & 0x02) != 0);
						if ((uniout_flags >>= 4) == uniout.UNIOUT_LF_NONE) uniout_flags = uniout.UNIOUT_LF_COUNT - 1;
						paramAdapter.findItemByNameId(R.string.unioutput2_condition_2_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT -1);
						paramAdapter.findItemByNameId(R.string.unioutput2_condition_2_inverse_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT -1);
						paramAdapter.findItemByNameId(R.string.unioutput2_condition2_on_value_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT - 1);
						paramAdapter.findItemByNameId(R.string.unioutput2_condition2_off_value_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT - 1);
						((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.unioutput2_logical_functions_title)).setIndex(uniout_flags);
					break;
					case R.string.uniout_par_unioutput_3_flags_title:
						uniout_flags = ((ProtoFieldInteger) field).getValue();
						((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.unioutput3_condition_1_inverse_title)).setValue((uniout_flags & 0x01) != 0);
						((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.unioutput3_condition_2_inverse_title)).setValue((uniout_flags & 0x02) != 0);
						if ((uniout_flags >>= 4) == uniout.UNIOUT_LF_NONE) uniout_flags = uniout.UNIOUT_LF_COUNT - 1;
						paramAdapter.findItemByNameId(R.string.unioutput3_condition_2_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT -1);
						paramAdapter.findItemByNameId(R.string.unioutput3_condition_2_inverse_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT -1);
						paramAdapter.findItemByNameId(R.string.unioutput3_condition2_on_value_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT - 1);
						paramAdapter.findItemByNameId(R.string.unioutput3_condition2_off_value_title).setEnabled(uniout_flags < uniout.UNIOUT_LF_COUNT - 1);
						((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.unioutput3_logical_functions_title)).setIndex(uniout_flags);
						break;
					case R.string.unioutput1_condition1_on_value_title:
					case R.string.unioutput1_condition1_off_value_title:
					case R.string.unioutput1_condition2_on_value_title:
					case R.string.unioutput1_condition2_off_value_title:
					case R.string.unioutput2_condition1_on_value_title:
					case R.string.unioutput2_condition1_off_value_title:
					case R.string.unioutput2_condition2_on_value_title:
					case R.string.unioutput2_condition2_off_value_title:
					case R.string.unioutput3_condition1_on_value_title:
					case R.string.unioutput3_condition1_off_value_title:
					case R.string.unioutput3_condition2_on_value_title:
					case R.string.unioutput3_condition2_off_value_title:
						uniout_condition = ((ProtoFieldInteger)packet.findField(uniout.getConditionFieldIdForValue(fieldId))).getValue();
						item = paramAdapter.findItemByNameId(fieldId);
						uniout.setParametersItem(((ProtoFieldInteger) field).getValue(), uniout_condition, (ParamItemFloat) item);
						break;
					default:
						if ((item = paramAdapter.findItemByNameId(fieldId)) != null) {
									if (item instanceof ParamItemInteger) ((ParamItemInteger) item).setValue(((ProtoFieldInteger) field).getValue());
									else if (item instanceof ParamItemFloat) ((ParamItemFloat) item).setValue(((ProtoFieldFloat) field).getValue());
									else if (item instanceof ParamItemBoolean) ((ParamItemBoolean) item).setValue(((ProtoFieldInteger) field).getValue()==1);
									else if (item instanceof ParamItemSpinner) ((ParamItemSpinner) item).setIndex(((ProtoFieldInteger)field).getValue());
									break;
						}
						break;
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
					adapter.setFloatItem(R.string.diag_input_carb_title, (((ProtoFieldFloat) packet.findField(R.string.diag_input_carb_title)).getValue()));
					adapter.setBooleanItem(R.string.diag_input_gas_v, ((((ProtoFieldInteger) packet.findField(R.string.diag_input_bitfield_title)).getValue() & 1) != 0));
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
		float speed = 0;
		if ((rawSpeed != 0) && (rawSpeed != 65535)) {
			float period_s = (float)rawSpeed/250000.0f;
			speed = (m_period_distance / period_s) * 3600.0f / 1000.0f;
			if (speed >= 999.9f) speed = 999.9f;
		}
		return speed;
	}
	
	public float calcDistance (int rawDistance) {
		float distance = m_period_distance * rawDistance / 1000.0f;
		if (distance > 9999.99f) distance = 9999.99f;
		return distance;
	}

	public static int calcInjectorConstant (float cylynder_displacement, int engine_cylinders, int injector_config, int injector_squirt_num, float injector_flow_rate)
	{
		//Log.d("secu3", String.format("Cylinder displacement %f", cylynder_displacement));
		//Log.d("secu3", String.format("Engine cylynders %d", engine_cylinders));
		//Log.d("secu3", String.format("Injector config %d", injector_config));
		//Log.d("secu3", String.format("Squirt number %d", injector_squirt_num));
		//Log.d("secu3", String.format("Flow rate %f", injector_flow_rate));
		int inj_num, bnk_num;
		switch (injector_config) {
			case Secu3Packet.INJCFG_TROTTLEBODY:
				inj_num = 1;
				bnk_num = 1;
				break;
			case Secu3Packet.INJCFG_SIMULTANEOUS:
				inj_num = engine_cylinders;
				bnk_num = 1;
				break;
			case Secu3Packet.INJCFG_SEMISEQUENTIAL:
				inj_num = engine_cylinders;
				bnk_num = engine_cylinders / 2;
				break;
			case Secu3Packet.INJCFG_FULLSEQUENTIAL:
			default:
				inj_num = engine_cylinders;
				bnk_num = engine_cylinders;
				break;
		}
		float mifr = injector_flow_rate * FUEL_DENSITY;
		return Math.round(((cylynder_displacement * 3.482f * 18750000.0f) / mifr) * ((float)bnk_num * (float) engine_cylinders) / ((float)inj_num * (float)injector_squirt_num));
	}

	public String getSensorString(int protocol_version, Secu3Packet packet) {
		String result = "";
		result += (String.format(Locale.US, context.getString(R.string.status_rpm_title), ((ProtoFieldInteger) packet.getField(R.string.sensor_dat_rpm_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.status_map_title),((ProtoFieldFloat) packet.getField(R.string.sensor_dat_map_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.status_voltage_title),((ProtoFieldFloat) packet.getField(R.string.sensor_dat_voltage_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.status_temperature_title),((ProtoFieldFloat) packet.getField(R.string.sensor_dat_temperature_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.status_angle_correction_title),((ProtoFieldFloat) packet.getField(R.string.sensor_dat_angle_correction_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.status_knock_title),((ProtoFieldFloat) packet.getField(R.string.sensor_dat_knock_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.status_knock_retard_title),((ProtoFieldFloat) packet.getField(R.string.sensor_dat_knock_retard_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.status_air_flow_title),((ProtoFieldInteger) packet.getField(R.string.sensor_dat_air_flow_title)).getValue()));
		int bitfield = ((ProtoFieldInteger) packet.getField(R.string.sensor_dat_bitfield_title)).getValue();
		result += (String.format(Locale.US,context.getString(R.string.status_fi_valve_title),Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_EPHH_VALVE)));
		result += (String.format(Locale.US,context.getString(R.string.status_carb_status_title),Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_CARB)));
		result += (String.format(Locale.US,context.getString(R.string.status_gas_valve_title),Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_GAS)));
		result += (String.format(Locale.US,context.getString(R.string.status_power_valve_title),Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_EPM_VALVE)));
		result += (String.format(Locale.US,context.getString(R.string.status_ecf_title),Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_COOL_FAN)));
		result += (String.format(Locale.US,context.getString(R.string.status_starter_block_title),Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_ST_BLOCK)));
		result += (String.format(Locale.US,context.getString(R.string.status_addi1_voltage_title),((ProtoFieldFloat) packet.getField(R.string.sensor_dat_addi1_voltage_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.status_addi2_voltage_title),((ProtoFieldFloat) packet.getField(R.string.sensor_dat_addi2_voltage_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.status_tps_title),((ProtoFieldFloat) packet.getField(R.string.sensor_dat_tps_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.status_choke_position_title),((ProtoFieldFloat) packet.getField(R.string.sensor_dat_choke_position_title)).getValue()));

		if (protocol_version >= SettingsActivity.PROTOCOL_28082013_SUMMER_RELEASE) {
			result += (String.format(Locale.US,context.getString(R.string.status_speed_title),calcSpeed(((ProtoFieldInteger) packet.getField(R.string.sensor_dat_speed_title)).getValue())));
			result += (String.format(Locale.US,context.getString(R.string.status_distance_title),calcDistance(((ProtoFieldInteger) packet.getField(R.string.sensor_dat_distance_title)).getValue())));
		}

		return result;
	}

	public String getRawSensorString (int protocol_version, Secu3Packet packet) {
		String result = "";
		result += (String.format(Locale.US,context.getString(R.string.raw_status_map_title),((ProtoFieldFloat) packet.getField(R.string.adcraw_map_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.raw_status_voltage_title),((ProtoFieldFloat) packet.getField(R.string.adcraw_voltage_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.raw_status_temperature_title),((ProtoFieldFloat) packet.getField(R.string.adcraw_temperature_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.raw_status_knock_title),((ProtoFieldFloat) packet.getField(R.string.adcraw_knock_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.raw_status_tps_title),((ProtoFieldFloat) packet.getField(R.string.adcraw_tps_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.raw_status_addi1_title),((ProtoFieldFloat) packet.getField(R.string.adcraw_addi1_title)).getValue()));
		result += (String.format(Locale.US,context.getString(R.string.raw_status_addi2_title),((ProtoFieldFloat) packet.getField(R.string.adcraw_addi2_title)).getValue()));
		return result;
	}
}

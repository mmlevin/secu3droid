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
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.ADCCorPar;
import org.secu3.android.api.io.Secu3Dat.AnglesPar;
import org.secu3.android.api.io.Secu3Dat.CKPSPar;
import org.secu3.android.api.io.Secu3Dat.CarburPar;
import org.secu3.android.api.io.Secu3Dat.ChokePar;
import org.secu3.android.api.io.Secu3Dat.FnNameDat;
import org.secu3.android.api.io.Secu3Dat.FunSetPar;
import org.secu3.android.api.io.Secu3Dat.IdlRegPar;
import org.secu3.android.api.io.Secu3Dat.MiscelPar;
import org.secu3.android.api.io.Secu3Dat.StartrPar;
import org.secu3.android.api.io.Secu3Dat.TemperPar;
import org.secu3.android.parameters.ParamPagerAdapter;
import org.secu3.android.parameters.items.*;

public class PacketUtils {
	public static Secu3Dat build (ParamPagerAdapter paramAdapter, char packetType) {
		if (paramAdapter != null) {
			switch (packetType) {
			case Secu3Dat.ADCCOR_PAR:
				return buildAdcCorPar (paramAdapter);
			case Secu3Dat.ANGLES_PAR:
				return buildAnglesPar (paramAdapter);
			case Secu3Dat.CARBUR_PAR:
				return buildCarburPar (paramAdapter);
			case Secu3Dat.CHOKE_PAR:
				return buildChokePar (paramAdapter);
			case Secu3Dat.CKPS_PAR:
				return buildCkpsPar (paramAdapter);
			case Secu3Dat.FUNSET_PAR:
				return buildFunsetPar (paramAdapter);
			case Secu3Dat.IDLREG_PAR:
				return buildIdlregPar (paramAdapter);
			case Secu3Dat.MISCEL_PAR:
				return buildMiscelPar (paramAdapter);
			case Secu3Dat.STARTR_PAR:
				return buildStartrPar (paramAdapter);
			case Secu3Dat.TEMPER_PAR:
				return buildTemperPar (paramAdapter);
			default: throw new IllegalArgumentException(String.format("Packet type %s not allowed", packetType));
			}
		}
		return null;
	}
	
	public static Secu3Dat build (ParamPagerAdapter paramAdapter, int itemId)
	{
		switch (itemId) {
		case R.string.starter_off_title:
		case R.string.starter_map_abandon_title:
			return buildStartrPar (paramAdapter);
		case R.string.angles_min_angle_title:
		case R.string.angles_max_angle_title:
		case R.string.angles_angle_decrement_step_title:
		case R.string.angles_angle_increment_step_title:
		case R.string.angles_zero_angle_title:
		case R.string.angles_octane_correction_title:
			return buildAnglesPar(paramAdapter);
		case R.string.idlreg_ifac1_title:
		case R.string.idlreg_ifac2_title:
		case R.string.idlreg_minimal_angle_title:
		case R.string.idlreg_maximal_angle_title:
		case R.string.idlreg_target_rpm_title:
		case R.string.idlreg_rpm_sensitivity_title:
		case R.string.idlreg_turn_on_temp_title:
		case R.string.idlreg_use_idle_regulator_title:
			return buildIdlregPar(paramAdapter);
		case R.string.funset_maps_set_gasoline_title:
		case R.string.funset_maps_set_gas_title:
		case R.string.funset_lower_pressure_title:
		case R.string.funset_upper_pressure_title:
		case R.string.funset_map_sensor_offset_title:
		case R.string.funset_map_sensor_gradient_title:
		case R.string.funset_tps_curve_offset_title:
		case R.string.funset_tps_curve_gradient_title:
			return buildFunsetPar(paramAdapter);
		case R.string.temper_fan_on_title:
		case R.string.temper_fan_off_title:
		case R.string.temper_use_temp_sensor_title:
		case R.string.temper_use_pwm_title:
		case R.string.temper_use_table:
			return buildTemperPar(paramAdapter);
		case R.string.carbur_overrun_lower_threshold_gasoline_title:
		case R.string.carbur_overrun_upper_threshold_gasoline_title:
		case R.string.carbur_overrun_lower_threshold_gas_title:
		case R.string.carbur_overrun_upper_threshold_gas_title:
		case R.string.carbur_overrun_valve_delay:
		case R.string.carbur_sensor_inverse_title:
		case R.string.carbur_epm_valve_on_pressure_title:
		case R.string.carbur_tps_threshold_title:
			return buildCarburPar(paramAdapter);
		case R.string.adccor_map_sensor_factor_title:
		case R.string.adccor_map_sensor_correction_title:
		case R.string.adccor_voltage_sensor_factor_title:
		case R.string.adccor_voltage_sensor_correction_title:
		case R.string.adccor_temper_sensor_factor_title:
		case R.string.adccor_temper_sensor_correction:
		case R.string.adccor_tps_sensor_factor_title:
		case R.string.adccor_tps_sensor_correction_title:
		case R.string.adccor_addi1_sensor_factor_title:
		case R.string.adccor_addi1_sensor_correction_title:
		case R.string.adccor_addi2_sensor_factor_title:
		case R.string.adccor_addi2_sensor_correction_title:
			return buildAdcCorPar (paramAdapter);
		case R.string.ckps_ckp_edge_title:
		case R.string.ckps_ref_s_edge_title:
		case R.string.ckps_merge_outputs:
		case R.string.ckps_cogs_number_title:
		case R.string.ckps_missing_cogs_number_title:
		case R.string.ckps_cogs_before_tdc_title:
		case R.string.ckps_engine_cylynders_title:
		case R.string.ckps_ignition_pulse_delay_title:
			return buildCkpsPar (paramAdapter);
		case R.string.miscel_baudrate_title:
		case R.string.miscel_period_title:
		case R.string.miscel_ignition_cutoff_title:
		case R.string.miscel_ignition_cutoff_rpm_title:
		case R.string.miscel_hall_output_start_title:
		case R.string.miscel_hall_output_delay_title:
			return buildMiscelPar (paramAdapter);
		case R.string.choke_steps_title:
		case R.string.choke_manual_step_down:
		case R.string.choke_manual_step_up:
		case R.string.choke_testing_title:
			return buildChokePar (paramAdapter);
		default: throw new IllegalArgumentException("Wrong item ID");
		}	
	}

	public static boolean isParamFromPage (int itemId, int pageId)
	{
		switch (itemId) {
		case R.string.starter_off_title:
		case R.string.starter_map_abandon_title:
			return (pageId == R.string.starter_title); 
		case R.string.angles_min_angle_title:
		case R.string.angles_max_angle_title:
		case R.string.angles_angle_decrement_step_title:
		case R.string.angles_angle_increment_step_title:
		case R.string.angles_zero_angle_title:
		case R.string.angles_octane_correction_title:
			return (pageId == R.string.angles_title);
		case R.string.idlreg_ifac1_title:
		case R.string.idlreg_ifac2_title:
		case R.string.idlreg_minimal_angle_title:
		case R.string.idlreg_maximal_angle_title:
		case R.string.idlreg_target_rpm_title:
		case R.string.idlreg_rpm_sensitivity_title:
		case R.string.idlreg_turn_on_temp_title:
		case R.string.idlreg_use_idle_regulator_title:
			return (pageId == R.string.idling_title);
		case R.string.funset_maps_set_gasoline_title:
		case R.string.funset_maps_set_gas_title:
		case R.string.funset_lower_pressure_title:
		case R.string.funset_upper_pressure_title:
		case R.string.funset_map_sensor_offset_title:
		case R.string.funset_map_sensor_gradient_title:
		case R.string.funset_tps_curve_offset_title:
		case R.string.funset_tps_curve_gradient_title:
			return (pageId == R.string.functions_title);
		case R.string.temper_fan_on_title:
		case R.string.temper_fan_off_title:
		case R.string.temper_use_temp_sensor_title:
		case R.string.temper_use_pwm_title:
		case R.string.temper_use_table:
			return (pageId == R.string.temperature_title);
		case R.string.carbur_overrun_lower_threshold_gasoline_title:
		case R.string.carbur_overrun_upper_threshold_gasoline_title:
		case R.string.carbur_overrun_lower_threshold_gas_title:
		case R.string.carbur_overrun_upper_threshold_gas_title:
		case R.string.carbur_overrun_valve_delay:
		case R.string.carbur_sensor_inverse_title:
		case R.string.carbur_epm_valve_on_pressure_title:
		case R.string.carbur_tps_threshold_title:
			return (pageId == R.string.carburetor_title);
		case R.string.adccor_map_sensor_factor_title:
		case R.string.adccor_map_sensor_correction_title:
		case R.string.adccor_voltage_sensor_factor_title:
		case R.string.adccor_voltage_sensor_correction_title:
		case R.string.adccor_temper_sensor_factor_title:
		case R.string.adccor_temper_sensor_correction:
		case R.string.adccor_tps_sensor_factor_title:
		case R.string.adccor_tps_sensor_correction_title:
		case R.string.adccor_addi1_sensor_factor_title:
		case R.string.adccor_addi1_sensor_correction_title:
		case R.string.adccor_addi2_sensor_factor_title:
		case R.string.adccor_addi2_sensor_correction_title:
			return (pageId == R.string.adc_errors_title);
		case R.string.ckps_ckp_edge_title:
		case R.string.ckps_ref_s_edge_title:
		case R.string.ckps_merge_outputs:
		case R.string.ckps_cogs_number_title:
		case R.string.ckps_missing_cogs_number_title:
		case R.string.ckps_cogs_before_tdc_title:
		case R.string.ckps_engine_cylynders_title:
		case R.string.ckps_ignition_pulse_delay_title:
			return (pageId == R.string.ckps_title);
		case R.string.miscel_baudrate_title:
		case R.string.miscel_period_title:
		case R.string.miscel_ignition_cutoff_title:
		case R.string.miscel_ignition_cutoff_rpm_title:
		case R.string.miscel_hall_output_start_title:
		case R.string.miscel_hall_output_delay_title:
			return (pageId == R.string.miscellaneous_title);
		case R.string.choke_steps_title:
		case R.string.choke_manual_step_down:
		case R.string.choke_manual_step_up:
		case R.string.choke_testing_title:
			return (pageId == R.string.choke_control_title);
		default: throw new IllegalArgumentException("Wrong item ID");
		}	
	}
	
	public static void setDataFromPacket (ParamPagerAdapter paramAdapter, Secu3Dat packet)
	{
		if (packet instanceof StartrPar) {
			setStartrPar(paramAdapter, (StartrPar) packet);
		} else if (packet instanceof AnglesPar) {			
			setAnglesPar(paramAdapter, (AnglesPar) packet);
		} else if (packet instanceof IdlRegPar) {
			setIdlRegPar(paramAdapter, (IdlRegPar) packet);
		} else if (packet instanceof FnNameDat) {
			setFnNameDat(paramAdapter, (FnNameDat) packet);			
		}else if (packet instanceof FunSetPar) {
			setFunSetPar(paramAdapter, (FunSetPar) packet);
		} else if (packet instanceof TemperPar) {
			setTemperPar(paramAdapter, (TemperPar) packet);
		} else if (packet instanceof CarburPar) {
			setCarburPar(paramAdapter, (CarburPar) packet);
		} else if (packet instanceof ADCCorPar) {
			setAdcCorPar(paramAdapter, (ADCCorPar) packet);
		} else if (packet instanceof CKPSPar) {
			setCkpsPar(paramAdapter, (CKPSPar) packet);
		} else if (packet instanceof MiscelPar) {
			setMiscelPar(paramAdapter, (MiscelPar) packet);
		} else if (packet instanceof ChokePar) {
			setChokePar(paramAdapter, (ChokePar) packet);
		}
	}

	public static void setChokePar(ParamPagerAdapter paramAdapter, ChokePar packet) {
		paramAdapter.setIntegerItem(R.string.choke_steps_title, packet.steps);
	}

	public static void setMiscelPar(ParamPagerAdapter paramAdapter, MiscelPar packet) {
		paramAdapter.setSpinnerItemIndex(R.string.miscel_baudrate_title, Secu3Dat.indexOf (Secu3Dat.BAUD_RATE_INDEX,((MiscelPar)packet).baud_rate_index));
		paramAdapter.setIntegerItem(R.string.miscel_period_title, packet.period_ms);
		paramAdapter.setBooleanItem(R.string.miscel_ignition_cutoff_title, packet.ign_cutoff == 1);
		paramAdapter.setIntegerItem(R.string.miscel_ignition_cutoff_rpm_title, packet.ign_cutoff_thrd);
		paramAdapter.setIntegerItem(R.string.miscel_hall_output_start_title, packet.hop_start_cogs);
		paramAdapter.setIntegerItem(R.string.miscel_hall_output_delay_title, packet.hop_durat_cogs);
	}

	public static void setCkpsPar(ParamPagerAdapter paramAdapter, CKPSPar packet) {
		paramAdapter.setSpinnerItemIndex(R.string.ckps_ckp_edge_title, packet.ckps_edge_type);
		paramAdapter.setSpinnerItemIndex(R.string.ckps_ref_s_edge_title, packet.ref_s_edge_type);
		paramAdapter.setBooleanItem(R.string.ckps_merge_outputs, packet.ckps_merge_ign_outs == 1);
		paramAdapter.setIntegerItem(R.string.ckps_cogs_number_title, packet.ckps_cogs_num);
		paramAdapter.setIntegerItem(R.string.ckps_missing_cogs_number_title, packet.ckps_miss_num);
		paramAdapter.setIntegerItem(R.string.ckps_cogs_before_tdc_title, packet.ckps_cogs_btdc);
		paramAdapter.setIntegerItem(R.string.ckps_engine_cylynders_title, packet.ckps_engine_cyl);
		paramAdapter.setIntegerItem(R.string.ckps_ignition_pulse_delay_title, packet.ckps_ignit_cogs);
	}

	public static void setAdcCorPar(ParamPagerAdapter paramAdapter, ADCCorPar packet) {
		paramAdapter.setFloatItem(R.string.adccor_map_sensor_factor_title, packet.map_adc_factor);
		paramAdapter.setFloatItem(R.string.adccor_map_sensor_correction_title, packet.map_adc_correction);
		paramAdapter.setFloatItem(R.string.adccor_voltage_sensor_factor_title, packet.ubat_adc_factor);
		paramAdapter.setFloatItem(R.string.adccor_voltage_sensor_correction_title, packet.ubat_adc_correction);
		paramAdapter.setFloatItem(R.string.adccor_temper_sensor_factor_title, packet.temp_adc_factor);
		paramAdapter.setFloatItem(R.string.adccor_temper_sensor_correction, packet.temp_adc_correction);
		paramAdapter.setFloatItem(R.string.adccor_tps_sensor_factor_title, packet.tps_adc_factor);
		paramAdapter.setFloatItem(R.string.adccor_tps_sensor_correction_title, packet.tps_adc_correction);
		paramAdapter.setFloatItem(R.string.adccor_addi1_sensor_factor_title, packet.add_i1_factor);
		paramAdapter.setFloatItem(R.string.adccor_addi1_sensor_correction_title, packet.add_i1_correction);
		paramAdapter.setFloatItem(R.string.adccor_addi2_sensor_factor_title, packet.add_i2_factor);
		paramAdapter.setFloatItem(R.string.adccor_addi2_sensor_correction_title, packet.add_i2_correction);
	}

	public static void setCarburPar(ParamPagerAdapter paramAdapter, CarburPar packet) {
		paramAdapter.setIntegerItem(R.string.carbur_overrun_lower_threshold_gasoline_title, packet.ephh_lot);
		paramAdapter.setIntegerItem(R.string.carbur_overrun_upper_threshold_gasoline_title, packet.ephh_hit);
		paramAdapter.setIntegerItem(R.string.carbur_overrun_lower_threshold_gas_title, packet.ephh_lot_g);
		paramAdapter.setIntegerItem(R.string.carbur_overrun_upper_threshold_gas_title, packet.ephh_hit_g);
		paramAdapter.setFloatItem(R.string.carbur_overrun_valve_delay, packet.shutoff_delay);
		paramAdapter.setBooleanItem(R.string.carbur_sensor_inverse_title, packet.carb_invers == 1);
		paramAdapter.setFloatItem(R.string.carbur_epm_valve_on_pressure_title, packet.epm_ont);
		paramAdapter.setFloatItem(R.string.carbur_tps_threshold_title, packet.tps_threshold);
	}

	public static void setTemperPar(ParamPagerAdapter paramAdapter, TemperPar packet) {
		paramAdapter.setFloatItem(R.string.temper_fan_on_title, packet.vent_on);
		paramAdapter.setFloatItem(R.string.temper_fan_off_title, packet.vent_off);
		paramAdapter.setBooleanItem(R.string.temper_use_temp_sensor_title, packet.tmp_use == 1);
		paramAdapter.setBooleanItem(R.string.temper_use_pwm_title, packet.vent_pwm == 1);
		paramAdapter.setBooleanItem(R.string.temper_use_table, packet.cts_use_map == 1);
	}

	public static void setFunSetPar(ParamPagerAdapter paramAdapter, FunSetPar packet) {
		paramAdapter.setSpinnerItemIndex(R.string.funset_maps_set_gasoline_title, packet.fn_benzin);
		paramAdapter.setSpinnerItemIndex(R.string.funset_maps_set_gas_title, packet.fn_gas);
		paramAdapter.setFloatItem(R.string.funset_lower_pressure_title, packet.map_lower_pressure);
		paramAdapter.setFloatItem(R.string.funset_upper_pressure_title, packet.map_upper_pressure);
		paramAdapter.setFloatItem(R.string.funset_map_sensor_offset_title, packet.map_curve_offset);
		paramAdapter.setFloatItem(R.string.funset_map_sensor_gradient_title, packet.map_curve_gradient);
		paramAdapter.setFloatItem(R.string.funset_tps_curve_offset_title, packet.tps_curve_offset);
		paramAdapter.setFloatItem(R.string.funset_tps_curve_gradient_title, packet.tps_curve_gradient);
	}

	public static void setFnNameDat(ParamPagerAdapter paramAdapter, FnNameDat packet) {
		if (packet.names_available()) {			
			String[] tableNames = new String [packet.names.length];
			System.arraycopy(packet.names, 0, tableNames, 0, packet.names.length);
			String data = "";
			for (int i=0; i != tableNames.length-1; i++) {
				data += tableNames[i] + "|";
			}
			data += tableNames[tableNames.length - 1];
			paramAdapter.setSpinnerItemValue(R.string.funset_maps_set_gasoline_title, data);
			paramAdapter.setSpinnerItemValue(R.string.funset_maps_set_gas_title, data);
		}
	}

	public static void setIdlRegPar(ParamPagerAdapter paramAdapter, IdlRegPar packet) {
		paramAdapter.setFloatItem (R.string.idlreg_ifac1_title,packet.ifac1);
		paramAdapter.setFloatItem (R.string.idlreg_ifac2_title,packet.ifac2);
		paramAdapter.setFloatItem (R.string.idlreg_minimal_angle_title,packet.min_angle);
		paramAdapter.setFloatItem (R.string.idlreg_maximal_angle_title,packet.max_angle);
		paramAdapter.setIntegerItem (R.string.idlreg_target_rpm_title,packet.idling_rpm);
		paramAdapter.setIntegerItem (R.string.idlreg_rpm_sensitivity_title,packet.MINEFR);
		paramAdapter.setFloatItem (R.string.idlreg_turn_on_temp_title,packet.turn_on_temp);
		paramAdapter.setBooleanItem(R.string.idlreg_use_idle_regulator_title,packet.idl_regul == 1);
	}

	public static void setAnglesPar(ParamPagerAdapter paramAdapter, AnglesPar packet) {
		paramAdapter.setFloatItem (R.string.angles_min_angle_title,packet.min_angle);
		paramAdapter.setFloatItem (R.string.angles_max_angle_title,packet.max_angle);
		paramAdapter.setFloatItem (R.string.angles_angle_decrement_step_title,packet.dec_spead);
		paramAdapter.setFloatItem (R.string.angles_angle_increment_step_title,packet.inc_spead);
		paramAdapter.setBooleanItem(R.string.angles_zero_angle_title,packet.zero_adv_ang == 1);
		paramAdapter.setFloatItem (R.string.angles_octane_correction_title,packet.angle_corr);
	}

	public static void setStartrPar(ParamPagerAdapter paramAdapter, StartrPar packet) {
		paramAdapter.setIntegerItem(R.string.starter_off_title, packet.starter_off);
		paramAdapter.setIntegerItem(R.string.starter_map_abandon_title, packet.smap_abandon);
	}
	
	public static Secu3Dat buildTemperPar(ParamPagerAdapter paramAdapter) {
		TemperPar packet = new TemperPar();
		packet.vent_on = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.temper_fan_on_title)).getValue();
		packet.vent_off = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.temper_fan_off_title)).getValue();
		packet.tmp_use = ((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.temper_use_temp_sensor_title)).getValue()?1:0;
		packet.vent_pwm = ((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.temper_use_pwm_title)).getValue()?1:0;
		packet.cts_use_map = ((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.temper_use_table)).getValue()?1:0;
		return packet;
	}

	public static Secu3Dat buildStartrPar(ParamPagerAdapter paramAdapter) {
		StartrPar packet = new StartrPar();
		packet.starter_off = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.starter_off_title)).getValue();
		packet.smap_abandon = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.starter_map_abandon_title)).getValue();
		return packet;
	}

	public static Secu3Dat buildMiscelPar(ParamPagerAdapter paramAdapter) {
		MiscelPar packet = new MiscelPar();
		int pos = ((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.miscel_baudrate_title)).getIndex();
		packet.baud_rate = Secu3Dat.BAUD_RATE[pos];
		packet.baud_rate_index = Secu3Dat.BAUD_RATE_INDEX[pos];
		packet.period_ms = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.miscel_period_title)).getValue();
		packet.ign_cutoff = ((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.miscel_ignition_cutoff_title)).getValue()?1:0;
		packet.ign_cutoff_thrd = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.miscel_ignition_cutoff_rpm_title)).getValue();
		packet.hop_start_cogs = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.miscel_hall_output_start_title)).getValue();
		packet.hop_durat_cogs = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.miscel_hall_output_delay_title)).getValue();
		return packet;
	}

	public static Secu3Dat buildIdlregPar(ParamPagerAdapter paramAdapter) {
		IdlRegPar packet = new IdlRegPar();
		packet.ifac1 = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.idlreg_ifac1_title)).getValue();
		packet.ifac2 = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.idlreg_ifac2_title)).getValue();
		packet.min_angle = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.idlreg_minimal_angle_title)).getValue();
		packet.max_angle = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.idlreg_maximal_angle_title)).getValue();
		packet.idling_rpm = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.idlreg_target_rpm_title)).getValue();
		packet.MINEFR = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.idlreg_rpm_sensitivity_title)).getValue();
		packet.turn_on_temp = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.idlreg_turn_on_temp_title)).getValue();
		packet.idl_regul = ((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.idlreg_use_idle_regulator_title)).getValue()?1:0;
		return packet;
	}

	public static Secu3Dat buildFunsetPar(ParamPagerAdapter paramAdapter) {
		FunSetPar packet = new FunSetPar();
		packet.map_lower_pressure = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.funset_lower_pressure_title)).getValue();
		packet.map_upper_pressure = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.funset_upper_pressure_title)).getValue();
		packet.map_curve_offset = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.funset_map_sensor_offset_title)).getValue();
		packet.map_curve_gradient = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.funset_map_sensor_gradient_title)).getValue();
		packet.fn_gas = ((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.funset_maps_set_gas_title)).getIndex();
		packet.fn_benzin = ((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.funset_maps_set_gasoline_title)).getIndex();
		packet.tps_curve_offset = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.funset_tps_curve_offset_title)).getValue();
		packet.tps_curve_gradient = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.funset_tps_curve_gradient_title)).getValue();
		return packet;
	}

	public static Secu3Dat buildCkpsPar(ParamPagerAdapter paramAdapter) {
		CKPSPar packet = new CKPSPar();
		packet.ckps_edge_type = ((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.ckps_ckp_edge_title)).getIndex();
		packet.ref_s_edge_type = ((ParamItemSpinner) paramAdapter.findItemByNameId(R.string.ckps_ref_s_edge_title)).getIndex();
		packet.ckps_merge_ign_outs = ((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.ckps_merge_outputs)).getValue()?1:0;
		packet.ckps_cogs_num = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.ckps_cogs_number_title)).getValue();
		packet.ckps_miss_num = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.ckps_missing_cogs_number_title)).getValue();
		packet.ckps_cogs_btdc = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.ckps_cogs_before_tdc_title)).getValue();
		packet.ckps_engine_cyl = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.ckps_engine_cylynders_title)).getValue();
		packet.ckps_ignit_cogs = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.ckps_ignition_pulse_delay_title)).getValue();
		return packet;
	}

	public static Secu3Dat buildChokePar(ParamPagerAdapter paramAdapter) {
		ChokePar packet = new ChokePar();
		packet.steps =  ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.choke_steps_title)).getValue();
		packet.manual_delta = 0;
		packet.testing = 0;
		return packet;
	}

	public static Secu3Dat buildCarburPar(ParamPagerAdapter paramAdapter) {
		CarburPar packet = new CarburPar();
		packet.ephh_lot = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.carbur_overrun_lower_threshold_gasoline_title)).getValue();
		packet.ephh_hit = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.carbur_overrun_upper_threshold_gasoline_title)).getValue();
		packet.ephh_lot_g = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.carbur_overrun_lower_threshold_gas_title)).getValue();
		packet.ephh_hit_g = ((ParamItemInteger) paramAdapter.findItemByNameId(R.string.carbur_overrun_upper_threshold_gas_title)).getValue();
		packet.shutoff_delay = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.carbur_overrun_valve_delay)).getValue();
		packet.carb_invers = ((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.carbur_sensor_inverse_title)).getValue()?1:0;
		packet.epm_ont = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.carbur_epm_valve_on_pressure_title)).getValue();
		packet.tps_threshold = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.carbur_tps_threshold_title)).getValue();
		return packet;
	}

	public static Secu3Dat buildAnglesPar(ParamPagerAdapter paramAdapter) {
		AnglesPar packet = new AnglesPar();
		packet.min_angle = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.angles_min_angle_title)).getValue();
		packet.max_angle = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.angles_max_angle_title)).getValue();
		packet.dec_spead = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.angles_angle_decrement_step_title)).getValue();
		packet.inc_spead = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.angles_angle_increment_step_title)).getValue();
		packet.zero_adv_ang = ((ParamItemBoolean) paramAdapter.findItemByNameId(R.string.angles_zero_angle_title)).getValue()?1:0;
		packet.angle_corr = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.angles_octane_correction_title)).getValue();
		return packet;
	}

	public static Secu3Dat buildAdcCorPar(ParamPagerAdapter paramAdapter) {
		ADCCorPar packet = new ADCCorPar();
		packet.map_adc_factor = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_map_sensor_factor_title)).getValue();
		packet.map_adc_correction = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_map_sensor_correction_title)).getValue();
		packet.ubat_adc_factor = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_voltage_sensor_factor_title)).getValue();
		packet.ubat_adc_correction = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_voltage_sensor_correction_title)).getValue();
		packet.temp_adc_factor = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_temper_sensor_factor_title)).getValue();
		packet.temp_adc_correction = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_temper_sensor_correction)).getValue();
		packet.tps_adc_factor = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_tps_sensor_factor_title)).getValue();
		packet.tps_adc_correction = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_tps_sensor_correction_title)).getValue();
		packet.add_i1_factor = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_addi1_sensor_factor_title)).getValue();
		packet.add_i1_correction = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_addi1_sensor_correction_title)).getValue();
		packet.add_i2_factor = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_addi2_sensor_factor_title)).getValue();
		packet.add_i1_correction = ((ParamItemFloat) paramAdapter.findItemByNameId(R.string.adccor_addi2_sensor_correction_title)).getValue();
		return packet;
	}
}

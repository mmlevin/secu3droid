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

package org.secu3.android.api.io;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.SettingsActivity;
import org.secu3.android.api.utils.PacketUtils;

import android.content.Context;

public class Secu3SensorLogger extends Secu3Logger {				
	private PacketUtils packetUtils = null;

	private static char CSV_DELIMETER = ';';
	private static final String cCSVTimeTemplateString = "%02d:02%02d.%02d";
	private static final String cCSVFileNameTemplateString = "%04d.%02d.%02d_%02d.%02d.%02d.csv";
	
	private int marker = 0;

	private String IntToBinaryString(int i)
	{
		 String res = "";
		 for (int j = 32768; j > 0; j >>= 1) res += ((i & j) == j) ? "1" : "0";
		 return res;
	}

	private String logString (Secu3Packet packet) {		
		int bitfield = ((ProtoFieldInteger) packet.getField(R.string.sensor_dat_bitfield_title)).getValue();
		
		String out = String.format(Locale.US, "%c ", CSV_DELIMETER);
		out += String.format(Locale.US, "%05d%c", ((ProtoFieldInteger) packet.getField(R.string.sensor_dat_rpm_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_angle_correction_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_map_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%5.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_voltage_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_temperature_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%4.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_knock_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%5.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_knock_retard_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%02d%c", ((ProtoFieldInteger) packet.getField(R.string.sensor_dat_air_flow_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%01d%c", Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_CARB), CSV_DELIMETER);
		out += String.format(Locale.US, "%01d%c", Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_GAS), CSV_DELIMETER);
		out += String.format(Locale.US, "%01d%c", Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_EPHH_VALVE), CSV_DELIMETER);
		out += String.format(Locale.US, "%01d%c", Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_EPM_VALVE), CSV_DELIMETER);
		out += String.format(Locale.US, "%01d%c", Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_COOL_FAN), CSV_DELIMETER);
		out += String.format(Locale.US, "%01d%c", Secu3Packet.bitTest(bitfield, Secu3Packet.BITNUMBER_ST_BLOCK), CSV_DELIMETER);
		out += String.format(Locale.US, "%01d%c", 0, CSV_DELIMETER);
		out += String.format(Locale.US, "%5.1f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_tps_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%6.3f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_addi1_voltage_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%6.3f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_addi2_voltage_title)).getValue(), CSV_DELIMETER);
		out += String.format(Locale.US, "%5.1f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_choke_position_title)).getValue(), CSV_DELIMETER);

		if (protocol_version >= SettingsActivity.PROTOCOL_DEVELOPER_RELEASE) {
			out += String.format(Locale.US, "%5.1f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_gasdose_position_title)).getValue(), CSV_DELIMETER);
		}

		if (protocol_version >= SettingsActivity.PROTOCOL_28082013_SUMMER_RELEASE) {
			out += String.format(Locale.US, "%5.1f%c", packetUtils.calcSpeed(((ProtoFieldInteger) packet.getField(R.string.sensor_dat_speed_title)).getValue()), CSV_DELIMETER);
			out += String.format(Locale.US, "%7.2f%c", packetUtils.calcDistance(((ProtoFieldInteger) packet.getField(R.string.sensor_dat_distance_title)).getValue()), CSV_DELIMETER);
		}

		if (protocol_version >= SettingsActivity.PROTOCOL_16052014_SPRING_RELEASE) {
			int airtemp = ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_air_temperature_title)).getIntValue();

			if (airtemp != 0x7FFF) {
				out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_air_temperature_title)).getValue(), CSV_DELIMETER);
			}
			else {
				out += String.format(Locale.US, "%6.2f%c", 0.0, CSV_DELIMETER);
			}

			out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_advance_angle_start_map_title)).getValue(), CSV_DELIMETER);
			out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_advance_angle_idle_map_title)).getValue(), CSV_DELIMETER);
			out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_advance_angle_work_map_title)).getValue(), CSV_DELIMETER);
			out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_advance_angle_cool_temp_map_title)).getValue(), CSV_DELIMETER);
			out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_advance_air_temp_map_title)).getValue(), CSV_DELIMETER);
			out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_advance_idling_regulator_title)).getValue(), CSV_DELIMETER);
			out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_advance_octane_correction_title)).getValue(), CSV_DELIMETER);
		}

		if (protocol_version >= SettingsActivity.PROTOCOL_10022015_WINTER_RELEASE) {
			out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_lambda_correction_title)).getValue(), CSV_DELIMETER);
			out += String.format(Locale.US, "%6.2f%c", ((ProtoFieldFloat) packet.getField(R.string.sensor_dat_injection_pulse_title)).getValue(), CSV_DELIMETER);
		}

		if (protocol_version >= SettingsActivity.PROTOCOL_14012014_WINTER_RELEASE) {
			out += String.format(Locale.US, "%01d%c", marker,CSV_DELIMETER);
		}
		marker = 0;
		
		out += String.format(Locale.US, "%s\r\n", IntToBinaryString(((ProtoFieldInteger) packet.getField(R.string.sensor_dat_errors_title)).getValue()));

		return out;
	}
	
	public void OnPacketReceived (Secu3Packet secu3Packet) {
		if (isStarted() && (secu3Packet != null) && (secu3Packet.getNameId() == R.string.sensor_dat_title)) {				
			long t = System.currentTimeMillis();
			getTime().setTimeInMillis(t);
			String time = String.format(Locale.US,cCSVTimeTemplateString, getTime().get(Calendar.HOUR_OF_DAY),getTime().get(Calendar.MINUTE),getTime().get(Calendar.SECOND),getTime().get (Calendar.MILLISECOND)/10);
			String out = logString(secu3Packet);
			log (time+out);
		}
	}
	
	public void beginLogging (int protocol_version, Context context) {
		this.packetUtils = new PacketUtils(context);
		super.beginLogging(protocol_version);
	}	
	
	public void setMarker (int marker) {
		this.marker = marker;
	}

	@Override
	public String getFileName() {
		getTime().setTime(new Date());
		return String.format(Locale.US, cCSVFileNameTemplateString, getTime().get(Calendar.YEAR), getTime().get(Calendar.MONTH),getTime().get(Calendar.DAY_OF_MONTH),getTime().get(Calendar.HOUR_OF_DAY),getTime().get(Calendar.MINUTE),getTime().get(Calendar.SECOND));
	}
	
	public void setCsvDelimeter (String delimeter) {
		CSV_DELIMETER = delimeter.charAt(0);
	}
}

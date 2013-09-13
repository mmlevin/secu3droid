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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.utils.PacketUtils;

import android.content.Context;
import android.os.Environment;
import android.text.format.Time;

public class Secu3Logger {			
	private BufferedWriter logWriter = null;	
	private boolean started = false;
	private Time time = null;
	private String path = null;
	private int protocol_version;
	private PacketUtils packetUtils = null;

	private static final char CSV_DELIMETER = ',';
	private static final String cCSVTimeTemplateString = "%H:%M:%S";
	private static final String cCSVFileNameTemplateString = "%Y.%m.%d_%H.%M.%S.csv";
	private static final String CSVMillisTemplateString = "%s.%02d";	
	

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
		
		if (protocol_version >= 2) {
			out += String.format(Locale.US, "%5.1f%c", packetUtils.calcSpeed(((ProtoFieldInteger) packet.getField(R.string.sensor_dat_speed_title)).getValue()), CSV_DELIMETER);
			out += String.format(Locale.US, "%7.2f%c", packetUtils.calcDistance(((ProtoFieldInteger) packet.getField(R.string.sensor_dat_distance_title)).getValue()), CSV_DELIMETER);
		}	
		
		out += String.format(Locale.US, "%s\r\n", IntToBinaryString(((ProtoFieldInteger) packet.getField(R.string.sensor_dat_errors_title)).getValue()));

		return out;
	}
	
	public void OnPacketReceived (Secu3Packet secu3Packet) {
		if (started && (secu3Packet != null) && (secu3Packet.getNameId() == R.string.sensor_dat_title)) {				
			long t = System.currentTimeMillis();
			time.set(t);
			String time = String.format(Locale.US,CSVMillisTemplateString,this.time.format(cCSVTimeTemplateString), (t%1000)/10);
			String out = logString(secu3Packet);
			try {
				logWriter.write(time);
				logWriter.write(out);
				logWriter.flush();
			} catch (IOException e) {				
			}
		}
	}
	
	public boolean beginLogging (int protocol_version, Context context) {
		this.protocol_version = protocol_version;
		this.packetUtils = new PacketUtils(context);
		if (started) return true; 
		else {
			time = new Time();
			time.setToNow();
			String fname = time.format(cCSVFileNameTemplateString);
			try {
				logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+File.separator+fname),"ISO-8859-1"));
				started = true;
				return true;
			} catch (IOException e) {	
				return false;
			}
		}
	}
	
	public boolean endLogging() {
		if (!started) return true;
		try {
			logWriter.flush();
			logWriter.close();
			started = false;
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if ((path == null) || (path.length() == 0)) path = Secu3Logger.getDefaultPath();
		this.path = path;
	}
	
	public static String getDefaultPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}
}

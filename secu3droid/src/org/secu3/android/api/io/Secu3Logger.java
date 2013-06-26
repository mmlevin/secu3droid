package org.secu3.android.api.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;
import org.secu3.android.api.io.Secu3Dat.SensorDat;

import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

public class Secu3Logger {		
	private final String LOG_TAG = "Secu3Logger";
	
	private BufferedWriter logWriter = null;	
	private boolean started = false;
	private Time time = null;
	private String path = null;

	private static final char CSV_DELIMETER = ',';
	private static final String cCSVTimeTemplateString = "%H:%M:%S";
	private static final String cCSVDataTemplateString = "%c %%05d%c%%6.2f%c %%6.2f%c %%5.2f%c %%6.2f%c %%4.2f%c %%5.2f%c %%02d%c %%01d%c %%01d%c %%01d%c %%01d%c %%01d%c %%01d%c %%01d%c %%5.1f%c %%6.3f%c %%6.3f%c %%5.1f%c %%s\r\n";
	private static final String cCSVFileNameTemplateString = "%Y.%m.%d_%H.%M.%S.csv";
	private static final String CSVMillisTemplateString = "%s.%02d";	
	

	private String IntToBinaryString(int i)
	{
		 String res = "";
		 for (int j = 32768; j > 0; j >>= 1) res += ((i & j) == j) ? "1" : "0";
		 return res;
	}

	public void OnPacketReceived (Secu3Dat packet) {
		if (started && (packet != null) && (packet.packet_id == Secu3Dat.SENSOR_DAT)) {				
			long t = System.currentTimeMillis();
			time.set(t);
			String time = String.format(CSVMillisTemplateString,this.time.format(cCSVTimeTemplateString), (t%1000)/10);
			char x = CSV_DELIMETER;
			String formatString = String.format(Locale.US,cCSVDataTemplateString, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x);
			String out = String.format (Locale.US,formatString, ((SensorDat)packet).frequen,
					((SensorDat)packet).adv_angle,
					((SensorDat)packet).pressure,
					((SensorDat)packet).voltage,
					((SensorDat)packet).temperat,
					((SensorDat)packet).knock_k,
					((SensorDat)packet).knock_retard,
					((SensorDat)packet).air_flow,
					((SensorDat)packet).carb,
					((SensorDat)packet).gas,
					((SensorDat)packet).ephh_valve,
					((SensorDat)packet).epm_valve,
					((SensorDat)packet).cool_fan,
					((SensorDat)packet).st_block,
					0,
					((SensorDat)packet).tps,
					((SensorDat)packet).add_i1,
					((SensorDat)packet).add_i2,
					((SensorDat)packet).choke_pos,
					IntToBinaryString((((SensorDat)packet).ce_errors)));
			try {
				logWriter.write(time);
				logWriter.write(out);
				logWriter.flush();
				Log.d(LOG_TAG, out);
			} catch (IOException e) {				
			}
		}
	}
	
	public boolean BeginLogging () {
		if (started) return true; 
		else {
			time = new Time();
			time.setToNow();
			String fname = time.format(cCSVFileNameTemplateString);
			try {
				logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+File.separator+fname),"ISO-8859-1"));
				Log.d(LOG_TAG, path+fname);
				started = true;
				return true;
			} catch (IOException e) {	
				Log.d(LOG_TAG,e.getMessage());
				return false;
			}
		}
	}
	
	public boolean EndLogging() {
		if (!started) return true;
		try {
			logWriter.flush();
			logWriter.close();
			started = false;
			return true;
		} catch (IOException e) {
			Log.d(LOG_TAG,e.getMessage());
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

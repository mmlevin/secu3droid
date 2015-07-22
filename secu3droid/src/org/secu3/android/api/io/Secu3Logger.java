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
import java.util.GregorianCalendar;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

public abstract class Secu3Logger {
	private static final String LOG_TAG = "Secu3Logger";

	private GregorianCalendar time = null;
	private BufferedWriter logWriter = null;	
	private boolean started = false;
	private String path = null;
	int protocol_version;
	
	void log (String log) {
		try {
			logWriter.write(log);
			logWriter.flush();
		} catch (IOException e) {
			Log.e(LOG_TAG,e.getMessage());
		}		
	}
		
	Secu3Logger() {
		time = new GregorianCalendar();
	}
	protected abstract String getFileName();
	
	public void beginLogging (int protocol_version) {
		this.protocol_version = protocol_version;
		if (isStarted()) return;
		else {
			try {
				logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+File.separator+getFileName()),"ISO-8859-1"));
				started = true;
			} catch (IOException e) {	
				e.printStackTrace();
			}
		}
	}
	
	public void endLogging() {
		if (!isStarted()) return;
		try {
			logWriter.flush();
			logWriter.close();
			started = false;
		} catch (IOException e) {
			e.printStackTrace();
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

	boolean isStarted() {
		return started;
	}

	GregorianCalendar getTime() {
		return time;
	}

	public void setTime(GregorianCalendar time) {
		this.time = time;
	}
}

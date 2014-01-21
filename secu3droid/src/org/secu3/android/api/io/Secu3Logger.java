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
import android.os.Environment;
import android.text.format.Time;

public abstract class Secu3Logger {			
	private Time time = null;	
	private BufferedWriter logWriter = null;	
	private boolean started = false;
	private String path = null;
	protected int protocol_version;
	
	public void log (String log) {
		try {
			logWriter.write(log);
			logWriter.flush();
		} catch (IOException e) {				
		}		
	}
		
	public Secu3Logger() {
		time = new Time();
	}
	public abstract String getFileName();
	
	public boolean beginLogging (int protocol_version) {
		this.protocol_version = protocol_version;
		if (isStarted()) return true; 
		else {
			try {
				logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+File.separator+getFileName()),"ISO-8859-1"));
				started = true;
				return true;
			} catch (IOException e) {	
				return false;
			}
		}
	}
	
	public boolean endLogging() {
		if (!isStarted()) return true;
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

	public boolean isStarted() {
		return started;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}
}

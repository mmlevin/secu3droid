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

import java.io.UnsupportedEncodingException;

import org.secu3.android.api.utils.EncodingCP866;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class ProtoFieldString extends BaseProtoField implements Parcelable{
	private String value = null;
	
	public static final Parcelable.Creator<ProtoFieldString> CREATOR = new Parcelable.Creator<ProtoFieldString>() {
		 public ProtoFieldString createFromParcel(Parcel in) {			 
			 return new ProtoFieldString(in);
		 }

		 public ProtoFieldString[] newArray(int size) {
			 return new ProtoFieldString[size];
		 }
	};
	
	public ProtoFieldString(Context context, int nameId, int type, int length, boolean binary) {
		setData(null);
		
		setNameId(nameId);
		setType(type);
		setLength(length);
		setBinary(binary);
		if (nameId != 0) this.setName(context.getString(nameId));		
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(value);
	}
	
	public ProtoFieldString(Parcel in) {
		super(in);
		value = in.readString();
	}
	
	public ProtoFieldString(ProtoFieldString field) {
		super (field);
		if (field != null) {
			this.value = field.value;
		}
	}
	
	@Override
	public void setData(String data) {
		super.setData(data);
		if (data != null) {
			int[] buf = new int[data.length()];
			for (int i = 0; i != buf.length; i++) buf[i] = data.charAt(i);
			EncodingCP866.Cp866ToUtf16(buf);
			data = new String(buf,0,buf.length);
		}
		setValue(data);
	}
	
	@Override
	public void pack() {
		byte[] buf = new byte[getLength()];
		for (int i = 0; i != Math.min(getLength(), value.length()); i++) {
			buf[i] = (byte) value.charAt(i);
		}
		String s;
		try {
			s = new String(buf,0,buf.length,"ISO-8859-1");
			setData(s);			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public void reset() {
		super.reset();
		this.value = null;
	}
}

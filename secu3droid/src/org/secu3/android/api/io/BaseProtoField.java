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

import android.os.Parcel;
import android.os.Parcelable;

public class BaseProtoField implements Parcelable {
	private String name;
	private int nameId;
	private int type;
	private boolean binary;
	private String data;
	private int length = 0;
		
	public BaseProtoField() {
	}
	
	public BaseProtoField(Parcel in) {
		this.name = in.readString();
		this.nameId = in.readInt();
		this.type = in.readInt();
		this.binary = (in.readInt()==1);
		this.data = in.readString();
		this.length = in.readInt();
	}
	
	public BaseProtoField (BaseProtoField field) {
		if(field != null) {
			this.name = field.name;
			this.nameId = field.nameId;
			this.type = field.type;
			this.binary = field.binary;
			this.data = field.data;
			this.length = field.length;			
		}
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(nameId);
		dest.writeInt(type);
		dest.writeInt(binary?1:0);
		dest.writeString(data);
		dest.writeInt(length);		
	}
	
	public static final Parcelable.Creator<BaseProtoField> CREATOR = new Parcelable.Creator<BaseProtoField>() {
		 public BaseProtoField createFromParcel(Parcel in) {			 
			 return new BaseProtoField(in);
		 }

		 public BaseProtoField[] newArray(int size) {
			 return new BaseProtoField[size];
		 }
	};
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getNameId() {
		return nameId;
	}
	
	public void setNameId(int nameId) {
		this.nameId = nameId;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void parse (String data) {
		setData(data);
	}

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void reset() {
		this.data = null;		
	}

	public void pack() {		
	}
	
	public int BinToInt (String data) {
		int v = 0;
		for (int i = 0; i != getLength(); i++) {
			v <<= 8;
			v |= data.charAt(i);
		}		
		return v;
	}
	
	public String IntToBin (int i) {
		String s = "";
		switch (getLength()) {
		case 1:
			s += (char)(i & 0x00FF);
			break;
		case 2:
			s += (char)((i & 0xFF00) >> 8);
			s += (char)(i & 0x00FF);
			break;
		case 3:
			s += (char)((i & 0xFF0000) >> 16);
			s += (char)((i & 0x00FF00) >> 8);
			s += (char)(i & 0x0000FF);
			break;
		case 4:
			s += (char)((i & 0xFF000000) >> 24);
			s += (char)((i & 0x00FF0000) >> 16);
			s += (char)((i & 0x0000FF00) >> 8);
			s += (char)(i & 0x000000FF);
			break;
		}
		return s;
	}
}

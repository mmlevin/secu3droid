package org.secu3.android.api.io;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseProtoField implements Parcelable {
	private String name;
	private int nameId;
	private int type;
	private int minVersion;
	private boolean binary;
	private String data;
	private int length = 0;
		
	public BaseProtoField() {
	}
	
	public BaseProtoField(Parcel in) {
		this.name = in.readString();
		this.nameId = in.readInt();
		this.type = in.readInt();
		this.minVersion = in.readInt();
		this.binary = (in.readInt()==1)?true:false;
		this.data = in.readString();
		this.length = in.readInt();
	}
	
	public BaseProtoField (BaseProtoField field) {
		if(field != null) {
			this.name = field.name;
			this.nameId = field.nameId;
			this.type = field.type;
			this.minVersion = field.minVersion;
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
		dest.writeInt(minVersion);
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
	
	public int getMinVersion() {
		return minVersion;
	}
	
	public void setMinVersion(int minVersion) {
		this.minVersion = minVersion;
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
}

package org.secu3.android.api.io;

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
	
	public ProtoFieldString(Context context, int nameId, int type, int length, int minVersion, boolean binary) {
		setData(null);
		
		setNameId(nameId);
		setType(type);
		setMinVersion(minVersion);
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
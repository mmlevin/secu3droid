package org.secu3.android.api.io;

import org.secu3.android.R;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class ProtoFieldFloat extends BaseProtoField implements Parcelable{
	private int intValue;
	private boolean signed;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(intValue);
		dest.writeInt(signed?1:0);
	}	
	
	public static final Parcelable.Creator<ProtoFieldInteger> CREATOR = new Parcelable.Creator<ProtoFieldInteger>() {
		 public ProtoFieldInteger createFromParcel(Parcel in) {			 
			 return new ProtoFieldInteger(in);
		 }

		 public ProtoFieldInteger[] newArray(int size) {
			 return new ProtoFieldInteger[size];
		 }
	};
	
	public ProtoFieldFloat(Parcel in) {
		super(in);
		intValue = in.readInt();
		signed = (in.readInt()==0)?false:true;
	}
	
	public ProtoFieldFloat(Context context, int nameId, int type, boolean signed, int minVersion, boolean binary) {
		intValue = 0;
		setData(null);
		
		setNameId(nameId);
		setType(type);
		setSigned(signed);
		setMinVersion(minVersion);
		setBinary(binary);
		if (nameId != 0) this.setName(context.getString(nameId));
		
		switch (type) {
		case R.id.field_type_int4:
			setLength(1);
			break;
		case R.id.field_type_int8:
			setLength(isBinary()?1:2);
			break;
		case R.id.field_type_int16:
			setLength(isBinary()?2:4);
			break;
		case R.id.field_type_int32:
			setLength(isBinary()?4:8);
			break;
		default:
			setLength(0);
		}
	}

	public int getValue() {
		return intValue;
	}

	public void setValue(int value) {
		this.intValue = value;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}
}

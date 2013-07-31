package org.secu3.android.api.io;

import org.secu3.android.R;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class ProtoFieldFloat extends BaseProtoField implements Parcelable{
	private int intValue;
	private int intDivider;
	private int intMultiplier;
	private float floatValue;
	private boolean signed;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(signed?1:0);		
		dest.writeInt(intValue);
		dest.writeInt(intDivider);
		dest.writeInt(intMultiplier);
		dest.writeFloat(floatValue);
	}	
	
	public static final Parcelable.Creator<ProtoFieldFloat> CREATOR = new Parcelable.Creator<ProtoFieldFloat>() {
		 public ProtoFieldFloat createFromParcel(Parcel in) {			 
			 return new ProtoFieldFloat(in);
		 }

		 public ProtoFieldFloat[] newArray(int size) {
			 return new ProtoFieldFloat[size];
		 }
	};
	
	public ProtoFieldFloat(Parcel in) {
		super(in);
		signed = (in.readInt()==0)?false:true;		
		intValue = in.readInt();
		intDivider = in.readInt();
		intMultiplier = in.readInt();
		floatValue = in.readFloat();
	}
	
	public ProtoFieldFloat(ProtoFieldFloat field) {
		super(field);
		if (field != null) {
			this.intValue = field.intValue;
			this.intDivider = field.intDivider;
			this.intMultiplier = field.intMultiplier;
			this.floatValue = field.floatValue;
			this.signed = field.signed;
		}
	}
	
	public ProtoFieldFloat(Context context, int nameId, int type, boolean signed, int divider, int minVersion, boolean binary) {
		intValue = 0;
		setData(null);
		
		setNameId(nameId);
		setType(type);
		setSigned(signed);
		setMinVersion(minVersion);
		setIntMultiplier(1);
		setIntDivider(divider);
		setBinary(binary);
		if (nameId != 0) this.setName(context.getString(nameId));
		
		switch (type) {
		case R.id.field_type_float4:
			setLength(1);
			break;
		case R.id.field_type_float8:
			setLength(isBinary()?1:2);
			break;
		case R.id.field_type_float16:
			setLength(isBinary()?2:4);
			break;
		case R.id.field_type_float32:
			setLength(isBinary()?4:8);
			break;
		default:
			setLength(0);
		}
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int value) {
		this.intValue = value;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public float getValue() {
		return floatValue;
	}

	public void setValue(float floatValue) {
		this.floatValue = floatValue;
	}

	public int getIntDivider() {
		return intDivider;
	}

	public void setIntDivider(int intDivider) {
		if (intDivider == 0) throw new IllegalArgumentException("Divider cannot be zero");		
		this.intDivider = intDivider;
	}

	public int getIntMultiplier() {
		return intMultiplier;
	}

	public void setIntMultiplier(int intMultiplier) {
		this.intMultiplier = intMultiplier;
	}
	
	@Override
	public void setData(String data) {
		super.setData(data);
		if (data != null) {
			if (signed) {
				switch (getType()) {
				case R.id.field_type_float4:
					throw new IllegalArgumentException("No rules for converting 4-bit value into a signed number");
				case R.id.field_type_float8:
					setIntValue(Integer.valueOf(data,16).byteValue());					
					break;
				case R.id.field_type_float16:
					setIntValue(Integer.valueOf(data,16).shortValue());
					break;
				case R.id.field_type_float32:				
					setIntValue(Long.valueOf(data,16).intValue());
					break;
				default:
					break;
				}
			} else {
				setIntValue(Integer.parseInt(data, 16));			
			}
			setValue((float)intValue*intMultiplier/intDivider);
		}		
	}
	
	@Override
	public void reset() {
		super.reset();
		this.intValue = 0;
		this.floatValue = 0;
	}
}

package org.secu3.android.api.io;

import org.secu3.android.R;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class ProtoFieldFloat extends BaseProtoField implements Parcelable{
	private int intValue;
	private int intDivider;
	private int intMultiplier;
	private int intOffset;
	private float floatValue;
	private boolean signed;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(signed?1:0);		
		dest.writeInt(intValue);
		dest.writeInt(intDivider);
		dest.writeInt(intMultiplier);
		dest.writeInt(intOffset);
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
		intOffset = in.readInt();
		floatValue = in.readFloat();
	}
	
	public ProtoFieldFloat(ProtoFieldFloat field) {
		super(field);
		if (field != null) {
			this.intValue = field.intValue;
			this.intDivider = field.intDivider;
			this.intMultiplier = field.intMultiplier;
			this.intOffset = field.intOffset;
			this.floatValue = field.floatValue;
			this.signed = field.signed;
		}
	}
	
	public ProtoFieldFloat(Context context, int nameId, int type, boolean signed, boolean binary) {
		intValue = 0;
		setData(null);
		
		setNameId(nameId);
		setType(type);
		setSigned(signed);
		setIntMultiplier(1);
		setIntOffset(0);
		setIntDivider(1);
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
		case R.id.field_type_float24:
			setLength(isBinary()?3:6);
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
			int v = 0;
			if (isBinary()) v = BinToInt();
			if (signed) {
				switch (getType()) {
				case R.id.field_type_float4:
				case R.id.field_type_float24:
					throw new IllegalArgumentException("No rules for converting 4/24-bit value into a signed number");
				case R.id.field_type_float8:
					if (isBinary()) v = Integer.valueOf(v).byteValue();
					else v = Integer.valueOf(data,16).byteValue(); 
					break;
				case R.id.field_type_float16:
					if (isBinary()) v = Integer.valueOf(v).shortValue();
					else v = Integer.valueOf(data,16).shortValue(); 
					break;
				case R.id.field_type_float32:	
					if (isBinary()) v = Long.valueOf(v).intValue();
					else v = Long.valueOf(data,16).intValue(); 
					break;
				default:
					break;
				}
			} else {
				if (!isBinary()) v = Integer.parseInt(data, 16);			
			}
			setIntValue (v);
			setValue((float)(v+intOffset)*intMultiplier/intDivider);
		}		
	}
	
	@Override
	public void pack() {
		if (signed) {
			switch (getType()) {
			case R.id.field_type_float4:
			case R.id.field_type_float24:
				throw new IllegalArgumentException("No rules for converting 4/24-bit value into a signed number");
			case R.id.field_type_float8:
				setData(String.format("%02X", Integer.valueOf(Math.round(floatValue*intDivider/intMultiplier-intOffset)).byteValue()));
				break;
			case R.id.field_type_float16:
				setData(String.format("%04X", Integer.valueOf(Math.round(floatValue*intDivider/intMultiplier-intOffset)).shortValue()));
				break;
			case R.id.field_type_float32:				
				setData(String.format("%08X", Long.valueOf(Math.round(floatValue*intDivider/intMultiplier-intOffset)).intValue()));
				break;
			default:
				break;
			}
		} else {	
			switch (getType()) {
			case R.id.field_type_float4:
				setData(String.format("%01X", Math.round(floatValue*intDivider/intMultiplier-intOffset)));
				break;
			case R.id.field_type_float8:
				setData(String.format("%02X", Math.round(floatValue*intDivider/intMultiplier-intOffset)));
				break;
			case R.id.field_type_float16:
				setData(String.format("%04X", Math.round(floatValue*intDivider/intMultiplier-intOffset)));
				break;
			case R.id.field_type_float24:
				setData(String.format("%06X", Math.round(floatValue*intDivider/intMultiplier-intOffset)));
				break;
			case R.id.field_type_float32:				
				setData(String.format("%08X", Math.round(floatValue*intDivider/intMultiplier-intOffset)));
				break;
			default:
				break;
			}
		}		
	}	
	
	@Override
	public void reset() {
		super.reset();
		this.intValue = 0;
		this.floatValue = 0;
	}

	public int getIntOffset() {
		return intOffset;
	}

	public void setIntOffset(int intOffset) {
		this.intOffset = intOffset;
	}
}

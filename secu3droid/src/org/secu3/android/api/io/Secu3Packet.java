package org.secu3.android.api.io;

import java.util.ArrayList;

import org.secu3.android.R;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class Secu3Packet implements Parcelable {
	public final static char INPUT_PACKET = '@';
	public final static char OUTPUT_PACKET = '!';
	public final static int INPUT_OUTPUT_POS = 0;
	public final static int PACKET_ID_POS = 1;	
	
	public final static int BITNUMBER_EPHH_VALVE = 0;
	public final static int BITNUMBER_CARB = 1;
	public final static int BITNUMBER_GAS = 2;
	public final static int BITNUMBER_EPM_VALVE = 3;
	public final static int BITNUMBER_CE_STATE = 4;
	public final static int BITNUMBER_COOL_FAN = 5;
	public final static int BITNUMBER_ST_BLOCK = 6;
	
	public static int bitTest (int value, int bitNumber) {
		value >>= bitNumber;
		return (value & 0x01);
	};
	 
	private ArrayList<BaseProtoField> fields;
	
	private String name;
	private String packetId;
	private int packetIdResId;
	private int nameId;	
	private int minVersion;	
	
	private boolean binary;	
	private String data; 
	
	 public static final Parcelable.Creator<Secu3Packet> CREATOR = new Parcelable.Creator<Secu3Packet>() {
		 public Secu3Packet createFromParcel(Parcel in) {			 
			 return new Secu3Packet(in);
		 }

		 public Secu3Packet[] newArray(int size) {
			 return new Secu3Packet[size];
		 }
	};	
	
	public Secu3Packet(Parcel in) {
		this.name = in.readString();
		this.packetId = in.readString();
		this.packetIdResId = in.readInt();
		this.nameId = in.readInt();
		this.minVersion = in.readInt();
		this.binary = (in.readInt()==0)?false:true;
		this.data = in.readString();
		int counter = in.readInt();
		this.fields = (counter == 0)?null:new ArrayList<BaseProtoField>();
		for (int i = 0; i != counter; i++) {
			int type = in.readInt();
			switch (type) {
			case R.id.field_type_int4:
			case R.id.field_type_int8:
			case R.id.field_type_int16:
			case R.id.field_type_int32:
				fields.add((BaseProtoField) in.readParcelable(ProtoFieldInteger.class.getClassLoader()));
				break;
			case R.id.field_type_string:
				fields.add((BaseProtoField) in.readParcelable(ProtoFieldString.class.getClassLoader()));				
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(packetId);
		dest.writeInt(packetIdResId);
		dest.writeInt(nameId);
		dest.writeInt(minVersion);
		dest.writeInt(binary?1:0);
		dest.writeString(data);
		int counter = (fields == null)?0:fields.size();
		dest.writeInt(counter);
		for (int i = 0; i != counter; i++) {
			BaseProtoField field = fields.get(i);
			dest.writeInt(field.getType());
			dest.writeParcelable(field, 0);
		}
	}	

	public Secu3Packet(Context context, int nameId, int packetIdResId, int minVersion, boolean binary) {
		setFields(null);
		setNameId(nameId);
		setPacketIdResId(packetIdResId);
		setMinVersion(minVersion);
		setBinary(binary);
		
		if (nameId != 0) this.setName(context.getString(nameId));
		if (packetIdResId != 0) {
			this.setPacketId(context.getString(packetIdResId));
			if (packetId.length() > 1) throw new IllegalArgumentException("Packet ID lenght cannot be greater than 1");			
		}
	}

	public ArrayList<BaseProtoField> getFields() {
		return fields;
	}

	public void setFields(ArrayList<BaseProtoField> fields) {
		this.fields = fields;
	}
	
	public void addField (BaseProtoField field) {
		if (field != null) {
			if (fields == null) fields = new ArrayList<BaseProtoField>();
			fields.add(field);
		}
	}
	
	public BaseProtoField getField(int fieldNameId) {
		BaseProtoField field;
		for (int i = 0; i != fields.size(); ++i) {
			field = fields.get(i);
			if (field.getNameId() == fieldNameId) return field;
		}
		return null;
	}
	
	public BaseProtoField findField (int fieldId) {
		if (fields != null) {
			for (int i = 0; i != fields.size(); i++) {
				if (fields.get(i).getNameId() == fieldId) return fields.get(i);
			}
		}
		return null;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}	
	
	public void parse (String data) {
		if ((data != null) && (fields != null)) {
			if (!isBinary()) {
				BaseProtoField field;
				
				if (data.charAt(INPUT_OUTPUT_POS) != INPUT_PACKET) throw new IllegalArgumentException("Not an input packet");
				char ch = data.charAt (PACKET_ID_POS);
				if (ch != packetId.charAt(0)) throw new IllegalArgumentException("Wrong packet type");
				setData(data);
				int position = 2; // Skip first 2 chars
				int delta = 0;
				for (int i = 0; i != fields.size(); i++) {
					field = fields.get(i);
					delta = field.getLength();
					String subdata = data.substring(position, position+=delta);
					field.setData(subdata);
				}
			} else throw new IllegalArgumentException("Non-hexadecimal mode is not supported in Secu3Packet.parse()");
		}
	}		

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}

	public String getPacketId() {
		return packetId;
	}

	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}

	public int getNameId() {
		return nameId;
	}

	public void setNameId(int nameId) {
		this.nameId = nameId;
	}

	public int getMinVersion() {
		return minVersion;
	}

	public void setMinVersion(int minVersion) {
		this.minVersion = minVersion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPacketIdResId() {
		return packetIdResId;
	}

	public void setPacketIdResId(int packetIdResId) {
		this.packetIdResId = packetIdResId;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public Intent getIntent ()
	{
		Intent intent = new Intent (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET);
		intent.putExtra (Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET,this);
		return intent;
	}

	public String pack() {
		// TODO Auto-generated method stub
		return null;
	}	
}

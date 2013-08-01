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

	public static final int COPT_ATMEGA16 = 0;
	public static final int COPT_ATMEGA32 = 1;
	public static final int COPT_ATMEGA64 = 2;
	public static final int COPT_ATMEGA128 = 3;
	public static final int COPT_VPSEM = 4;
	public static final int COPT_WHEEL_36_1 = 5; /*
												 * Obsolete! Left for
												 * compatibility reasons
												 */
	public static final int COPT_INVERSE_IGN_OUTPUTS = 6; /*
														 * Obsolete! Left for
														 * compatibility reasons
														 */
	public static final int COPT_DWELL_CONTROL = 7;
	public static final int COPT_COOLINGFAN_PWM = 8;
	public static final int COPT_REALTIME_TABLES = 9;
	public static final int COPT_ICCAVR_COMPILER = 10;
	public static final int COPT_AVRGCC_COMPILER = 11;
	public static final int COPT_DEBUG_VARIABLES = 12;
	public static final int COPT_PHASE_SENSOR = 13;
	public static final int COPT_PHASED_IGNITION = 14;
	public static final int COPT_FUEL_PUMP = 15;
	public static final int COPT_THERMISTOR_CS = 16;
	public static final int COPT_SECU3T = 17;
	public static final int COPT_DIAGNOSTICS = 18;
	public static final int COPT_HALL_OUTPUT = 19;
	public static final int COPT_REV9_BOARD = 20;
	public static final int COPT_STROBOSCOPE = 21;

	public final static int SECU3_ECU_ERRORS_COUNT = 11;

	public final static int ETTS_GASOLINE_SET = 0; // tables's set: petrol
	public final static int ETTS_GAS_SET = 1; // tables's set: gas

	public final static int ETMT_STRT_MAP = 0; // start map
	public final static int ETMT_IDLE_MAP = 1; // idle map
	public final static int ETMT_WORK_MAP = 2; // work map
	public final static int ETMT_TEMP_MAP = 3; // temp.corr. map
	public final static int ETMT_NAME_STR = 4; // name of tables's set

	public static final String COPT[] = { "COPT_ATMEGA16", "COPT_ATMEGA32",
			"COPT_ATMEGA64", "COPT_ATMEGA128", "COPT_VPSEM", "COPT_WHEEL_36_1", /*
																				 * Obsolete
																				 * !
																				 * Left
																				 * for
																				 * compatibility
																				 * reasons
																				 */
			"COPT_INVERSE_IGN_OUTPUTS", /*
										 * Obsolete! Left for compatibility
										 * reasons
										 */
			"COPT_DWELL_CONTROL", "COPT_COOLINGFAN_PWM",
			"COPT_REALTIME_TABLES", "COPT_ICCAVR_COMPILER",
			"COPT_AVRGCC_COMPILER", "COPT_DEBUG_VARIABLES",
			"COPT_PHASE_SENSOR", "COPT_PHASED_IGNITION", "COPT_FUEL_PUMP",
			"COPT_THERMISTOR_CS", "COPT_SECU3T", "COPT_DIAGNOSTICS",
			"COPT_HALL_OUTPUT", "COPT_REV9_BOARD", "COPT_STROBOSCOPE" };

	public final static int OPCODE_EEPROM_PARAM_SAVE = 1;
	public final static int OPCODE_CE_SAVE_ERRORS = 2;
	public final static int OPCODE_READ_FW_SIG_INFO = 3;
	public final static int OPCODE_LOAD_TABLSET = 4; // realtime tables
	public final static int OPCODE_SAVE_TABLSET = 5; // realtime tables
	public final static int OPCODE_DIAGNOST_ENTER = 6; // enter diagnostic mode
	public final static int OPCODE_DIAGNOST_LEAVE = 7; // leave diagnostic mode

	public final static int BAUD_RATE[] = { 2400, 4800, 9600, 14400, 19200,
			28800, 38400, 57600 };
	public final static int BAUD_RATE_INDEX[] = { 0x340, 0x1A0, 0xCF, 0x8A,
			0x67, 0x44, 0x33, 0x22 };

	/** Вычисляет индекс элемента в масиве **/
	public static int indexOf(int array[], int search) {
		for (int i = 0; i != array.length; i++)
			if (array[i] == search)
				return i;
		return -1;
	}

	public static int bitTest(int value, int bitNumber) {
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
	public static final int MAX_PACKET_SIZE = 128;

	public Secu3Packet(Parcel in) {
		this.name = in.readString();
		this.packetId = in.readString();
		this.packetIdResId = in.readInt();
		this.nameId = in.readInt();
		this.minVersion = in.readInt();
		this.binary = (in.readInt() == 0) ? false : true;
		this.data = in.readString();
		int counter = in.readInt();
		this.fields = (counter == 0) ? null : new ArrayList<BaseProtoField>();
		for (int i = 0; i != counter; i++) {
			int type = in.readInt();
			switch (type) {
			case R.id.field_type_int4:
			case R.id.field_type_int8:
			case R.id.field_type_int16:
			case R.id.field_type_int32:
				this.fields.add((BaseProtoField) in
						.readParcelable(ProtoFieldInteger.class
								.getClassLoader()));
				break;
			case R.id.field_type_float4:
			case R.id.field_type_float8:
			case R.id.field_type_float16:
			case R.id.field_type_float32:
				this.fields
						.add((BaseProtoField) in
								.readParcelable(ProtoFieldFloat.class
										.getClassLoader()));
				break;
			case R.id.field_type_string:
				this.fields
						.add((BaseProtoField) in
								.readParcelable(ProtoFieldString.class
										.getClassLoader()));
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
		int bin = 0;
		if (binary)
			bin = 1;
		dest.writeInt(bin); // Do not know why, but in my case
							// dest.writeInt(binary?1:0) interrupts routine
		dest.writeString(data);
		int counter = (fields == null) ? 0 : fields.size();
		dest.writeInt(counter);
		for (int i = 0; i != counter; i++) {
			BaseProtoField field = fields.get(i);
			dest.writeInt(field.getType());
			dest.writeParcelable(field, 0);
		}
	}

	public Secu3Packet(Context context, int nameId, int packetIdResId,
			int minVersion, boolean binary) {
		setFields(null);
		setNameId(nameId);
		setPacketIdResId(packetIdResId);
		setMinVersion(minVersion);
		setBinary(binary);

		if (nameId != 0)
			this.setName(context.getString(nameId));
		if (packetIdResId != 0) {
			this.setPacketId(context.getString(packetIdResId));
			if (packetId.length() > 1)
				throw new IllegalArgumentException(
						"Packet ID lenght cannot be greater than 1");
		}
	}

	public Secu3Packet(Secu3Packet packet) {
		if (packet != null) {
			this.name = packet.name;
			this.nameId = packet.nameId;
			this.minVersion = packet.minVersion;
			this.binary = packet.binary;
			this.data = packet.data;
			this.packetId = packet.packetId;
			this.packetIdResId = packet.packetIdResId;
			this.fields = null;
			if (packet.fields != null) {
				this.fields = new ArrayList<BaseProtoField>();
				BaseProtoField field = null;
				for (int i = 0; i != packet.fields.size(); ++i) {
					field = packet.fields.get(i);
					if (field instanceof ProtoFieldString)
						addField(new ProtoFieldString((ProtoFieldString) field));
					else if (field instanceof ProtoFieldInteger)
						addField(new ProtoFieldInteger(
								(ProtoFieldInteger) field));
					else if (field instanceof ProtoFieldFloat)
						addField(new ProtoFieldFloat((ProtoFieldFloat) field));
				}
			}
		}
	}

	public ArrayList<BaseProtoField> getFields() {
		return fields;
	}

	public void setFields(ArrayList<BaseProtoField> fields) {
		this.fields = fields;
	}

	public void addField(BaseProtoField field) {
		if (field != null) {
			if (fields == null)
				fields = new ArrayList<BaseProtoField>();
			fields.add(field);
		}
	}

	public BaseProtoField getField(int fieldNameId) {
		BaseProtoField field;
		for (int i = 0; i != fields.size(); ++i) {
			field = fields.get(i);
			if (field.getNameId() == fieldNameId)
				return field;
		}
		return null;
	}

	public BaseProtoField findField(int fieldId) {
		if (fields != null) {
			for (int i = 0; i != fields.size(); i++) {
				if (fields.get(i).getNameId() == fieldId)
					return fields.get(i);
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

	public void parse(String data) {
		if ((data != null) && (fields != null)) {
			if (!isBinary()) {
				BaseProtoField field;

				if (data.charAt(INPUT_OUTPUT_POS) != INPUT_PACKET)
					throw new IllegalArgumentException("Not an input packet");
				char ch = data.charAt(PACKET_ID_POS);
				if (ch != packetId.charAt(0))
					throw new IllegalArgumentException("Wrong packet type");
				setData(data);
				int position = 2; // Skip first 2 chars
				int delta = 0;
				for (int i = 0; i != fields.size(); i++) {
					field = fields.get(i);
					delta = field.getLength();
					String subdata = data
							.substring(position, position += delta);
					field.setData(subdata);
				}
			} else
				throw new IllegalArgumentException(
						"Non-hexadecimal mode is not supported in Secu3Packet.parse()");
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

	public Intent getIntent() {
		Intent intent = new Intent(
				Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PACKET);
		intent.putExtra(Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_PACKET,
				this);
		return intent;
	}

	public Intent getSkeletonIntent() {
		Intent intent = new Intent(
				Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_SKELETON_PACKET);
		intent.putExtra(
				Secu3Service.EVENT_SECU3_SERVICE_RECEIVE_PARAM_SKELETON_PACKET,
				this);
		return intent;
	}

	public String pack() {
		if (fields != null) {
			String pack = String.format("%s%2", OUTPUT_PACKET, getPacketId());
			for (int i = 0; i != fields.size(); i++) {
				BaseProtoField field = fields.get(i);
				field.pack();
				pack += field.getData();
			}
			return pack;
		}
		return null;
	}

	public void reset() {
		if (fields != null) {
			for (int i = 0; i != fields.size(); i++) {
				fields.get(i).reset();
			}
		}
	}
}

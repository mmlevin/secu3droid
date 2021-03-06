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

import org.secu3.android.R;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class ProtoFieldInteger extends BaseProtoField implements Parcelable {
	private int value;
	private int multiplier;
	private boolean signed;

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(value);
		dest.writeInt(multiplier);
		dest.writeInt(signed ? 1 : 0);
	}

	public static final Parcelable.Creator<ProtoFieldInteger> CREATOR = new Parcelable.Creator<ProtoFieldInteger>() {
		public ProtoFieldInteger createFromParcel(Parcel in) {
			return new ProtoFieldInteger(in);
		}

		public ProtoFieldInteger[] newArray(int size) {
			return new ProtoFieldInteger[size];
		}
	};

	public ProtoFieldInteger(Parcel in) {
		super(in);
		value = in.readInt();
		multiplier = in.readInt();
		signed = in.readInt() != 0;
	}

	public ProtoFieldInteger(ProtoFieldInteger field) {
		super(field);
		if (field != null) {
			this.value = field.value;
			this.multiplier = field.multiplier;
			this.signed = field.signed;
		}
	}

	public ProtoFieldInteger(Context context, int nameId, int type,
			boolean signed, boolean binary) {
		value = 0;
		setData(null);

		setNameId(nameId);
		setType(type);
		setSigned(signed);
		setMultiplier(1);
		setBinary(binary);
		if (nameId != 0)
			this.setName(context.getString(nameId));

		switch (type) {
		case R.id.field_type_int4:
			setLength(1);
			break;
		case R.id.field_type_int8:
			setLength(isBinary() ? 1 : 2);
			break;
		case R.id.field_type_int16:
			setLength(isBinary() ? 2 : 4);
			break;
		case R.id.field_type_int24:
			setLength(isBinary() ? 3 : 6);
			break;
		case R.id.field_type_int32:
			setLength(isBinary() ? 4 : 8);
			break;
		default:
			setLength(0);
		}
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	@Override
	public void setData(String data) {
		super.setData(data);
		if (data != null) {
			int v = 0;
			if (isBinary())
				v = BinToInt(data);
			if (signed) {
				switch (getType()) {
				case R.id.field_type_int4:
				case R.id.field_type_int24:
					throw new IllegalArgumentException(
							"No rules for converting 4/24-bit value into a signed number");
				case R.id.field_type_int8:
					if (isBinary())
						v = Integer.valueOf(v).byteValue();
					else
						v = Integer.valueOf(data, 16).byteValue();
					break;
				case R.id.field_type_int16:
					if (isBinary())
						v = Integer.valueOf(v).shortValue();
					else
						v = Integer.valueOf(data, 16).shortValue();
					break;
				case R.id.field_type_int32:
					if (isBinary())
						v = Long.valueOf(v).intValue();
					else
						v = Long.valueOf(data, 16).intValue();
					break;
				default:
					break;
				}
			} else {
				if (!isBinary())
					v = Integer.parseInt(data, 16);
			}
			setValue(v * multiplier);
		}
	}

	@Override
	public void pack() {
		int v = value / multiplier;
		switch (getType()) {
		case R.id.field_type_int4:
			if (signed)
				throw new IllegalArgumentException(
						"No rules for converting 4/24-bit value into a signed number");
			if (!isBinary())
				setData(String.format("%01X", v));
			break;
		case R.id.field_type_int8:
			if (signed)
				v = Integer.valueOf(v).byteValue();
			if (!isBinary())
				setData(String.format("%02X", (byte)v));
			break;
		case R.id.field_type_int16:
			if (signed)
				v = Integer.valueOf(v).shortValue();
			if (!isBinary())
				setData(String.format("%04X", (short)v));
			break;
		case R.id.field_type_int24:
			if (signed)
				throw new IllegalArgumentException(
						"No rules for converting 4/24-bit value into a signed number");
			if (!isBinary())
				setData(String.format("%06X", v));
			break;
		case R.id.field_type_int32:
			if (signed)
				v = Long.valueOf(v).intValue();
			if (!isBinary())
				setData(String.format("%08X", v));
			break;
		default:
			break;
		}
		if (isBinary())
			setData(IntToBin(v));
	}

	@Override
	public void reset() {
		super.reset();
		this.value = 0;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}
}

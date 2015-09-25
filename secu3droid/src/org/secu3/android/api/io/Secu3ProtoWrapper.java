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

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.secu3.android.R;
import org.secu3.android.api.utils.ResourcesUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

public class Secu3ProtoWrapper {
	private static final String LOG_TAG = "Secu3ProtoWrapper";

	private static final String SIGNED = "signed";
	private static final String LENGTH = "length";
	private static final String OFFSET = "offset";
	private static final String MULTIPLIER = "multiplier";
	private static final String DIVIDER = "divider";
	private static final String TYPE = "type";
	private static final String FIELD = "Field";
	private static final String MAX_VERSION = "maxVersion";
	private static final String MIN_VERSION = "minVersion";
	private static final String PACKET_ID = "packet_id";
	private static final String PACKET_DIR = "packet_dir";
	private static final String NAME = "name";
	private static final String PACKET = "Packet";
	private static final String PROTOCOL = "Protocol";
	private SparseArray<Secu3Packet> inputPackets;
	private SparseArray<Secu3Packet> outputPackets;
	private String funsetNames[] = null;
	private Context context;
	private Secu3Packet lastPacket;
	private boolean binary;

	public Secu3ProtoWrapper (Context context) {
		this.setContext(context);
	}
	
	public SparseArray<Secu3Packet>  getInputPackets() {
		return inputPackets;
	}

	public void setInputPackets(SparseArray<Secu3Packet>  packets) {
		this.inputPackets = packets;
	}
	
	public boolean instantiateFromXml (int xmlId, int protocolVersion) throws ParseException {
		String name;
		
		Secu3Packet packet = null;
		BaseProtoField field;
				
		String attr;
		String attrValue;
		
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		
		try {
			XmlPullParser xpp = getContext().getResources().getXml(xmlId);
			inputPackets = new SparseArray<> ();
			outputPackets = new SparseArray<> ();
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_TAG:
					name = xpp.getName();
					if (name.equalsIgnoreCase(PROTOCOL)) {
						if (inputPackets.size() != 0) throw new IllegalArgumentException("Pages adapter is non empty, probably nested Protocol element"); 
						if (outputPackets.size() != 0) throw new IllegalArgumentException("Pages adapter is non empty, probably nested Protocol element");
					} else					
					// Found new packet element
					if (name.equalsIgnoreCase(PACKET)) {
						String packetId = null;
						String packetName = null;
						String packetDir = null;
						int minVersion = protocolVersion;
						int maxVersion = protocolVersion;
						int count = xpp.getAttributeCount(); 
						if (count > 0) {
							for (int i = 0; i != count; i++) {
								attr  = xpp.getAttributeName(i);
								attrValue = xpp.getAttributeValue(i);
								switch (attr) {
									case NAME:
										if (!ResourcesUtils.isResource(attrValue))
											throw new IllegalArgumentException("Packet name must be a string reference");
										packetName = attrValue;
										break;
									case PACKET_ID:
										packetId = attrValue;
										break;
									case MIN_VERSION:
										minVersion = Integer.parseInt(attrValue);
										break;
									case MAX_VERSION:
										maxVersion = Integer.parseInt(attrValue);
										break;
									case PACKET_DIR:
										if (!ResourcesUtils.isResource(attrValue))
											throw new IllegalArgumentException("Packet direction must be a string reference");
										packetDir = attrValue;
										break;
								}
							}
							if ((packetName == null) || (TextUtils.isEmpty(packetName)) || (packetId == null) || (TextUtils.isEmpty(packetId))) {
								throw new IllegalArgumentException("Packet element is invalid");							
							} else {
								packet = new Secu3Packet(getContext(), ResourcesUtils.referenceToInt(packetName), ResourcesUtils.referenceToInt(packetId), isBinary());
								if (packetDir == null) packet.setPacketDirResId(Secu3Packet.INPUT_TYPE);
								else packet.setPacketDirResId(ResourcesUtils.referenceToInt(packetDir));
								if ((protocolVersion >= minVersion) && (protocolVersion <= maxVersion)) {
									if (packet.getPacketDirResId() == R.string.packet_dir_input)
										inputPackets.put(packet.getNameId(),packet);
									else if (packet.getPacketDirResId() == R.string.packet_dir_output)
										outputPackets.put(packet.getNameId(),packet);
								}
							}							
						}												
					} else
					// Found new field element
					if (name.equalsIgnoreCase(FIELD)){
						String fieldName = null; 
						int fieldType = 0;
						int minVersion = protocolVersion;
						int maxVersion = protocolVersion;
						String fieldDivider = null;
						String fieldSigned = null;		
						String fieldLength = null;
						String fieldOffset = null;
						String fieldMultiplier = null;
						int count = xpp.getAttributeCount(); 
						if (count > 0) {
							for (int i = 0; i != count; i++) {
								attr  = xpp.getAttributeName(i);
								attrValue = xpp.getAttributeValue(i);
								if (attr.equalsIgnoreCase(NAME)) {
									if (!ResourcesUtils.isResource(attrValue)) throw new IllegalArgumentException("Field name must be a string reference");
									fieldName = attrValue;									
								} else if (attr.equalsIgnoreCase(TYPE)) {
									if (ResourcesUtils.isResource(attrValue)) {
										fieldType = ResourcesUtils.referenceToInt(attrValue);
									} else throw new IllegalArgumentException("Field type must be a reference");									
								} else if (attr.equalsIgnoreCase(MIN_VERSION)) {
									minVersion = Integer.parseInt(attrValue);
								} else if (attr.equalsIgnoreCase(MAX_VERSION)) {
									maxVersion = Integer.parseInt(attrValue);
								} else if (attr.equalsIgnoreCase(DIVIDER)) {
									fieldDivider = attrValue;
								} else if (attr.equalsIgnoreCase(MULTIPLIER)) {
									fieldMultiplier = attrValue;
								} else if (attr.equalsIgnoreCase(OFFSET)) {
									fieldOffset = attrValue;
								} else if (attr.equalsIgnoreCase(LENGTH)) {
									fieldLength = attrValue;
								} else
								if (attr.equalsIgnoreCase(SIGNED)) {
									fieldSigned = attrValue;
								}
							} 
							
							if ((fieldName == null) || (fieldType == 0) || (TextUtils.isEmpty(fieldName))) throw new IllegalArgumentException("Field element is invalid");
							else {													
								switch (fieldType) {
								case R.id.field_type_int4:
								case R.id.field_type_int8:
								case R.id.field_type_int16:
								case R.id.field_type_int24:
								case R.id.field_type_int32:
									field = new ProtoFieldInteger(getContext(), ResourcesUtils.referenceToInt(fieldName), fieldType, Boolean.parseBoolean(fieldSigned), isBinary());
									if (fieldMultiplier != null)
										((ProtoFieldInteger) field).setMultiplier((ResourcesUtils.isResource(fieldMultiplier))?ResourcesUtils.getReferenceInt(getContext(), fieldMultiplier):Integer.valueOf(fieldMultiplier));
									break;
								case R.id.field_type_float4:
								case R.id.field_type_float8:
								case R.id.field_type_float16:
								case R.id.field_type_float24:
								case R.id.field_type_float32:		
									field = new ProtoFieldFloat(getContext(), ResourcesUtils.referenceToInt(fieldName), fieldType, Boolean.parseBoolean(fieldSigned), isBinary());
									if (fieldDivider != null)
										((ProtoFieldFloat) field).setIntDivider((ResourcesUtils.isResource(fieldDivider))?ResourcesUtils.getReferenceInt(getContext(), fieldDivider):Integer.valueOf(fieldDivider));
									if (fieldMultiplier != null)
										((ProtoFieldFloat) field).setIntMultiplier ((ResourcesUtils.isResource(fieldMultiplier))?ResourcesUtils.getReferenceInt(getContext(), fieldMultiplier):Integer.valueOf(fieldMultiplier));
									if (fieldOffset != null)
										((ProtoFieldFloat) field).setIntOffset ((ResourcesUtils.isResource(fieldOffset))?ResourcesUtils.getReferenceInt(getContext(), fieldOffset):Integer.valueOf(fieldOffset));
									break;															
								case R.id.field_type_string:
									field = new ProtoFieldString(getContext(), ResourcesUtils.referenceToInt(fieldName), fieldType, format.parse(fieldLength).intValue(), isBinary());
									break;
								default: throw new IllegalArgumentException("Unknown field type for: "+fieldName);
								}
								if ((protocolVersion >= minVersion) && (protocolVersion <= maxVersion)) {
									packet.addField(field);
								}
							}							
						}											
					}
					break;
				}
				xpp.next();
			}
	    	} catch (XmlPullParserException e) {
	    		e.printStackTrace();
	    		return false;
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    		return false;
	    	}		
		return true;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}
	
	public void parse (String data) {
		if (inputPackets != null) {
			
			boolean result = false;
			Secu3Packet packet;
			for (int i = 0; i != inputPackets.size(); i++) {
				try {
					packet = inputPackets.valueAt(i);
					if (!packet.parse(data)) continue;
					lastPacket = packet;
					result = true;
					if (packet.getNameId() == R.string.fnname_dat_title) {
						if (funsetNames == null) {
							funsetNames = new String [((ProtoFieldInteger) packet.findField(R.string.fnname_dat_quantity_title)).getValue()];
						}
						if (getFunsetNames() != null) {
							funsetNames[((ProtoFieldInteger) packet.findField(R.string.fnname_dat_index_title)).getValue()] = ((ProtoFieldString) packet.findField(R.string.fnname_dat_data_title)).getValue();
						}
					}
					break;
				} catch (IllegalArgumentException e) {
					Log.e (LOG_TAG, e.getMessage());
				}
			}
			if (!result) {
				throw new IllegalArgumentException("No valid parser for packet");
			}
		}
	}

	public synchronized Secu3Packet obtainPacketSkeleton (int packetNameId, int prefferedDir) {
		SparseArray<Secu3Packet> prefferedPackets;
		SparseArray<Secu3Packet> reservePackets;
		if ((prefferedDir == 0) || (prefferedDir == R.string.packet_dir_input)) {
			prefferedPackets = inputPackets;
			reservePackets = outputPackets;
		} else {
			prefferedPackets = outputPackets;
			reservePackets = inputPackets;
		}
		
		if (prefferedPackets != null) {
			Secu3Packet samplePacket = prefferedPackets.get(packetNameId);
			if ((samplePacket == null) && (reservePackets != null)) samplePacket = reservePackets.get(packetNameId); 
			Secu3Packet packet = new Secu3Packet(samplePacket);
			packet.reset();
			return packet;
		}
		return null;
	}
	
	public Secu3Packet getLastPacket() {
		return lastPacket;
	}

	public Intent getLastPacketIntent() {
		if (lastPacket != null) return lastPacket.getIntent(); else return new Intent();
	}
	
	public void init() {
		funsetNames = null;
	}

	public String getLogString() {
		if (lastPacket != null) return lastPacket.getName();
		return "";
	}

	public int funsetNamesCounter() {
		if (funsetNames == null) return 0;
		int counter = 0;
		for (int i = 0; i != funsetNames.length; i++) {
			if (funsetNames[i] != null) counter ++;
		}
		return counter;
	}
	
	public boolean funsetNamesValid() {
		return funsetNamesCounter() == funsetNames.length;
	}
	
	public synchronized String[] getFunsetNames() {
		return funsetNames;
	}

	public void setFunsetNames(String funsetNames[]) {
		this.funsetNames = funsetNames;
	}
}

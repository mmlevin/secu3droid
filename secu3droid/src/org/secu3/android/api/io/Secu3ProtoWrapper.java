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
import android.util.SparseArray;

public class Secu3ProtoWrapper {
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
	private static final String NAME = "name";
	private static final String PACKET = "Packet";
	private static final String PROTOCOL = "Protocol";
	private SparseArray<Secu3Packet> packets;
	private String funsetNames[] = null;
	private Context context;
	private Secu3Packet lastPacket;
	private boolean binary;

	public Secu3ProtoWrapper (Context context) {
		this.setContext(context);
	}
	
	public SparseArray<Secu3Packet>  getPackets() {
		return packets;
	}

	public void setPackets(SparseArray<Secu3Packet>  packets) {
		this.packets = packets;
	}
	
	public boolean instantiateFromXml (int xmlId, int protocolVersion) throws ParseException {
		String name;
		
		Secu3Packet packet = null;
		BaseProtoField field = null;
			
		String packetName = null;
		String packetId = null;
		String packetMinVersion = null;
		String packetMaxVersion = null;
		
		String fieldName = null; 
		int fieldType = 0;
		String fieldMinVersion = null;
		String fieldMaxVersion = null;
		String fieldSigned = null;
		String fieldOffset = null;
		String fieldDivider = null;
		String fieldMultiplier = null;
		String fieldLength = null;

		String attr = null;
		String attrValue = null;
		
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		
		try {
			XmlPullParser xpp = getContext().getResources().getXml(xmlId);
			packets = new SparseArray<Secu3Packet> ();
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					name = xpp.getName();
					if (name.equalsIgnoreCase(PROTOCOL)) {
						if (packets.size() != 0) throw new IllegalArgumentException("Pages adapter is non empty, probably nested Protocol element"); 
					} else					
					// Found new packet element
					if (name.equalsIgnoreCase(PACKET)) {
						if (packet != null) {
							throw new IllegalArgumentException("Packets cannot be nested");
						}
						packetId = null;
						packetMinVersion = null;
						packetName = null;
						int count = xpp.getAttributeCount(); 
						if (count > 0) {
							for (int i = 0; i != count; i++) {
								attr  = xpp.getAttributeName(i);
								attrValue = xpp.getAttributeValue(i);
								if (attr.equals(NAME)) {	
									if (!ResourcesUtils.isResource(attrValue)) throw new IllegalArgumentException("Packet name must be a string reference");
									packetName = attrValue;
								} else
								if (attr.equals(PACKET_ID)) {
									packetId = attrValue;
								} else
								if (attr.equals(MIN_VERSION)) {
									packetMinVersion = attrValue;
								}if (attr.equals(MAX_VERSION)) {
									packetMaxVersion = attrValue;
								}
							}
						}
						if ((packetName == null) || (TextUtils.isEmpty(packetName)) || (packetMinVersion == null) || (TextUtils.isEmpty(packetMinVersion)) || (packetId == null) || (TextUtils.isEmpty(packetId))) {
							throw new IllegalArgumentException("Packet element is invalid");							
						} else {
							int maxVersion = 0;
							if (packetMaxVersion != null) maxVersion = format.parse(packetMaxVersion).intValue();
							packet = new Secu3Packet(getContext(), ResourcesUtils.referenceToInt(packetName), ResourcesUtils.referenceToInt(packetId), format.parse(packetMinVersion).intValue(), maxVersion, isBinary());
							if ((protocolVersion >= packet.getMinVersion()) && (protocolVersion <= packet.getMaxVersion())) {
								packets.put(packet.getNameId(),packet);
							}
						}						
					} else
					// Found new field element
					if (name.equalsIgnoreCase(FIELD)){
						if (field != null) {
							throw new IllegalArgumentException("Fields can't be nested");
						}
						fieldName = null; 
						fieldType = 0;
						fieldMinVersion = null;
						fieldMaxVersion = null;
						fieldDivider = null;
						fieldSigned = null;		
						fieldLength = null;
						fieldOffset = null;
						fieldMultiplier = null;
						int count = xpp.getAttributeCount(); 
						if (count > 0) {
							for (int i = 0; i != count; i++) {
								attr  = xpp.getAttributeName(i);
								attrValue = xpp.getAttributeValue(i);
								if (attr.equalsIgnoreCase(NAME)) {
									if (!ResourcesUtils.isResource(attrValue)) throw new IllegalArgumentException("Field name must be a string reference");
									fieldName = attrValue;									
								}
								if (attr.equalsIgnoreCase(TYPE)) {
									if (ResourcesUtils.isResource(attrValue)) {
										fieldType = ResourcesUtils.referenceToInt(attrValue);
									} else throw new IllegalArgumentException("Field type must be a reference");									
								} else 
								if (attr.equalsIgnoreCase(MIN_VERSION)) {
									fieldMinVersion = attrValue;
								} else
								if (attr.equalsIgnoreCase(MAX_VERSION)) {
									fieldMaxVersion = attrValue;
								} else
								if (attr.equalsIgnoreCase(DIVIDER)) {
									fieldDivider = attrValue;
								} else
								if (attr.equalsIgnoreCase(MULTIPLIER)) {
									fieldMultiplier = attrValue;
								} else
								if (attr.equalsIgnoreCase(OFFSET)) {
									fieldOffset = attrValue;
								} else
								if (attr.equalsIgnoreCase(LENGTH)) {
									fieldLength = attrValue;
								} else
								if (attr.equalsIgnoreCase(SIGNED)) {
									fieldSigned = attrValue;
								}
							}
						}											
					}
					break;
				case XmlPullParser.END_TAG:
					name = xpp.getName();
					if (name.equalsIgnoreCase(PROTOCOL)) {
						if (packets.size() == 0) throw new IllegalArgumentException("Protocol closed, but not opened");
					} else				
					if (name.equalsIgnoreCase(PACKET)) {
						if (packet == null) throw new IllegalArgumentException("Packet closed, but not opened");
						packet = null;
					} else
					if (name.equalsIgnoreCase(FIELD)) {
						int maxVersion = 0;
						if (fieldMaxVersion != null) maxVersion = format.parse(fieldMaxVersion).intValue(); 
						
						if ((fieldName == null) || (fieldType == 0) || (TextUtils.isEmpty(fieldName))) throw new IllegalArgumentException("Field element is invalid");
						else {													
							switch (fieldType) {
							case R.id.field_type_int4:
							case R.id.field_type_int8:
							case R.id.field_type_int16:
							case R.id.field_type_int32:
								field = new ProtoFieldInteger(getContext(), ResourcesUtils.referenceToInt(fieldName), fieldType, Boolean.parseBoolean(fieldSigned), format.parse(fieldMinVersion).intValue(), maxVersion, isBinary());
								if (fieldMultiplier != null)
									((ProtoFieldInteger) field).setMultiplier((ResourcesUtils.isResource(fieldMultiplier))?ResourcesUtils.getReferenceInt(getContext(), fieldMultiplier):Integer.valueOf(fieldMultiplier));
								break;
							case R.id.field_type_float4:
							case R.id.field_type_float8:
							case R.id.field_type_float16:
							case R.id.field_type_float32:		
								field = new ProtoFieldFloat(getContext(), ResourcesUtils.referenceToInt(fieldName), fieldType, Boolean.parseBoolean(fieldSigned), format.parse(fieldMinVersion).intValue(), maxVersion, isBinary());
								if (fieldDivider != null)
									((ProtoFieldFloat) field).setIntDivider((ResourcesUtils.isResource(fieldDivider))?ResourcesUtils.getReferenceInt(getContext(), fieldDivider):Integer.valueOf(fieldDivider));
								if (fieldMultiplier != null)
									((ProtoFieldFloat) field).setIntMultiplier ((ResourcesUtils.isResource(fieldMultiplier))?ResourcesUtils.getReferenceInt(getContext(), fieldMultiplier):Integer.valueOf(fieldMultiplier));
								if (fieldOffset != null)
									((ProtoFieldFloat) field).setIntOffset ((ResourcesUtils.isResource(fieldOffset))?ResourcesUtils.getReferenceInt(getContext(), fieldOffset):Integer.valueOf(fieldOffset));
								break;															
							case R.id.field_type_string:
								field = new ProtoFieldString(getContext(), ResourcesUtils.referenceToInt(fieldName), fieldType, format.parse(fieldLength).intValue(), format.parse(fieldMinVersion).intValue(), maxVersion, isBinary());
								break;
							default: throw new IllegalArgumentException("Unknown field type");
							}
							if ((protocolVersion >= field.getMinVersion()) && (protocolVersion <= field.getMaxVersion())) {
								packet.addField(field);
							}
							field = null;
						}
					}
					break;
				case XmlPullParser.TEXT:					
					break;
				default:
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
		if (packets != null) {
			boolean result = false;
			Secu3Packet packet = null;
			if (packets != null) {
				for (int i = 0; i != packets.size(); i++) {
					try {
						packet = packets.valueAt(i);
						if (packet != null) {
							packet.parse(data);
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
						}
					} catch (IllegalArgumentException e) {						
					}
				}
			}
			if (!result) {
				throw new IllegalArgumentException("No valid parser for packet");
			}
		}
	}

	public synchronized Secu3Packet obtainPacketSkeleton (int packetNameId) {
		if (packets != null) {
			Secu3Packet packet = new Secu3Packet(packets.get(packetNameId));
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

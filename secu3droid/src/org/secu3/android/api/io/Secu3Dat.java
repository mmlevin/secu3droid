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

import java.util.Arrays;
import java.util.Locale;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/** Общий класс для пакетов **/
public class Secu3Dat implements Parcelable {	
	 public final static int MAX_PACKET_SIZE = 128;
	
	 public final static int MAP_PHYSICAL_MAGNITUDE_MULTIPLAYER  = 64;
	 public final static int UBAT_PHYSICAL_MAGNITUDE_MULTIPLAYER = 400;
     public final static int TEMP_PHYSICAL_MAGNITUDE_MULTIPLAYER = 4;
     public final static int TPS_PHYSICAL_MAGNITUDE_MULTIPLAYER = 2;
     public final static int CHOKE_PHYSICAL_MAGNITUDE_MULTIPLIER = 2;
     
     static final float ANGLE_MULTIPLIER = 32.0f;
     static final float ADC_DISCRETE = 0.0025f;
     
     public final static String RECEIVE_SECU_3DAT 		= "org.secu3.android.intent.action.RECEIVE_SECU3_DAT";
     public final static String RECEIVE_CHANGEMODE 		= "org.secu3.android.intent.action.RECEIVE_CHANGEMODE";
     public final static String RECEIVE_BOOTLOADER 		= "org.secu3.android.intent.action.RECEIVE_BOOTLOADER";
	 public final static String RECEIVE_TEMPER_PAR 		= "org.secu3.android.intent.action.RECEIVE_TEMPER_PAR";
	 public final static String RECEIVE_CARBUR_PAR 		= "org.secu3.android.intent.action.RECEIVE_CARBUR_PAR";
	 public final static String RECEIVE_IDLREG_PAR 		= "org.secu3.android.intent.action.RECEIVE_IDLREG_PAR";
	 public final static String RECEIVE_ANGLES_PAR 		= "org.secu3.android.intent.action.RECEIVE_ANGLES_PAR";
	 public final static String RECEIVE_FUNSET_PAR 		= "org.secu3.android.intent.action.RECEIVE_FUNSET_PAR";
	 public final static String RECEIVE_STARTER_PAR 	= "org.secu3.android.intent.action.RECEIVE_STARTER_PAR"; 
	 public final static String RECEIVE_FNNAME_DAT 		= "org.secu3.android.intent.action.RECEIVE_FNNAME_DAT";
	 public final static String RECEIVE_SENSOR_DAT 		= "org.secu3.android.intent.action.RECEIVE_SENSOR_DAT";
	 public final static String RECEIVE_ADCCOR_PAR 		= "org.secu3.android.intent.action.RECEIVE_ADCCOR_PAR";
	 public final static String RECEIVE_ADCRAW_DAT 		= "org.secu3.android.intent.action.RECEIVE_ADCRAW_DAT";
	 public final static String RECEIVE_CKPS_PAR 		= "org.secu3.android.intent.action.RECEIVE_CKPS_PAR";
	 public final static String RECEIVE_OP_COMP_NC 		= "org.secu3.android.intent.action.RECEIVE_OP_COMP_NC";
	 public final static String RECEIVE_CE_ERR_CODES 	= "org.secu3.android.intent.action.RECEIVE_CE_ERR_CODES";
	 public final static String RECEIVE_KNOCK_PAR	  	= "org.secu3.android.intent.action.RECEIVE_KNOCK_PAR";
	 public final static String RECEIVE_CE_SAVED_ERR 	= "org.secu3.android.intent.action.RECEIVE_CE_SAVED_ERR";
	 public final static String RECEIVE_FWINFO_DAT 		= "org.secu3.android.intent.action.RECEIVE_FWINFO_DAT";
	 public final static String RECEIVE_MISCEL_PAR 		= "org.secu3.android.intent.action.RECEIVE_MISCEL_PAR";
	 public final static String RECEIVE_EDITAB_PAR 		= "org.secu3.android.intent.action.RECEIVE_EDITTAB_PAR";
	 public final static String RECEIVE_ATTTAB_PAR 		= "org.secu3.android.intent.action.RECEIVE_ATTTAB_PAR";
	 public final static String RECEIVE_DBGVAR_DAT 		= "org.secu3.android.intent.action.RECEIVE_DBGVAR_DAT";
	 public final static String RECEIVE_DIAGINP_DAT 	= "org.secu3.android.intent.action.RECEIVE_DIAGINP_DAT";
	 public final static String RECEIVE_CHOKE_PAR 		= "org.secu3.android.intent.action.RECEIVE_CHOKE_PAR";
	 
	 
	 public final static String SEND_STARTR_PAR 		= "org.secu3.android.intent.action.SEND_STARTR_PAR";
	 public final static String SEND_ANGLES_PAR 		= "org.secu3.android.intent.action.SEND_ANGLES_PAR";
	 public final static String SEND_IDLREG_PAR 		= "org.secu3.android.intent.action.SEND_IDLREG_PAR";
	 public final static String SEND_KNOCK_PAR 			= "org.secu3.android.intent.action.SEND_KNOCK_PAR";
	 public final static String SEND_FUNSET_PAR 		= "org.secu3.android.intent.action.SEND_FUNSET_PAR";
	 public final static String SEND_TEMPER_PAR 		= "org.secu3.android.intent.action.SEND_TEMPER_PAR";
	 public final static String SEND_CARBUR_PAR 		= "org.secu3.android.intent.action.SEND_CARBUR_PAR";
	 public final static String SEND_ADCCOR_PAR 		= "org.secu3.android.intent.action.SEND_ADCCOR_PAR";
	 public final static String SEND_CKPS_PAR 			= "org.secu3.android.intent.action.SEND_CKPS_PAR";
	 public final static String SEND_MISCEL_PAR 		= "org.secu3.android.intent.action.SEND_MISCEL_PAR";
	 public final static String SEND_CE_SAVED_ERR 		= "org.secu3.android.intent.action.SEND_CE_SAVED_ERR";
	 public final static String SEND_DIAGOUT_DAT 		= "org.secu3.android.intent.action.SEND_DIAGOUT_DAT";	
	 public final static String SEND_EDITAB_PAR 		= "org.secu3.android.intent.action.SEND_EDITTAB_PAR";
	 public final static String SEND_OP_COMP_NC 		= "org.secu3.android.intent.action.SEND_OP_COMP_NC";
	 public final static String SEND_CHOKE_PAR 			= "org.secu3.android.intent.action.SEND_CHOKE_PAR";
	 
	 public final static int OPCODE_EEPROM_PARAM_SAVE    = 1;
	 public final static int OPCODE_CE_SAVE_ERRORS       = 2;
	 public final static int OPCODE_READ_FW_SIG_INFO     = 3;
	 public final static int OPCODE_LOAD_TABLSET         = 4;  //realtime tables
	 public final static int OPCODE_SAVE_TABLSET         = 5;  //realtime tables
	 public final static int OPCODE_DIAGNOST_ENTER       = 6;  //enter diagnostic mode
	 public final static int OPCODE_DIAGNOST_LEAVE       = 7;  //leave diagnostic mode
	 
	 public final static int SECU3_ECU_ERRORS_COUNT		  = 11;
	 public final static int ECUERROR_CKPS_MALFUNCTION    = 0;
	 public final static int ECUERROR_EEPROM_PARAM_BROKEN = 1;
	 public final static int ECUERROR_PROGRAM_CODE_BROKEN = 2;
	 public final static int ECUERROR_KSP_CHIP_FAILED     = 3;
	 public final static int ECUERROR_KNOCK_DETECTED      = 4;
	 public final static int ECUERROR_MAP_SENSOR_FAIL     = 5;
	 public final static int ECUERROR_TEMP_SENSOR_FAIL    = 6;
	 public final static int ECUERROR_VOLT_SENSOR_FAIL    = 7;
	 public final static int ECUERROR_DWELL_CONTROL       = 8;
	 public final static int ECUERROR_CAMS_MALFUNCTION    = 9;
	 public final static int ECUERROR_TPS_SENSOR_FAIL     = 10;

	 
	 public final static int FW_SIGNATURE_INFO_SIZE	  = 48;
	 
	 public final static int ETTS_GASOLINE_SET = 0;       //tables's set: petrol
	 public final static int ETTS_GAS_SET = 1;            //tables's set: gas
	
	 public final static int ETMT_STRT_MAP = 0;           //start map
	 public final static int ETMT_IDLE_MAP = 1;           //idle map
	 public final static int ETMT_WORK_MAP = 2;           //work map
	 public final static int ETMT_TEMP_MAP = 3;           //temp.corr. map
	 public final static int ETMT_NAME_STR = 4;           //name of tables's set
	 
	 public final static char INPUT_PACKET = '@';
	 public final static char OUTPUT_PACKET = '!';
	 
	 public final static char CHANGEMODE = 'h';   //!< change mode (type of default packet)
	 public final static char BOOTLOADER = 'i';   //!< start boot loader

	 public final static char TEMPER_PAR = 'j';   //!< temperature parameters (coolant sensor, engine cooling etc)
	 public final static char CARBUR_PAR = 'k';   //!< carburetor's parameters
	 public final static char IDLREG_PAR = 'l';   //!< idling regulator parameters
	 public final static char ANGLES_PAR = 'm';   //!< advance angle (ign. timing) parameters
	 public final static char FUNSET_PAR = 'n';   //!< parameters related to set of functions (lookup tables)
	 public final static char STARTR_PAR = 'o';   //!< engine start parameters

	 public final static char FNNAME_DAT = 'p';   //!< used for transferring of names of set of functions (lookup tables)
	 public final static char SENSOR_DAT = 'q';   //!< used for transferring of sensors data

	 public final static char ADCCOR_PAR = 'r';   //!< parameters related to ADC corrections
	 public final static char ADCRAW_DAT = 's';   //!< used for transferring 'raw' values directly from ADC

	 public final static char CKPS_PAR = 't';   //!< CKP sensor parameters
	 public final static char OP_COMP_NC = 'u';   //!< used to indicate that specified (suspended) operation completed

	 public final static char CE_ERR_CODES ='v';   //!< used for transferring of CE codes

	 public final static char KNOCK_PAR = 'w';   //!< parameters related to knock detection and knock chip

	 public final static char CE_SAVED_ERR ='x';   //!< used for transferring of CE codes stored in the EEPROM
	 public final static char FWINFO_DAT = 'y';   //!< used for transferring information about firmware
	 public final static char MISCEL_PAR = 'z';   //!< miscellaneous parameters
	 public final static char EDITAB_PAR = '{';   //!< used for transferring of data for realtime tables editing
	 public final static char ATTTAB_PAR = '}';   //!< used for transferring of attenuator map (knock detection related)
	 public final static char DBGVAR_DAT = ':';   //!< for watching of firmware variables (used for debug purposes)

	 public final static char DIAGINP_DAT = '=';   //!< diagnostics: send input values (analog & digital values)
	 public final static char DIAGOUT_DAT = '^';   //!< diagnostics: receive output states (bits)
	 public final static char CHOKE_PAR = '%'; 
	 
	 public final static int INPUT_OUTPUT_POS = 0;
	 public final static int PACKET_ID_POS = 1;

	 public final static String PACKET_ID[] = {"CHANGEMODE","BOOTLOADER","TEMPER_PAR","CARBUR_PAR","IDLREG_PAR","ANGLES_PAR","FUNSET_PAR","STARTR_PAR",
			 "FNNAME_DAT","SENSOR_DAT","ADCCOR_PAR","ADCRAW_DAT","CKPS_PAR","OP_COMP_NC","CE_ERR_CODES","KNOCK_PAR",
			 "CE_SAVED_ERR","FWINFO_DAT","MISCEL_PAR","EDITAB_PAR","ATTTAB_PAR","DBGVAR_DAT","DIAGINP_DAT","DIAGOUT_DAT","CHOKE_PAR"};
	 
	 public final static char PACKET_ID_INDEX[] = {CHANGEMODE,BOOTLOADER,TEMPER_PAR,CARBUR_PAR,IDLREG_PAR,ANGLES_PAR,FUNSET_PAR,STARTR_PAR,
		 FNNAME_DAT,SENSOR_DAT,ADCCOR_PAR,ADCRAW_DAT,CKPS_PAR,OP_COMP_NC,CE_ERR_CODES,KNOCK_PAR,
		 CE_SAVED_ERR,FWINFO_DAT,MISCEL_PAR,EDITAB_PAR,ATTTAB_PAR,DBGVAR_DAT,DIAGINP_DAT,DIAGOUT_DAT,CHOKE_PAR};

	 public final static int BAUD_RATE[] = {2400,4800,9600,14400,19200,28800,38400,57600};
	 public final static int BAUD_RATE_INDEX[] = {0x340,0x1A0,0xCF,0x8A,0x67,0x44,0x33,0x22};
	 
	 protected char packet_id;
	 protected int packet_size;
	 String data;	// Строковое представление пакета	 
	 String intent_action;
	 
	 /** Вычисляет индекс элемента в масиве **/
	 public static int indexOf (int array[], int search)
	 {
		 for (int i=0; i!=array.length; i++) if (array[i] == search) return i;
		 return -1;
	 }
	 
	 /** Вычисляет индекс элемента в масиве **/
	 static int indexOf (char array[], char search)
	 {
		 for (int i=0; i!=array.length; i++) if (array[i] == search) return i;
		 return -1;
	 }
	 
	 /** Осуществляет разбор строки с данными входящего пакета **/
	 public void parse (String packet) throws Exception {		 
		 data = packet;
		 if (data.charAt(INPUT_OUTPUT_POS) != INPUT_PACKET) throw new Exception("Not a input packet");
		 char ch = data.charAt (PACKET_ID_POS);
		 int idx = indexOf(PACKET_ID_INDEX, ch);
		 if (idx <= 0) throw new Exception (String.format("Wrong packet: '%s' ID recieved", ch));
		 if (ch != packet_id) throw new Exception(String.format("Not a %s('%s') packet: '%s' instead",PACKET_ID[indexOf(PACKET_ID_INDEX,packet_id)],packet_id,ch));
		 if (data.length() != packet_size) throw new Exception (String.format("Wrong %s size: %d byte(s)instead of %d byte(s)",PACKET_ID[indexOf(PACKET_ID_INDEX,packet_id)],data.length(),packet_size));		 
	 }
	 
	 /** Базовый класс для упаковки данных в выходной формат пакета **/
	 public String pack () throws Exception {
		 return "";
	 }
	 
	 /** Возвращает строку для вывода в logcat **/
	 public String getLogString ()
	 {
		 return "No log string for class " + this.getClass().getCanonicalName();
	 }
	 
	 @Override
	 public int describeContents() {
		 return 0;
	 }
	 
	 @Override
	 public void writeToParcel(Parcel dest, int flags) {		
		 Log.d(this.getClass().getCanonicalName(), "Write to Parcel");
		 dest.writeInt(packet_id);
		 dest.writeInt(packet_size);
		 dest.writeString(data);
		 dest.writeString(intent_action);
	 }
	 
	 public static final Parcelable.Creator<Secu3Dat> CREATOR = new Parcelable.Creator<Secu3Dat>() {
		 public Secu3Dat createFromParcel(Parcel in) {
			 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
			 return new Secu3Dat (in);
		 }

		 public Secu3Dat[] newArray(int size) {
			 return new Secu3Dat[size];
		 }
	};
	
	public Secu3Dat () {		
		intent_action = RECEIVE_SECU_3DAT;
	}
	
	public Secu3Dat (Parcel in) {		
		Log.d(this.getClass().getCanonicalName(), "Constructor (Parcel)");
		this.packet_id = (char) in.readInt();
		this.packet_size = in.readInt();
		this.data = in.readString();
		this.intent_action = in.readString();
	}	 
	
	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (this.getClass() == o.getClass()) {
			result = true;
			result &= this.data.equals(((Secu3Dat)o).data);
			result &= this.packet_id == ((Secu3Dat)o).packet_id;
			result &= this.packet_size == ((Secu3Dat)o).packet_size;
		}
		return result;
	}	
	
	public Intent getIntent ()
	{
		Intent intent = new Intent (intent_action);
		intent.putExtra (this.getClass().getCanonicalName(),this);
		return intent;
	}
	 
	 /** Класс пакета параметров коррекции АЦП **/
	 public static class ADCCorPar extends Secu3Dat {		 
		static final int PACKET_SIZE = 74;
		
		/** Коэффициент коррекции для канала ДАД АЦП AAAA **/
		public float  map_adc_factor;                //коэффициент передаточной погрешности
		/** Коррекция для канала ДАД АЦП (см. исх. текст прошивки) BBBBBBBB **/
		public float  map_adc_correction;            //сдвиг в вольтах
		/** Коэффициент коррекции для канала бортсети АЦП CCCC **/
		public float  ubat_adc_factor;
		/** Коррекция для канала бортсети АЦП DDDDDDDD **/
		public float  ubat_adc_correction;
		/** Коэффициент коррекции для канала температуры АЦП EEEE **/
		public float  temp_adc_factor;
		/** Коррекция для канала температуры АЦП FFFFFFFF **/
		public float  temp_adc_correction;
		public float tps_adc_factor;
		public float tps_adc_correction;
		public float add_i1_factor;
		public float add_i1_correction;
		public float add_i2_factor;
		public float add_i2_correction;

		public static final Parcelable.Creator<ADCCorPar> CREATOR = new Parcelable.Creator<ADCCorPar>() {
			 public ADCCorPar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new ADCCorPar(in);
			 }

			 public ADCCorPar[] newArray(int size) {
				 return new ADCCorPar[size];
			 }
		};
		
		public ADCCorPar() {
			packet_id = ADCCOR_PAR;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_ADCCOR_PAR;
		}		
		
		public ADCCorPar (Parcel in) {
			super (in);
			map_adc_factor = in.readFloat();
			map_adc_correction = in.readFloat();
			ubat_adc_factor = in.readFloat();
			ubat_adc_correction = in.readFloat();
			temp_adc_factor = in.readFloat();
			temp_adc_correction = in.readFloat();
			tps_adc_factor = in.readFloat();
			tps_adc_correction = in.readFloat();
			add_i1_factor = in.readFloat();
			add_i1_correction = in.readFloat();
			add_i2_factor = in.readFloat();
			add_i2_correction = in.readFloat();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeFloat(map_adc_factor);
			dest.writeFloat(map_adc_correction);
			dest.writeFloat(ubat_adc_factor);
			dest.writeFloat(ubat_adc_correction);
			dest.writeFloat(temp_adc_factor);
			dest.writeFloat(temp_adc_correction);
			dest.writeFloat(tps_adc_factor);
			dest.writeFloat(tps_adc_correction);
			dest.writeFloat(add_i1_factor);
			dest.writeFloat(add_i1_correction);
			dest.writeFloat(add_i2_factor);
			dest.writeFloat(add_i2_correction);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.map_adc_correction == ((ADCCorPar)o).map_adc_correction;
				result &= this.map_adc_factor == ((ADCCorPar)o).map_adc_factor;
				result &= this.temp_adc_correction == ((ADCCorPar)o).temp_adc_correction;
				result &= this.temp_adc_factor == ((ADCCorPar)o).temp_adc_factor;
				result &= this.ubat_adc_correction == ((ADCCorPar)o).ubat_adc_correction;
				result &= this.ubat_adc_factor == ((ADCCorPar)o).ubat_adc_factor;
				result &= this.tps_adc_factor == ((ADCCorPar)o).tps_adc_factor;
				result &= this.tps_adc_correction == ((ADCCorPar)o).tps_adc_correction;
				result &= this.add_i1_factor == ((ADCCorPar)o).add_i1_factor;
				result &= this.add_i1_correction == ((ADCCorPar)o).add_i1_correction;
				result &= this.add_i2_factor == ((ADCCorPar)o).add_i2_factor;
				result &= this.add_i2_correction == ((ADCCorPar)o).add_i2_correction;
			}
			return result;
		}
		
		
		public void parse (String packet) throws Exception {			  
			super.parse(packet);
			
			try {			
				map_adc_factor = (float)Integer.valueOf(data.substring(2,6),16).shortValue() / 16384f; // AAAA
				map_adc_correction = (((float)Long.valueOf(data.substring(6,14),16).intValue() / 16384f) - 0.5f) / map_adc_factor * ADC_DISCRETE; // BBBBBBBB
				ubat_adc_factor = (float)Integer.valueOf(data.substring(14,18),16).shortValue() / 16384f; // CCCC
				ubat_adc_correction = (((float)Long.valueOf(data.substring(18,26),16).intValue() / 16384f) - 0.5f) / ubat_adc_factor * ADC_DISCRETE; // DDDDDDDD			
				temp_adc_factor = (float)Integer.valueOf(data.substring(26,30),16).shortValue() / 16384f; // EEEE
				temp_adc_correction = (((float)Long.valueOf(data.substring(30,38),16).intValue() / 16384f) - 0.5f) / temp_adc_factor * ADC_DISCRETE; // FFFFFFFF						
				tps_adc_factor = (float)Integer.valueOf(data.substring(38,42),16).shortValue() / 16384f; // EEEE
				tps_adc_correction = (((float)Long.valueOf(data.substring(42,50),16).intValue() / 16384f) - 0.5f) / tps_adc_factor * ADC_DISCRETE; // FFFFFFFF
				add_i1_factor = (float)Integer.valueOf(data.substring(50,54),16).shortValue() / 16384f; // EEEE
				add_i1_correction = (((float)Long.valueOf(data.substring(54,62),16).intValue() / 16384f) - 0.5f) / add_i1_factor * ADC_DISCRETE; // FFFFFFFF
				add_i2_factor = (float)Integer.valueOf(data.substring(62,66),16).shortValue() / 16384f; // EEEE
				add_i2_correction = (((float)Long.valueOf(data.substring(66,74),16).intValue() / 16384f) - 0.5f) / add_i2_factor * ADC_DISCRETE; // FFFFFFFF
			}
			catch (Exception e) {
				throw e;
			}	  
		}		
		
		@Override
		public String pack() throws Exception {
			long map_correction_d = Math.round(-map_adc_correction / ADC_DISCRETE);
			long ubat_correction_d = Math.round(-ubat_adc_correction / ADC_DISCRETE);
			long temp_correction_d = Math.round(-temp_adc_correction / ADC_DISCRETE);
			long tps_correction_d = Math.round(-tps_adc_correction / ADC_DISCRETE);
			long add_i1_correction_d = Math.round(-add_i1_correction / ADC_DISCRETE);
			long add_i2_correction_d = Math.round(-add_i2_correction / ADC_DISCRETE);
			
			return String.format("%s%s%04X%08X%04X%08X%04X%08X%04X%08X%04X%08X%04X%08X", OUTPUT_PACKET,packet_id,
					Integer.valueOf(Math.round(map_adc_factor * 16384)).shortValue(),
					Long.valueOf(Math.round(16384f * (0.5f - map_correction_d * map_adc_factor))).intValue(),
					Integer.valueOf(Math.round(ubat_adc_factor * 16384)).shortValue(),
					Long.valueOf(Math.round(16384f * (0.5f - ubat_correction_d * ubat_adc_factor))).intValue(),
					Integer.valueOf(Math.round(temp_adc_factor * 16384)).shortValue(),
					Long.valueOf(Math.round(16384f * (0.5f - temp_correction_d * temp_adc_factor))).intValue(),
					Integer.valueOf(Math.round(tps_adc_factor * 16384)).shortValue(),
					Long.valueOf(Math.round(16384f * (0.5f - tps_correction_d * tps_adc_factor))).intValue(),
					Integer.valueOf(Math.round(add_i1_factor * 16384)).shortValue(),
					Long.valueOf(Math.round(16384f * (0.5f - add_i1_correction_d * add_i1_factor))).intValue(),
					Integer.valueOf(Math.round(add_i2_factor * 16384)).shortValue(),
					Long.valueOf(Math.round(16384f * (0.5f - add_i2_correction_d * add_i2_factor))).intValue()
					);
		}
		
		public String getLogString() {
			return (String.format(Locale.US,"%s: MAP Factor: %f, MAP Correction: %f, UBAT Factor: %f, UBAT Correction: %f, Temp factor: %f, Temp correction: %f, TPS factor: %f, TPS Correction: %f, ADD_I1 factor: %f, ADD_I1 Correction: %f, ADD_I2 factor: %f, ADD_I2 Correction: %f", getClass().getCanonicalName(),
					map_adc_factor, map_adc_correction, ubat_adc_factor, ubat_adc_correction, temp_adc_factor, temp_adc_correction,tps_adc_factor,tps_adc_correction,add_i1_factor,add_i1_correction,add_i2_factor,add_i2_correction));
		}
	
	}
	 
	/** Класс пакета сырых данных АЦП **/
	public static class ADCRawDat extends Secu3Dat {
		static final int PACKET_SIZE = 30;
			
		/** Сырое значение АЦП для ДАД AAAA **/
		public float map_value;
		/** Сырое значение АЦП для бортсети BBBB **/
		public float ubat_value;
		/** Сырое значение АЦП для температуры CCCC **/
		public float temp_value;
		/** Сырое значение АЦП для датчика детонации DDDD **/
		public float knock_value;
		public float tps_value;
		public float add_i1_value;
		public float add_i2_value;
			
		public static final Parcelable.Creator<ADCRawDat> CREATOR = new Parcelable.Creator<ADCRawDat>() {
			 public ADCRawDat createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new ADCRawDat(in);
			 }

			 public ADCRawDat[] newArray(int size) {
				 return new ADCRawDat[size];
			 }
		};
		
		public ADCRawDat() {
			packet_id = ADCRAW_DAT;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_ADCRAW_DAT;
		}
		
		public ADCRawDat(Parcel in) {
			super(in);
			map_value = in.readFloat();
			ubat_value = in.readFloat();
			temp_value = in.readFloat();
			knock_value = in.readFloat();
			tps_value = in.readFloat();
			add_i1_value = in.readFloat();
			add_i2_value = in.readFloat();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeFloat(map_value);
			dest.writeFloat(ubat_value);
			dest.writeFloat(temp_value);
			dest.writeFloat(knock_value);
			dest.writeFloat(tps_value);
			dest.writeFloat(add_i1_value);
			dest.writeFloat(add_i2_value);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.map_value == ((ADCRawDat)o).map_value;
				result &= this.ubat_value == ((ADCRawDat)o).ubat_value;
				result &= this.temp_value == ((ADCRawDat)o).temp_value;
				result &= this.knock_value == ((ADCRawDat)o).knock_value;
				result &= this.tps_value == ((ADCRawDat)o).tps_value;
				result &= this.add_i1_value == ((ADCRawDat)o).add_i1_value;
				result &= this.add_i2_value == ((ADCRawDat)o).add_i2_value;
			}
			return result;
		}
		
		@Override
		public void parse (String packet) throws Exception {  	 	
			super.parse(packet) ;
			
			try {				
				map_value = (float)Integer.parseInt(data.substring(2,6),16) * ADC_DISCRETE; // AAAA
				ubat_value = (float)Integer.parseInt(data.substring(6,10),16) * ADC_DISCRETE; // BBBB
				temp_value = (float)Integer.parseInt(data.substring(10,14),16) * ADC_DISCRETE; // CCCC
				knock_value = (float)Integer.parseInt(data.substring(14,18),16) * ADC_DISCRETE; // DDDD		
				tps_value = (float)Integer.parseInt(data.substring(18,22),16) * ADC_DISCRETE; // DDDD
				add_i1_value = (float)Integer.parseInt(data.substring(22,26),16) * ADC_DISCRETE; // DDDD
				add_i2_value = (float)Integer.parseInt(data.substring(26,30),16) * ADC_DISCRETE; // DDDD
			}
			catch (Exception e) {
				throw e;
			}
		}
		
		@Override
		public String getLogString() {
			return (String.format("%s: ", getClass().getCanonicalName()));
		}
	
	}
	 
	/** Класс пакета параметров угла опережения зажигания **/
	public static class AnglesPar extends Secu3Dat {
		static final int PACKET_SIZE = 23;
		
		public static final String MAX_ANGLE = "MAX_ANGLE";
		public static final String MIN_ANGLE = "MIN_ANGLE";
		public static final String ANGLE_CORR = "ANGLE_CORR";
		public static final String DEC_SPREAD = "DEC_SPREAD";
		public static final String INC_SPREAD = "INC_SPREAD";
		public static final String ZERO_ADV_ANGLE = "ZERO_ADV_ANGLE";
		
		/** Максимальный УОЗ AAAA **/
		public float  max_angle;
		/** Минимальный УОЗ BBBB **/
		public float  min_angle;
		/** Октан-коррекция CCCC **/
		public float  angle_corr;
		/** Скорость уменьшения УОЗ, град./цикл DDDD **/
		public float  dec_spead;
		/** Скорость увеличения УОЗ, град./цикл EEEE **/
		public float  inc_spead;
		/** Признак нулевого УОЗ (0,1) Z **/
		public int zero_adv_ang;
			
		public static final Parcelable.Creator<AnglesPar> CREATOR = new Parcelable.Creator<AnglesPar>() {
			 public AnglesPar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new AnglesPar(in);
			 }

			 public AnglesPar[] newArray(int size) {
				 return new AnglesPar[size];
			 }
		};
		
		public AnglesPar ()
		{
			packet_id = ANGLES_PAR;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_ANGLES_PAR;
		}	
		
		public AnglesPar(Parcel in) {
			super (in);
			max_angle = in.readFloat();
			min_angle = in.readFloat();
			angle_corr = in.readFloat();
			dec_spead = in.readFloat();
			inc_spead = in.readFloat();
			zero_adv_ang = in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeFloat(max_angle);
			dest.writeFloat(min_angle);
			dest.writeFloat(angle_corr);
			dest.writeFloat(dec_spead);
			dest.writeFloat(inc_spead);
			dest.writeInt(zero_adv_ang);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.max_angle == ((AnglesPar)o).max_angle;
				result &= this.min_angle == ((AnglesPar)o).min_angle;
				result &= this.angle_corr == ((AnglesPar)o).angle_corr;
				result &= this.dec_spead == ((AnglesPar)o).dec_spead;
				result &= this.inc_spead == ((AnglesPar)o).inc_spead;
				result &= this.zero_adv_ang == ((AnglesPar)o).zero_adv_ang;
			}
			return result;
		}
		
		@Override
		public void parse (String packet) throws Exception {	 
			super.parse(packet);	
			
			try {						
				max_angle = (float)Integer.valueOf(data.substring(2,6),16).shortValue() / ANGLE_MULTIPLIER; // AAAA
				min_angle = (float)Integer.valueOf(data.substring(6,10),16).shortValue() / ANGLE_MULTIPLIER; // BBBB
				angle_corr = (float)Integer.valueOf(data.substring(10,14),16).shortValue() / ANGLE_MULTIPLIER; // CCCC			
				dec_spead = (float)Integer.valueOf(data.substring(14,18),16).shortValue() / ANGLE_MULTIPLIER; // DDDD
				inc_spead = (float)Integer.valueOf(data.substring(18,22),16).shortValue() / ANGLE_MULTIPLIER; // EEEE
					
				zero_adv_ang = Integer.parseInt(data.substring(22,23),16); // Z
			}
			catch (Exception e) {
				throw e;
			}	  
		}	
		
		@Override
		public String pack() throws Exception {
			return String.format("%s%s%04X%04X%04X%04X%04X%01X", OUTPUT_PACKET, packet_id,
					Integer.valueOf(Math.round(max_angle * ANGLE_MULTIPLIER)).shortValue(),
					Integer.valueOf(Math.round(min_angle * ANGLE_MULTIPLIER)).shortValue(),
					Integer.valueOf(Math.round(angle_corr * ANGLE_MULTIPLIER)).shortValue(),
					Integer.valueOf(Math.round(dec_spead * ANGLE_MULTIPLIER)).shortValue(),
					Integer.valueOf(Math.round(inc_spead * ANGLE_MULTIPLIER)).shortValue(),
					zero_adv_ang);
		}
			
		@Override
		public String getLogString() {
			return (String.format(Locale.US,"%s: Max angle: %f, Min angle: %f, Angle corr: %f, Dec spread: %f, Inc spread: %f, Zero angle: %d", getClass().getCanonicalName(), max_angle, min_angle, angle_corr, dec_spead, inc_spead, zero_adv_ang));
		}		
	}
	
	/** Класс пакета параметров карбюратора **/
	public static class CarburPar extends Secu3Dat {
		public final int PACKET_SIZE = 27;
				
		/** Нижний порог ЭПХХ для бензина (об./мин) AAAA**/
		public int  ephh_lot;
		/** Верхний порог ЭПХХ для бензина (об./мин) BBBB **/
		public int  ephh_hit;
		/** Признак инверсии концевика карбюратора (0,1) C **/
		public int carb_invers;
		/** Порог давления включения клапана экономайзера мощностных режимов DDDD **/
		public float epm_ont;                        //порог включения ЭМР (со знаком)
		/** Нижний порог ЭПХХ для газа (об./мин) EEEE **/
		public int  ephh_lot_g;
		/** Верхний порог ЭПХХ для газа (об./мин FFFF **/
		public int  ephh_hit_g;
		/** Задержка выключения клапана ЭПХХ * 10мс GG **/
		public float shutoff_delay;                  //задержка выключения клапана
		/** Порог переключения в режим ХХ по ДПДЗ **/
		public float tps_threshold;
			 
		public static final Parcelable.Creator<CarburPar> CREATOR = new Parcelable.Creator<CarburPar>() {
			 public CarburPar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new CarburPar(in);
			 }

			 public CarburPar[] newArray(int size) {
				 return new CarburPar[size];
			 }
		};
		
		public CarburPar() {
			packet_id = CARBUR_PAR;
			packet_size = PACKET_SIZE;	
			intent_action = RECEIVE_CARBUR_PAR;
		}  
			  
		public CarburPar(Parcel in) {
			super(in);
			ephh_lot = in.readInt();
			ephh_hit = in.readInt();
			carb_invers = in.readInt();
			epm_ont = in.readFloat();
			ephh_lot_g = in.readInt();
			ephh_hit_g = in.readInt();
			shutoff_delay = in.readFloat();
			tps_threshold = in.readFloat();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(ephh_lot);
			dest.writeInt(ephh_hit);
			dest.writeInt(carb_invers);
			dest.writeFloat(epm_ont);
			dest.writeInt(ephh_lot_g);
			dest.writeInt(ephh_hit_g);
			dest.writeFloat(shutoff_delay);
			dest.writeFloat(tps_threshold);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.ephh_lot == ((CarburPar)o).ephh_lot;
				result &= this.ephh_hit == ((CarburPar)o).ephh_hit;
				result &= this.carb_invers == ((CarburPar)o).carb_invers;
				result &= this.epm_ont == ((CarburPar)o).epm_ont;
				result &= this.ephh_lot_g == ((CarburPar)o).ephh_lot_g;
				result &= this.ephh_hit_g == ((CarburPar)o).ephh_hit_g;
				result &= this.shutoff_delay == ((CarburPar)o).shutoff_delay;
				result &= this.tps_threshold == ((CarburPar)o).tps_threshold;
			}
			return result;
		}
		
		@Override
		public void parse (String packet) throws Exception {
			super.parse(packet);
					  
			try {
				ephh_lot = Integer.parseInt(data.substring(2,6),16); // AAAA
				ephh_hit = Integer.parseInt(data.substring(6,10),16); // BBBB
				carb_invers = Integer.parseInt(data.substring(10,11),16); // C
				epm_ont = (float)Integer.valueOf(data.substring(11,15),16).shortValue() / MAP_PHYSICAL_MAGNITUDE_MULTIPLAYER; // DDDD		  
				ephh_lot_g = Integer.parseInt(data.substring(15,19),16); // EEEE
				ephh_hit_g = Integer.parseInt(data.substring(19,23),16); // FFFF	  	 	 
				shutoff_delay = (float)Integer.parseInt(data.substring(23,25),16) / 100; // GG
				tps_threshold = (float)Integer.parseInt(data.substring(25,27),16) / TPS_PHYSICAL_MAGNITUDE_MULTIPLAYER;
			}
			catch (Exception e) {
				throw e;
			}	  
		} 
		
		@Override
		public String pack() throws Exception {
			return String.format("%s%s%04X%04X%01X%04X%04X%04X%02X%02X", OUTPUT_PACKET, packet_id,
					ephh_lot,
					ephh_hit,
					carb_invers,
					Integer.valueOf(Math.round(epm_ont * MAP_PHYSICAL_MAGNITUDE_MULTIPLAYER)).shortValue(),
					ephh_lot_g,
					ephh_hit_g,
					Math.round(shutoff_delay * 100),
					Integer.valueOf(Math.round(tps_threshold * TPS_PHYSICAL_MAGNITUDE_MULTIPLAYER)));
		}
			
		@Override
		public String getLogString() {
			return (String.format(Locale.US,"%s: EPHH Lo: %d, EPHH Hi: %d, Inverse: %d, EPM On: %f, EPHH LoG: %d, EPHH Hi G: %d, Shutoff delay: %f", getClass().getCanonicalName(),ephh_lot, ephh_hit, carb_invers, epm_ont, ephh_lot_g, ephh_hit_g, shutoff_delay / 10));
		}

	}
	
	/** Класс пакета ошибок **/
	public static class CEErrCodes extends Secu3Dat {
		static final int PACKET_SIZE = 6;
			
		/** Битовые флаги ошибок, более подробно см. ce_errors.h AAAA **/
		public int flags;

		public static final Parcelable.Creator<CEErrCodes> CREATOR = new Parcelable.Creator<CEErrCodes>() {
			 public CEErrCodes createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new CEErrCodes(in);
			 }

			 public CEErrCodes[] newArray(int size) {
				 return new CEErrCodes[size];
			 }
		};
		
		public CEErrCodes() {
			packet_id = CE_ERR_CODES;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_CE_ERR_CODES;
		}
		
		public CEErrCodes (Parcel in) {
			super (in);
			flags = in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(this.flags);
		}

		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.flags == ((CEErrCodes)o).flags;
			}
			return result;
		}
			
		@Override
		public void parse (String packet) throws Exception {			  
			super.parse(packet);
				
			try {			
				flags = Integer.parseInt(data.substring(2,6),16); // AAAA			
			}
			catch (Exception e) {
				throw e;
			}	  
		}	
			
		@Override
		public String getLogString() {
			return (String.format(Locale.US,"%s: %d", getClass().getCanonicalName(), flags));
		}

	}

	/** Класс пакета сохраненных ошибок **/
	public static class CESavedErr extends Secu3Dat {
		static final int PACKET_SIZE = 6;

		/** Битовые флаги ошибок, более подробно см. ce_errors.h AAAA **/
		public int flags;

		public static final Parcelable.Creator<CESavedErr> CREATOR = new Parcelable.Creator<CESavedErr>() {
			 public CESavedErr createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new CESavedErr(in);
			 }

			 public CESavedErr[] newArray(int size) {
				 return new CESavedErr[size];
			 }
		};
		
		public CESavedErr() {
			packet_id = CE_SAVED_ERR;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_CE_SAVED_ERR;
		}
		
		public CESavedErr(Parcel in) {
			super(in);
			flags = in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(this.flags);
		}		

		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.flags == ((CESavedErr)o).flags;
			}
			return result;
		}
		
		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {
				flags = Integer.parseInt(data.substring(2, 6), 16); // AAAA
			} catch (Exception e) {
				throw e;
			}
		}
		
		@Override
		public String pack() throws Exception {
			return String.format("%s%s%04X", OUTPUT_PACKET, packet_id, flags);
		}
		
		public static String pack (int flags) {
			return String.format("%s%s%04X", OUTPUT_PACKET, CE_SAVED_ERR, flags);
		}
		
		@Override
		public String getLogString() {
			return (String.format(Locale.US,"%s: %d", getClass().getCanonicalName(), flags));
		}

	}

	/** Класс пакета управления подсосом **/
	public static class ChokePar extends Secu3Dat {
		static final int PACKET_SIZE = 9;

		public int steps;
		public int testing;
		public byte manual_delta;

		public static final Parcelable.Creator<ChokePar> CREATOR = new Parcelable.Creator<ChokePar>() {
			 public ChokePar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new ChokePar(in);
			 }

			 public ChokePar[] newArray(int size) {
				 return new ChokePar[size];
			 }
		};
		
		public ChokePar() {
			packet_id = CHOKE_PAR;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_CHOKE_PAR;
		}
		
		public ChokePar(Parcel in) {
			super(in);
			steps = in.readInt();
			testing = in.readInt();
			manual_delta = in.readByte();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(this.steps);
			dest.writeInt(this.testing);
			dest.writeByte(this.manual_delta);
		}		

		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.steps == ((ChokePar)o).steps;
				result &= this.testing == ((ChokePar)o).testing;
				result &= this.manual_delta == ((ChokePar)o).manual_delta;
			}
			return result;
		}
		
		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {
				steps = Integer.parseInt(data.substring(2, 6), 16); // AAAA
				testing = Integer.parseInt(data.substring(6, 7), 16); // B
				manual_delta = Integer.valueOf(data.substring(7, 9), 16).byteValue(); // CC
			} catch (Exception e) {
				throw e;
			}
		}
		
		@Override
		public String pack() throws Exception {
			return String.format("%s%s%04X%01X%02X", OUTPUT_PACKET, packet_id, steps,testing,Integer.valueOf(manual_delta).byteValue());
		}
		
		@Override
		public String getLogString() {
			return (String.format(Locale.US,"%s: %d,%d,%d", getClass().getCanonicalName(), steps,testing,manual_delta));
		}

	}
	
	/** Класс параметров настройки ДПКВ и ДНО **/
	public static class CKPSPar extends Secu3Dat {
		static final int PACKET_SIZE = 15;

		/** Тип фронта ДПКВ (0 - отрицательный, 1 - положительный) A **/
		public int ckps_edge_type;
		/** Тип фронта ДНО (0 - отрицательный, 1 - положительный A **/
		public int ref_s_edge_type;
		/** Количество зубьев от синхрометки до в.м.т. BB **/
		public int ckps_cogs_btdc;
		/** Длительность импульса запуска коммутатора в зубьях шкива CC **/
		public int ckps_ignit_cogs;
		/** Количество цилиндров двигателя PP **/
		public int ckps_engine_cyl;
		/** Признак объединения сигналовзажигания на первом выходе (0,1) M **/
		public int ckps_merge_ign_outs;
		/** Количество зубьев шкива, включая отсутствующие TT **/
		public int ckps_cogs_num;
		/** Количество отсутствующих зубьев шкива UU **/
		public int ckps_miss_num;

		public static final Parcelable.Creator<CKPSPar> CREATOR = new Parcelable.Creator<CKPSPar>() {
			 public CKPSPar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new CKPSPar(in);
			 }

			 public CKPSPar[] newArray(int size) {
				 return new CKPSPar[size];
			 }
		};
		
		public CKPSPar() {
			packet_id = CKPS_PAR;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_CKPS_PAR;
		}

		public CKPSPar (Parcel in){
			super (in);
			ckps_edge_type = in.readInt();
			ref_s_edge_type = in.readInt();
			ckps_cogs_btdc = in.readInt();
			ckps_ignit_cogs = in.readInt();
			ckps_engine_cyl = in.readInt();
			ckps_merge_ign_outs = in.readInt();
			ckps_cogs_num = in.readInt();
			ckps_miss_num = in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(ckps_edge_type);
			dest.writeInt(ref_s_edge_type);
			dest.writeInt(ckps_cogs_btdc);
			dest.writeInt(ckps_ignit_cogs);
			dest.writeInt(ckps_engine_cyl);
			dest.writeInt(ckps_merge_ign_outs);
			dest.writeInt(ckps_cogs_num);
			dest.writeInt(ckps_miss_num);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.ckps_edge_type == ((CKPSPar)o).ckps_edge_type;
				result &= this.ref_s_edge_type == ((CKPSPar)o).ref_s_edge_type;
				result &= this.ckps_cogs_btdc == ((CKPSPar)o).ckps_cogs_btdc;
				result &= this.ckps_ignit_cogs == ((CKPSPar)o).ckps_ignit_cogs;
				result &= this.ckps_engine_cyl == ((CKPSPar)o).ckps_engine_cyl;
				result &= this.ckps_merge_ign_outs == ((CKPSPar)o).ckps_merge_ign_outs;				
				result &= this.ckps_cogs_num == ((CKPSPar)o).ckps_cogs_num;
				result &= this.ckps_miss_num == ((CKPSPar)o).ckps_miss_num;
			}
			return result;
		}
		
		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {
				ckps_edge_type = Integer.parseInt(data.substring(2, 3), 16); // A
				ref_s_edge_type = Integer.parseInt(data.substring(3, 4), 16); // A
				ckps_cogs_btdc = Integer.parseInt(data.substring(4, 6), 16); // BB
				ckps_ignit_cogs = Integer.parseInt(data.substring(6, 8), 16); // CC
				ckps_engine_cyl = Integer.parseInt(data.substring(8, 10), 16); // PP
				ckps_merge_ign_outs = Integer.parseInt(data.substring(10, 11),16); // M
				ckps_cogs_num = Integer.parseInt(data.substring(11, 13), 16); // TT
				ckps_miss_num = Integer.parseInt(data.substring(13, 15), 16); // UU
			} catch (Exception e) {
				throw e;
			}
		}

		@Override
		public String pack() throws Exception {
			return String.format("%s%s%01X%01X%02X%02X%02X%01X%02X%02X", OUTPUT_PACKET, packet_id, ckps_edge_type, ref_s_edge_type, ckps_cogs_btdc, ckps_ignit_cogs, ckps_engine_cyl, ckps_merge_ign_outs, ckps_cogs_num, ckps_miss_num);
		}
		
		@Override
		public String getLogString() {
			return (String.format("%s: ", getClass().getCanonicalName()));
		}

	}

	/** Класс пакета имени таблиц **/
	public static class FnNameDat extends Secu3Dat {
		static final int PACKET_SIZE = 22;
		static final int MAX_STRING_SIZE = 64;

		/** Количество таблиц, хранимое в прошивке AA **/
		public int tables_num;
		/** Номер соответствующей таблицы BB **/
		public int index;
		/** Имя соответствующей таблицы (16 символов) (name) **/
		public String name;
		
		public String[] names = null;
		public boolean collected[] = null;
		
		private boolean all_collected = false;
		private int collected_count = 0;

		public static final Parcelable.Creator<FnNameDat> CREATOR = new Parcelable.Creator<FnNameDat>() {
			 public FnNameDat createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new FnNameDat(in);
			 }

			 public FnNameDat[] newArray(int size) {
				 return new FnNameDat[size];
			 }
		};
		
		public FnNameDat() {
			packet_id = FNNAME_DAT;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_FNNAME_DAT;
			names = null;
			collected = null;
			all_collected = false;
			collected_count = 0;
		}
		
		public FnNameDat (Parcel in) {
			super (in);
			tables_num = in.readInt();
			index = in.readInt();
			name = in.readString();
			all_collected = (Boolean) in.readValue(boolean.class.getClassLoader());
			collected_count = in.readInt();			
			names = in.createStringArray();
			collected = in.createBooleanArray();			
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(tables_num);
			dest.writeInt(index);
			dest.writeString(name);
			dest.writeValue(all_collected);
			dest.writeInt(collected_count);			
			dest.writeStringArray(names);
			dest.writeBooleanArray(collected);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.tables_num == ((FnNameDat)o).tables_num;
				result &= this.index == ((FnNameDat)o).index;
				result &= this.name.equals(((FnNameDat)o).name);
				result &= Arrays.equals(this.names,((FnNameDat)o).names);			
				result &= Arrays.equals(this.collected,((FnNameDat)o).collected);
				result &= this.all_collected == ((FnNameDat)o).all_collected;
				result &= this.collected_count == ((FnNameDat)o).collected_count;
			}
			return result;
		}		

		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {
				tables_num = Integer.parseInt(data.substring(2, 4), 16); // AA
				index = Integer.parseInt(data.substring(4, 6), 16); // BB
				name = data.substring(6).trim();
				
				if (collected == null) {
					collected = new boolean [tables_num];
					collected_count = 0;
					names = new String [tables_num];					
					for (int i = 0; i < tables_num; i++) {
						collected[i] = false;
					}
				}
				if (collected != null) {
					collected [index] = true;
					names[index] = name;
					collected_count = 0;
					boolean _all_collected = true;
					for (int i = 0; i < tables_num; i++) {
						if (!collected[i]) _all_collected = false; else collected_count++;
					} 
					all_collected = _all_collected;							
				}
				
			} catch (Exception e) {
				throw e;
			}
		}
		
		public synchronized void clear() {
			all_collected = false;	
			collected = null;
			collected_count = 0;
			names = null;
		}

		public synchronized boolean names_available() {
			return all_collected;
		}
		
		public synchronized int names_count() {
			return collected_count;
		}
		
		@Override
		public String getLogString() {
			return (String.format("%s: ", getClass().getCanonicalName()));
		}

	}

	/** Класс пакета используемых таблиц **/
	public static class FunSetPar extends Secu3Dat {
		static final int PACKET_SIZE = 30;

		/** Номер семейства характеристик, используемого для бензина AA **/
		public int fn_benzin;
		/** Номер семейства характеристик, используемого для газа BB **/
		public int fn_gas;
		/** Нижнее значение по шкале давлений ДАД CCCC **/
		public float map_lower_pressure;
		/** Нижнее значение по шкале давлений ДАД DDDD **/
		public float map_upper_pressure;
		/** Смещение кривой ДАД в Вольтах EEEE **/
		public float map_curve_offset;
		/** Наклон кривой ДАД FFFF **/
		public float map_curve_gradient;
		/** Смещение кривой ДПДЗ в Вольтах EEEE **/
		public float tps_curve_offset;
		/** Наклон кривой ДПДЗ FFFF **/
		public float tps_curve_gradient;


		public static final Parcelable.Creator<FunSetPar> CREATOR = new Parcelable.Creator<FunSetPar>() {
			 public FunSetPar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new FunSetPar(in);
			 }

			 public FunSetPar[] newArray(int size) {
				 return new FunSetPar[size];
			 }
		};
		
		public FunSetPar() {
			packet_id = FUNSET_PAR;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_FUNSET_PAR;
		}
		
		public FunSetPar(Parcel in) {
			super (in);
			fn_benzin = in.readInt();
			fn_gas = in.readInt();
			map_lower_pressure = in.readFloat();
			map_upper_pressure = in.readFloat();
			map_curve_offset = in.readFloat();
			map_curve_gradient = in.readFloat();
			tps_curve_offset = in.readFloat();
			tps_curve_gradient = in.readFloat();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt (fn_benzin);
			dest.writeInt (fn_gas);
			dest.writeFloat(map_lower_pressure);
			dest.writeFloat(map_upper_pressure);
			dest.writeFloat(map_curve_offset);
			dest.writeFloat(map_curve_gradient);
			dest.writeFloat(tps_curve_offset);
			dest.writeFloat(tps_curve_gradient);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.fn_benzin == ((FunSetPar)o).fn_benzin;
				result &= this.fn_gas == ((FunSetPar)o).fn_gas;
				result &= this.map_lower_pressure == ((FunSetPar)o).map_lower_pressure;
				result &= this.map_upper_pressure == ((FunSetPar)o).map_upper_pressure;
				result &= this.map_curve_offset == ((FunSetPar)o).map_curve_offset;
				result &= this.map_curve_gradient == ((FunSetPar)o).map_curve_gradient;
				result &= this.tps_curve_offset == ((FunSetPar)o).tps_curve_offset;
				result &= this.tps_curve_gradient == ((FunSetPar)o).tps_curve_gradient;
			}
			return result;
		}		

		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {

				fn_benzin = Integer.parseInt(data.substring(2, 4), 16); // AA
				fn_gas = Integer.parseInt(data.substring(4, 6), 16); // BB

				map_lower_pressure = (float) Integer.valueOf(
						data.substring(6, 10), 16).shortValue()
						/ MAP_PHYSICAL_MAGNITUDE_MULTIPLAYER; // CCCC
				map_upper_pressure = (float) Integer.valueOf(
						data.substring(10, 14), 16).shortValue()
						/ MAP_PHYSICAL_MAGNITUDE_MULTIPLAYER; // DDDD
				map_curve_offset = (float) Integer.valueOf(
						data.substring(14, 18), 16).shortValue()
						* ADC_DISCRETE; // EEEE
				map_curve_gradient = (float) Integer.valueOf(
						data.substring(18, 22), 16).shortValue()
						/ (MAP_PHYSICAL_MAGNITUDE_MULTIPLAYER * 128.0f * ADC_DISCRETE); // FFFF
				
				tps_curve_offset = (float) Integer.valueOf(
						data.substring(22, 26), 16).shortValue()
						* ADC_DISCRETE; // EEEE
				
				tps_curve_gradient = (float) Integer.valueOf(
						data.substring(26, 30), 16).shortValue()
						/ (TPS_PHYSICAL_MAGNITUDE_MULTIPLAYER * 64 * 128.0f * ADC_DISCRETE); // FFFF
			} catch (Exception e) {
				throw e;
			}
		}
		
		@Override
		public String pack() throws Exception {
			return String.format("%s%s%02X%02X%04X%04X%04X%04X%04X%04X", OUTPUT_PACKET, packet_id,
					fn_benzin,
					fn_gas,
					Integer.valueOf(Math.round(map_lower_pressure * MAP_PHYSICAL_MAGNITUDE_MULTIPLAYER)).shortValue(),
					Integer.valueOf(Math.round(map_upper_pressure * MAP_PHYSICAL_MAGNITUDE_MULTIPLAYER)).shortValue(),
					Integer.valueOf(Math.round(map_curve_offset / ADC_DISCRETE)).shortValue(),
					Integer.valueOf(Math.round(128.0f * map_curve_gradient * MAP_PHYSICAL_MAGNITUDE_MULTIPLAYER * ADC_DISCRETE)).shortValue(),
					Integer.valueOf(Math.round(tps_curve_offset / ADC_DISCRETE)).shortValue(),
					Integer.valueOf(Math.round(128.0f * tps_curve_gradient * TPS_PHYSICAL_MAGNITUDE_MULTIPLAYER * 64 * ADC_DISCRETE)).shortValue());
		}

		@Override
		public String getLogString() {
			return (String
					.format(Locale.US,"%s: Gasoline: %d, Gas: %d, Lower pressure: %f, Upper pressure: %f, Curve offset: %f, Curve gradient: %f, TPS Offset: %f, TPS gradient: %f",
							getClass().getCanonicalName(), fn_benzin, fn_gas,
							map_lower_pressure, map_upper_pressure,
							map_curve_offset, map_curve_gradient,tps_curve_offset,tps_curve_gradient));
		}

	}

	/** Класс пакета параметров холостого хода **/
	public static class IdlRegPar extends Secu3Dat {
		static final int PACKET_SIZE = 31;

		/** Признак использования регулятора (0,1) A **/
		public int idl_regul;
		/** Коэффициент регулятора при положительной ошибке BBBB **/
		public float ifac1; // со знаком
		/** Коэффициент регулятора при отрицательной ошибке CCCC **/
		public float ifac2; // со знаком
		/** Зона нечувствительности регулятора (об./мин) DDDD **/
		public int MINEFR;
		/** Поддерживаемые обороты EEEE **/
		public int idling_rpm;
		/** Минимально разрешенный УОЗ FFFF **/
		public float min_angle; // со знаком
		/** Максимально разрешенный УОЗ GGGG **/
		public float max_angle; // со знаком
		/** Температура включения регулятора **/
		public float turn_on_temp;
		

		public static final Parcelable.Creator<IdlRegPar> CREATOR = new Parcelable.Creator<IdlRegPar>() {
			 public IdlRegPar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new IdlRegPar(in);
			 }

			 public IdlRegPar[] newArray(int size) {
				 return new IdlRegPar[size];
			 }
		};
		
		public IdlRegPar() {
			packet_id = IDLREG_PAR;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_IDLREG_PAR;
		}
		
		public IdlRegPar (Parcel in) {
			super (in);
			idl_regul = in.readInt();
			ifac1 = in.readFloat();
			ifac2 = in.readFloat();
			MINEFR = in.readInt();
			idling_rpm = in.readInt();
			min_angle = in.readFloat();
			max_angle = in.readFloat();
			turn_on_temp = in.readFloat();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(idl_regul);
			dest.writeFloat(ifac1);
			dest.writeFloat(ifac2);
			dest.writeInt(MINEFR);
			dest.writeInt(idling_rpm);
			dest.writeFloat(min_angle);
			dest.writeFloat(max_angle);
			dest.writeFloat(turn_on_temp);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.idl_regul == ((IdlRegPar)o).idl_regul;
				result &= this.ifac1 == ((IdlRegPar)o).ifac1;
				result &= this.ifac2 == ((IdlRegPar)o).ifac2;
				result &= this.MINEFR == ((IdlRegPar)o).MINEFR;
				result &= this.idling_rpm == ((IdlRegPar)o).idling_rpm;
				result &= this.min_angle == ((IdlRegPar)o).min_angle;
				result &= this.max_angle == ((IdlRegPar)o).max_angle;
				result &= this.turn_on_temp == ((IdlRegPar)o).turn_on_temp;
			}
			return result;
		}		

		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {
				idl_regul = Integer.parseInt(data.substring(2, 3), 16); // A
				ifac1 = (float) Integer.valueOf(data.substring(3, 7), 16)
						.shortValue() / ANGLE_MULTIPLIER; // BBBB
				ifac2 = (float) Integer.valueOf(data.substring(7, 11), 16)
						.shortValue() / ANGLE_MULTIPLIER; // CCCC
				MINEFR = Integer.parseInt(data.substring(11, 15), 16); // DDDD
				idling_rpm = Integer.parseInt(data.substring(15, 19), 16); // EEEE
				min_angle = (float) Integer.valueOf(data.substring(19, 23), 16)
						.shortValue() / ANGLE_MULTIPLIER; // FFFF
				max_angle = (float) Integer.valueOf(data.substring(23, 27), 16)
						.shortValue() / ANGLE_MULTIPLIER; // GGGG
				turn_on_temp = (float) Integer.parseInt(data.substring(27,31),16) / TEMP_PHYSICAL_MAGNITUDE_MULTIPLAYER;
			} catch (Exception e) {
				throw e;
			}
		}
		
		@Override
		public String pack() throws Exception {
			return String.format("%s%s%01X%04X%04X%04X%04X%04X%04X%04X", OUTPUT_PACKET,packet_id,idl_regul,
					Integer.valueOf(Math.round(ifac1 * ANGLE_MULTIPLIER)).shortValue(),
					Math.round(ifac2 * ANGLE_MULTIPLIER),
					MINEFR,
					idling_rpm,
					Integer.valueOf(Math.round(min_angle * ANGLE_MULTIPLIER)).shortValue(),
					Integer.valueOf(Math.round(max_angle * ANGLE_MULTIPLIER)).shortValue(),
					Integer.valueOf(Math.round(turn_on_temp * TEMP_PHYSICAL_MAGNITUDE_MULTIPLAYER)));
		}

		@Override
		public String getLogString() {
			return (String
					.format(Locale.US,"%s: Use: %d, Ifac1: %f, Ifac2: %f, Mine FR: %d, RPM: %d, Min angle: %f, Max angle: %f, Turn on temp: %f",
							getClass().getCanonicalName(), idl_regul, ifac1, ifac2,
							MINEFR, idling_rpm, min_angle, max_angle, turn_on_temp));
		}

	}

	/** Класс параметров настройки датчика детонации **/
	public static class KnockPar extends Secu3Dat {
		static final int PACKET_SIZE = 33;

		/** Признак использования канала детонации (0,1) A **/
		public int knock_use_knock_channel;
		/** Частота полосового фильтра **/		
		public float knock_bpf_frequency;
		/** Код частоты полосового фильтра по спецификации HIP9011 BB **/
		public int knock_bpf_frequency_index;
		/** Начало фазового окна в градусах по коленвалу CCCC **/
		public float knock_k_wnd_begin_angle;
		/** Конец фазового окна в градусах по коленвалу DDDD **/
		public float knock_k_wnd_end_angle;
		/** Код постоянной времени интегрирования по спецификации HIP9011 FF **/
		public int knock_int_time_const_index;
		/** Постоянная времени интегрирования **/
		public int knock_int_time_const;
		/** Шаг уменьшения УОЗ при детонации GGGG **/
		public float knock_retard_step; // шаг смещения УОЗ при детонации
		/** Шаг фосстановления УОЗ при детонации HHHH **/
		public float knock_advance_step; // шаг восстановления УОЗ
		/** Максимальное смещение УОЗ при детонации IIII **/
		public float knock_max_retard; // максимальное смещение УОЗ
		/** Абсолютный порог обнаружения детонации, В JJJJ **/
		public float knock_threshold; // в вольтах
		/** Задержка восстановления УОЗ в циклах KK **/
		public int knock_recovery_delay; // в рабочих циклах двигателя

		public static final Parcelable.Creator<KnockPar> CREATOR = new Parcelable.Creator<KnockPar>() {
			 public KnockPar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new KnockPar(in);
			 }

			 public KnockPar[] newArray(int size) {
				 return new KnockPar[size];
			 }
		};
		
		public KnockPar() {
			packet_id = KNOCK_PAR;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_KNOCK_PAR;
		}
		
		public KnockPar (Parcel in) {
			super (in);
			knock_use_knock_channel = in.readInt();
			knock_bpf_frequency = in.readFloat();
			knock_bpf_frequency_index = in.readInt();
			knock_k_wnd_begin_angle = in.readFloat();
			knock_k_wnd_end_angle = in.readFloat();
			knock_int_time_const_index = in.readInt();
			knock_int_time_const = in.readInt();
			knock_retard_step = in.readFloat();
			knock_advance_step = in.readFloat();
			knock_max_retard = in.readFloat();
			knock_threshold = in.readFloat();
			knock_recovery_delay = in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(knock_use_knock_channel);
			dest.writeFloat(knock_bpf_frequency);
			dest.writeInt(knock_bpf_frequency_index);
			dest.writeFloat(knock_k_wnd_begin_angle);
			dest.writeFloat(knock_k_wnd_end_angle);
			dest.writeInt(knock_int_time_const_index);
			dest.writeInt(knock_int_time_const);
			dest.writeFloat(knock_retard_step);
			dest.writeFloat(knock_advance_step);
			dest.writeFloat(knock_max_retard);
			dest.writeFloat(knock_threshold);
			dest.writeInt(knock_recovery_delay);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.knock_use_knock_channel == ((KnockPar)o).knock_use_knock_channel;
				result &= this.knock_bpf_frequency == ((KnockPar)o).knock_bpf_frequency;
				result &= this.knock_bpf_frequency_index == ((KnockPar)o).knock_bpf_frequency_index;
				result &= this.knock_k_wnd_begin_angle == ((KnockPar)o).knock_k_wnd_begin_angle;
				result &= this.knock_k_wnd_end_angle == ((KnockPar)o).knock_k_wnd_end_angle;
				result &= this.knock_int_time_const_index == ((KnockPar)o).knock_int_time_const_index;
				result &= this.knock_int_time_const == ((KnockPar)o).knock_int_time_const;
				result &= this.knock_retard_step == ((KnockPar)o).knock_retard_step;
				result &= this.knock_advance_step == ((KnockPar)o).knock_advance_step;
				result &= this.knock_max_retard == ((KnockPar)o).knock_max_retard;
				result &= this.knock_threshold == ((KnockPar)o).knock_threshold;
				result &= this.knock_recovery_delay == ((KnockPar)o).knock_recovery_delay;
			}
			return result;
		}		

		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {
				knock_use_knock_channel = Integer.parseInt(
						data.substring(2, 3), 16); // A
				knock_bpf_frequency_index = Integer.parseInt(
						data.substring(3, 5), 16); // BB
				if (knock_bpf_frequency >= HIP9011.GAIN_FREQUENCES_SIZE)
					throw (new Exception(String.format(
							"Wrong knock_bpf_frequency_index recieved: '%d'",
							knock_bpf_frequency_index)));
				knock_bpf_frequency = HIP9011.hip9011_gain_freqnences[knock_bpf_frequency_index];
				knock_k_wnd_begin_angle = (float) Integer.valueOf(
						data.substring(5, 9), 16).shortValue()
						/ ANGLE_MULTIPLIER; // CCCC
				knock_k_wnd_end_angle = (float) Integer.valueOf(
						data.substring(9, 13), 16).shortValue()
						/ ANGLE_MULTIPLIER; // DDDD
				knock_int_time_const_index = Integer.parseInt(
						data.substring(13, 15), 16); // FF
				if (knock_int_time_const_index >= HIP9011.INTEGRATOR_LEVELS_SIZE)
					throw (new Exception(String.format(
							"Wrong knock_int_time_const recieved: '%d'",
							knock_bpf_frequency_index)));
				knock_int_time_const = HIP9011.hip9011_integrator_const[knock_int_time_const_index];
				knock_retard_step = (float) Integer.valueOf(
						data.substring(15, 19), 16).shortValue()
						/ ANGLE_MULTIPLIER; // GGGG
				knock_advance_step = (float) Integer.valueOf(
						data.substring(19, 23), 16).shortValue()
						/ ANGLE_MULTIPLIER; // HHHH
				knock_max_retard = (float) Integer.valueOf(
						data.substring(23, 27), 16).shortValue()
						/ ANGLE_MULTIPLIER; // IIII
				knock_threshold = (float) Integer.parseInt(
						data.substring(27, 31), 16)
						* ADC_DISCRETE; // JJJJ
				knock_recovery_delay = Integer.parseInt(data.substring(31, 33),
						16); // KK
			} catch (Exception e) {
				throw e;
			}
		}

		@Override
		public String pack() throws Exception {
			return String.format("%s%s%01X%02X%04X%04X%02X%04X%04X%04X%04X%02X", OUTPUT_PACKET,packet_id,
					knock_use_knock_channel,
					knock_bpf_frequency_index,
					Integer.valueOf(Math.round(knock_k_wnd_begin_angle * ANGLE_MULTIPLIER)).shortValue(),
					Integer.valueOf(Math.round(knock_k_wnd_end_angle * ANGLE_MULTIPLIER)).shortValue(),
					knock_int_time_const_index,
					Integer.valueOf(Math.round(knock_retard_step * ANGLE_MULTIPLIER)).shortValue(),
					Integer.valueOf(Math.round(knock_advance_step * ANGLE_MULTIPLIER)).shortValue(),
					Integer.valueOf(Math.round(knock_max_retard * ANGLE_MULTIPLIER)).shortValue(),
					Integer.valueOf(Math.round(knock_threshold / ADC_DISCRETE)).shortValue(),
					knock_recovery_delay);
		}
		
		@Override
		public String getLogString() {
			return (String.format("%s: ", getClass().getCanonicalName()));
		}

	}

	/** Класс пакета различных настроек **/
	public static class MiscelPar extends Secu3Dat {
		static final int PACKET_SIZE = 17;
				
		/** Скорость UART **/
		public int baud_rate; // скорость UART-a
		/** Значение делителя частоты для UART AAAA **/
		public int baud_rate_index; // скорость UART-a
		/** Период передачи пакетов BB **/
		public int period_ms; // период посылки пакетов мс.
		/** Признак использования отсечки зажигания C **/
		public int ign_cutoff; // признак использования отсечки зажигания
		/** Порог отсечки зажигания (об./мин) DDDD **/
		public int ign_cutoff_thrd; // обороты отсечки зажигания
		/** Выход ДХ: Начало импульса в зубьях шкива относительно в.м.т. EE **/
		public int hop_start_cogs; // Выход ДХ: Начало испульса в зубьях шкива относ. ВМТ
		/** Выход ДХ: Длительность импульса в зубьях шкива FF **/
		public int hop_durat_cogs; // Выход ДХ: Длительность импульса в зубьях шкива

		public static final Parcelable.Creator<MiscelPar> CREATOR = new Parcelable.Creator<MiscelPar>() {
			 public MiscelPar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new MiscelPar(in);
			 }

			 public MiscelPar[] newArray(int size) {
				 return new MiscelPar[size];
			 }
		};
		
		public MiscelPar() {
			packet_id = MISCEL_PAR;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_MISCEL_PAR;
		}
		
		public MiscelPar (Parcel in) {
			super (in);
			baud_rate = in.readInt();
			baud_rate_index = in.readInt();
			period_ms = in.readInt();
			ign_cutoff = in.readInt();
			ign_cutoff_thrd = in.readInt();
			hop_start_cogs = in.readInt();
			hop_durat_cogs = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(baud_rate);
			dest.writeInt(baud_rate_index);
			dest.writeInt(period_ms);
			dest.writeInt(ign_cutoff);
			dest.writeInt(ign_cutoff_thrd);
			dest.writeInt(hop_start_cogs);
			dest.writeInt(hop_durat_cogs);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.baud_rate == ((MiscelPar)o).baud_rate;
				result &= this.baud_rate_index == ((MiscelPar)o).baud_rate_index;
				result &= this.period_ms == ((MiscelPar)o).period_ms;
				result &= this.ign_cutoff == ((MiscelPar)o).ign_cutoff;
				result &= this.ign_cutoff_thrd == ((MiscelPar)o).ign_cutoff_thrd;
				result &= this.hop_start_cogs == ((MiscelPar)o).hop_start_cogs;
				result &= this.hop_durat_cogs == ((MiscelPar)o).hop_durat_cogs;
			}
			return result;
		}
		
		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {
				baud_rate_index = Integer.parseInt(data.substring(2, 6), 16); // AAAA
				baud_rate = indexOf(BAUD_RATE_INDEX, baud_rate_index);
				if (baud_rate < 0)
					throw (new Exception(String.format(
							"MISCEL_PAR: Unsupported baud rate index: '%d'",
							baud_rate_index)));
				baud_rate = BAUD_RATE[baud_rate];
				period_ms = Integer.parseInt(data.substring(6, 8), 16) * 10; // BB
				ign_cutoff = Integer.parseInt(data.substring(8, 9), 16); // C
				ign_cutoff_thrd = Integer.parseInt(data.substring(9, 13), 16); // DDDD
				hop_start_cogs = Integer.parseInt(data.substring(13, 15), 16); // EE
				hop_durat_cogs = Integer.parseInt(data.substring(15, 17), 16); // FF
			} catch (Exception e) {
				throw e;
			}
		}

		@Override
		public String pack() throws Exception {
			return String.format("%s%s%04X%02X%01X%04X%02X%02X", OUTPUT_PACKET,packet_id,baud_rate_index,period_ms / 10, ign_cutoff, ign_cutoff_thrd, hop_start_cogs, hop_durat_cogs);
		}
		
		@Override
		public String getLogString() {
			return (String.format("%s: ", getClass().getCanonicalName()));
		}

	}

	/** Класс пакета параметров реального времени **/
	static public class SensorDat extends Secu3Dat {
		static final int PACKET_SIZE = 50;
		
		/** Частота вращения коленвала AAAA **/
		public int frequen; // частота вращения коленвала (усредненная)
		/** Абсолютное давление во впускном коллекторе BBBB **/
		public float pressure; // давление во впускном коллекторе (усредненное)
		/** Напряжение бортсети CCCC **/
		public float voltage; // напряжение бортовой сети (усредненное)
		/** Температура охлаждающей жидкости DDDD **/
		public float temperat; // температура охлаждающей жидкости (усредненная)
		/** Текущий УОЗ EEEE **/
		public float adv_angle; // текущий УОЗ (число со знаком)		
		/** Состояние дроссельной заслонки (1 - открыта, 0 - закрыта) BB **/
		public int carb; // состояние концевика карбюратора
		/** Состояние газового клапана (1 - открыт, 0 - закрыт) BB **/
		public int gas; // состояние газового клапана
		/** Расход воздуха (0..16), 0 - дроссель закрыт FF **/
		public int air_flow; // расход воздуха
		/** Состояние клапана ЭПХХ (1 - открыта, 0 - закрыта) BB **/
		public int ephh_valve; // состояние клапана ЭПХХ
		/** Состояние клапана ЭМР (1 - открыта, 0 - закрыта) BB **/
		public int epm_valve; // состояние клапана ЭМР
		/** Состояние вентилятора охлаждения **/
		public int cool_fan;
		/** Состояние блокировки стартера **/
		public int st_block;
		/** Уровень сигнала детонации на выходе HIP9011 KKKK **/
		public float knock_k; // уровень сигнала детонации (усредненный за время
								// фазового окна)
		/** Корректировка УОЗ при детонации RRRR **/
		public float knock_retard; // корректировка УОЗ при детонации
		/** Состояние индикатора CE (1 - включен, 0 - выключен) BB **/
		public int ce_state; // !currently is not used!
		/** Биты ошибок CE **/
		public int ce_errors;
		/** ДПДЗ **/
		public float tps;
		/** ADD_I1 **/
		public float add_i1;
		/** ADD_I2 **/
		public float add_i2;
		
		public float choke_pos;

		public static final Parcelable.Creator<SensorDat> CREATOR = new Parcelable.Creator<SensorDat>() {
			 public SensorDat createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new SensorDat(in);
			 }

			 public SensorDat[] newArray(int size) {
				 return new SensorDat[size];
			 }
		};
		
		public SensorDat() {
			packet_id = SENSOR_DAT;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_SENSOR_DAT;
		}		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel (dest,flags);
			dest.writeInt(frequen);
			dest.writeFloat(pressure);
			dest.writeFloat(voltage);
			dest.writeFloat(temperat);
			dest.writeFloat(adv_angle);
			dest.writeFloat(knock_k);
			dest.writeFloat(knock_retard);
			dest.writeInt(air_flow);
			dest.writeInt(ephh_valve);
			dest.writeInt(carb);
			dest.writeInt(gas);
			dest.writeInt(epm_valve);
			dest.writeInt(cool_fan);
			dest.writeInt(st_block);
			dest.writeInt(ce_state);
			dest.writeInt(ce_errors);
			dest.writeFloat(tps);
			dest.writeFloat(add_i1);
			dest.writeFloat(add_i2);
			dest.writeFloat(choke_pos);
		}
		
		public SensorDat (Parcel in) {
			super (in);			
			this.frequen = in.readInt();
			this.pressure = in.readFloat();
			this.voltage = in.readFloat();
			this.temperat = in.readFloat();
			this.adv_angle = in.readFloat();
			this.knock_k = in.readFloat();
			this.knock_retard= in.readFloat();
			this.air_flow = in.readInt();
			this.ephh_valve = in.readInt();
			this.carb = in.readInt();
			this.gas = in.readInt();
			this.epm_valve = in.readInt();
			this.cool_fan = in.readInt();
			this.st_block = in.readInt();
			this.ce_state = in.readInt();			
			this.ce_errors = in.readInt();
			this.tps = in.readFloat();
			this.add_i1 = in.readFloat();
			this.add_i2 = in.readFloat();
			this.choke_pos = in.readFloat();
		}				

		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.frequen == ((SensorDat)o).frequen;
				result &= this.pressure == ((SensorDat)o).pressure;
				result &= this.voltage == ((SensorDat)o).voltage;
				result &= this.temperat == ((SensorDat)o).temperat;
				result &= this.adv_angle == ((SensorDat)o).adv_angle;
				result &= this.knock_k == ((SensorDat)o).knock_k;
				result &= this.knock_retard == ((SensorDat)o).knock_retard;
				result &= this.air_flow == ((SensorDat)o).air_flow;
				result &= this.ephh_valve == ((SensorDat)o).ephh_valve;
				result &= this.carb == ((SensorDat)o).carb;
				result &= this.gas == ((SensorDat)o).gas;
				result &= this.epm_valve == ((SensorDat)o).epm_valve;
				result &= this.cool_fan == ((SensorDat)o).cool_fan;
				result &= this.st_block == ((SensorDat)o).st_block;
				result &= this.ce_state == ((SensorDat)o).ce_state;
				result &= this.ce_errors == (((SensorDat)o)).ce_errors;
				result &= this.tps == (((SensorDat)o)).tps;
				result &= this.add_i1 == (((SensorDat)o)).add_i1;
				result &= this.add_i2 == (((SensorDat)o)).add_i2;
				result &= this.choke_pos == (((SensorDat)o)).choke_pos;
			}
			return result;
		}
		
		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {
				frequen = Integer.parseInt(data.substring(2, 6), 16); // AAAA
				pressure = (float) Integer.parseInt(data.substring(6, 10), 16)
						/ MAP_PHYSICAL_MAGNITUDE_MULTIPLAYER; // BBBB
				voltage = (float) Integer.parseInt(data.substring(10, 14), 16)
						/ UBAT_PHYSICAL_MAGNITUDE_MULTIPLAYER; // CCCC
				temperat = (float) Integer.parseInt(data.substring(14, 18), 16)
						/ TEMP_PHYSICAL_MAGNITUDE_MULTIPLAYER; // DDDD
				adv_angle = (float) Integer.valueOf(data.substring(18, 22), 16)
						.shortValue() / ANGLE_MULTIPLIER; // EEEE
				knock_k = (float) Integer.parseInt(data.substring(22, 26), 16)
						* ADC_DISCRETE; // KKKK
				knock_retard = (float) Integer.parseInt(data.substring(26, 30))
						/ ANGLE_MULTIPLIER; // RRRR
				air_flow = Integer.parseInt(data.substring(30, 32), 16); // FF

				int flags = Integer.parseInt(data.substring(32, 34), 16); // BB
				ephh_valve = flags & 0x01;
				carb = (flags & 0x02) >> 1;
				gas = (flags & 0x04) >> 2;
				epm_valve = (flags & 0x08) >> 3;
				ce_state = (flags & 0x10) >> 4;
				cool_fan = (flags & 0x20) >> 5;
				st_block = (flags & 0x40) >> 6;
				
				tps = (float)Integer.parseInt(data.substring(34, 36),16) / TPS_PHYSICAL_MAGNITUDE_MULTIPLAYER; // TPS AA
				add_i1 = Integer.parseInt(data.substring(36, 40),16) * ADC_DISCRETE; // ADD_I1 AAAA
				add_i2 = Integer.parseInt(data.substring(40, 44),16) * ADC_DISCRETE; // ADD_I1 AAAA
				ce_errors = Integer.parseInt(data.substring(44,48),16); // CE errors
				choke_pos = (float)Integer.parseInt(data.substring(48,50),16) / Secu3Dat.CHOKE_PHYSICAL_MAGNITUDE_MULTIPLIER; // Choke position
			} catch (Exception e) {
				throw e;
			}
		}

		@Override
		public String getLogString() {
			return String
					.format(Locale.US,"%s: RPM: %d, Pressure: %f, Voltage: %f, Temparature: %f, Angle: %f, Knock level: %f, Knock retard: %f, Air flow: %d, EPHH Valve: %d, Carb sensor: %d, Gas sensor: %d, EPM Valve: %d, CE State: %d, CE Errors: %d, TPS: %f, ADD_I1: %f, ADD_I2: %f, Choke: %f",
							getClass().getCanonicalName(), frequen, pressure, voltage,
							temperat, adv_angle, knock_k, knock_retard,
							air_flow, ephh_valve, carb, gas, epm_valve,
							ce_state,ce_errors,tps,add_i1,add_i2,choke_pos);
		}					
	}

	/** Класс пакета параметров запуска двигателя **/
	public static class StartrPar extends Secu3Dat {
		static final int PACKET_SIZE = 10;		

		/** Обороты, при которых стартер будет выключен (об./мин) AAAA **/
		public int starter_off; // порог выключения стартера (обороты)
		/** Обороты перехода с пусковой карты BBBB **/
		public int smap_abandon; // обороты перехода с пусковой карты на рабочую

		public static final Parcelable.Creator<StartrPar> CREATOR = new Parcelable.Creator<StartrPar>() {
			 public StartrPar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new StartrPar(in);
			 }

			 public StartrPar[] newArray(int size) {
				 return new StartrPar[size];
			 }
		};
		
		public StartrPar() {
			packet_id = STARTR_PAR;
			packet_size = PACKET_SIZE;
			intent_action = Secu3Dat.RECEIVE_STARTER_PAR;
		}
		
		public StartrPar (Parcel in) {
			super (in);
			starter_off = in.readInt();
			smap_abandon = in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(starter_off);
			dest.writeInt(smap_abandon);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.starter_off == ((StartrPar)o).starter_off;
				result &= this.smap_abandon == ((StartrPar)o).smap_abandon;
			}
			return result;
		}		

		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {
				starter_off = Integer.parseInt(data.substring(2, 6), 16); // AAAA
				smap_abandon = Integer.parseInt(data.substring(6, 10), 16); // BBBB
			} catch (Exception e) {
				throw e;
			}
		}	
		
		@Override
		public String pack() throws Exception {
			return String.format("%s%s%04X%04X", OUTPUT_PACKET, packet_id, starter_off, smap_abandon);
		}

		@Override
		public String getLogString() {
			return (String.format(Locale.US,"%s: Off RPM: %d, Map RPM: %d", getClass()
					.getCanonicalName(), starter_off, smap_abandon));
		}
				
	}

	/** Класс пакета параметров датчика температуры **/
	public static class TemperPar extends Secu3Dat {
		static final int PACKET_SIZE = 13;
		
		public static final String TMP_USE = "TMP_USE";
		public static final String VENT_PWM = "VENT_PWM";
		public static final String CTS_USE_MAP = "CTS_USE_MAP";
		public static final String VENT_ON = "VENT_ON";
		public static final String VENT_OFF = "VENT_OFF";

		/** Признак комплектации датчика температуры (0,1) A **/
		public int tmp_use;
		/** Признак использования ШИМ для управления вентилятором (0,1) P **/
		public int vent_pwm;
		/** Признак использования таблицы преобразования ДТОЖ M **/
		public int cts_use_map;
		/** Порог включения вентилятора, град. BBBB **/
		public float vent_on; // со знаком
		/** Порог выключения вентилятора, град. CCCC **/
		public float vent_off; // со знаком
		
		public static final Parcelable.Creator<TemperPar> CREATOR = new Parcelable.Creator<TemperPar>() {
			 public TemperPar createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new TemperPar(in);
			 }

			 public TemperPar[] newArray(int size) {
				 return new TemperPar[size];
			 }
		};		

		public TemperPar() {
			packet_id = TEMPER_PAR;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_TEMPER_PAR;
		}
		
		public TemperPar(Parcel in) {
			super (in);
			tmp_use = in.readInt();
			vent_pwm = in.readInt();
			cts_use_map = in.readInt();
			vent_on = in.readFloat();
			vent_off = in.readFloat();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(tmp_use);
			dest.writeInt(vent_pwm);
			dest.writeInt(cts_use_map);
			dest.writeFloat(vent_on);
			dest.writeFloat(vent_off);
		}

		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.tmp_use == ((TemperPar)o).tmp_use;
				result &= this.vent_pwm == ((TemperPar)o).vent_pwm;
				result &= this.cts_use_map == ((TemperPar)o).cts_use_map;
				result &= this.vent_on == ((TemperPar)o).vent_on;
				result &= this.vent_off == ((TemperPar)o).vent_off;
			}
			return result;
		}
		
		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);

			try {
				tmp_use = Integer.parseInt(data.substring(2, 3), 16); // A
				vent_pwm = Integer.parseInt(data.substring(3, 4), 16); // P
				cts_use_map = Integer.parseInt(data.substring(4, 5), 16); // M
				vent_on = (float) Integer.valueOf(data.substring(5, 9), 16)
						.shortValue() / TEMP_PHYSICAL_MAGNITUDE_MULTIPLAYER; // BBBB
				vent_off = (float) Integer.valueOf(data.substring(9, 13), 16)
						.shortValue() / TEMP_PHYSICAL_MAGNITUDE_MULTIPLAYER; // CCCC
			} catch (Exception e) {
				throw e;
			}
		}
		
		@Override
		public String pack() throws Exception {
			return String.format("%s%s%01X%01X%01X%04X%04X", OUTPUT_PACKET, packet_id,
					tmp_use,
					vent_pwm,
					cts_use_map,
					Integer.valueOf(Math.round(vent_on * TEMP_PHYSICAL_MAGNITUDE_MULTIPLAYER)).shortValue(),
					Integer.valueOf(Math.round(vent_off * TEMP_PHYSICAL_MAGNITUDE_MULTIPLAYER)).shortValue());
		}

		@Override		
		public String getLogString() {
			return (String
					.format(Locale.US,"%s: Use: %d, Fan PWM: %d, Use map: %d, Fan on: %f, Fan off: %f",
							getClass().getCanonicalName(), tmp_use, vent_pwm,
							cts_use_map, vent_on, vent_off));
		}

	}
	
	public static class OpCompNc extends Secu3Dat { //используется если надо просто принять или послать определенный код действия
		static final int PACKET_SIZE = 6;
		
		public int opcode;   //operation code
		public int opdata;   //operation data
		
		public static final Parcelable.Creator<OpCompNc> CREATOR = new Parcelable.Creator<OpCompNc>() {
			 public OpCompNc createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new OpCompNc(in);
			 }

			 public OpCompNc[] newArray(int size) {
				 return new OpCompNc[size];
			 }
		};	
		
		public OpCompNc() {
			packet_id = OP_COMP_NC;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_OP_COMP_NC;
		}
		
		public OpCompNc (int opcode, int opdata) {
			packet_id = OP_COMP_NC;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_OP_COMP_NC;
			this.opcode = opcode;
			this.opdata = opdata;
		}
		
		public OpCompNc(Parcel in) {
			super (in);
			opcode = in.readInt();
			opdata = in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(opcode);
			dest.writeInt(opdata);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.opcode == ((OpCompNc)o).opcode;
				result &= this.opdata == ((OpCompNc)o).opdata;
			}
			return result;
		}
		
		@Override
		public void parse (String packet) throws Exception {			  
			super.parse(packet);
			
			try {			
				opdata = Integer.parseInt(data.substring(2,4),16); // AA			
				opcode = Integer.parseInt(data.substring(4,6),16); // BB			
			}
			catch (Exception e) {
				throw e;
			}	  
		}	
		
		
		@Override
		public String pack() throws Exception {
			return String.format("%s%s%02X%02X", OUTPUT_PACKET, packet_id, opdata, opcode);
		}
		
		public static String pack (int opcode, int opdata) throws Exception
		{
			return String.format("%s%s%02X%02X", OUTPUT_PACKET, OP_COMP_NC, opdata, opcode);
		}
		
		@Override
		public String getLogString() {
			return (String.format("%s: ", getClass().getCanonicalName()));
		}

	}
	
	public static class ChangeMode extends Secu3Dat {
		static final int PACKET_SIZE = 38;
		
		public char mode;
				
		public static final Parcelable.Creator<ChangeMode> CREATOR = new Parcelable.Creator<ChangeMode>() {
			 public ChangeMode createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new ChangeMode(in);
			 }

			 public ChangeMode[] newArray(int size) {
				 return new ChangeMode[size];
			 }
		};
		
		public ChangeMode() {
			packet_id = CHANGEMODE;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_CHANGEMODE;
		}
		
		public ChangeMode (Parcel in) {
			super (in);
			mode = (char)in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mode);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.mode == ((ChangeMode)o).mode;
			}
			return result;
		}
		
		@Override
		public void parse(String packet) throws Exception {
			super.parse(packet);
			
			try {
				mode = (char) Integer.parseInt(data.substring(2,3),16); // A
			} catch (Exception e) {
				throw e;
			}
		}			
		
		@Override
		public String pack() throws Exception {
			return String.format("%s%s%s\r", OUTPUT_PACKET,packet_id,mode);
		}
		
		public static String pack (char mode) {
			return String.format("%s%s%s\r", OUTPUT_PACKET,CHANGEMODE,mode);
		}
		
		@Override
		public String getLogString() {
			return (String.format("%s: ", getClass().getCanonicalName()));
		}

	}	
	
	public static class FWInfoDat extends Secu3Dat {
		static final int PACKET_SIZE = FW_SIGNATURE_INFO_SIZE + 10;

		public static final int COPT_ATMEGA16 = 0;
		public static final int COPT_ATMEGA32 = 1;
		public static final int COPT_ATMEGA64 = 2;
		public static final int COPT_ATMEGA128 = 3;
		public static final int COPT_VPSEM = 4;
		public static final int COPT_WHEEL_36_1 = 5;          /*Obsolete! Left for compatibility reasons*/
		public static final int COPT_INVERSE_IGN_OUTPUTS = 6; /*Obsolete! Left for compatibility reasons*/
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
		
		public static final String COPT[] = {
			"COPT_ATMEGA16",
			"COPT_ATMEGA32",
			"COPT_ATMEGA64",
			"COPT_ATMEGA128",
			"COPT_VPSEM",
			"COPT_WHEEL_36_1",          /*Obsolete! Left for compatibility reasons*/
			"COPT_INVERSE_IGN_OUTPUTS", /*Obsolete! Left for compatibility reasons*/
			"COPT_DWELL_CONTROL",
			"COPT_COOLINGFAN_PWM",
			"COPT_REALTIME_TABLES",
			"COPT_ICCAVR_COMPILER",
			"COPT_AVRGCC_COMPILER",
			"COPT_DEBUG_VARIABLES",
			"COPT_PHASE_SENSOR",
			"COPT_PHASED_IGNITION",
			"COPT_FUEL_PUMP",
			"COPT_THERMISTOR_CS",
			"COPT_SECU3T",
			"COPT_DIAGNOSTICS",
			"COPT_HALL_OUTPUT",
			"COPT_REV9_BOARD",
			"COPT_STROBOSCOPE"};
		
		public String info; 
		public long  options;
		
		public static final Parcelable.Creator<FWInfoDat> CREATOR = new Parcelable.Creator<FWInfoDat>() {
			 public FWInfoDat createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new FWInfoDat(in);
			 }

			 public FWInfoDat[] newArray(int size) {
				 return new FWInfoDat[size];
			 }
		};		
	  
		public FWInfoDat () {
			packet_id = FWINFO_DAT;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_FWINFO_DAT;
		}
		
		public FWInfoDat (Parcel in) {
			super (in);
			info = in.readString();
			options = in.readLong();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(info);
			dest.writeLong(options);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.info.equals(((FWInfoDat)o).info);
				result &= this.options == ((FWInfoDat)o).options;
			}
			return result;
		}
		
		@Override
		public void parse (String packet) throws Exception {			  
			super.parse(packet);
			
			try {			
				info = data.substring(2,2+FW_SIGNATURE_INFO_SIZE).trim();		
				options = Long.parseLong(data.substring(2+FW_SIGNATURE_INFO_SIZE,2+FW_SIGNATURE_INFO_SIZE+8),16); // AA			
			}
			catch (Exception e) {
				throw e;
			}	  
		}		
		
		@Override
		public String getLogString() {
			return (String.format("%s: ", getClass().getCanonicalName()));
		}

	}
	
	public static class DiagInpDat extends Secu3Dat {
		static final int PACKET_SIZE = 36;
		
		public float voltage;                        //board voltage
		public float map;                            //MAP sensor
		public float temp;                           //coolant temperature
		public float add_io1;                        //additional input 1 (analog)
		public float add_io2;                        //additional input 2 (analog)
		public int carb;                             //carburetor switch, throttle position sensor (analog)
		public int gas;                              //gas valve state (digital)
		public int ckps;                             //CKP sensor (digital)
		public int ref_s;                            //VR type cam sensor (digital)
		public int ps;                               //Hall-effect cam sensor (digital)
		public int bl;                               //"Bootloader" jumper
		public int de;                               //"Default EEPROM" jumper
		public float ks_1;                           //knock sensor 1  
		public float ks_2;                           //knock sensor 2
		
		public static final Parcelable.Creator<DiagInpDat> CREATOR = new Parcelable.Creator<DiagInpDat>() {
			 public DiagInpDat createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new DiagInpDat(in);
			 }

			 public DiagInpDat[] newArray(int size) {
				 return new DiagInpDat[size];
			 }
		};	
		
		public DiagInpDat() {
			packet_id = DIAGINP_DAT;
			packet_size = PACKET_SIZE;
			intent_action = RECEIVE_DIAGINP_DAT;
		}
		
		public DiagInpDat (Parcel in) {
			super (in);
			voltage = in.readFloat();
			map = in.readFloat();
			temp = in.readFloat();
			add_io1 = in.readFloat();
			add_io2 = in.readFloat();
			carb = in.readInt();
			gas = in.readInt();
			ckps = in.readInt();
			ref_s = in.readInt();
			ps = in.readInt();
			bl = in.readInt();
			de = in.readInt();
			ks_1 = in.readFloat();
			ks_2 = in.readFloat();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeFloat(voltage);
			dest.writeFloat(map);
			dest.writeFloat(temp);
			dest.writeFloat(add_io1);
			dest.writeFloat(add_io2);
			dest.writeInt(carb);
			dest.writeInt(gas);
			dest.writeInt(ckps);
			dest.writeInt(ref_s);
			dest.writeInt(ps);
			dest.writeInt(bl);
			dest.writeInt(de);
			dest.writeFloat(ks_1);
			dest.writeFloat(ks_2);
		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.voltage == ((DiagInpDat)o).voltage;
				result &= this.map == ((DiagInpDat)o).map;
				result &= this.temp == ((DiagInpDat)o).temp;
				result &= this.add_io1 == ((DiagInpDat)o).add_io1;
				result &= this.add_io2 == ((DiagInpDat)o).add_io2;
				result &= this.carb == ((DiagInpDat)o).carb;
				result &= this.gas == ((DiagInpDat)o).gas;
				result &= this.ckps == ((DiagInpDat)o).ckps;
				result &= this.ref_s == ((DiagInpDat)o).ref_s;
				result &= this.ps == ((DiagInpDat)o).ps;
				result &= this.bl == ((DiagInpDat)o).bl;
				result &= this.de == ((DiagInpDat)o).de;
				result &= this.ks_1 == ((DiagInpDat)o).ks_1;
				result &= this.ks_2 == ((DiagInpDat)o).ks_2;
			}
			return result;
		}
		
		@Override
		public void parse (String packet) throws Exception {			  
			super.parse(packet);			
			try {			
				voltage = (float) Integer.parseInt(data.substring(2,6),16) * ADC_DISCRETE;
				map = (float) Integer.parseInt(data.substring(6,10),16) * ADC_DISCRETE;
				temp = (float) Integer.parseInt(data.substring(10,14),16) * ADC_DISCRETE;
				add_io1 = (float) Integer.parseInt(data.substring(14,18),16) * ADC_DISCRETE; 
				add_io2 = (float) Integer.parseInt(data.substring(18,22),16) * ADC_DISCRETE;
				carb =  Integer.parseInt(data.substring(22,26),16);
				ks_1 = (float) Integer.parseInt(data.substring(26,30),16) * ADC_DISCRETE;
				ks_2 = (float) Integer.parseInt(data.substring(30,34),16) * ADC_DISCRETE;
				int i = Integer.parseInt(data.substring(34,36),16);				
				gas = (i >> 0) & 0x01;
				ckps = (i >> 1) & 0x01; 
				ref_s = (i >> 2) & 0x01;
				ps = (i >> 3) & 0x01;
				bl = (i >> 4) & 0x01;
				de = (i >> 5) & 0x01;

			}
			catch (Exception e) {
				throw e;
			}	  
		}		
		
		@Override
		public String getLogString() {
			return (String.format("%s: ", getClass().getCanonicalName()));
		}		
	}
	
	public static class DiagOutDat extends Secu3Dat{
		static final int PACKET_SIZE = 36;
		
		public int ign_out1;                         //ignition output 1
		public int ign_out2;                         //ignition output 2  
		public int ign_out3;                         //ignition output 3
		public int ign_out4;                         //ignition output 4
		public int add_io1;                          //additional output 1
		public int add_io2;                          //additional output 2		
		public int ie;                               //idle edconomizer
		public int fe;                               //fuel economizer
		public int ecf;                              //electric cooling fan
		public int ce;                               //Check engine
		public int st_block;                         //starter blocking
		
		
		public static final Parcelable.Creator<DiagOutDat> CREATOR = new Parcelable.Creator<DiagOutDat>() {
			 public DiagOutDat createFromParcel(Parcel in) {
				 Log.d(this.getClass().getCanonicalName(), "Create from Parcel");			 
				 return new DiagOutDat(in);
			 }

			 public DiagOutDat[] newArray(int size) {
				 return new DiagOutDat[size];
			 }
		};	
		
		public DiagOutDat() {
			packet_id = DIAGOUT_DAT;
			packet_size = PACKET_SIZE;
		}
		
		public DiagOutDat (Parcel in) {
			super (in);
			ign_out1 = in.readInt();
			ign_out2 = in.readInt();
			ign_out3 = in.readInt();
			ign_out4 = in.readInt();
			add_io1 = in.readInt();
			add_io2 = in.readInt();			
			ie = in.readInt();
			fe = in.readInt();
			ecf = in.readInt();
			ce = in.readInt();
			st_block = in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(ign_out1);
			dest.writeInt(ign_out2);
			dest.writeInt(ign_out3);
			dest.writeInt(ign_out4);
			dest.writeInt(add_io1);
			dest.writeInt(add_io2);			
			dest.writeInt(ie);
			dest.writeInt(fe);
			dest.writeInt(ecf);
			dest.writeInt(ce);
			dest.writeInt(st_block);

		}
		
		@Override
		public boolean equals(Object o) {
			boolean result = false;
			if (super.equals(o)) {
				result = true;
				result &= this.ign_out1 == ((DiagOutDat)o).ign_out1;
				result &= this.ign_out2 == ((DiagOutDat)o).ign_out2;
				result &= this.ign_out3 == ((DiagOutDat)o).ign_out3;
				result &= this.ign_out4 == ((DiagOutDat)o).ign_out4;
				result &= this.add_io1 == ((DiagOutDat)o).add_io1;
				result &= this.add_io2 == ((DiagOutDat)o).add_io2;				
				result &= this.ie == ((DiagOutDat)o).ie;
				result &= this.fe == ((DiagOutDat)o).fe;
				result &= this.ecf == ((DiagOutDat)o).ecf;
				result &= this.ce == ((DiagOutDat)o).ce;
				result &= this.st_block == ((DiagOutDat)o).st_block;				
			}
			return result;
		}
		
		@Override
		public String pack() throws Exception {
			int i = 0;
			i |= (ign_out1 & 0x01) << 0;
			i |= (ign_out2 & 0x01) << 1;
			i |= (ign_out3 & 0x01) << 2;
			i |= (ign_out4 & 0x01) << 3;
			i |= (add_io1 & 0x01)  << 4;
			i |= (add_io2 & 0x01)  << 5;
			i |= (ie & 0x01) 	   << 6;
			i |= (fe & 0x01) 	   << 7;
			i |= (ecf & 0x01) 	   << 8;
			i |= (ce & 0x01) 	   << 9;
			i |= (st_block & 0x01) << 10;
			return String.format("%s%s%04X\r", OUTPUT_PACKET, DIAGOUT_DAT, i);
		}
		
		public void setOutputs (int outputs) {
			ign_out1 = (outputs >> 0) & 0x01;
			ign_out2 = (outputs >> 1) & 0x01;
			ign_out3 = (outputs >> 2) & 0x01;
			ign_out4 = (outputs >> 3) & 0x01;
			add_io1 = (outputs >> 4) & 0x01;
			add_io2 = (outputs >> 5) & 0x01;			
			ie = (outputs >> 6) & 0x01;
			fe = (outputs >> 7) & 0x01;
			ecf = (outputs >> 8) & 0x01;
			ce = (outputs >> 9) & 0x01;
			st_block = (outputs >> 10) & 0x01;
		}
		
		public static String pack(int outputs) throws Exception {
			return String.format("%s%s%04X\r", OUTPUT_PACKET, DIAGOUT_DAT, outputs);
		}
	}
}


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
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;

public class Secu3Parser extends Secu3Dat {
	public Map <String,Secu3Dat> parsers;
	public Secu3Dat lastParser;
	char lastPackedId;
	
	public Secu3Parser() {
		parsers = new HashMap <String,Secu3Dat> ();
		parsers.put(String.valueOf(TEMPER_PAR), new TemperPar());
		parsers.put(String.valueOf(CARBUR_PAR), new CarburPar());
		parsers.put(String.valueOf(IDLREG_PAR), new IdlRegPar());
		parsers.put(String.valueOf(ANGLES_PAR), new AnglesPar());
		parsers.put(String.valueOf(FUNSET_PAR), new FunSetPar());
		parsers.put(String.valueOf(STARTR_PAR), new StartrPar());
		parsers.put(String.valueOf(FNNAME_DAT), new FnNameDat());
		parsers.put(String.valueOf(SENSOR_DAT), new SensorDat());
		parsers.put(String.valueOf(ADCCOR_PAR), new ADCCorPar());
		parsers.put(String.valueOf(ADCRAW_DAT), new ADCRawDat());
		parsers.put(String.valueOf(CKPS_PAR), new CKPSPar());
		parsers.put(String.valueOf(OP_COMP_NC), new OPCompNc());
		parsers.put(String.valueOf(CE_ERR_CODES), new CEErrCodes());
		parsers.put(String.valueOf(KNOCK_PAR), new KnockPar());
		parsers.put(String.valueOf(CE_SAVED_ERR), new CESavedErr());
		parsers.put(String.valueOf(FWINFO_DAT), new FWInfoDat());
		parsers.put(String.valueOf(MISCEL_PAR), new MiscelPar());
		parsers.put(String.valueOf(CHOKE_PAR), new ChokePar());
	}
	
	public void parse (String packet) throws Exception {
		try {
			lastPackedId = packet.charAt(PACKET_ID_POS);
			Secu3Dat parser = parsers.get(String.valueOf(lastPackedId));
			lastParser = parser;			
			if (parser == null) {
				throw new Exception(String.format("No parser for packet type '%s'", packet.charAt(PACKET_ID_POS)));
			} else {
				parser.parse(packet);
			}
		} catch (Exception e) {
			lastPackedId = 0;
			throw e;
		}
	}
	
	public char getLastPackedId () {
		return lastPackedId;
	}
	
	public Secu3Dat getLastPacket() {
		return lastParser;
	}
	
	public String getLogString ()
	{
		if (lastParser != null) return lastParser.getLogString(); else return "Incorrect or null packet";
	}
	
	public Intent getLastPacketIntent() {
		if (lastParser != null) return lastParser.getIntent(); else return new Intent();
	}

}

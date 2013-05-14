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

public class DiagInpDat extends Secu3Dat {
	public float voltage;                        //board voltage
	public float map;                            //MAP sensor
	public float temp;                           //coolant temperature
	public float add_io1;                        //additional input 1 (analog)
	public float add_io2;                        //additional input 2 (analog)
	public float carb;                           //carburetor switch, throttle position sensor (analog)
	public int gas;                              //gas valve state (digital)
	public int ckps;                             //CKP sensor (digital)
	public int ref_s;                            //VR type cam sensor (digital)
	public int ps;                               //Hall-effect cam sensor (digital)
	public int bl;                               //"Bootloader" jumper
	public int de;                               //"Default EEPROM" jumper
	public float ks_1;                           //knock sensor 1  
	public float ks_2;                           //knock sensor 2	
}

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

public class DiagOutDat {
	public int ign_out1;                         //ignition output 1
	public int ign_out2;                         //ignition output 2  
	public int ign_out3;                         //ignition output 3
	public int ign_out4;                         //ignition output 4
	public int ie;                               //idle edconomizer
	public int fe;                               //fuel economizer
	public int ecf;                              //electric cooling fan
	public int ce;                               //Check engine
	public int st_block;                         //starter blocking
	public int add_io1;                          //additional output 1
	public int add_io2;                          //additional output 2
}

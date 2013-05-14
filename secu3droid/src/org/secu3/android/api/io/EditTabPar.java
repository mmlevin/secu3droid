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

public class EditTabPar extends Secu3Dat {
	public int tab_set_index;          //номер набора таблиц
	public int tab_id;                 //идентификатор таблицы(данных) в наборе
	public int address;                 //адрес начала фрагмента данных в таблице
	public float table_data[];                 //фрагмент данных (не более 16-ти байт)
	public int name_data[]; 
	public int data_size;               //размер фрагмента данных
  
	public EditTabPar() {
		table_data = new float[32];
		name_data = new int[32];
	}
}

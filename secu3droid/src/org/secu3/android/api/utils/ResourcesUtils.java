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

package org.secu3.android.api.utils;

import android.content.Context;

public class ResourcesUtils {
	public static boolean isResource (String name) {
		if (name == null) return false;
		return name.matches("[@]\\d+");
	}
	
	public static int referenceToInt (String reference) {
		if (reference == null) return 0;
		return Integer.parseInt(reference.replace("@",""));
	}
	
	public static String getReferenceString (Context context,String reference) {
		if (reference == null) return null;
		return context.getString(Integer.parseInt(reference.replace("@","")));
	}
}

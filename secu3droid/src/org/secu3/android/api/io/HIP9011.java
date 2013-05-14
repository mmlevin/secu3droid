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

public class HIP9011 {
	 //таблица перекодировки кода частоты ПФ в частоту
	 public final static int GAIN_FREQUENCES_SIZE = 64;
	 public final static float hip9011_gain_freqnences[] = {
		 01.22f,01.26f,01.31f,01.35f,01.40f,01.45f,01.51f,01.57f, // 00 - 07
		 01.63f,01.71f,01.78f,01.87f,01.96f,02.07f,02.18f,02.31f, // 08 - 15
		 02.46f,02.54f,02.62f,02.71f,02.81f,02.92f,03.03f,03.15f, // 16 - 23
		 03.28f,03.43f,03.59f,03.76f,03.95f,04.16f,04.39f,04.66f, // 24 - 31
		 04.95f,05.12f,05.29f,05.48f,05.68f,05.90f,06.12f,06.37f, // 32 - 39
		 06.64f,06.94f,07.27f,07.63f,08.02f,08.46f,08.95f,09.50f, // 40 - 47
		 10.12f,10.46f,10.83f,11.22f,11.65f,12.10f,12.60f,13.14f, // 48 - 55
		 13.72f,14.36f,15.07f,15.84f,16.71f,17.67f,18.76f,19.98f};// 56 - 63
	
	 //таблица перекодировки кода коэфф. усиления аттенюатора в коэфф. усиления
	 public final static int ATTENUATOR_LEVELS_SIZE = 64;
	 public final static float hip9011_attenuator_gains[] = {
		 2.000f, 1.882f, 1.778f, 1.684f, 1.600f, 1.523f, 1.455f, 1.391f,
		 1.333f, 1.280f, 1.231f, 1.185f, 1.143f, 1.063f, 1.000f, 0.944f,
		 0.895f, 0.850f, 0.810f, 0.773f, 0.739f, 0.708f, 0.680f, 0.654f,
		 0.630f, 0.607f, 0.586f, 0.567f, 0.548f, 0.500f, 0.471f, 0.444f,
		 0.421f, 0.400f, 0.381f, 0.364f, 0.348f, 0.333f, 0.320f, 0.308f,
		 0.296f, 0.286f, 0.276f, 0.267f, 0.258f, 0.250f, 0.236f, 0.222f,
		 0.211f, 0.200f, 0.190f, 0.182f, 0.174f, 0.167f, 0.160f, 0.154f,
		 0.148f, 0.143f, 0.138f, 0.133f, 0.129f, 0.125f, 0.118f, 0.111f};
	
	 //таблица перекодировки кода постоянной времени интегрирования в постоянную времени
	 //интегрирования
	 public final static int INTEGRATOR_LEVELS_SIZE = 32;
	 public final static int hip9011_integrator_const[] = {
		 40,  45,  50,  55,  60,  65,  70,   75, // 00 - 07
		 80,  90,  100, 110, 120, 130, 140, 150, // 08 - 15
		 160, 180, 200, 220, 240, 260, 280, 300, // 16 - 23
		 320, 360, 400, 440, 480, 520, 560, 600  // 24 - 31
	 };
}

package org.secu3.android.api.io;

public class SepTabPar {
	public int address;                 //адрес начала фрагмента данных в таблице
	public float table_data[];                 //фрагмент данных (не более 16-ти байт)
	public int data_size;               //размер фрагмента данных
  
	public SepTabPar()
	{
		table_data = new float[32];
	}
}

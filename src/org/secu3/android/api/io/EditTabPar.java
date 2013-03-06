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

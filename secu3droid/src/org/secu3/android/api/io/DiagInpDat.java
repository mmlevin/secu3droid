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

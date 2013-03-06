package org.secu3.android.test;

import junit.framework.TestCase;

import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.*;
import android.os.Parcel;

public class PacketsTest extends TestCase {
	
	private void testParcel (Secu3Dat packet) {
		Parcel p1 = Parcel.obtain();
		Parcel p2 = Parcel.obtain();
		
		try {				
			byte[] bytes;				
			p1.writeParcelable(packet, 0);
			bytes = p1.marshall();
			p2.unmarshall(bytes, 0, bytes.length);
			p2.setDataPosition(0);
			
			Object getObject = null;
			packet.getClass().cast(getObject);
			getObject = p2.readParcelable(packet.getClass().getClassLoader());
			assertTrue(packet.getClass().getCanonicalName(),packet.equals(getObject));
		} finally {
			p1.recycle();
			p2.recycle();
		}			
	}
	
	public void testSensorDat () {		
		
		SensorDat packet = new SensorDat();
		try {
			packet.parse ("@q032009C51518013400AA000000000311"); 
			assertEquals(packet.getClass().getCanonicalName() + "Engine RPM",packet.frequen, 800);
			assertEquals(packet.getClass().getCanonicalName() + "Pressure",packet.pressure, 39.078125f);
			assertEquals(packet.getClass().getCanonicalName() + "Voltage",packet.voltage, 13.5f);
			assertEquals(packet.getClass().getCanonicalName() + "Temperature",packet.temperat, 77.0f);			
			assertEquals(packet.getClass().getCanonicalName() + "Angle",packet.adv_angle, 5.3125f);
			assertEquals(packet.getClass().getCanonicalName() + "Carb",packet.carb, 0);		
			assertEquals(packet.getClass().getCanonicalName() + "Gas",packet.gas, 0);			
			assertEquals(packet.getClass().getCanonicalName() + "Air flow",packet.air_flow, 3);			
			assertEquals(packet.getClass().getCanonicalName() + "EPHH",packet.ephh_valve, 1);
			
			packet.parse ("@q032019B9124801DAFF78000000001019"); 
			assertEquals(packet.getClass().getCanonicalName() + "Engine RPM",packet.frequen, 800);
			assertEquals(packet.getClass().getCanonicalName() + "Pressure",packet.pressure, 102.890625f);
			assertEquals(packet.getClass().getCanonicalName() + "Voltage",packet.voltage, 11.7f);
			assertEquals(packet.getClass().getCanonicalName() + "Temperature",packet.temperat, 118.5f);			
			assertEquals(packet.getClass().getCanonicalName() + "Angle",packet.adv_angle, -4.25f);
			assertEquals(packet.getClass().getCanonicalName() + "Carb",packet.carb, 0);		
			assertEquals(packet.getClass().getCanonicalName() + "Gas",packet.gas, 0);			
			assertEquals(packet.getClass().getCanonicalName() + "Air flow",packet.air_flow, 16);			
			assertEquals(packet.getClass().getCanonicalName() + "EPHH",packet.ephh_valve, 1);	
			
			testParcel(packet);					
		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);
		}
	}	
	
	public void testTemperPar()
	{
		TemperPar packet = new TemperPar();
		try {
			packet.parse("@j10001880180");
			assertEquals(packet.getClass().getCanonicalName() + "Use temperature sensor",packet.tmp_use, 1);
			assertEquals(packet.getClass().getCanonicalName() + "Use fan PWM",packet.vent_pwm, 0);
			assertEquals(packet.getClass().getCanonicalName() + "Use temp calibration table",packet.cts_use_map, 0);
			assertEquals(packet.getClass().getCanonicalName() + "Fan on temperature",packet.vent_on, 98.0f);
			assertEquals(packet.getClass().getCanonicalName() + "Fan off temperature",packet.vent_off, 96.0f);
			
			testParcel(packet);				

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);
		}		
	}
	
	public void testCarburPar()
	{
		CarburPar packet = new CarburPar();
		try {
			packet.parse("@k076C083400188076C083400");
			assertEquals(packet.getClass().getCanonicalName() + "EPHH low",packet.ephh_lot, 1900);
			assertEquals(packet.getClass().getCanonicalName() + "EPHH high",packet.ephh_hit, 2100);	
			assertEquals(packet.getClass().getCanonicalName() + "EPHH low gas",packet.ephh_lot_g, 1900);			
			assertEquals(packet.getClass().getCanonicalName() + "EPHH high gas",packet.ephh_hit_g, 2100);			
			assertEquals(packet.getClass().getCanonicalName() + "Carburator inverse",packet.carb_invers, 0);
			assertEquals(packet.getClass().getCanonicalName() + "Carburator off delay",packet.shutoff_delay, 0f);
			assertEquals(packet.getClass().getCanonicalName() + "Carburator EMR",packet.epm_ont, 6.125f);
			
			testParcel(packet);	
		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}
	}
	
	public void testIdlRegPar ()
	{
		IdlRegPar packet = new IdlRegPar();
		try {
			packet.parse("@l000040004000A0320FEC00140");
			assertEquals(packet.getClass().getCanonicalName() + "Use idle regulator",packet.idl_regul, 0);
			assertEquals(packet.getClass().getCanonicalName() + "coeff 1",packet.ifac1, 0.125f);
			assertEquals(packet.getClass().getCanonicalName() + "coeff 2",packet.ifac2, 0.125f);
			assertEquals(packet.getClass().getCanonicalName() + "Min angle",packet.min_angle, -10.0f);
			assertEquals(packet.getClass().getCanonicalName() + "Max angle",packet.max_angle, 10.0f);
			assertEquals(packet.getClass().getCanonicalName() + "RPM",packet.idling_rpm, 800);
			assertEquals(packet.getClass().getCanonicalName() + "Insensitivity",packet.MINEFR, 10);
			
			testParcel(packet);				

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}
	}

	public void testAnglesPar ()
	{
		AnglesPar packet = new AnglesPar();
		try {
			packet.parse("@m0640FEC00000006000600");
			assertEquals(packet.getClass().getCanonicalName() + "Max angle",packet.max_angle, 50.0f);
			assertEquals(packet.getClass().getCanonicalName() + "Min angle",packet.min_angle, -10.0f);
			assertEquals(packet.getClass().getCanonicalName() + "Correction",packet.angle_corr, 0.00f);
			assertEquals(packet.getClass().getCanonicalName() + "Decrement speed",packet.dec_spead, 3.00f);
			assertEquals(packet.getClass().getCanonicalName() + "Increment speed",packet.inc_spead, 3.00f);
			assertEquals(packet.getClass().getCanonicalName() + "Use zero angle",packet.zero_adv_ang, 0);
			
			testParcel(packet);			

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}
	}	
	
	public void testFunsetPar() {
		FunSetPar packet = new FunSetPar();
		try {
			packet.parse("@n01010780190000360441");
			assertEquals(packet.getClass().getCanonicalName() + "Gasoline table",packet.fn_benzin, 1);
			assertEquals(packet.getClass().getCanonicalName() + "Gas table",packet.fn_gas, 1);
			assertEquals(packet.getClass().getCanonicalName() + "Lower pressure",packet.map_lower_pressure, 30.0f);
			assertEquals(packet.getClass().getCanonicalName() + "Upper pressure",packet.map_upper_pressure, 100.0f);
			assertEquals(packet.getClass().getCanonicalName() + "MAP Sensor offset",packet.map_curve_offset, 0.13499999f);
			assertEquals(packet.getClass().getCanonicalName() + "MAP Sensor gradient",packet.map_curve_gradient, 53.17383f);			

			testParcel(packet);			

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}		
	}
	
	public void testStartrPar() {
		StartrPar packet = new StartrPar();
		try {
			packet.parse("@o0258028A");
			assertEquals(packet.getClass().getCanonicalName() + "Starter off rpm",packet.starter_off, 600);
			assertEquals(packet.getClass().getCanonicalName() + "Starter map switch RPM",packet.smap_abandon, 650);
			
			testParcel(packet);				

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}		
	}	
	
	public void testADCCorPar() {
		ADCCorPar packet = new ADCCorPar();
		try {
			packet.parse("@r400000002000400000002000400000002000");
			assertEquals(packet.getClass().getCanonicalName(), packet.map_adc_factor, 1.0f);
			assertEquals(packet.getClass().getCanonicalName(), packet.map_adc_correction, 0f);
			assertEquals(packet.getClass().getCanonicalName(), packet.ubat_adc_factor, 1.0f);
			assertEquals(packet.getClass().getCanonicalName(), packet.ubat_adc_correction, 0f);			
			assertEquals(packet.getClass().getCanonicalName(), packet.temp_adc_factor, 1.0f);
			assertEquals(packet.getClass().getCanonicalName(), packet.temp_adc_correction, 0f);

			testParcel(packet);				

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}		
	}
	
	public void testCKPSPar() {
		CKPSPar packet = new CKPSPar();
		try {
			packet.parse("@t00140A0403C02");
			assertEquals(packet.getClass().getCanonicalName() + "CKPS Edge Type",packet.ckps_edge_type, 0);
			assertEquals(packet.getClass().getCanonicalName() + "CKPS Ref Sensor Edge Type",packet.ref_s_edge_type, 0);			
			assertEquals(packet.getClass().getCanonicalName() + "Cogs btdc",packet.ckps_cogs_btdc, 20);
			assertEquals(packet.getClass().getCanonicalName() + "Ignit cogs",packet.ckps_ignit_cogs, 10);
			assertEquals(packet.getClass().getCanonicalName() + "Cylynders",packet.ckps_engine_cyl, 4);
			assertEquals(packet.getClass().getCanonicalName() + "Merge ignition outputs",packet.ckps_merge_ign_outs, 0);
			assertEquals(packet.getClass().getCanonicalName() + "Cogs num",packet.ckps_cogs_num, 60);
			assertEquals(packet.getClass().getCanonicalName() + "Missing cogs num",packet.ckps_miss_num, 2);
			
			testParcel(packet);	
		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}
	}
	
	public void testKnockPar() {
		KnockPar packet = new KnockPar();
		try {
			packet.parse("@w023000003201700800008020003E802");
			assertEquals(packet.getClass().getCanonicalName() + "Use knock channel",packet.knock_use_knock_channel, 0);
			assertEquals(packet.getClass().getCanonicalName() + "Knock BPF frequency index ",packet.knock_bpf_frequency_index, 35);
			assertEquals(packet.getClass().getCanonicalName() + "Knock BPF frequency",packet.knock_bpf_frequency, 5.48f);
			assertEquals(packet.getClass().getCanonicalName() + "Knock windows begin angle",packet.knock_k_wnd_begin_angle, 0f);
			assertEquals(packet.getClass().getCanonicalName() + "Knock windows end angle",packet.knock_k_wnd_end_angle, 25f);
			assertEquals(packet.getClass().getCanonicalName() + "Knock integration time const index",packet.knock_int_time_const_index, 23);
			assertEquals(packet.getClass().getCanonicalName() + "Knock integration time const",packet.knock_int_time_const, 300);
			assertEquals(packet.getClass().getCanonicalName() + "Knock angle retardstep",packet.knock_retard_step, 4f);
			assertEquals(packet.getClass().getCanonicalName() + "Knock angle advance step",packet.knock_advance_step, 0.25f);
			assertEquals(packet.getClass().getCanonicalName() + "Knock max retard angle",packet.knock_max_retard, 16f);
			assertEquals(packet.getClass().getCanonicalName() + "Knock threshhold level",packet.knock_threshold, 2.5f);
			assertEquals(packet.getClass().getCanonicalName() + "Knock recovery delay",packet.knock_recovery_delay, 2);
			
			testParcel(packet);				

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}
	}
	
	public void testMiscelPar() {
		MiscelPar packet = new MiscelPar();
		try {
			packet.parse("@z00CF0801D4C000A");
			assertEquals(packet.getClass().getCanonicalName(), packet.baud_rate_index, 0xCF);
			assertEquals(packet.getClass().getCanonicalName(), packet.baud_rate, 9600);
			assertEquals(packet.getClass().getCanonicalName(), packet.period_ms, 8);
			assertEquals(packet.getClass().getCanonicalName(), packet.ign_cutoff, 0);
			assertEquals(packet.getClass().getCanonicalName(), packet.ign_cutoff_thrd, 7500);
			assertEquals(packet.getClass().getCanonicalName(), packet.hop_start_cogs, 0);
			assertEquals(packet.getClass().getCanonicalName(), packet.hop_durat_cogs, 10);		
			
			testParcel(packet);				

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}		
	}
	
	public void testCEErrors() {
		CEErrCodes packet= new CEErrCodes();
		try {
			packet.parse("@v0001");
			assertEquals(packet.getClass().getCanonicalName() + "Flags",packet.flags, 1);
			
			testParcel(packet);		

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}
	}
	
	public void testCESavedErrors() {
		CESavedErr packet= new CESavedErr();
		try {
			packet.parse("@x0003");
			assertEquals(packet.getClass().getCanonicalName() + "Flags",packet.flags, 3);	
			
			testParcel(packet);				

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}
	}
	
	public void testOpCompNc() {
		OPCompNc packet = new OPCompNc();
		try {
			packet.parse("@u1234");
			assertEquals(packet.getClass().getCanonicalName(), packet.opdata, 0x12);
			assertEquals(packet.getClass().getCanonicalName(), packet.opcode, 0x34);
			
			testParcel(packet);			

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}		
	}
	
	public void testAdcRawDat() {
		ADCRawDat packet = new ADCRawDat();
		try {
			packet.parse("@s00F0151805780000");
			assertEquals(packet.getClass().getCanonicalName() + "MAP",packet.map_value, 0.59999996f);
			assertEquals(packet.getClass().getCanonicalName() + "UBAT",packet.ubat_value, 13.5f);
			assertEquals(packet.getClass().getCanonicalName() + "TEMP",packet.temp_value, 3.5f);
			assertEquals(packet.getClass().getCanonicalName() + "KNOCK",packet.knock_value, 0f);
			
			testParcel(packet);			

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}
	}
	
	public void testFnNameDat () {
		FnNameDat packet = new FnNameDat();
		try {
			packet.parse("@p0A0021083 —Ú‡Ì‰‡Ú  ");
			assertEquals(packet.getClass().getCanonicalName(),packet.tables_num, 0x0A);
			assertEquals(packet.getClass().getCanonicalName(),packet.index, 0);
			assertEquals(packet.getClass().getCanonicalName(),packet.name, "21083 —Ú‡Ì‰‡Ú");
			
			testParcel(packet);			

//			fnNameDat.Parse("@p0A09††††††††††††††††\r");
//			assertEquals(packet.getClass().getCanonicalName() + "FNNAME_DAT",fnNameDat.tables_num, 0x0A);
//			assertEquals(packet.getClass().getCanonicalName() + "FNNAME_DAT",fnNameDat.index, 0x09);
//			assertEquals(packet.getClass().getCanonicalName() + "FNNAME_DAT",fnNameDat.name, "");			
		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}
	}
	
	public void testFWInfoDat () {
		FWInfoDat packet = new FWInfoDat();
		try {
			packet.parse("@ySECU-3 firmware v3.5. Build [Feb 12 2013]       000F8A02");
			assertEquals(packet.getClass().getCanonicalName(),packet.info, "SECU-3 firmware v3.5. Build [Feb 12 2013]");
			assertEquals(packet.getClass().getCanonicalName(),packet.options, 0x00F8A02);
			
			testParcel(packet);			

		} catch (Exception e)
		{
			assertNull(e.getMessage(),e);			
		}
	}	
}

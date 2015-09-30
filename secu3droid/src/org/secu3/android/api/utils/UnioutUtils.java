package org.secu3.android.api.utils;

import android.content.Context;
import android.util.SparseArray;

import org.secu3.android.R;
import org.secu3.android.api.io.ProtoFieldInteger;
import org.secu3.android.api.io.Secu3ProtoWrapper;
import org.secu3.android.parameters.ParamPagerAdapter;
import org.secu3.android.parameters.items.ParamItemFloat;

/**
 * Created by mmlevin on 29.09.2015.
 */
public class UnioutUtils {
    public static final int UNIOUT_LF_OR = 0;
    public static final int UNIOUT_LF_AND = 1;
    public static final int UNIOUT_LF_XOR = 2;
    public static final int UNIOUT_LF_2ND = 3;
    public static final int UNIOUT_LF_NONE = 15;
    public static final int UNIOUT_LF_COUNT = 5;

    private static final int UNIOUTPUT_CONDITIONS_COUNTER = 21;
    public static final int UNIOUT_COND_CTS = 0;    // Coolant temperature
    public static final int UNIOUT_COND_RPM = 1;    // RPM
    public static final int UNIOUT_COND_MAP = 2;    // MAP
    public static final int UNIOUT_COND_UBAT = 3;   // Board voltage
    public static final int UNIOUT_COND_CARB = 4;   // Throttle position limit switch
    public static final int UNIOUT_COND_VSPD = 5;   // Vehicle speed
    public static final int UNIOUT_COND_AIRFL = 6;  // Air flow
    public static final int UNIOUT_COND_TMR = 7;    // Timer, allowed only for 2nd condition
    public static final int UNIOUT_COND_ITTMR = 8;  // Timer, triggered after turning on of ignition
    public static final int UNIOUT_COND_ESTMR = 9;  // Timer, triggered after starting of engine
    public static final int UNIOUT_COND_CPOS = 10;  // Choke position
    public static final int UNIOUT_COND_AANG = 11;  // Advance angle
    public static final int UNIOUT_COND_KLEV = 12;  // Knock signal level
    public static final int UNIOUT_COND_TPS = 13;   // Throttle position sensor
    public static final int UNIOUT_COND_ATS = 14;   // Intake air temperature sensor
    public static final int UNIOUT_COND_AI1 = 15;   // Analog input 1
    public static final int UNIOUT_COND_AI2 = 16;   // Analog input 2
    public static final int UNIOUT_COND_GASV = 17;  // Gas valve input
    public static final int UNIOUT_COND_IPW = 18;   // Injector pulse width
    public static final int UNIOUT_COND_CE = 19;    // CE state
    public static final int UNIOUT_COND_OFTMR = 20; // On/Off delay timer

    private int m_quartz_freq;
    private float m_period_distance;

    private class UnioutputFormatter {
        public float minValue;
        public float maxValue;
        public float step;
        public float on_value;
        public float off_value;
        public String format;
        public String units;

        public UnioutputFormatter (float minValue, float maxValue, float step, float on_value, float off_value, String format, String units)
        {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.step = step;
            this.on_value = on_value;
            this.off_value = off_value;
            this.format = format;
            this.units = units;
        }
    }

    SparseArray<UnioutputFormatter> unifmt;

    public UnioutUtils (Context context, int quartz_freq, float period_distance)
    {
        m_quartz_freq = quartz_freq;
        m_period_distance = period_distance;

        unifmt = new SparseArray<>(UNIOUTPUT_CONDITIONS_COUNTER);
        unifmt.append(UNIOUT_COND_CTS,  new UnioutputFormatter( -40.0f,  180.0f,    0.25f,  55.0f,      50.0f,      "%.2f", context.getString(R.string.units_degrees_celcius)));
        unifmt.append(UNIOUT_COND_RPM,  new UnioutputFormatter( 50.0f,  20000.0f,   10.0f,  1500.0f,    1200.0f,    "%.0f", context.getString(R.string.units_rpm)));
        unifmt.append(UNIOUT_COND_MAP,  new UnioutputFormatter( 0.25f,  500.0f,     0.25f,  95.0f,      90.0f,      "%.2f", context.getString(R.string.units_pressure_kpa)));
        unifmt.append(UNIOUT_COND_UBAT, new UnioutputFormatter( 5.0f,   16.0f,      0.1f,   10.0f,      10.5f,      "%.1f",  context.getString(R.string.units_volts)));
        unifmt.append(UNIOUT_COND_CARB, new UnioutputFormatter( 0.0f,   1.0f,       1.0f,   0.0f,       1.0f,       "%.0f", ""));
        unifmt.append(UNIOUT_COND_VSPD, new UnioutputFormatter( 5.0f,   250.0f,     0.1f,   70.0f,      65.0f,      "%.1f", context.getString(R.string.units_kilometer_per_hour)));
        unifmt.append(UNIOUT_COND_AIRFL,new UnioutputFormatter( 0.0f,   16.0f,      1.0f,   13.0f,      12.0f,      "%.0f", ""));
        unifmt.append(UNIOUT_COND_TMR,  new UnioutputFormatter( 0.0f,   600.0f,     0.1f,   0.0f,       5.0f,       "%.1f", context.getString(R.string.units_sec)));
        unifmt.append(UNIOUT_COND_ITTMR,new UnioutputFormatter( 0.0f,   600.0f,     0.1f,   0.0f,       5.0f,       "%.1f", context.getString(R.string.units_sec)));
        unifmt.append(UNIOUT_COND_ESTMR,new UnioutputFormatter( 0.0f,   600.0f,     0.1f,   0.0f,       5.0f,       "%.1f", context.getString(R.string.units_sec)));
        unifmt.append(UNIOUT_COND_CPOS, new UnioutputFormatter( 0.0f,   100.0f,     0.5f,   60.0f,      55.0f,      "%.1f", context.getString(R.string.units_percents)));
        unifmt.append(UNIOUT_COND_AANG, new UnioutputFormatter(-15.0f,  65.0f,      0.1f,   55.0f,      53.0f,      "%.1f", context.getString(R.string.units_degree)));
        unifmt.append(UNIOUT_COND_KLEV, new UnioutputFormatter( 0.0f,   5.0f,       0.01f,  2.5f,       2.45f,      "%.2f", context.getString(R.string.units_volts)));
        unifmt.append(UNIOUT_COND_TPS,  new UnioutputFormatter( 0.0f,   100.0f,     0.5f,   30.0f,      29.0f,      "%.1f", context.getString(R.string.units_percents)));
        unifmt.append(UNIOUT_COND_ATS,  new UnioutputFormatter(-40.0f,  180.0f,     0.25f,  55.0f,      50.0f,      "%.2f", context.getString(R.string.units_degrees_celcius)));
        unifmt.append(UNIOUT_COND_AI1,  new UnioutputFormatter( 0.0f,   5.0f,       0.01f,  2.5f,       2.48f,      "%.2f", context.getString(R.string.units_degrees_celcius)));
        unifmt.append(UNIOUT_COND_AI2,  new UnioutputFormatter( 0.0f,   5.0f,       0.01f,  2.5f,       2.48f,      "%.2f", context.getString(R.string.units_volts)));
        unifmt.append(UNIOUT_COND_GASV, new UnioutputFormatter( 0.0f,   1.0f,       1.0f,   0.0f,       1.0f,       "%.0f", ""));
        unifmt.append(UNIOUT_COND_IPW,  new UnioutputFormatter( 0.01f,  200.0f,     0.01f,  20.0f,      19.9f,      "%.2f", context.getString(R.string.units_ms)));
        unifmt.append(UNIOUT_COND_CE,   new UnioutputFormatter( 0.0f,   1.0f,       1.0f,   0.0f,       1.0f,       "%.0f", ""));
        unifmt.append(UNIOUT_COND_OFTMR,new UnioutputFormatter( 0.0f,   600.0f,     0.1f,   0.0f,       5.0f,       "%.1f", context.getString(R.string.units_sec)));
    }

    public int unioutEncodeCondVal (float val, int cond)
    {
        switch (cond) {
            case UNIOUT_COND_CTS:
                return Math.round(val * Secu3ProtoWrapper.TEMP_PHYSICAL_MAGNITUDE_MULTIPLIER);
            case UNIOUT_COND_RPM:
                return Math.round(val);
            case UNIOUT_COND_MAP:
                return Math.round(val * Secu3ProtoWrapper.MAP_PHYSICAL_MAGNITUDE_MULTIPLIER);
            case UNIOUT_COND_UBAT:
                return Math.round(val * Secu3ProtoWrapper.UBAT_PHYSICAL_MAGNITUDE_MULTIPLIER);
            case UNIOUT_COND_CARB:
                return Math.round(val);
            case UNIOUT_COND_VSPD:
                return Math.round(((m_period_distance * (3600.0f / 1000.0f)) * ((m_quartz_freq==20000000)?312500.0f:250000.0f)));
            case UNIOUT_COND_AIRFL:
                return Math.round(val);
            case UNIOUT_COND_TMR:
            case UNIOUT_COND_ITTMR:
            case UNIOUT_COND_ESTMR:
                return Math.round(val * 100.0f);
            case UNIOUT_COND_CPOS:
                return Math.round(val * Secu3ProtoWrapper.CHOKE_PHYSICAL_MAGNITUDE_MULTIPLIER);
            case UNIOUT_COND_AANG:
                return Math.round(val * Secu3ProtoWrapper.ANGLE_MULTIPLIER);
            case UNIOUT_COND_KLEV:
                return Math.round(val * Secu3ProtoWrapper.ADC_MULTIPLIER);
            case UNIOUT_COND_TPS:
                return Math.round(val * Secu3ProtoWrapper.TPS_PHYSICAL_MAGNITUDE_MULTIPLIER);
            case UNIOUT_COND_ATS:
                return Math.round(val * Secu3ProtoWrapper.TEMP_PHYSICAL_MAGNITUDE_MULTIPLIER);
            case UNIOUT_COND_AI1:
            case UNIOUT_COND_AI2:
                return Math.round(val * Secu3ProtoWrapper.ADC_MULTIPLIER);
            case UNIOUT_COND_GASV:
                return Math.round(val);
            case UNIOUT_COND_IPW:
                return  Math.round(val * 1000.0f / 3.2f);
            case UNIOUT_COND_CE:
                return Math.round(val);
            case UNIOUT_COND_OFTMR:
                return Math.round(val * 100.0f);
        }
        return 0;
    }

    public float unioutDecodeCondVal (int val, int cond)
    {
        switch (cond) {
            case UNIOUT_COND_CTS:
                return (float)val / Secu3ProtoWrapper.TEMP_PHYSICAL_MAGNITUDE_MULTIPLIER;
            case UNIOUT_COND_RPM:
                return (float)val;
            case UNIOUT_COND_MAP:
                return (float)val / Secu3ProtoWrapper.MAP_PHYSICAL_MAGNITUDE_MULTIPLIER;
            case UNIOUT_COND_UBAT:
                return (float)val / Secu3ProtoWrapper.UBAT_PHYSICAL_MAGNITUDE_MULTIPLIER;
            case UNIOUT_COND_CARB:
                return (float)val;
            case UNIOUT_COND_VSPD:
                float period_s = ((float)val / ((m_quartz_freq == 20000000)? 312500.0f: 2500000.0f)); // Period in seconds
                float speed = ((m_period_distance / period_s) * 3600.0f) / 1000.0f; // Kph
                if (speed > 999.9f) speed = 999.9f;
                return speed;
            case UNIOUT_COND_AIRFL:
                return (float)val;
            case UNIOUT_COND_TMR:
            case UNIOUT_COND_ITTMR:
            case UNIOUT_COND_ESTMR:
                return  (float)val / 100.0f;
            case UNIOUT_COND_CPOS:
                return (float)val / Secu3ProtoWrapper.CHOKE_PHYSICAL_MAGNITUDE_MULTIPLIER;
            case UNIOUT_COND_AANG:
                return (float)val / Secu3ProtoWrapper.ANGLE_MULTIPLIER;
            case UNIOUT_COND_KLEV:
                return (float)val / Secu3ProtoWrapper.ADC_MULTIPLIER;
            case UNIOUT_COND_TPS:
                return (float)val / Secu3ProtoWrapper.TPS_PHYSICAL_MAGNITUDE_MULTIPLIER;
            case UNIOUT_COND_ATS:
                return (float)val / Secu3ProtoWrapper.TEMP_PHYSICAL_MAGNITUDE_MULTIPLIER;
            case UNIOUT_COND_AI1:
            case UNIOUT_COND_AI2:
                return (float)val / Secu3ProtoWrapper.ADC_MULTIPLIER;
            case UNIOUT_COND_GASV:
                return (float)val;
            case UNIOUT_COND_IPW:
                return ((float)val * 3.2f) / 1000.0f;
            case UNIOUT_COND_CE:
                return (float)val;
            case UNIOUT_COND_OFTMR:
                return (float)val / 100.0f;
        }
        return 0f;
    }

    public void prepareParametersItem (int cond, ParamItemFloat item) {
        UnioutputFormatter fmt = unifmt.get(cond);
        item.setMinValue(fmt.minValue);
        item.setMaxValue(fmt.maxValue);
        item.setStepValue(fmt.step);
        item.setFormat(fmt.format);
        item.setUnits(fmt.units);
    }

    public void setParametersItem (int value, int cond, ParamItemFloat item) {
        prepareParametersItem(cond, item);
        item.setValue(unioutDecodeCondVal(value, cond));
    }

    public int getConditionFieldIdForValue (int valueId) {
        switch (valueId) {
            case R.string.unioutput1_condition1_on_value_title:
            case R.string.unioutput1_condition1_off_value_title:
                return R.string.unioutput1_condition_1_title;
            case R.string.unioutput1_condition2_on_value_title:
            case R.string.unioutput1_condition2_off_value_title:
                return R.string.unioutput1_condition_2_title;
            case R.string.unioutput2_condition1_on_value_title:
            case R.string.unioutput2_condition1_off_value_title:
                return R.string.unioutput2_condition_1_title;
            case R.string.unioutput2_condition2_on_value_title:
            case R.string.unioutput2_condition2_off_value_title:
                return R.string.unioutput2_condition_2_title;
            case R.string.unioutput3_condition1_on_value_title:
            case R.string.unioutput3_condition1_off_value_title:
                return R.string.unioutput3_condition_1_title;
            case R.string.unioutput3_condition2_on_value_title:
            case R.string.unioutput3_condition2_off_value_title:
                return R.string.unioutput3_condition_2_title;
        }
        return 0;
    }

    boolean isSigned (int cond)
    {
        switch (cond) {
            case UNIOUT_COND_CTS:
            case UNIOUT_COND_ATS:
            case UNIOUT_COND_AANG:
                return true;
            default:
                return false;
        }
    }
}

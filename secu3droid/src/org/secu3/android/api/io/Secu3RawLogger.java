package org.secu3.android.api.io;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Secu3RawLogger extends Secu3Logger {
	
	private static final String cRawLogFileNameTemplateString = "%04d.%02d.%02d_%02d.%02d.%02d.log";
	private static final String CRawMillisTemplateString = "%02d:%02d:%02d.%02d  ";

	@Override
	public void log(String log) {
		if (isStarted()) {
			long t = System.currentTimeMillis();
			getTime().setTimeInMillis(t);
			String time = String.format(Locale.US,CRawMillisTemplateString,getTime().get(Calendar.HOUR_OF_DAY), getTime().get(Calendar.MINUTE),getTime().get(Calendar.SECOND), getTime().get(Calendar.MILLISECOND)/10);
			super.log(time + log);
		}
	}
	
	@Override
	public String getFileName() {
		getTime().setTime(new Date());
		return String.format(Locale.US, cRawLogFileNameTemplateString, getTime().get(Calendar.YEAR), getTime().get(Calendar.MONTH), getTime().get(Calendar.DAY_OF_MONTH), getTime().get(Calendar.HOUR_OF_DAY), getTime().get(Calendar.MINUTE), getTime().get(Calendar.SECOND));
	}

}

package org.secu3.android.api.io;

import java.util.Locale;

public class Secu3RawLogger extends Secu3Logger {
	
	private static final String cRawLogFileNameTemplateString = "%Y.%m.%d_%H.%M.%S.log";
	private static final String CRawMillisTemplateString = "%s.%02d  ";	
	private static final String cRawTimeTemplateString = "%H:%M:%S";

	@Override
	public void log(String log) {
		if (isStarted()) {
			long t = System.currentTimeMillis();
			getTime().set(t);
			String time = String.format(Locale.US,CRawMillisTemplateString,this.getTime().format(cRawTimeTemplateString), (t%1000)/10);
			super.log(time + log);
		}
	}
	
	@Override
	public String getFileName() {
		getTime().setToNow();
		return getTime().format(cRawLogFileNameTemplateString);
	}

}

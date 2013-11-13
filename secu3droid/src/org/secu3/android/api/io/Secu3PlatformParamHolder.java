package org.secu3.android.api.io;

public class Secu3PlatformParamHolder {
	enum EECUPlatform {
		 EP_ATMEGA16,
		 EP_ATMEGA32,
		 EP_ATMEGA64,
		 EP_ATMEGA128,
		 EP_ATMEGA644,            //redundant to EP_ATMEGA64 by firmware and EEPROM sizes
		 EP_NR_OF_PLATFORMS       //must be last!
	}
	
	public class PPFlashParam {
		int pageSize;
		int pageCount;
		int totalSize;
		int blSectionSize;
	}
}

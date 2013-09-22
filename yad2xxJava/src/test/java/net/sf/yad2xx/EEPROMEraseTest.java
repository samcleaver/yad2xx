package net.sf.yad2xx;

/**
 * Erase a device EEPROM.
 *
 * @since 22/09/2013
 * @author Stephen Davies
 */
public class EEPROMEraseTest {

	private static final int PID = 0x84E0;
	
	public static void main(String[] args) throws FTDIException {
		FTDIInterface ftdi = new FTDIInterface();

		ftdi.setVidPid(0x0403, PID);
		
		if (ftdi.getDevices().length > 0) {
			Device dev = ftdi.getDevices()[0];
			
			try {
				dev.open();
				dev.eraseEE();
			}
			finally {
				dev.close();
			}
		}
	}

}

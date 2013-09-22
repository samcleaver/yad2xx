package net.sf.yad2xx;

import java.io.PrintStream;

/**
 * Dump the EEPROM contents to the console.
 * 
 * @since 22/09/2013
 * @author Stephen Davies
 */
public class EEPROMDumpTest {

	/**
	 * EEPROM size in bytes.
	 * Defaults to 93C46 (1k).
	 */
	private static int PROM_SIZE = 1024;
	
	private static String HEX = "0123456789ABCDEF";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		FTDIInterface ftdi = new FTDIInterface();

		ftdi.setVidPid(0x0403, 0x84e0);
		
		if (ftdi.getDevices().length > 0) {
			Device dev = ftdi.getDevices()[0];
			
			try {
				dev.open();
				
				for (int i = 0; i < (PROM_SIZE / 2);) {
					int word = dev.readEE(i++);
					printHex(System.out, word & 0xff);
					System.out.print(' ');
					printHex(System.out, (word >> 8) & 0xff);

					if (i % 8 == 0) {
						System.out.println();
					} else if (i % 4 == 0) {
						System.out.print('-');
					} else {
						System.out.print(' ');
					}
				}
			}
			finally {
				dev.close();
			}
		}
	}

	private static void printHex(PrintStream out, int value) {
		out.print(HEX.charAt(value / 16));
		out.print(HEX.charAt(value & 0xf));
	}
}

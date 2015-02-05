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
	 * Defaults to 93C46 (128x8).
	 */
	private static final int PROM_SIZE = 128;
	
	private static final String HEX = "0123456789ABCDEF";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		FTDIInterface ftdi = new FTDIInterface();

		ftdi.setVidPid(0x0403, 0x84e0);
		
		if (ftdi.getDevices().length > 0) {
			Device dev = ftdi.getDevices()[0];
			
			try {
				System.out.println("Found: " + dev.getDescription());
				dev.open();
				
				StringBuilder characters = null;
				
				for (int i = 0; i < (PROM_SIZE / 2);) {
					
					// print byte counts
					if (i % 8 == 0) {
						if (i != 0) {
							// suppress first time
							System.out.println(characters.toString());
						}
						printHex(System.out, (i >> 7) & 0xff);
						printHex(System.out, (i << 1) & 0xff);
						System.out.print(' ');
						characters = new StringBuilder();
					}
					
					int word = dev.readEE(i++);
					int lowByte = word & 0xff;
					int highByte = (word >> 8) & 0xff;
					
					// print hex values
					printHex(System.out, lowByte);
					System.out.print(' ');
					printHex(System.out, highByte);
					
					// Accumulate ascii characters
					characters.append(mapAscii(lowByte));
					characters.append(mapAscii(highByte));
					
					if ((i % 4 == 0) && (i % 8 != 0)) {
						System.out.print('-');
						characters.append('-');
					} else {
						System.out.print(' ');
					}
				}
			}
			catch (FTDIException e) {
				e.printStackTrace(System.err);
				System.err.println("In FTDI function: " + e.getFunction());
			}
			finally {
				dev.close();
			}
		} else {
			System.err.println("No device found");
		}
	}

	/**
	 * Convert an ASCII byte value to its printable value. Non-printable values appear as dots.
	 * 
	 * @param value
	 * @return
	 */
	private static char mapAscii(int value) {
		if ((value < 0x20) || (value > 0x7F)) {
			return '.';
		} else {
			return (char) (value & 0x7F);
		}
	}
	
	private static void printHex(PrintStream out, int value) {
		out.print(HEX.charAt(value / 16));
		out.print(HEX.charAt(value & 0xf));
	}
}

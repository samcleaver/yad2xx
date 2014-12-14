package net.sf.yad2xx;

import java.io.PrintStream;
import static net.sf.yad2xx.FTDIConstants.*;

/**
 * Send and receive characters to the serial port. Connect tx to rx to get loopback.
 * 
 * @since Dec 8, 2014
 * @author Stephen Davies
 */
public class SerialEcho {

	public static void main(String[] args) {
		try {
			PrintStream out = System.out;
			FTDIInterface ftdi = new FTDIInterface();
		
			out.println("Serial Echo");
			out.println("-----------");
			out.println("D2XX Library version: " + ftdi.getLibraryVersion());
			out.println();
			
			out.println("---------");
			if (ftdi.getDevices().length > 0) {
				Device dev = ftdi.getDevices()[0];
				if (!dev.isOpen()) {
					out.println("Opening device 0");
					dev.open();
					out.println("Setting baud");
					dev.setBaudRate(19200);
					out.println("Setting 8,N,1");
					dev.setDataCharacteristics(FT_BITS_8, FT_STOP_BITS_1, FT_PARITY_NONE);
					out.println("Sending data");
					byte[] data = { (byte)0x61, (byte)0x62 };
					for (int i = 0; i < 100; i++) {
						dev.write(data);
						try { Thread.sleep(100); }
						catch (InterruptedException ie) {}

						byte[] input = new byte[2];
						out.println("Reading bytes: " + dev.read(input));
						out.println(input[0] + " " + input[1]);
					}
					out.println(dev);
					out.println("Closing device 0");
					dev.close();
					out.println(dev);
				} else {
					out.println("Unable to open device");
				}
			}
		} catch (FTDIException e) {
			e.printStackTrace();
			System.err.println("Function: " + e.getFunction());
			System.err.println("Status: " + e.getStatus());
		}
	}

}

package net.sf.yad2xx;

import java.io.PrintStream;

/**
 * Exercise the JNI interface by listing all attached FTDI devices and their properties.
 * 
 * @since Jun 20, 2012
 * @author Stephen Davies
 */
public class FTDIInterfaceTest {

	public static void main(String[] args) {
		try {
			PrintStream out = System.out;
			FTDIInterface ftdi = new FTDIInterface();
		
			out.println("FTDI Test");
			out.println("---------");
			out.println("D2XX Library version: " + ftdi.getLibraryVersion());
			out.println();
			
			out.println("Standard device count: " + ftdi.getDeviceCount());
			out.println("Standard FTDI devices:");
			listDevices(ftdi, out);
			
			//
			// Include FTDI devices with non-factory VID/PID settings.
			// If your FTDI device(s) have been reconfigured to use a custom VID/PID you should add them here.
			//
			out.println("Setting custom VID/PID\n");
			ftdi.setVidPid(0x0403, 0x84e0);
			
			out.println("Total device count: " + ftdi.getDeviceCount());
			out.println("All FTDI devices:");
			listDevices(ftdi, out);

			out.println("---------");
			if (ftdi.getDevices().length > 0) {
				Device dev = ftdi.getDevices()[0];
				if (!dev.isOpen()) {
					out.println("Opening device 0");
					dev.open();
					out.println(dev);
					out.println("Closing device 0");
					dev.close();
					out.println(dev);
				}
			}
		} catch (FTDIException e) {
			e.printStackTrace();
			System.err.println("Function: " + e.getFunction());
			System.err.println("Status: " + e.getStatus());
		}
	}

	private static void listDevices(FTDIInterface ftdi, PrintStream out) throws FTDIException {
		Device[] devices = ftdi.getDevices();
		for (int i = 0; i < devices.length; i++) {
			Device dev = devices[i];
			out.println(dev);
		}
	}
}

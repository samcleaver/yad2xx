package net.sf.yad2xx;

/**
 * Exercise the JNI interface.
 * 
 * @since Jun 20, 2012
 * @author Stephen Davies
 */
public class FTDIInterfaceTest {

	public static void main(String[] args) {
		try {
			FTDIInterface ftdi = new FTDIInterface();
		
			System.out.println("FTDI Test");
			System.out.println("---------");
			System.out.println("Library version: " + ftdi.getLibraryVersion());
			System.out.println("Device count: " + ftdi.getDeviceCount());
			System.out.println("Setting VID/PID");
			ftdi.setVidPid(0x0403, 0x84e0);
			System.out.println("Device count: " + ftdi.getDeviceCount());

			System.out.println("---------");
			Device[] devices = ftdi.getDevices();
			System.out.println(devices.length);
			for (int i = 0; i < devices.length; i++) {
				Device dev = devices[i];
				System.out.println(dev);
				System.out.println("isOpen: " + dev.isOpen());
				System.out.println("isHighSpeed: " + dev.isHighSpeed());
				System.out.println("type: " + dev.getType());
			}

			System.out.println("---------");
			Device dev = devices[0];
			dev.open();
			dev.setBitMode((byte) 0x0B, FTDIBitMode.FT_BITMODE_ASYNC_BITBANG);
			dev.close();
		} catch (FTDIException e) {
			e.printStackTrace();
			System.err.println("Function: " + e.getFunction());
			System.err.println("Status: " + e.getStatus());
		}
	}

}

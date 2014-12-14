/*
 * Copyright 2012 Stephen Davies
 * 
 * This file is part of yad2xx.
 * 
 * yad2xx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * yad2xx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with yad2xx.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.yad2xx;

/**
 * JNI module to adapt FTDI D2XX library to a more OO based approach.
 * 
 * Some functions are intended to be callable directly, some are 
 * intended to be called indirectly i.e. via a Device. In general,
 * directly callable functions are public. Methods intended to be called
 * indirectly have default (package-private) visibility. 
 * 
 * @since May 20, 2012
 * @author Stephen Davies
 */
public class FTDIInterface {

	/**
	 * Loads the native library on first class usage. The location of the
	 * library is JVM/platform dependent.
	 */
	static {
		System.loadLibrary("FTDIInterface");
	}

	/**
	 * Common formatting for driver and DLL version strings.
	 */
	static String formatVersion(int version) throws FTDIException {
		int major = (version & 0xff0000) >> 16;
		int minor = (version & 0xff00) >> 8;
		int patch = (version & 0xff);
		
		return "" + major + "." + minor + "." + patch;
	}

	/**
	 * This function (FT_CreateDeviceInfoList) builds a device information list
	 * and returns the number of D2XX devices connected to the system. The list
	 * contains information about both unopen and open devices.
	 *
	 * Not sure how useful this really is in Java. Probably better off using
	 * getDevices() and using the returned array length.
	 * 
	 * @return number of devices connected to the system
	 * @throws FTDIException
	 * @since 0.1
	 */
	public native int getDeviceCount() throws FTDIException;

	/**
	 * Returns an array of Devices which describe attached D2XX devices. Combines
	 * calls to FT_CreateDeviceInfoList and FT_GetDeviceInfoList.
	 *
	 * Copies values returned from FT_GetDeviceInfoList into individual Device objects.
	 * @since 0.1
	 */
	public native Device[] getDevices() throws FTDIException;

	/**
	 * FT_GetDriverVersion in its raw format.
	 *
	 * @param ftHandle
	 * @return the D2XX driver version number
	 * @since 0.3
	 */
	native int getDriverVersionRaw(long ftHandle) throws FTDIException;

	/**
	 * Returns the D2XX library version as M.m.p. A prettier way of calling
	 * FT_GetLibraryVersion.
	 *
	 * @return library version string
	 * @throws FTDIException 
	 * @since 0.1
	 */
	public String getLibraryVersion() throws FTDIException {
		return formatVersion(getLibraryVersionInt());
	}

	/**
	 * FT_GetLibraryVersion in its raw format.
	 * 
	 * @return the D2XX DLL version number.
	 * @since 0.1
	 */
	public native int getLibraryVersionInt() throws FTDIException;

	/**
	 * A command to include a custom VID and PID combination within the internal device list table.
	 * This will allow the driver to load for the specified VID and PID combination.
	 * Note, on Windows this performs a no-op.
	 * 
	 * @param vid
	 * @param pid
	 * @throws FTDIException
	 * @since 0.1
	 */
	public native void setVidPid(int vid, int pid) throws FTDIException;

	/**
	 * Close the opened device. Calls FT_Close. ftHandle and flags will be 
	 * reset at completion.
	 *
	 * @param device to close
	 * @throws FTDIException
	 * @see Device#close()
	 * @since 0.1
	 */
	native void close(Device device) throws FTDIException;

	/**
	 * This function clears the Data Terminal Ready (DTR) control signal.
	 * 
	 * @param ftHandle
	 * @throws FTDIException
	 * @since 0.2
	 */
	native void clrDtr(long ftHandle) throws FTDIException;
	
	/**
	 * This function clears the Request To Send (RTS) control signal.
	 * 
	 * @param ftHandle
	 * @throws FTDIException
	 * @since 0.2
	 */
	native void clrRts(long ftHandle) throws FTDIException;
	
	/**
	 * Erases the device EEPROM.
	 *
	 * @param ftHandle
	 * @throws FTDIException
	 * @since 0.2
	 */
	native void eraseEE(long ftHandle) throws FTDIException;

	/**
	 * Gets the instantaneous value of the data bus.
	 * 
	 * @param ftHandle
	 * @return current bit mode
	 * @throws FTDIException
	 * @since 0.2
	 */
	native byte getBitMode(long ftHandle) throws FTDIException;
	
	/**
	 * Get the current value of the latency timer. In the FT8U232AM and FT8U245AM
	 * devices, the receive buffer timeout that is used to flush remaining data
	 * from the receive buffer was fixed at 16 ms. In all other FTDI devices, this
	 * timeout is programmable and can be set at 1 ms intervals between 2ms and 
	 * 255 ms. This allows the device to be better optimized for protocols requiring
	 * faster response times from short data packets.
	 * 
	 * @param ftHandle
	 * @return timeout value in ms
	 * @throws FTDIException
	 * @since 0.2
	 */
	native byte getLatencyTimer(long ftHandle) throws FTDIException;
	
	/**
	 * Returns numbers of bytes in the receive queue. Calls FT_GetQueueStatus.
	 * 
	 * @param ftHandle
	 * @throws FTDIException
	 * @see Device#getQueueStatus()
	 * @since 0.1
	 */
	native int getQueueStatus(long ftHandle) throws FTDIException;
	
	/**
	 * Opens the device. Returned handle is recorded in the device. Calls FT_Open.
	 * 
	 * @param dev 
	 * @throws FTDIException
	 * @see Device#open()
	 * @since 0.1
	 */
	native void open(Device dev) throws FTDIException;
	
	/**
	 * Reads data from device up to the size of the buffer. Calls FT_Read. Note that
	 * this call will block if the requested number of bytes is not immediately 
	 * available. Call getQueueStatus to get the number of bytes actually available
	 * to avoid blocking. 
	 * 
	 * @param ftHandle
	 * @return number of bytes actually read.
	 * @throws FTDIException
	 * @since 0.1
	 */
	native int read(long ftHandle, byte[] buffer, int bufferLength) throws FTDIException;
	
	/**
	 * Read a 16-bit value from an EEPROM location.
	 * 
	 * @param ftHandle
	 * @param wordOffset EEPROM location to read from
	 * @return WORD value read from the EEPROM
	 * @throws FTDIException
	 * @since 0.2
	 */
	native int readEE(long ftHandle, int wordOffset) throws FTDIException;
	
	/**
	 * Send a reset command to the device. Calls FT_ResetDevice.
	 * 
	 * @param ftHandle
	 * @throws FTDIException
	 * @see Device#reset()
	 * @since 0.1
	 */
	native void reset(long ftHandle) throws FTDIException;
	
	/**
	 * This function sets the baud rate for the device. Calls FT_SetBaudRate.
	 * 
	 * @param ftHandle
	 * @param baudRate
	 * @throws FTDIException
	 * @see Device#setBaudRate()
	 * @since 0.1
	 */
	native void setBaudRate(long ftHandle, int baudRate) throws FTDIException;
	
	/**
	 * Enables different chips modes. Calls FT_SetBitMode.
	 *
	 * @param ftHandle
	 * @param pinDirection
	 * @param mode
	 * @throws FTDIException
	 * @since 0.2
	 */
	native void setBitMode(long ftHandle, byte pinDirection, byte mode) throws FTDIException;
	
	/**
	 * Resets the BREAK condition for the device.
	 * 
	 * @param ftHandle
	 * @throws FTDIException
	 * @since 0.3
	 */
	native void setBreakOff(long ftHandle) throws FTDIException;
	
	/**
	 * Sets the BREAK condition for the device.
	 * 
	 * @param ftHandle
	 * @throws FTDIException
	 * @since 0.3
	 */
	native void setBreakOn(long ftHandle) throws FTDIException;
	
	/**
	 * This function sets the special characters for the device.
	 *
	 * @param ftHandle
	 * @param event - Event character.
	 * @param eventEnable - Enable event character.
	 * @param error - Error character.
	 * @param errorEnable - Enable error character.
	 * @throws FTDIException
	 * @since 0.2
	 */
	native void setChars(long ftHandle, char event, boolean eventEnable, char error, boolean errorEnable) throws FTDIException;
	
	/**
	 * This function sets the data characteristics for the device.
	 *
	 * @param ftHandle
	 * @param wordLength - Number of bits per word - must be FT_BITS_8 or FT_BITS_7
	 * @param stopBits - Number of stop bits - must be FT_STOP_BITS_1 or FT_STOP_BITS_2
	 * @param parity - Parity - must be FT_PARITY_NONE, FT_PARITY_ODD, FT_PARITY_EVEN, FT_PARITY_MARK or FT_PARITY SPACE
	 * @throws FTDIException
	 * @since 0.3
	 */
	native void setDataCharacteristics(long ftHandle, byte wordLength, byte stopBits, byte parity) throws FTDIException;

	/**
	 * This function sets the Data Terminal Ready (DTR) control signal.
	 * 
	 * @param ftHandle
	 * @throws FTDIException
	 * @since 0.2
	 */
	native void setDtr(long ftHandle) throws FTDIException;
	
	/**
	 * Sets the flow control for the device.
	 * 
	 * @param ftHandle
	 * @param flowControl Must be one of FT_FLOW_NONE, FT_FLOW_RTS_CTS, FT_FLOW_DTR_DSR or FT_FLOW_XON_XOFF
	 * @param xOn Character used to signal Xon. Only used if flow control is FT_FLOW_XON_XOFF
	 * @param xOff Character used to signal Xoff. Only used if flow control is FT_FLOW_XON_XOFF
	 * @throws FTDIException
	 * @since 0.3
	 */
	native void setFlowControl(long ftHandle, short flowControl, char xOn, char xOff) throws FTDIException;
	
	/**
	 * This function sets the Request To Send (RTS) control signal.
	 * 
	 * @param ftHandle
	 * @throws FTDIException
	 * @since 0.2
	 */
	native void setRts(long ftHandle) throws FTDIException;
	
	/**
	 * Set the latency timer value.
	 * 
	 * In the FT8U232AM and FT8U245AM devices, the receive buffer timeout that is used 
	 * to flush remaining data from the receive buffer was fixed at 16 ms. In all other 
	 * FTDI devices, this timeout is programmable and can be set at 1 ms intervals between 
	 * 2ms and 255 ms. This allows the device to be better optimized for protocols 
	 * requiring faster response times from short data packets.
	 * 
	 * @param ftHandle
	 * @param timer Required value, in milliseconds, of latency timer. Valid range is 2 - 255.
	 * @since 0.2
	 */
	native void setLatencyTimer(long ftHandle, byte timer) throws FTDIException;
	
	/**
	 * Sets the read and write timeouts for the device.
	 * 
	 * @param ftHandle
	 * @param readTimeout read timeout in milliseconds.
	 * @param writeTimeout write timeout in milliseconds.
	 * @throws FTDIException
	 * @since 0.1
	 */
	native void setTimeouts(long ftHandle, int readTimeout, int writeTimeout) throws FTDIException;
	
	/**
	 * Set the USB request transfer size.
	 * 
	 * This function can be used to change the transfer sizes from the default 
	 * transfer size of 4096 bytes to better suit the application requirements.
	 * Transfer sizes must be set to a multiple of 64 bytes between 64 bytes and 64k bytes.
	 * When FT_SetUSBParameters is called, the change comes into effect immediately and any 
	 * data that was held in the driver at the time of the change is lost.
     * Note that, at present, only dwInTransferSize is supported.
	 * 
	 * @param ftHandle
	 * @param inTransferSize Transfer size for USB IN request.
	 * @param outTransferSize Transfer size for USB OUT request.
	 * @since 0.2
	 */
	native void setUSBParameters(long ftHandle, int inTransferSize, int outTransferSize) throws FTDIException;

	/**
	 * Write data to the device. Calls FT_Write.
	 *
	 * @param ftHandle
	 * @param buffer bytes to write to device.
	 * @return number of bytes actually written
	 * @throws FTDIException
	 * @since 0.1
	 */
	native int write(long ftHandle, byte[] buffer, int numBytesToWrite) throws FTDIException;

	/**
	 * Write a 16-bit value to an EEPROM location.
	 *
	 * @param ftHandle
	 * @param wordOffset EEPROM location to write to.
	 * @param value the WORD value to write to the EEPROM.
	 * @throws FTDIException
	 * @since 0.2
	 */
	native void writeEE(long ftHandle, int wordOffset, int value) throws FTDIException;

}

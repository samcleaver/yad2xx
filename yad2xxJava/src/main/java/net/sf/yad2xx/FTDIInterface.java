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
 * @since May 20, 2012
 * @author Stephen Davies
 */
public class FTDIInterface {

	static {
		System.loadLibrary("FTDIInterface");
	}

	/**
	 * Close the opened device. Calls FT_Close. ftHandle and flags will be 
	 * reset at completion.
	 *
	 * @param device to close
	 * @throws FTDIException
	 */
	public native void close(Device device) throws FTDIException;
	
	/**
	 * This function (FT_CreateDeviceInfoList) builds a device information list
	 * and returns the number of D2XX devices connected to the system. The list
	 * contains information about both unopen and open devices.
	 *
	 * @return number of devices connected to the system.
	 * @throws FTDIException
	 */
	public native int getDeviceCount() throws FTDIException;

	/**
	 * Returns the D2XX library version as M.m.p. A prettier way of calling
	 * FT_GetLibraryVersion.
	 *
	 * @throws FTDIException 
	 */
	public String getLibraryVersion() throws FTDIException {
		int version = getLibraryVersionInt();
		int major = (version & 0xff0000) >> 16;
		int minor = (version & 0xff00) >> 8;
		int patch = (version &0xff);
		
		return "" + major + "." + minor + "." + patch;
	}

	/**
	 * FT_GetLibraryVersion.
	 * 
	 * @return the D2XX DLL version number.
	 */
	private native int getLibraryVersionInt() throws FTDIException;

	/**
	 * Combines FT_CreateDeviceInfoList and FT_GetDeviceInfoList.
	 *
	 * Copies values returned from FT_GetDeviceInfoList into individual Device objects.
	 */
	public native Device[] getDevices() throws FTDIException;
	
	/**
	 * Returns numbers of bytes in the receive queue. Calls FT_GetQueueStatus.
	 * 
	 * @param ftHandle
	 * @throws FTDIException
	 */
	public native int getQueueStatus(long ftHandle) throws FTDIException;
	
	/**
	 * Opens the device. Returned handle is recorded in the device. Calls FT_Open.
	 * 
	 * @param dev 
	 * @throws FTDIException
	 */
	public native void open(Device dev) throws FTDIException;
	
	/**
	 * Reads data from device up to the size of the buffer. Calls FT_Read. Note that
	 * this call will block if the requested number of bytes is not immediately 
	 * available. Call getQueueStatus to get the number of bytes actually available
	 * to avoid blocking. 
	 * 
	 * @param ftHandle
	 * @return number of bytes actually read.
	 * @throws FTDIException
	 */
	public native int read(long ftHandle, byte[] buffer, int bufferLength) throws FTDIException;
	
	/**
	 * Send a reset command to the device. Calls FT_ResetDevice.
	 * 
	 * @param ftHandle
	 * @throws FTDIException
	 */
	public native void reset(long ftHandle) throws FTDIException;
	
	/**
	 * Enables different chips modes. Calls FT_SetBitMode.
	 *
	 * @param ftHandle
	 * @param pinDirection
	 * @param mode
	 * @throws FTDIException
	 */
	public native void setBitMode(long ftHandle, byte pinDirection, byte mode) throws FTDIException;
	
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
	 */
	public native void setLatencyTimer(long ftHandle, byte timer) throws FTDIException;
	
	/**
	 * Sets the read and write timeouts for the device.
	 * 
	 * @param ftHandle
	 * @param readTimeout read timeout in milliseconds.
	 * @param writeTimeout write timeout in milliseconds.
	 * @throws FTDIException
	 */
	public native void setTimeouts(long ftHandle, int readTimeout, int writeTimeout) throws FTDIException;
	
	/**
	 * A command to include a custom VID and PID combination within the internal device list table.
	 * This will allow the driver to load for the specified VID and PID combination.
	 * Note, on Windows this performs a no-op.
	 * 
	 * @param vid
	 * @param pid
	 * @throws FTDIException
	 */
	public native void setVidPid(int vid, int pid) throws FTDIException;

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
	 */
	public native void setUSBParameters(long ftHandle, int inTransferSize, int outTransferSize) throws FTDIException;

	/**
	 * Write data to the device. Calls FT_Write.
	 *
	 * @param ftHandle
	 * @param buffer bytes to write to device.
	 * @return number of bytes actually written
	 * @throws FTDIException
	 */
	public native int write(long ftHandle, byte[] buffer, int numBytesToWrite) throws FTDIException;

}

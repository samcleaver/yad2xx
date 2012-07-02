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

import static net.sf.yad2xx.FTDIConstants.FT_FLAGS_HISPEED;
import static net.sf.yad2xx.FTDIConstants.FT_FLAGS_OPENED;

/**
 * Client proxy for an FTDI USB device. Data values here are sourced from
 * FT_GetDeviceInfoList. Most important is the ftHandle.
 *
 * @since May 24, 2012
 * @author Stephen Davies
 */
public class Device {

	private FTDIInterface iFace;
	private int index;
	private int flags;
	private int type;
	private int id;
	private int locationId;
	private String serialNumber;
	private String description;
	private long ftHandle;
	
	public Device(FTDIInterface iFace, int index, int flags, int type, int id, int locationId, String serialNumber, String description, long ftHandle) {
		this.iFace = iFace;
		this.index = index;
		this.flags = flags;
		this.type = type;
		this.id = id;
		this.locationId = locationId;
		this.serialNumber = serialNumber;
		this.description = description;
		this.ftHandle = ftHandle;
	}
	
	/**
	 * Close the opened device.
	 * 
	 * @throws FTDIException
	 */
	public void close() throws FTDIException {
		iFace.close(this);
	}

	/**
	 * Gets the instantaneous value of the databus.
	 *
	 * @return
	 * @throws FTDIException
	 */
	public FTDIBitMode getBitMode() throws FTDIException {
		return FTDIBitMode.lookup(iFace.getBitMode(ftHandle));
	}

	/**
	 * Device description from FT_DEVICE_LIST_INFO_NODE.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the current value of the latency timer.
	 * 
	 * In the FT8U232AM and FT8U245AM devices, the receive buffer timeout that is
	 * used to flush remaining data from the receive buffer was fixed at 16 ms. In
	 * all other FTDI devices, this timeout is programmable and can be set at 1 ms
	 * intervals between 2ms and 255 ms. This allows the device to be better
	 * optimized for protocols requiring faster response times from short data 
	 * packets.
	 * 
	 * @return timeout value in ms
	 * @throws FTDIException
	 */
	public int getLatencyTimer() throws FTDIException {
		return (0xff & iFace.getLatencyTimer(ftHandle));
	}
	
	/**
	 * Returns number of bytes in receive queue.
	 * 
	 * @return number of bytes in receive queue.
	 * @throws FTDIException
	 */
	public int getQueueStatus() throws FTDIException {
		return iFace.getQueueStatus(ftHandle);
	}

	/**
	 * Device serial number from FT_DEVICE_LIST_INFO_NODE.
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Device type from FT_DEVICE_LIST_INFO_NODE. Mapped to an Enum.
	 */
	public FTDIDeviceType getType() {
		return FTDIDeviceType.values()[type];
	}

	/**
	 * Convenient way to test for USB HiSpeed capability.
	 */
	public boolean isHighSpeed() {
		return (flags & FT_FLAGS_HISPEED) != 0;
	}

	/**
	 * Test if device is open.
	 */
	public boolean isOpen() {
		return (flags & FT_FLAGS_OPENED) != 0;
	}
	
	public void open() throws FTDIException {
		if (isOpen())
			throw new IllegalStateException("Device in use");
		iFace.open(this);
	}
	
	/**
	 * Read data from the device.
	 * 
	 * @param buffer bytes read from device. Buffer length determines maximum number of bytes read.
	 * @return number of bytes actually read
	 * @throws FTDIException
	 */
	public int read(byte[] buffer) throws FTDIException {
		return iFace.read(ftHandle, buffer, buffer.length);
	}
	
	/**
	 * This function sets the baud rate for the device.
	 * 
	 * @param baudRate
	 * @throws FTDIException
	 */
	public void setBaudRate(int baudRate) throws FTDIException {
		iFace.setBaudRate(ftHandle, baudRate);
	}
	
	/**
	 * Enables different chip modes.
	 *
	 * @param pinDirection sets up which bits are inputs and outputs. 0 = input, 1 = output.
	 * @param bitMode
	 * @throws FTDIException
	 */
	public void setBitMode(byte pinDirection, FTDIBitMode bitMode) throws FTDIException {
		iFace.setBitMode(ftHandle, pinDirection, (byte)bitMode.getMode());
	}
	
	/**
	 * This function sets the special characters for the device.
	 *
	 * @throws FTDIException
	 */
	public void setChars() throws FTDIException {
		throw new RuntimeException("Not implemented yet");
	}
	
	/**
	 * Sets or clears the Data Terminal Ready (DTR) control signal.
	 * 
	 * @param dtr
	 * @throws FTDIException
	 */
	public void setDtr(boolean dtr) throws FTDIException {
		if (dtr) {
			iFace.setDtr(ftHandle);
		} else {
			iFace.clrDtr(ftHandle);
		}
	}
	
	/**
	 * Set the latency timer value.
	 * 
	 * In the FT8U232AM and FT8U245AM devices, the receive buffer timeout that is used 
	 * to flush remaining data from the receive buffer was fixed at 16 ms. In all other 
	 * FTDI devices, this timeout is programmable and can be set at 1 ms intervals between 
	 * 2ms and 255 ms. This allows the device to be better optimized for protocols 
	 * requiring faster response times from short data packets.
	 * 
	 * @param timer Required value, in milliseconds, of latency timer. Valid range is 2 – 255.
	 */
	public void setLatencyTimer(byte timer) throws FTDIException {
		iFace.setLatencyTimer(ftHandle, timer);
	}
	
	/**
	 * Sets or clears the Request To Send (RTS) control signal.
	 * 
	 * @param rts
	 * @throws FTDIException
	 */
	public void setRts(boolean rts) throws FTDIException {
		if (rts) {
			iFace.setRts(ftHandle);
		} else {
			iFace.clrRts(ftHandle);
		}
	}
	
	/**
	 * Sets the read and write timeouts for the device.
	 * 
	 * @param readTimeout read timeout in milliseconds.
	 * @param writeTimeout write timeout in milliseconds.
	 * @throws FTDIException
	 */
	public void setTimeouts(int readTimeout, int writeTimeout) throws FTDIException {
		iFace.setTimeouts(ftHandle, readTimeout, writeTimeout);
	}
	
	/**
	 * Set the USB request transfer size.
	 * 
	 * @param inTransferSize Transfer size for USB IN request.
	 * @param outTransferSize Transfer size for USB OUT request.
	 */
	public void setUSBParameters(int inTransferSize, int outTransferSize) throws FTDIException {
		iFace.setUSBParameters(ftHandle, inTransferSize, outTransferSize);
	}
	
	/**
	 * Sends a reset command to the device.
	 * 
	 * @throws FTDIException
	 */
	public void reset() throws FTDIException {
		iFace.reset(ftHandle);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Dev ");
		result.append(index);
		result.append(":\n");
		result.append("\tFlags=");
		result.append(Integer.toHexString(flags));
		result.append("\n\tType=");
		result.append(Integer.toHexString(type));
		result.append("\n\tID=");
		result.append(Integer.toHexString(id));
		result.append("\n\tlocID=");
		result.append(Integer.toHexString(locationId));
		result.append("\n\tSerialNumber=");
		result.append(serialNumber);
		result.append("\n\tDescription=");
		result.append(description);
		result.append("\n\tftHandle=");
		result.append(Long.toHexString(ftHandle));
		result.append('\n');
		return result.toString();
	}
	
	/**
	 * Write data to the device.
	 * 
	 * @param buffer bytes to write to device.
	 * @return number of bytes actually written
	 * @throws FTDIException
	 */
	public int write(byte[] buffer) throws FTDIException {
		return write(buffer, buffer.length);
	}
	
	/**
	 * Write data to the device.
	 * 
	 * @param buffer bytes to write to device.
	 * @return number of bytes actually written
	 * @throws FTDIException
	 */
	public int write(byte[] buffer, int numBytesToWrite) throws FTDIException {
		return iFace.write(ftHandle, buffer, numBytesToWrite);
	}

}

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
	 * @since 0.1
	 */
	public void close() throws FTDIException {
		iFace.close(this);
	}

	/**
	 * Erases the device EEPROM.
	 *
	 * @throws FTDIException
	 * @since 0.2
	 */
	public void eraseEE() throws FTDIException {
		iFace.eraseEE(ftHandle);
		return;
	}
	
	/**
	 * Gets the instantaneous value of the databus.
	 *
	 * @return
	 * @throws FTDIException
	 * @since 0.2
	 */
	public FTDIBitMode getBitMode() throws FTDIException {
		return FTDIBitMode.lookup(iFace.getBitMode(ftHandle));
	}

	/**
	 * Device description from FT_DEVICE_LIST_INFO_NODE.
	 * 
	 * @since 0.1
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the D2XX driver version as Major.minor.build. NB. Device has to be opened
	 * before calling this function.
	 *
	 * @return driver version string
	 * @throws FTDIException
	 * @since 0.3
	 */
	public String getDriverVersion() throws FTDIException {
		return FTDIInterface.formatVersion(iFace.getDriverVersionRaw(ftHandle));
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
 	 * @since 0.1
	 */
	public int getQueueStatus() throws FTDIException {
		return iFace.getQueueStatus(ftHandle);
	}

	/**
	 * Device serial number from FT_DEVICE_LIST_INFO_NODE.
	 *
	 * @since 0.1
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Device type from FT_DEVICE_LIST_INFO_NODE. Mapped to an Enum.
	 *
 	 * @since 0.1
	 */
	public FTDIDeviceType getType() {
		return FTDIDeviceType.values()[type];
	}

	/**
	 * Convenient way to test for USB HiSpeed capability.
	 *
	 * @since 0.1
	 */
	public boolean isHighSpeed() {
		return (flags & FT_FLAGS_HISPEED) != 0;
	}

	/**
	 * Test if device is open.
	 *
	 * @since 0.1
	 */
	public boolean isOpen() {
		return (flags & FT_FLAGS_OPENED) != 0;
	}
	
	/**
	 * Begin a session.
	 *  
	 * @throws FTDIException
	 * @since 0.1
	 */
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
	 * @since 0.1
	 */
	public int read(byte[] buffer) throws FTDIException {
		return iFace.read(ftHandle, buffer, buffer.length);
	}
	
	/**
	 * Read a 16-bit value from an EEPROM location.
	 * 
	 * @param offset EEPROM location to read from
	 * @return WORD value read from the EEPROM
	 * @throws FTDIException
	 * @since 0.2
	 */
	public int readEE(int offset) throws FTDIException {
		return iFace.readEE(ftHandle, offset);
	};
	
	/**
	 * Sends a reset command to the device.
	 * 
	 * @throws FTDIException
	 * @since 0.1
	 */
	public void reset() throws FTDIException {
		iFace.reset(ftHandle);
	}

	/**
	 * This function sets the baud rate for the device.
	 * 
	 * @param baudRate
	 * @throws FTDIException
	 * @since 0.1
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
	 * @since 0.2
	 */
	public void setBitMode(byte pinDirection, FTDIBitMode bitMode) throws FTDIException {
		iFace.setBitMode(ftHandle, pinDirection, (byte)bitMode.getMode());
	}
	
	/**
	 * Sets or resets the device break condition. (Combines SetBreakOn and SetBreakOff).
	 *
	 * @param breakCondition true for on, false for off
	 * @throws FTDIException
	 * @since 0.3
	 */
	public void setBreak(boolean breakCondition) throws FTDIException {
		if (breakCondition) {
			iFace.setBreakOn(ftHandle);
		} else {
			iFace.setBreakOff(ftHandle);
		}
	}
	
	/**
	 * This function sets the special characters for the device.
	 *
	 * @throws FTDIException
	 * @since 0.2
	 */
	public void setChars(char event, boolean eventEnable, char error, boolean errorEnable) throws FTDIException {
		iFace.setChars(ftHandle, event, eventEnable, error, errorEnable);
	}
	
	/**
	 * This function sets the data characteristics for the device.
	 * 
	 * @param wordLength Number of bits per word - must be FT_BITS_8 or FT_BITS_7
	 * @param stopBits Number of stop bits - must be FT_STOP_BITS_1 or FT_STOP_BITS_2
	 * @param parity Parity - must be FT_PARITY_NONE, FT_PARITY_ODD, FT_PARITY_EVEN, FT_PARITY_MARK or FT_PARITY SPACE
	 * @throws FTDIException
	 * @since 0.3
	 */
	public void setDataCharacteristics(byte wordLength, byte stopBits, byte parity) throws FTDIException {
		iFace.setDataCharacteristics(ftHandle, wordLength, stopBits, parity);
	}
	
	/**
	 * Sets or clears the Data Terminal Ready (DTR) control signal.
	 * 
	 * @param dtr
	 * @throws FTDIException
	 * @since 0.2
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
	 * @param timer Required value, in milliseconds, of latency timer. Valid range is 2 - 255.
	 * @since 0.2
	 */
	public void setLatencyTimer(byte timer) throws FTDIException {
		iFace.setLatencyTimer(ftHandle, timer);
	}
	
	/**
	 * Sets or clears the Request To Send (RTS) control signal.
	 * 
	 * @param rts
	 * @throws FTDIException
	 * @since 0.2
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
	 * @since 0.1
	 */
	public void setTimeouts(int readTimeout, int writeTimeout) throws FTDIException {
		iFace.setTimeouts(ftHandle, readTimeout, writeTimeout);
	}
	
	/**
	 * Set the USB request transfer size.
	 * 
	 * @param inTransferSize Transfer size for USB IN request.
	 * @param outTransferSize Transfer size for USB OUT request.
	 * @since 0.2
	 */
	public void setUSBParameters(int inTransferSize, int outTransferSize) throws FTDIException {
		iFace.setUSBParameters(ftHandle, inTransferSize, outTransferSize);
	}
	
	/**
	 * Verbose debugging.
	 * 
	 * @since 0.1
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Dev ");
		result.append(index);
		result.append(":\n");
		result.append("\tFlags=");
		result.append(Integer.toHexString(flags));
		result.append(" (isOpen: ");
		result.append(isOpen());
		result.append(" isHighSpeed: ");
		result.append(isHighSpeed());
		result.append(")\n\tType=");
		result.append(Integer.toHexString(type));
		result.append(" (");
		result.append(getType());
		result.append(")\n\tID=");
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
	 * @since 0.1
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
	 * @since 0.1
	 */
	public int write(byte[] buffer, int numBytesToWrite) throws FTDIException {
		return iFace.write(ftHandle, buffer, numBytesToWrite);
	}

	/**
	 * Write a 16-bit value to an EEPROM location.
	 *
	 * @param offset EEPROM location to write to.
	 * @param value the WORD value to write to the EEPROM.
	 * @throws FTDIException
	 * @since 0.2
	 */
	public void writeEE(int offset, int value) throws FTDIException {
		iFace.writeEE(ftHandle, offset, value);
		return;
	}


}

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
 * Indicates failure within the native library. Function is
 * the name of the FTDI D2XX function that failed. Status indicates
 * the return code from that function.
 * 
 * @since May 24, 2012
 * @author Stephen Davies
 */
public class FTDIException extends Exception {

	private static final long serialVersionUID = -5734156165680539506L;

	private int status;
	private String function;
	
	public FTDIException(int status, String function) {
		super("Exception in D2XX native library call.");
		this.status = status;
		this.function = function;
	}
	
	public String getFunction() {
		return function;
	}
	
	public int getStatus() {
		return status;
	}
}

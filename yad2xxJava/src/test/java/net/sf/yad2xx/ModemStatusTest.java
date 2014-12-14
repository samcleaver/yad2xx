package net.sf.yad2xx;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test ModemStatus.
 * 
 * @since December 14, 2014
 * @since 0.3
 * @author Stephen Davies
 */
public class ModemStatusTest {

	/**
	 * Object under test.
	 */
	private ModemStatus status;
	
	/**
	 * Initialise test feature.
	 */
	@Before
	public void setUp() {
		status = new ModemStatus(0x1460);
		// LSB is modem status - DSR and RI asserted
		// line status - Break and parity error asserted
	}
	
	@Test
	public void testModemStatus() {
		assertFalse("CTS failed", status.hasCTS());
		assertTrue("DSR failed", status.hasDSR());
		assertTrue("RI failed", status.hasRI());
		assertFalse("DCD failed", status.hasDCD());
	}
	
	@Test
	public void testLineStatus() {
		assertTrue("Break interrupt failed", status.hasBreakInterrupt());
		assertFalse("Framing error failed", status.hasFramingError());
		assertFalse("Overrun error failed", status.hasOverrunError());
		assertTrue("Parity error failed", status.hasParityError());
	}
	
}

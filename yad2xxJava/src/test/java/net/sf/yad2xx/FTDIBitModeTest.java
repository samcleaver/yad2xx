package net.sf.yad2xx;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static net.sf.yad2xx.FTDIBitMode.FT_BITMODE_MPSSE;
import static net.sf.yad2xx.FTDIBitMode.FT_BITMODE_RESET;

import org.junit.Test;

/**
 * Unit test FTDIBitMode.
 * 
 * @since June 29, 2012
 * @author Stephen Davies
 */
public class FTDIBitModeTest {

	@Test
	public void testLookupPass() {
		assertSame(FT_BITMODE_RESET, FTDIBitMode.lookup((byte) 0));
		assertSame(FT_BITMODE_MPSSE, FTDIBitMode.lookup((byte) 0x02));
	}
	
	@Test
	public void testLookupFail() {
		assertNull(FTDIBitMode.lookup((byte) 3));
	}
	
}

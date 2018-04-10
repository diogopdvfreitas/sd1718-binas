package org.binas.station.ws.it;

import static org.junit.Assert.*;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.StationView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class ReturnBinaIT extends BaseIT {
	
	private static final int X = 10;
	private static final int Y = 20;
	private static final int CAPACITY = 5;
	private static final int RETURNPRIZE = 1;
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(X, Y, CAPACITY, RETURNPRIZE);
	}
	
	@Test
	public void returnBinaTest() {
		try {
			client.getBina();
			client.returnBina();
			StationView sv = client.getInfo();
			assertEquals(1, sv.getTotalGets());
			assertEquals(1, sv.getTotalReturns());
			assertEquals(0, sv.getFreeDocks());
		} catch (NoBinaAvail_Exception nbae) {
			fail("No Binas available when there should be");
		} catch (NoSlotAvail_Exception nsae) {
			fail("No slots available when there should be");
		}
	}
	
	@Test(expected = NoSlotAvail_Exception.class)
	public void returnBinaNoSlotsAvailable() throws NoSlotAvail_Exception {
		client.returnBina();
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}

}

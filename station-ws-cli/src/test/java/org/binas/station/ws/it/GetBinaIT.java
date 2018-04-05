package org.binas.station.ws.it;

import static org.junit.Assert.*;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.StationView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class GetBinaIT extends BaseIT {
	
	private static final int X = 10;
	private static final int Y = 20;
	private static final int CAPACITY = 5;
	private static final int RETURNPRIZE = 1;
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(X, Y, CAPACITY, RETURNPRIZE);
	}
	
	@Test
	public void getBinaTest() throws NoBinaAvail_Exception{
		client.getBina();
		StationView sv = client.getInfo();
		assertEquals(1, sv.getTotalGets());
		assertEquals(0, sv.getTotalReturns());
		assertEquals(1, sv.getFreeDocks());
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void getBinaTestNoBinasAvailable() throws NoBinaAvail_Exception {
		for(int i = 0; i <= CAPACITY; i++) {
			client.getBina();
		}
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}

}

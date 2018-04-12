package org.binas.ws.it;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.StationView;

public class GetInfoStationIT extends BaseIT {
	
	private static final String STATION_ID = "A37_Station1";
	private static final int X = 5;
	private static final int Y = 15;
	private static final int CAPACITY = 20;
	private static final int RETURN_PRIZE = 3;

	@Before
	public void setUp() throws Exception {
		client.testInitStation(STATION_ID, X, Y, CAPACITY, RETURN_PRIZE);
	}

	@Test
	public void sucess() {
		try {
			StationView stationView = client.getInfoStation(STATION_ID);
			assertEquals(stationView.getId(), STATION_ID);
			assertEquals(stationView.getCoordinate().getX(), (Integer) X);
			assertEquals(stationView.getCoordinate().getY(), (Integer) Y);
			assertEquals(stationView.getCapacity(), CAPACITY);
			/*assertEquals(stationView.getTotalGets(), 0);
			assertEquals(stationView.getTotalReturns(), 0);
			assertEquals(stationView.getAvailableBinas(), );
			assertEquals(stationView.getFreeDocks(), );*/
		} catch (InvalidStation_Exception ise) {
			fail("Invalid Station ID");
		}
	}
	
	@After
	public void tearDown() throws Exception {
		client.testClear();
	}

}

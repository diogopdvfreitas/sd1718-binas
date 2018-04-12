package org.binas.ws.it;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.StationView;

public class GetInfoStationIT extends BaseIT {
	
	private static final Integer USER_INITIAL_POINTS = 10;
	
	private static final String STATION_ID = "A37_Station1";
	private static final int X = 5;
	private static final int Y = 15;
	private static final int CAPACITY = 20;
	private static final int RETURN_PRIZE = 3;

	@Before
	public void setUp() throws Exception {
		client.testInit(USER_INITIAL_POINTS);
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
		} catch (InvalidStation_Exception ise) {
			fail("Invalid Station ID");
		}
	}
	
	@Test(expected = InvalidStation_Exception.class)
	public void invalidStationId() throws InvalidStation_Exception {
		client.getInfoStation("A37_Station200");
	}
	
	
	@After
	public void tearDown() throws Exception {
		client.testClear();
	}

}

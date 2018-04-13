package org.binas.ws.it;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.StationView;

public class GetInfoStationIT extends BaseIT {
	
	private static final Integer USER_INITIAL_POINTS = 10;

	@Before
	public void setUp() throws Exception {
		client.testInit(USER_INITIAL_POINTS);
		client.testInitStation(STATION1_ID, X1, Y1, CAPACITY1, BONUS1);
		client.testInitStation(STATION2_ID, X2, Y2, CAPACITY2, BONUS2);
		client.testInitStation(STATION3_ID, X3, Y3, CAPACITY3, BONUS3);
	}

	@Test
	public void sucess() {
		try {
			StationView stationView1 = client.getInfoStation(STATION1_ID);
			StationView stationView2 = client.getInfoStation(STATION2_ID);
			StationView stationView3 = client.getInfoStation(STATION3_ID);
			
			assertEquals(stationView1.getId(), STATION1_ID);
			assertEquals(stationView1.getCoordinate().getX(), (Integer) X1);
			assertEquals(stationView1.getCoordinate().getY(), (Integer) Y1);
			assertEquals(stationView1.getCapacity(), CAPACITY1);
			
			
			assertEquals(stationView2.getId(), STATION2_ID);
			assertEquals(stationView2.getCoordinate().getX(), (Integer) X2);
			assertEquals(stationView2.getCoordinate().getY(), (Integer) Y2);
			assertEquals(stationView2.getCapacity(), CAPACITY2);
			
			
			assertEquals(stationView3.getId(), STATION3_ID);
			assertEquals(stationView3.getCoordinate().getX(), (Integer) X3);
			assertEquals(stationView3.getCoordinate().getY(), (Integer) Y3);
			assertEquals(stationView3.getCapacity(), CAPACITY3);
			
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

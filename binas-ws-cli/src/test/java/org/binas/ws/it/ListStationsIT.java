package org.binas.ws.it;

import static org.junit.Assert.*;

import java.util.List;

import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ListStationsIT extends BaseIT {
	
	private static final Integer USER_INITIAL_POINTS = 10;
	
	@Before
	public void setUp() throws Exception {
		client.testInit(USER_INITIAL_POINTS);
		client.testInitStation(STATION1_ID, X1, Y1, CAPACITY1, BONUS1);
		client.testInitStation(STATION2_ID, X2, Y2, CAPACITY2, BONUS2);
		client.testInitStation(STATION3_ID, X3, Y3, CAPACITY3, BONUS3);
	}

	@Test
	public void success() {
		CoordinatesView cv = new CoordinatesView();
		cv.setX(0); cv.setY(0);
		
		List<StationView> list = client.listStations(2, cv);
		
		assertEquals(2, list.size());
		
		assertEquals(list.get(0).getId(), STATION1_ID);
		assertEquals(list.get(0).getCoordinate().getX(), (Integer) X1);
		assertEquals(list.get(0).getCoordinate().getY(), (Integer) Y1);
		assertEquals(list.get(0).getCapacity(), CAPACITY1);
		
		assertEquals(list.get(1).getId(), STATION3_ID);
		assertEquals(list.get(1).getCoordinate().getX(), (Integer) X3);
		assertEquals(list.get(1).getCoordinate().getY(), (Integer) Y3);
		assertEquals(list.get(1).getCapacity(), CAPACITY3);
	}
	
	@Test
	public void successWithRequestesNumberOfStationsBiggerThanExistingStations() {
		CoordinatesView cv = new CoordinatesView();
		cv.setX(0); cv.setY(0);
		
		List<StationView> list = client.listStations(10, cv);
		
		assertEquals(3, list.size());
		
		assertEquals(list.get(0).getId(), STATION1_ID);
		assertEquals(list.get(0).getCoordinate().getX(), (Integer) X1);
		assertEquals(list.get(0).getCoordinate().getY(), (Integer) Y1);
		assertEquals(list.get(0).getCapacity(), CAPACITY1);
		
		assertEquals(list.get(1).getId(), STATION3_ID);
		assertEquals(list.get(1).getCoordinate().getX(), (Integer) X3);
		assertEquals(list.get(1).getCoordinate().getY(), (Integer) Y3);
		assertEquals(list.get(1).getCapacity(), CAPACITY3);
		
		assertEquals(list.get(2).getId(), STATION2_ID);
		assertEquals(list.get(2).getCoordinate().getX(), (Integer) X2);
		assertEquals(list.get(2).getCoordinate().getY(), (Integer) Y2);
		assertEquals(list.get(2).getCapacity(), CAPACITY2);
	}
	
	@After
	public void tearDown() throws Exception {
		client.testClear();
	}
}

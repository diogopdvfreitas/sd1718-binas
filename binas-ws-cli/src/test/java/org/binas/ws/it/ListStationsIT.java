package org.binas.ws.it;

import static org.junit.Assert.*;

import java.util.List;

import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ListStationsIT extends BaseIT {
	
	private static final String STATION_ID = "A37_Station";
	private static final int X = 0;
	private static final int Y = 0;
	private static final int CAPACITY = 20;
	private static final int RETURN_PRIZE = 3;
	
	@Before
	public void setUp() throws Exception {
		for (int i = 1; i < 4; i++) {
			client.testInitStation(STATION_ID + i, X + (i * 5), Y + (i * 5), CAPACITY, RETURN_PRIZE);
		}
	}

	@Test
	public void success() {
		CoordinatesView cv = new CoordinatesView();
		cv.setX(0); cv.setY(0);
		List<StationView> list = client.listStations(2, cv);
		
		assertEquals(list.get(0).getId(), "A37_Station1");
		assertEquals(list.get(0).getCoordinate().getX(), (Integer) 5);
		assertEquals(list.get(0).getCoordinate().getY(), (Integer) 5);
		assertEquals(list.get(0).getCapacity(), CAPACITY);
		assertEquals(list.get(1).getId(), "A37_Station2");
		assertEquals(list.get(1).getCoordinate().getX(), (Integer) 10);
		assertEquals(list.get(1).getCoordinate().getY(), (Integer) 10);
		assertEquals(list.get(1).getCapacity(), CAPACITY);
	}
	
	@After
	public void tearDown() throws Exception {
	}
}

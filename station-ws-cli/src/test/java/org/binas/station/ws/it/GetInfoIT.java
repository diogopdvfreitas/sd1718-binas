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
public class GetInfoIT extends BaseIT {
	
	private static final int X = 10;
	private static final int Y = 20;
	private static final int CAPACITY = 5;
	private static final int RETURNPRIZE = 1;
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(X, Y, CAPACITY, RETURNPRIZE);
	}
	
	@Test
	public void getInfoTest() {
		StationView sv = client.getInfo();
		assertNotNull(sv);
		assertEquals(X, sv.getCoordinate().getX());
		assertEquals(Y, sv.getCoordinate().getY());
		assertEquals(CAPACITY, sv.getCapacity());
		assertEquals(0, sv.getFreeDocks());
		assertEquals(0, sv.getTotalGets());
		assertEquals(0, sv.getTotalReturns());
		assertEquals(CAPACITY, sv.getAvailableBinas());
	}
	
	@Test
	public void getInfoTest1() throws BadInit_Exception, NoBinaAvail_Exception {
		int capacity = 1;
		int x = 1;
		int y = 5;
		
		client.testInit(x, y, capacity, RETURNPRIZE);
		
		/* StationView sv = client.getInfo();
		assertNotNull(sv);
		assertEquals(x, sv.getCoordinate().getX());
		assertEquals(y, sv.getCoordinate().getY());
		assertEquals(capacity, sv.getCapacity());
		assertEquals(0, sv.getFreeDocks());
		assertEquals(0, sv.getTotalGets());
		assertEquals(0, sv.getTotalReturns());
		assertEquals(capacity, sv.getAvailableBinas()); */
		
		for(int i = 0; i < capacity; i++) {
			client.getBina();
		}
		
		StationView sv = client.getInfo();
		assertEquals(capacity, sv.getFreeDocks());
		assertEquals(capacity, sv.getTotalGets());
		assertEquals(0, sv.getAvailableBinas());
	}
	
	@Test
	public void getInfoTest2() throws BadInit_Exception, NoBinaAvail_Exception {
		int capacity = 10;
		int x = 10;
		int y = 1;
		
		client.testInit(x, y, capacity, RETURNPRIZE);
		
		/* StationView sv = client.getInfo();
		assertNotNull(sv);
		assertEquals(x, sv.getCoordinate().getX());
		assertEquals(y, sv.getCoordinate().getY());
		assertEquals(capacity, sv.getCapacity());
		assertEquals(0, sv.getFreeDocks());
		assertEquals(0, sv.getTotalGets());
		assertEquals(0, sv.getTotalReturns());
		assertEquals(capacity, sv.getAvailableBinas()); */
		
		for(int i = 0; i < capacity; i++) {
			client.getBina();
		}
		
		StationView sv = client.getInfo();
		assertEquals(capacity, sv.getFreeDocks());
		assertEquals(capacity, sv.getTotalGets());
		assertEquals(0, sv.getAvailableBinas());
	}
	
	@Test
	public void getInfoTest3() throws BadInit_Exception, NoBinaAvail_Exception {
		int capacity = 25;
		int x = 25;
		int y = 25;
		
		client.testInit(x, y, capacity, RETURNPRIZE);


		/* StationView sv = client.getInfo();
		assertNotNull(sv);
		assertEquals(x, sv.getCoordinate().getX());
		assertEquals(y, sv.getCoordinate().getY());
		assertEquals(capacity, sv.getCapacity());
		assertEquals(0, sv.getFreeDocks());
		assertEquals(0, sv.getTotalGets());
		assertEquals(0, sv.getTotalReturns());
		assertEquals(capacity, sv.getAvailableBinas()); */
		
		for(int i = 0; i < capacity; i++) {
			client.getBina();
		}
		
		StationView sv = client.getInfo();
		assertEquals(capacity, sv.getFreeDocks());
		assertEquals(capacity, sv.getTotalGets());
		assertEquals(0, sv.getAvailableBinas());
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}

}

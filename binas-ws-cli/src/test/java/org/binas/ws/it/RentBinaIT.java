package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RentBinaIT extends BaseIT {
	
	private static final Integer USER_INITIAL_POINTS = 10;
	private static final String EMAIL1 = "e.1.mail@that.serv1ce.com";
	private static final String EMAIL2 = "e.2.mail@that.serv1ce.com";
	private static final String EMAIL3 = "e.3.mail@that.serv1ce.com";
	
	private static final String STATION_ID = "A37_Station1";
	
	private int availableBinas;
	private UserView user1, user2, user3;

	@Before
	public void setUp() throws Exception {
		client.testInit(USER_INITIAL_POINTS);
		user1 = client.activateUser(EMAIL1);
		user2 = client.activateUser(EMAIL2);
		user3 = client.activateUser(EMAIL3);
		availableBinas = client.getInfoStation(STATION_ID).getAvailableBinas();
	}

	@Test
	public void success() {
		int availableBinas;
		
		try {
			client.rentBina(STATION_ID, EMAIL1);
			availableBinas = client.getInfoStation(STATION_ID).getAvailableBinas();
			assertEquals(this.availableBinas - 1, availableBinas);
			assertEquals(user1.getCredit() - 1, client.getCredit(EMAIL1));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test(expected = AlreadyHasBina_Exception.class)
	public void AlreadyHasBina() throws AlreadyHasBina_Exception {
		
		try {
			client.rentBina(STATION_ID, EMAIL1);
			client.rentBina(STATION_ID, EMAIL1);
		} catch (NoBinaAvail_Exception nba) {
			fail(nba.getMessage());
		} catch (NoCredit_Exception nce) {
			fail(nce.getMessage());
		} catch (UserNotExists_Exception unee) {
			fail(unee.getMessage());
		} catch (InvalidStation_Exception iee) {
			fail(iee.getMessage());
		}
	}

	@After
	public void tearDown() throws Exception {
		client.testClear();
	}
}

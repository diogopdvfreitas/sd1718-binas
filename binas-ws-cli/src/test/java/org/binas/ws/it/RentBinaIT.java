package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RentBinaIT extends BaseIT {
	
	private static final Integer USER_INITIAL_POINTS = 10;
	private static final String EMAIL = "e.1.mail@that.serv1ce.com";
	private static final String EMAIL2 = "e.2.mail@that.serv1ce.com";
	
	private static final String STATION1_ID = "A37_Station1";
	private static final String STATION2_ID = "A37_Station2";
	private static final String STATION3_ID = "A37_Station3";
	
	private static final int X = 0;
	private static final int Y = 0;
	private static final int CAPACITY = 10;
	private static final int BONUS = 2;
	
	private int availableBinas1, availableBinas2, availableBinas3;
	private UserView user;

	@Before
	public void setUp() throws Exception {
		client.testInit(USER_INITIAL_POINTS);
		
		user = client.activateUser(EMAIL);
		
		client.testInitStation(STATION1_ID, X, Y, CAPACITY, BONUS);
		client.testInitStation(STATION2_ID, X, Y, CAPACITY, BONUS);
		client.testInitStation(STATION3_ID, X, Y, CAPACITY, BONUS);
		
		availableBinas1 = client.getInfoStation(STATION1_ID).getAvailableBinas();
		availableBinas2 = client.getInfoStation(STATION2_ID).getAvailableBinas();
		availableBinas3 = client.getInfoStation(STATION3_ID).getAvailableBinas();
	}

	@Test
	public void success() {
		int availableBinas1, availableBinas2, availableBinas3;
		
		try {
			client.rentBina(STATION1_ID, EMAIL);
			
			availableBinas1 = client.getInfoStation(STATION1_ID).getAvailableBinas();
			availableBinas2 = client.getInfoStation(STATION2_ID).getAvailableBinas();
			availableBinas3 = client.getInfoStation(STATION3_ID).getAvailableBinas();
			
			assertEquals(this.availableBinas1 - 1, availableBinas1);
			assertEquals(this.availableBinas2, availableBinas2);
			assertEquals(this.availableBinas3, availableBinas3);
			
			assertEquals(user.getCredit() - 1, client.getCredit(EMAIL));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test(expected = AlreadyHasBina_Exception.class)
	public void alreadyHasBina() throws AlreadyHasBina_Exception {
		try {
			client.rentBina(STATION1_ID, EMAIL);
			client.rentBina(STATION1_ID, EMAIL);
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
	
	@Test(expected = InvalidStation_Exception.class)
	public void invalidStation() throws InvalidStation_Exception {
		try {
			client.rentBina("example of invalid bina", EMAIL);
		} catch (NoBinaAvail_Exception nba) {
			fail(nba.getMessage());
		} catch (NoCredit_Exception nce) {
			fail(nce.getMessage());
		} catch (UserNotExists_Exception unee) {
			fail(unee.getMessage());
		} catch (AlreadyHasBina_Exception ahbe) {
			fail(ahbe.getMessage());
		}
	}

	@Test(expected = NoBinaAvail_Exception.class)
	public void noBinasAvailable() throws NoBinaAvail_Exception {
		try {
			client.testInitStation(STATION1_ID, X, Y, 0, BONUS);
			client.rentBina(STATION1_ID, EMAIL);
		} catch (NoCredit_Exception nce) {
			fail(nce.getMessage());
		} catch (UserNotExists_Exception unee) {
			fail(unee.getMessage());
		} catch (AlreadyHasBina_Exception ahbe) {
			fail(ahbe.getMessage());
		} catch (InvalidStation_Exception ise) {
			fail(ise.getMessage());
		} catch (BadInit_Exception bie) {
			fail(bie.getMessage());
		}
	}

	@Test(expected = NoCredit_Exception.class)
	public void noCredit() throws NoCredit_Exception {		
		try {
			client.testInit(0);
			client.activateUser(EMAIL2);
			client.rentBina(STATION1_ID, EMAIL2);
		} catch (NoBinaAvail_Exception nba) {
			fail(nba.getMessage());
		} catch (UserNotExists_Exception unee) {
			fail(unee.getMessage());
		} catch (AlreadyHasBina_Exception ahbe) {
			fail(ahbe.getMessage());
		} catch (InvalidStation_Exception ise) {
			fail(ise.getMessage());
		} catch (BadInit_Exception bie) {
			fail(bie.getMessage());
		} catch (EmailExists_Exception eee) {
			fail(eee.getMessage());
		} catch (InvalidEmail_Exception iee) {
			fail(iee.getMessage());
		}
	}

	@Test(expected = UserNotExists_Exception.class)
	public void userNotExists() throws UserNotExists_Exception {
		try {
			client.rentBina(STATION1_ID, "invalid@email");
		} catch (NoBinaAvail_Exception nba) {
			fail(nba.getMessage());
		} catch (NoCredit_Exception nce) {
			fail(nce.getMessage());
		} catch (AlreadyHasBina_Exception ahbe) {
			fail(ahbe.getMessage());
		} catch (InvalidStation_Exception ise) {
			fail(ise.getMessage());
		}
	}
	@After
	public void tearDown() throws Exception {
		client.testClear();
	}
}
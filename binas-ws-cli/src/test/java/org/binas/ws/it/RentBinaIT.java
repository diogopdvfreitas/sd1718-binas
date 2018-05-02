package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.Internal_Exception;
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
	
	private int availableBinas1, availableBinas2, availableBinas3;
	private UserView user;

	@Before
	public void setUp() throws Exception {
		client.testInit(USER_INITIAL_POINTS);
		
		user = client.activateUser(EMAIL);
		
		client.testInitStation(STATION1_ID, X1, Y1, CAPACITY1, BONUS1);
		client.testInitStation(STATION2_ID, X2, Y2, CAPACITY2, BONUS2);
		client.testInitStation(STATION3_ID, X3, Y3, CAPACITY3, BONUS3);
		
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
	
	@Test
	public void alreadyHasBina() throws InvalidStation_Exception, NoBinaAvail_Exception,
		NoCredit_Exception, UserNotExists_Exception, Internal_Exception {
		int availableBinas1, availableBinas2, availableBinas3;
		
		try {
			client.rentBina(STATION1_ID, EMAIL);
			client.rentBina(STATION1_ID, EMAIL);
		} catch (AlreadyHasBina_Exception ahbe) {
			availableBinas1 = client.getInfoStation(STATION1_ID).getAvailableBinas();
			availableBinas2 = client.getInfoStation(STATION2_ID).getAvailableBinas();
			availableBinas3 = client.getInfoStation(STATION3_ID).getAvailableBinas();
			
			assertEquals(this.availableBinas1 - 1, availableBinas1);
			assertEquals(this.availableBinas2, availableBinas2);
			assertEquals(this.availableBinas3, availableBinas3);
			
			assertEquals(user.getCredit() - 1, client.getCredit(EMAIL));
		}
	}
	
	@Test
	public void invalidStation() throws AlreadyHasBina_Exception, NoBinaAvail_Exception,
		NoCredit_Exception, UserNotExists_Exception, InvalidStation_Exception, Internal_Exception {
		int availableBinas1, availableBinas2, availableBinas3;
		
		try {
			client.rentBina("example of invalid bina", EMAIL);
		} catch (InvalidStation_Exception ise) {
			availableBinas1 = client.getInfoStation(STATION1_ID).getAvailableBinas();
			availableBinas2 = client.getInfoStation(STATION2_ID).getAvailableBinas();
			availableBinas3 = client.getInfoStation(STATION3_ID).getAvailableBinas();
			
			assertEquals(this.availableBinas1, availableBinas1);
			assertEquals(this.availableBinas2, availableBinas2);
			assertEquals(this.availableBinas3, availableBinas3);
			
			assertEquals(user.getCredit().intValue(), client.getCredit(EMAIL));
		}
	}
	
	@Test
	public void nullStation() throws AlreadyHasBina_Exception, NoBinaAvail_Exception,
		NoCredit_Exception, UserNotExists_Exception, InvalidStation_Exception, Internal_Exception {
		int availableBinas1, availableBinas2, availableBinas3;
		
		try {
			client.rentBina(null, EMAIL);
		} catch (InvalidStation_Exception ise) {
			availableBinas1 = client.getInfoStation(STATION1_ID).getAvailableBinas();
			availableBinas2 = client.getInfoStation(STATION2_ID).getAvailableBinas();
			availableBinas3 = client.getInfoStation(STATION3_ID).getAvailableBinas();
			
			assertEquals(this.availableBinas1, availableBinas1);
			assertEquals(this.availableBinas2, availableBinas2);
			assertEquals(this.availableBinas3, availableBinas3);
			
			assertEquals(user.getCredit().intValue(), client.getCredit(EMAIL));
		}
	}

	@Test
	public void emptyStation() throws AlreadyHasBina_Exception, NoBinaAvail_Exception,
		NoCredit_Exception, UserNotExists_Exception, InvalidStation_Exception, Internal_Exception {
		int availableBinas1, availableBinas2, availableBinas3;
		
		try {
			client.rentBina("", EMAIL);
		} catch (InvalidStation_Exception ise) {
			availableBinas1 = client.getInfoStation(STATION1_ID).getAvailableBinas();
			availableBinas2 = client.getInfoStation(STATION2_ID).getAvailableBinas();
			availableBinas3 = client.getInfoStation(STATION3_ID).getAvailableBinas();
			
			assertEquals(this.availableBinas1, availableBinas1);
			assertEquals(this.availableBinas2, availableBinas2);
			assertEquals(this.availableBinas3, availableBinas3);
			
			assertEquals(user.getCredit().intValue(), client.getCredit(EMAIL));
		}
	}

	@Test
	public void noBinasAvailable() throws BadInit_Exception, AlreadyHasBina_Exception,
		InvalidStation_Exception, NoCredit_Exception, UserNotExists_Exception, Internal_Exception {
		int availableBinas1, availableBinas2, availableBinas3;
		
		try {
			client.testInitStation(STATION1_ID, X1, Y1, 0, BONUS1);
			client.rentBina(STATION1_ID, EMAIL);
		} catch (NoBinaAvail_Exception nce) {
			availableBinas1 = client.getInfoStation(STATION1_ID).getAvailableBinas();
			availableBinas2 = client.getInfoStation(STATION2_ID).getAvailableBinas();
			availableBinas3 = client.getInfoStation(STATION3_ID).getAvailableBinas();
			
			assertEquals(0, availableBinas1);
			assertEquals(this.availableBinas2, availableBinas2);
			assertEquals(this.availableBinas3, availableBinas3);
			
			assertEquals(user.getCredit().intValue(), client.getCredit(EMAIL));
		}
	}

	@Test
	public void noCredit() throws NoCredit_Exception, EmailExists_Exception,
		InvalidEmail_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
		NoBinaAvail_Exception, UserNotExists_Exception, BadInit_Exception, Internal_Exception {
		int availableBinas = 0, newAvailableBinas = 0;
		UserView newUser = null;
		
		try {
			client.testInit(0);
			newUser = client.activateUser(EMAIL2);
			availableBinas = client.getInfoStation(STATION1_ID).getAvailableBinas();
			client.rentBina(STATION1_ID, EMAIL2);
		} catch (NoCredit_Exception nba) {
			newAvailableBinas = client.getInfoStation(STATION1_ID).getAvailableBinas();
			
			assertEquals(availableBinas, newAvailableBinas);
			assertEquals(newUser.getCredit().intValue(), client.getCredit(EMAIL2));
		}
	}

	@Test(expected = UserNotExists_Exception.class)
	public void userNotExists() throws UserNotExists_Exception, Internal_Exception {
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
	
	@Test(expected = UserNotExists_Exception.class)
	public void nullUser() throws UserNotExists_Exception, Internal_Exception {
		try {
			client.rentBina(STATION1_ID, null);
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
	
	@Test(expected = UserNotExists_Exception.class)
	public void emptyUser() throws UserNotExists_Exception, Internal_Exception {
		try {
			client.rentBina(STATION1_ID, "");
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

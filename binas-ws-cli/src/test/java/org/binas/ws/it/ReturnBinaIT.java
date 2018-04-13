package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.FullStation_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReturnBinaIT extends BaseIT {
	
	private static final Integer USER_INITIAL_POINTS = 10;
	private static final String EMAIL = "e.1.mail@that.serv1ce.com";
	private static final String EMAIL2 = "e.2.mail@that.serv1ce.com";
	private static final String EMAIL3 = "e.3.mail@that.serv1ce.com";
	
	private int availableBinas1, availableBinas2, availableBinas3;
	private UserView userA, userB, userC;

	@Before
	public void setUp() throws Exception {
		client.testInit(USER_INITIAL_POINTS);
		
		userA = client.activateUser(EMAIL);
		userB = client.activateUser(EMAIL2);
		userC = client.activateUser(EMAIL3);
		
		client.testInitStation(STATION1_ID, X1, Y1, CAPACITY1, BONUS1);
		client.testInitStation(STATION2_ID, X2, Y2, CAPACITY2, BONUS2);
		client.testInitStation(STATION3_ID, X3, Y3, CAPACITY3, BONUS3);
		
		client.rentBina(STATION1_ID, EMAIL);
		client.rentBina(STATION2_ID, EMAIL2);
		
		availableBinas1 = client.getInfoStation(STATION1_ID).getAvailableBinas();
		availableBinas2 = client.getInfoStation(STATION2_ID).getAvailableBinas();
		availableBinas3 = client.getInfoStation(STATION3_ID).getAvailableBinas();
	}

	@Test
	public void success() {
		int availableBinas1, availableBinas2, availableBinas3;
		
		try {
			client.returnBina(STATION1_ID, EMAIL);
			
			availableBinas1 = client.getInfoStation(STATION1_ID).getAvailableBinas();
			availableBinas2 = client.getInfoStation(STATION2_ID).getAvailableBinas();
			availableBinas3 = client.getInfoStation(STATION3_ID).getAvailableBinas();
			
			assertEquals(this.availableBinas1 + 1, availableBinas1);
			assertEquals(this.availableBinas2, availableBinas2);
			assertEquals(this.availableBinas3, availableBinas3);
			
			assertEquals(userA.getCredit() - 1 + BONUS1, client.getCredit(EMAIL));
			assertEquals(userB.getCredit() - 1, client.getCredit(EMAIL2));
			assertEquals(userC.getCredit().intValue(), client.getCredit(EMAIL3));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void userNotExists() throws UserNotExists_Exception {
		try {
			client.returnBina(STATION1_ID, "invalid@email");
		} catch (FullStation_Exception fse) {
			fail(fse.getMessage());
		} catch (InvalidStation_Exception ise) {
			fail(ise.getMessage());
		}  catch (NoBinaRented_Exception nce) {
			fail(nce.getMessage());
		}
	}
	
	@Test(expected = InvalidStation_Exception.class)
	public void invalidStation() throws InvalidStation_Exception {
		try {
			client.returnBina("example of invalid bina", EMAIL);
		} catch (FullStation_Exception fse) {
			fail(fse.getMessage());
		} catch (UserNotExists_Exception unee) {
			fail(unee.getMessage());
		} catch (NoBinaRented_Exception nce) {
			fail(nce.getMessage());
		}
	}
	
	@Test(expected = FullStation_Exception.class)
	public void fullStation() throws FullStation_Exception {
		try {
			client.returnBina(STATION3_ID, EMAIL);
		} catch (InvalidStation_Exception ise) {
			fail(ise.getMessage());
		} catch (UserNotExists_Exception unee) {
			fail(unee.getMessage());
		} catch (NoBinaRented_Exception nce) {
			fail(nce.getMessage());
		}
	}
	
	@Test(expected = NoBinaRented_Exception.class)
	public void noBinaRented() throws NoBinaRented_Exception {
		try {
			client.returnBina(STATION1_ID, EMAIL3);
		} catch (InvalidStation_Exception ise) {
			fail(ise.getMessage());
		} catch (UserNotExists_Exception unee) {
			fail(unee.getMessage());
		} catch (FullStation_Exception fse) {
			fail(fse.getMessage());
		}
	}
	
	@After
	public void tearDown() throws Exception {
		client.testClear();
	}
}

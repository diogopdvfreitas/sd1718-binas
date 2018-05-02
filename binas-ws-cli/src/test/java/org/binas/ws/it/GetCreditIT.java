package org.binas.ws.it;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.Internal_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;

public class GetCreditIT extends BaseIT {

	private static final Integer USER_INITIAL_POINTS = 10;
	private static final String EMAIL = "e.2.mail@that.serv1ce.com";
	private static final String EMAIL1 = "e.1.mail@that.serv1ce.com";
	
	@Before
	public void setUp() throws Exception {
		client.testInit(USER_INITIAL_POINTS);
	}
	
	@Test
	public void sucess() throws Internal_Exception {
		int credit;
		
		try {
			UserView userView = client.activateUser(EMAIL);
			credit = client.getCredit(EMAIL);
			assertEquals(userView.getCredit().intValue(), credit);	
		} catch (UserNotExists_Exception uee) {
			fail("User does not exists");
		} catch (EmailExists_Exception eee) {
			fail("Invalid duplicated user error");
		} catch (InvalidEmail_Exception iee) {
			fail("Invalid email format error");
		}
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void userNotExists() throws UserNotExists_Exception, Internal_Exception {
		try {
			client.activateUser(EMAIL);
			client.getCredit(EMAIL1);
		} catch (EmailExists_Exception eee) {
			fail("Invalid duplicated user error");
		} catch (InvalidEmail_Exception iee) {
			fail("Invalid email format error");
		}
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void nullEmail() throws EmailExists_Exception, InvalidEmail_Exception, Internal_Exception {
		client.activateUser(null);
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmail() throws EmailExists_Exception, InvalidEmail_Exception, Internal_Exception {
		client.activateUser("");
	}
	
	@After
	public void tearDown() throws Exception {
		client.testClear();
	}
}

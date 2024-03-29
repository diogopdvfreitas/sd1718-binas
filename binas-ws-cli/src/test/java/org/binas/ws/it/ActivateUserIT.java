package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.EmailExists_Exception;
import org.binas.ws.Internal_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActivateUserIT extends BaseIT {
	
	private static final Integer USER_INITIAL_POINTS = 10;
	private static final String EMAIL = "e.2.mail@that.serv1ce.com";
	
	private static final String[] invalidEmailExamples = {
			"", ".", "@", "this", "1", "this-@gmail.com", "@service", "e.2.mail@", "this@ser-ice"
	};

	@Before
	public void setUp() throws Exception {
		client.testInit(USER_INITIAL_POINTS);
	}

	@Test
	public void success() throws Internal_Exception {
		try {
			UserView userView = client.activateUser(EMAIL);
			assertEquals(userView.getEmail(), EMAIL);
			assertEquals(userView.getCredit(), USER_INITIAL_POINTS);
			assertFalse(userView.isHasBina());
		} catch (EmailExists_Exception eee) {
			fail("Invalid duplicated user error");
		} catch (InvalidEmail_Exception iee) {
			fail("Invalid email format error");
		}
	}
	
	@Test(expected = EmailExists_Exception.class)
	public void emailExists() throws EmailExists_Exception, Internal_Exception {
		try {
			client.activateUser(EMAIL);
			client.activateUser(EMAIL);			
		} catch (InvalidEmail_Exception iee) {
			fail("Invalid email format error");
		}
	}
	
	@Test
	public void invalidEmailFormat() throws Internal_Exception {
		int invalidEmails = 0;
		
		for (String email : invalidEmailExamples) {
			try {
				client.activateUser(email);
			} catch (EmailExists_Exception eee) {
				fail("Invalid duplicated user error");
			} catch (InvalidEmail_Exception iee) {
				// record it and proceed
				invalidEmails++;
			}
		}
		
		assertEquals(invalidEmails, invalidEmailExamples.length);
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void nullEmail() throws EmailExists_Exception, InvalidEmail_Exception, Internal_Exception {
		client.activateUser(null);
	}

	@After
	public void tearDown() throws Exception {
		client.testClear();
	}
}

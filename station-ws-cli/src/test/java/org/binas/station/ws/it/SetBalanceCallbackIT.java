package org.binas.station.ws.it;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.InvalidEmail_Exception;
import org.binas.station.ws.InvalidUserReplic_Exception;
import org.binas.station.ws.SetBalanceResponse;
import org.binas.station.ws.TagView;
import org.binas.station.ws.UserNotExists_Exception;
import org.binas.station.ws.UserReplicView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class SetBalanceCallbackIT extends BaseIT {
	
	private static final int X = 10;
	private static final int Y = 20;
	private static final int CAPACITY = 5;
	private static final int RETURNPRIZE = 1;
	
	private static UserReplicView user;
	private static TagView tag;
	
	private static final String EMAIL1 = "example1@gmail.com";
	private static final String EMAIL2 = "example2@gmail.com";
	
	private static final long SEQ = 1;
	private static final int VALUE = 0;
	
	static Response<SetBalanceResponse> setBalanceResponse;
	
	private static final String[] invalidEmailExamples = {
			"", ".", "@", "this", "1", "this-@gmail.com", "@service", "e.2.mail@", "this@ser-ice"
	};
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(X, Y, CAPACITY, RETURNPRIZE);
		
		setBalanceResponse = null;

		tag = new TagView();
		tag.setSeq(SEQ);
		
		user = new UserReplicView();
		user.setTag(tag);
		user.setValue(VALUE);
	}
	
	private AsyncHandler<SetBalanceResponse> handler() {
		return new AsyncHandler<SetBalanceResponse>() {
			@Override
			public void handleResponse(Response<SetBalanceResponse> res) {
				setBalanceResponse = res;
			}
		}; 
	}
	
	@Test
	public void successWithNoUser() throws UserNotExists_Exception, InvalidEmail_Exception, InvalidUserReplic_Exception, InterruptedException, ExecutionException {
		UserReplicView newUser;
		
		client.setBalanceAsync(EMAIL1, user, this.handler());
		
		while(setBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		setBalanceResponse.get();
		client.setBalance(EMAIL1, user);
		
		newUser = client.getBalance(EMAIL1);
		assertEquals(SEQ, newUser.getTag().getSeq());
		assertEquals(VALUE, newUser.getValue());
	}
	
	@Test
	public void successUpdateUser() throws InterruptedException, ExecutionException, UserNotExists_Exception {
		UserReplicView newUser;
		TagView newTag = new TagView();
		
		newTag.setSeq(SEQ + 1);
		
		client.setBalanceAsync(EMAIL1, user, this.handler());
		
		while(setBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		setBalanceResponse.get();
		setBalanceResponse = null;
		
		user.setTag(newTag);
		user.setValue(VALUE + 1);
		
		client.setBalanceAsync(EMAIL1, user, this.handler());
		
		while(setBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		setBalanceResponse.get();
		
		newUser = client.getBalance(EMAIL1);
		
		assertEquals(SEQ + 1, newUser.getTag().getSeq());
		assertEquals(VALUE + 1, newUser.getValue());
	}
	
	@Test
	public void successMultipleUsers() throws InterruptedException, ExecutionException, UserNotExists_Exception {
		UserReplicView newUser1, newUser2;
		TagView newTag = new TagView();
		
		newTag.setSeq(SEQ + 2);
		
		UserReplicView otherUser = new UserReplicView();
		
		otherUser.setTag(newTag);
		otherUser.setValue(VALUE + 2);
		
		client.setBalanceAsync(EMAIL1, user, this.handler());
		
		while(setBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		setBalanceResponse.get();
		setBalanceResponse = null;
		
		client.setBalanceAsync(EMAIL2, otherUser, this.handler());
		
		while(setBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		setBalanceResponse.get();
		
		newUser1 = client.getBalance(EMAIL1);
		newUser2 = client.getBalance(EMAIL2);
		
		assertEquals(SEQ, newUser1.getTag().getSeq());
		assertEquals(VALUE, newUser1.getValue());
		
		assertEquals(SEQ + 2, newUser2.getTag().getSeq());
		assertEquals(VALUE + 2, newUser2.getValue());
	}
	
	@Test
	public void nullEmail() throws InterruptedException {
		client.setBalanceAsync(null, user, this.handler());
		
		while(setBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		try {
			setBalanceResponse.get();
			fail("Should get an InvalidEMail_Exception, and did not");
		} catch (ExecutionException e) {
			assertEquals(InvalidEmail_Exception.class.getName(), e.getCause().getClass().getName());
		}
	}
	
	@Test
	public void emptyEmail() throws InterruptedException {
		client.setBalanceAsync("", user, this.handler());
		
		while(setBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		try {
			setBalanceResponse.get();
			fail("Should get an InvalidEMail_Exception, and did not");
		} catch (ExecutionException e) {
			assertEquals(InvalidEmail_Exception.class.getName(), e.getCause().getClass().getName());
		}
	}
	
	@Test
	public void invalidEmailFormat() throws InterruptedException {
		int invalidEmails = 0;
		
		for (String email : invalidEmailExamples) {
			client.setBalanceAsync(email, user, this.handler());
			
			while(setBalanceResponse == null) {
				Thread.sleep(100);
			}
			
			try {
				setBalanceResponse.get();
				fail("Should get an InvalidEMail_Exception, and did not");
			} catch (ExecutionException e) {
				assertEquals(InvalidEmail_Exception.class.getName(), e.getCause().getClass().getName());

				// record it and proceed
				invalidEmails++;
				
				setBalanceResponse = null;
			}
		}
		
		assertEquals(invalidEmails, invalidEmailExamples.length);
	}
	
	@Test
	public void invalidUserReplic() throws InterruptedException {
		client.setBalanceAsync(EMAIL1, null, this.handler());
		
		while(setBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		try {
			setBalanceResponse.get();
			fail("Should get an InvalidEMail_Exception, and did not");
		} catch (ExecutionException e) {
			assertEquals(InvalidUserReplic_Exception.class.getName(), e.getCause().getClass().getName());
		}
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}

}

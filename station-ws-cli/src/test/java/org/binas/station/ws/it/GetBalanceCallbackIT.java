package org.binas.station.ws.it;

import static org.junit.Assert.*;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.InvalidEmail_Exception;
import org.binas.station.ws.InvalidUserReplic_Exception;
import org.binas.station.ws.TagView;
import org.binas.station.ws.UserNotExists_Exception;
import org.binas.station.ws.UserReplicView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class GetBalanceCallbackIT extends BaseIT {
	
	private static final int X = 10;
	private static final int Y = 20;
	private static final int CAPACITY = 5;
	private static final int RETURNPRIZE = 1;
	
	private static UserReplicView user;
	private static TagView tag;
	
	private static final String EMAIL1 = "example1@gmail.com";
	
	private static final long SEQ = 1;
	private static final int VALUE = 0;
	
	static Response<GetBalanceResponse> getBalanceResponse;
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(X, Y, CAPACITY, RETURNPRIZE);
		
		getBalanceResponse = null;
		
		tag = new TagView();
		tag.setSeq(SEQ);
		
		user = new UserReplicView();
		user.setTag(tag);
		user.setValue(VALUE);
	}
	
	private AsyncHandler<GetBalanceResponse> handler() {
		return new AsyncHandler<GetBalanceResponse>() {
			@Override
			public void handleResponse(Response<GetBalanceResponse> res) {
				getBalanceResponse = res;
			}
		};
	}
	
	@Test
	public void noUsers() throws InterruptedException, CancellationException {
		client.getBalanceAsync(EMAIL1, this.handler());
		
		while(getBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		try {
			getBalanceResponse.get().getUserReplic();
			fail("Should get a execution exception");
		} catch (ExecutionException ee) {
			assertEquals(UserNotExists_Exception.class.getName(), ee.getCause().getClass().getName());
		}
	}
	
	@Test
	public void getBalance() throws InterruptedException, CancellationException, InvalidEmail_Exception, InvalidUserReplic_Exception, ExecutionException {
		UserReplicView newUser;
		
		client.setBalance(EMAIL1, user);
		client.getBalanceAsync(EMAIL1, this.handler());
		
		while(getBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		newUser = getBalanceResponse.get().getUserReplic();
		
		assertEquals(SEQ, newUser.getTag().getSeq());
		assertEquals(VALUE, newUser.getValue());
	}
	
	@Test
	public void userNotExists() throws InterruptedException, CancellationException {
		client.getBalanceAsync(EMAIL1, this.handler());
		
		while(getBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		try {
			getBalanceResponse.get();
			fail("Should get a UserNotExists_Exception");
		} catch (ExecutionException e) {
			assertEquals(UserNotExists_Exception.class.getName(), e.getCause().getClass().getName());
		}
	}
	
	@Test
	public void nullEmail() throws InterruptedException, CancellationException {
		client.getBalanceAsync(null, this.handler());
		
		while(getBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		try {
			getBalanceResponse.get();
			fail("Should get a UserNotExists_Exception");
		} catch (ExecutionException e) {
			assertEquals(UserNotExists_Exception.class.getName(), e.getCause().getClass().getName());
		}
	}
	
	@Test
	public void emptyEmail() throws InterruptedException, CancellationException {
		client.getBalanceAsync("", this.handler());
		
		while(getBalanceResponse == null) {
			Thread.sleep(100);
		}
		
		try {
			getBalanceResponse.get();
			fail("Should get a UserNotExists_Exception");
		} catch (ExecutionException e) {
			assertEquals(UserNotExists_Exception.class.getName(), e.getCause().getClass().getName());
		}
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}

}

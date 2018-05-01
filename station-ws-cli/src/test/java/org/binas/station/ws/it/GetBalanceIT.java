package org.binas.station.ws.it;

import static org.junit.Assert.*;

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
public class GetBalanceIT extends BaseIT {
	
	private static final int X = 10;
	private static final int Y = 20;
	private static final int CAPACITY = 5;
	private static final int RETURNPRIZE = 1;
	
	private static UserReplicView user;
	private static TagView tag;
	
	private static final String EMAIL1 = "example1@gmail.com";
	
	private static final long SEQ = 1;
	private static final int VALUE = 0;
	
	static UserReplicView userReplicAsync = null;
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(X, Y, CAPACITY, RETURNPRIZE);
		
		tag = new TagView();
		tag.setSeq(SEQ);
		
		user = new UserReplicView();
		user.setTag(tag);
		user.setValue(VALUE);
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void noUsers() throws UserNotExists_Exception {
		client.getBalance(EMAIL1);
	}
	
	@Test
	public void getBalance() throws UserNotExists_Exception, InvalidEmail_Exception, InvalidUserReplic_Exception {
		client.setBalance(EMAIL1, user);
		UserReplicView newUser = client.getBalance(EMAIL1);
		
		assertEquals(SEQ, newUser.getTag().getSeq());
		assertEquals(VALUE, newUser.getValue());
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void userNotExists() throws UserNotExists_Exception {
		client.getBalance(EMAIL1);
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void nullEmail() throws UserNotExists_Exception {
		client.getBalance(null);
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void emptyEmail() throws UserNotExists_Exception {
		client.getBalance("");
	}
	
	@Test
	public void getBalanceAsyncCallback() throws UserNotExists_Exception, InvalidEmail_Exception, InvalidUserReplic_Exception, InterruptedException {
		client.setBalance(EMAIL1, user);
		client.getBalanceAsync(EMAIL1, new AsyncHandler<GetBalanceResponse>() {
			@Override
			public void handleResponse(Response<GetBalanceResponse> res) {
				try {
					userReplicAsync = res.get().getUserReplic();
				} catch (InterruptedException | ExecutionException e) {
					fail("Error on the getBalance async callback");
				}
			}
		});
		
		while(userReplicAsync == null) {
			Thread.sleep(100);
			assertEquals(SEQ, userReplicAsync.getTag().getSeq());
			assertEquals(VALUE, userReplicAsync.getValue());
		}
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}

}

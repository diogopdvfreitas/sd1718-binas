package org.binas.station.ws.it;

import static org.junit.Assert.*;

import org.binas.station.ws.BadInit_Exception;
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
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(X, Y, CAPACITY, RETURNPRIZE);
		
		tag = new TagView();
		tag.setSeq(SEQ);
		
		user = new UserReplicView();
		user.setEmail(EMAIL1);
		user.setTag(tag);
		user.setValue(VALUE);
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void noUsers() throws UserNotExists_Exception {
		client.getBalance(EMAIL1);
	}
	
	@Test
	public void getBalance() throws UserNotExists_Exception {
		client.setBalance(EMAIL1, user);
		UserReplicView newUser = client.getBalance(EMAIL1);
		
		assertEquals(EMAIL1, newUser.getEmail());
		assertEquals(SEQ, newUser.getTag().getSeq());
		assertEquals(VALUE, newUser.getValue());
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}

}

package org.binas.station.ws.it;

import static org.junit.Assert.*;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.TagView;
import org.binas.station.ws.UserReplicView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class SetBalanceIT extends BaseIT {
	
	private static final int X = 10;
	private static final int Y = 20;
	private static final int CAPACITY = 5;
	private static final int RETURNPRIZE = 1;
	
	private static UserReplicView user;
	private static TagView tag;
	
	private static final String EMAIL1 = "example1@gmail.com";
	private static final String EMAIL2 = "example2@gmail.com";
	
	private static final int SEQ = 1;
	private static final int CLIENT_ID = 100;
	
	private static final int VALUE = 0;
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(X, Y, CAPACITY, RETURNPRIZE);

		tag = new TagView();
		tag.setSeq(SEQ);
		tag.setClientID(CLIENT_ID);
		
		user = new UserReplicView();
		user.setEmail(EMAIL1);
		user.setTag(tag);
		user.setValue(VALUE);
	}
	
	@Test
	public void successWithNoUser() {
		client.setBalance(EMAIL1, user);
		
		UserReplicView newUser = client.getBalance(EMAIL1);
		assertEquals(EMAIL1, newUser.getEmail());
		assertEquals(SEQ, newUser.getTag().getSeq());
		assertEquals(CLIENT_ID, newUser.getTag().getClientID());
		assertEquals(VALUE, newUser.getValue());
	}
	
	@Test
	public void successUpdateUser() {
		TagView newTag = new TagView();
		
		newTag.setSeq(SEQ + 1);
		newTag.setClientID(CLIENT_ID);
		
		client.setBalance(EMAIL1, user);
		
		user.setTag(newTag);
		user.setValue(VALUE + 1);
		
		client.setBalance(EMAIL1, user);
		
		UserReplicView newUser = client.getBalance(EMAIL1);
		
		assertEquals(EMAIL1, newUser.getEmail());
		assertEquals(SEQ + 1, newUser.getTag().getSeq());
		assertEquals(CLIENT_ID, newUser.getTag().getClientID());
		assertEquals(VALUE + 1, newUser.getValue());
	}
	
	@Test
	public void successMultipleUsers() {
		TagView newTag = new TagView();
		
		newTag.setSeq(SEQ + 2);
		newTag.setClientID(CLIENT_ID);
		
		UserReplicView otherUser = new UserReplicView();
		
		otherUser.setEmail(EMAIL2);
		otherUser.setTag(newTag);
		otherUser.setValue(VALUE + 2);
		
		client.setBalance(EMAIL1, user);
		client.setBalance(EMAIL2, otherUser);
		
		UserReplicView newUser1 = client.getBalance(EMAIL1);
		UserReplicView newUser2 = client.getBalance(EMAIL2);
		
		assertEquals(EMAIL1, newUser1.getEmail());
		assertEquals(SEQ, newUser1.getTag().getSeq());
		assertEquals(CLIENT_ID, newUser1.getTag().getClientID());
		assertEquals(VALUE, newUser1.getValue());
		
		assertEquals(EMAIL2, newUser2.getEmail());
		assertEquals(SEQ + 2, newUser2.getTag().getSeq());
		assertEquals(CLIENT_ID, newUser2.getTag().getClientID());
		assertEquals(VALUE + 2, newUser2.getValue());
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}

}

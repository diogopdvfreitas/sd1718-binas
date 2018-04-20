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
public class SetBalanceIT extends BaseIT {
	
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
	
	@Test
	public void successWithNoUser() throws UserNotExists_Exception {
		client.setBalance(EMAIL1, user);
		
		UserReplicView newUser = client.getBalance(EMAIL1);
		assertEquals(EMAIL1, newUser.getEmail());
		assertEquals(SEQ, newUser.getTag().getSeq());
		assertEquals(VALUE, newUser.getValue());
	}
	
	@Test
	public void successUpdateUser() throws UserNotExists_Exception {
		TagView newTag = new TagView();
		
		newTag.setSeq(SEQ + 1);
		
		client.setBalance(EMAIL1, user);
		
		user.setTag(newTag);
		user.setValue(VALUE + 1);
		
		client.setBalance(EMAIL1, user);
		
		UserReplicView newUser = client.getBalance(EMAIL1);
		
		assertEquals(EMAIL1, newUser.getEmail());
		assertEquals(SEQ + 1, newUser.getTag().getSeq());
		assertEquals(VALUE + 1, newUser.getValue());
	}
	
	@Test
	public void successMultipleUsers() throws UserNotExists_Exception {
		TagView newTag = new TagView();
		
		newTag.setSeq(SEQ + 2);
		
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
		assertEquals(VALUE, newUser1.getValue());
		
		assertEquals(EMAIL2, newUser2.getEmail());
		assertEquals(SEQ + 2, newUser2.getTag().getSeq());
		assertEquals(VALUE + 2, newUser2.getValue());
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}

}

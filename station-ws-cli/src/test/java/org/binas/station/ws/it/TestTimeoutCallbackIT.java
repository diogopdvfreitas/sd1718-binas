package org.binas.station.ws.it;

import static org.junit.Assert.*;

import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.TestTimeoutResponse;
import org.binas.station.ws.cli.StationClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class TestTimeoutCallbackIT extends BaseIT {
	
	private static final int X = 10;
	private static final int Y = 20;
	private static final int CAPACITY = 5;
	private static final int RETURNPRIZE = 1;
	
	static Response<TestTimeoutResponse> testTimeoutResponse;
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(X, Y, CAPACITY, RETURNPRIZE);
		
		testTimeoutResponse = null;
	}
	
	private AsyncHandler<TestTimeoutResponse> handler() {
		return new AsyncHandler<TestTimeoutResponse>() {
			@Override
			public void handleResponse(Response<TestTimeoutResponse> res) {
				testTimeoutResponse = res;
			}
		};
	}
	
	@Test
	public void success() throws InterruptedException {
		client.testTimeoutAsync(StationClient.getReceiveTimeout() * 2, this.handler());
		
		while(testTimeoutResponse == null) {
			Thread.sleep(100);
		}
		
		try {
			testTimeoutResponse.get();
			fail("Should get a timeout exception");
		} catch (ExecutionException ee) {
			assertTrue(ee.getCause() instanceof WebServiceException);
			Throwable t = ee.getCause();
			assertTrue(t.getCause() instanceof SocketTimeoutException);
		}
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}

}

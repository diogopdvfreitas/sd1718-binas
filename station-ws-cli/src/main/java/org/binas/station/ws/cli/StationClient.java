package org.binas.station.ws.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.GetBinaResponse;
import org.binas.station.ws.GetInfoResponse;
import org.binas.station.ws.InvalidEmail_Exception;
import org.binas.station.ws.InvalidUserReplic_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.ReturnBinaResponse;
import org.binas.station.ws.SetBalanceResponse;
import org.binas.station.ws.StationPortType;
import org.binas.station.ws.StationService;
import org.binas.station.ws.StationView;
import org.binas.station.ws.TestClearResponse;
import org.binas.station.ws.TestInitResponse;
import org.binas.station.ws.TestPingResponse;
import org.binas.station.ws.TestTimeoutResponse;
import org.binas.station.ws.TimeoutInterruption_Exception;
import org.binas.station.ws.UserNotExists_Exception;
import org.binas.station.ws.UserReplicView;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

/**
 * Client port wrapper.
 *
 * Adds easier end point address configuration to the Port generated by
 * wsimport.
 */
public class StationClient implements StationPortType {

	/** WS service */
	StationService service = null;

	/** WS port (port type is the interface, port is the implementation) */
	StationPortType port = null;

	/** UDDI server URL */
	private String uddiURL = null;
	
	/** WS name */
	private String wsName = null;

	/** WS end point address */
	private String wsURL = null; // default value is defined inside WSDL
	
	static final int CONN_TIMEOUT = 1000;
	static final int RECV_TIMEOUT = 2000;

	public String getWsURL() {
		return wsURL;
	}

	/** output option **/
	private boolean verbose = false;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public static int getConnectionTimeout() { return CONN_TIMEOUT; }
	public static int getReceiveTimeout() { return RECV_TIMEOUT; }

	/** constructor with provided web service URL */
	public StationClient(String wsURL) throws StationClientException {
		this.wsURL = wsURL;
		createStub();
	}

	/** constructor with provided UDDI location and name */
	public StationClient(String uddiURL, String wsName) throws StationClientException, UDDINamingException {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		
		uddiLookup();
		createStub();			
	}

	/** UDDI lookup */
	private void uddiLookup() throws StationClientException, UDDINamingException {
		UDDINaming uddiNaming = new UDDINaming(uddiURL);

		System.out.printf("Looking for '%s'%n", wsName);
		this.wsURL = uddiNaming.lookup(wsName);
		
		if( this.wsURL == null) {
			System.out.printf("Service '%s' not found in UDDI%n", this.uddiURL, this.wsName);
			throw new StationClientException();
		}
	}


	/** Stub creation and configuration */
	private void createStub() {
		if (verbose)
			System.out.println("Creating stub ...");
		service = new StationService();
		port = service.getStationPort();
		
		if (wsURL != null) {
			if (verbose)
				System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsURL);
			System.out.printf("Found service URL: %s%n", wsURL);
			
			final List<String> CONN_TIME_PROPS = new ArrayList<String>();
            
            CONN_TIME_PROPS.add("com.sun.xml.ws.connect.timeout");
            CONN_TIME_PROPS.add("com.sun.xml.internal.ws.connect.timeout");
            CONN_TIME_PROPS.add("javax.xml.ws.client.connectionTimeout");
            
            for (String propName : CONN_TIME_PROPS)
                requestContext.put(propName, CONN_TIMEOUT);

            final List<String> RECV_TIME_PROPS = new ArrayList<String>();
            RECV_TIME_PROPS.add("com.sun.xml.ws.request.timeout");
            RECV_TIME_PROPS.add("com.sun.xml.internal.ws.request.timeout");
            RECV_TIME_PROPS.add("javax.xml.ws.client.receiveTimeout");

            for (String propName : RECV_TIME_PROPS)
                requestContext.put(propName, RECV_TIMEOUT);
		}
	}

	// remote invocation methods ----------------------------------------------

	@Override
	public UserReplicView getBalance(String email) throws UserNotExists_Exception {
		return port.getBalance(email);
	}
	
	@Override
	public Response<GetBalanceResponse> getBalanceAsync(String email) {
		return port.getBalanceAsync(email);
	}

	@Override
	public void setBalance(String email, UserReplicView userReplic) throws InvalidEmail_Exception, InvalidUserReplic_Exception {
		port.setBalance(email, userReplic);
	}
	
	@Override
	public Response<SetBalanceResponse> setBalanceAsync(String email, UserReplicView userReplic) {
		return port.setBalanceAsync(email, userReplic);
	}

	@Override
	public Future<?> setBalanceAsync(String email, UserReplicView userReplic,
			AsyncHandler<SetBalanceResponse> asyncHandler) {
		return port.setBalanceAsync(email, userReplic, asyncHandler);
	}
	
	@Override
	public Future<?> getBalanceAsync(String email, AsyncHandler<GetBalanceResponse> asyncHandler) {
		return port.getBalanceAsync(email, asyncHandler);
	}
	
	@Override
	public StationView getInfo() {
		return port.getInfo();
	}

	@Override
	public Response<GetInfoResponse> getInfoAsync() {
		return port.getInfoAsync();
	}
	
	@Override
	public Future<?> getInfoAsync(AsyncHandler<GetInfoResponse> asyncHandler) {
		return port.getInfoAsync(asyncHandler);
	}
	
	@Override
	public void getBina() throws NoBinaAvail_Exception {
		port.getBina();
	}
	
	@Override
	public Response<GetBinaResponse> getBinaAsync() {
		return port.getBinaAsync();
	}

	@Override
	public Future<?> getBinaAsync(AsyncHandler<GetBinaResponse> asyncHandler) {
		return port.getBinaAsync(asyncHandler);
	}

	@Override
		public int returnBina() throws NoSlotAvail_Exception {
		return port.returnBina();
	}
	
	@Override
	public Response<ReturnBinaResponse> returnBinaAsync() {
		return port.returnBinaAsync();
	}
	
	@Override
	public Future<?> returnBinaAsync(AsyncHandler<ReturnBinaResponse> asyncHandler) {
		return port.returnBinaAsync(asyncHandler);
	}

	// test control operations ------------------------------------------------

	@Override
	public String testPing(String inputMessage) {
		return port.testPing(inputMessage);
	}
	
	@Override
	public Response<TestPingResponse> testPingAsync(String inputMessage) {
		return port.testPingAsync(inputMessage);
	}
	
	@Override
	public Future<?> testPingAsync(String inputMessage, AsyncHandler<TestPingResponse> asyncHandler) {
		return port.testPingAsync(inputMessage, asyncHandler);
	}
	
	@Override
	public void testTimeout(Integer timeToWait) throws TimeoutInterruption_Exception {
		port.testTimeout(timeToWait);
	}
	
	@Override
	public Response<TestTimeoutResponse> testTimeoutAsync(Integer timeToWait) {
		return port.testTimeoutAsync(timeToWait);
	}

	@Override
	public Future<?> testTimeoutAsync(Integer timeToWait, AsyncHandler<TestTimeoutResponse> asyncHandler) {
		return port.testTimeoutAsync(timeToWait, asyncHandler);
	}
	
	@Override
	public void testClear() {
		port.testClear();
	}
	
	@Override
	public Response<TestClearResponse> testClearAsync() {
		return port.testClearAsync();
	}
	
	@Override
	public Future<?> testClearAsync(AsyncHandler<TestClearResponse> asyncHandler) {
		return port.testClearAsync(asyncHandler);
	}
	
	@Override
	public void testInit(int x, int y, int capacity, int returnPrize) throws
		BadInit_Exception {
		port.testInit(x, y, capacity, returnPrize);
	}
	
	@Override
	public Response<TestInitResponse> testInitAsync(int x, int y, int capacity, int returnPrize) {
		return port.testInitAsync(x, y, capacity, returnPrize);
	}

	@Override
	public Future<?> testInitAsync(int x, int y, int capacity, int returnPrize,
			AsyncHandler<TestInitResponse> asyncHandler) {
		return port.testInitAsync(x, y, capacity, returnPrize, asyncHandler);
	}

}

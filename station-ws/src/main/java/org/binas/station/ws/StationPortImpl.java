package org.binas.station.ws;

import javax.jws.WebService;

import org.binas.station.domain.Coordinates;
import org.binas.station.domain.Station;
import org.binas.station.domain.Tag;
import org.binas.station.domain.UserReplic;
import org.binas.station.domain.exception.BadInitException;
import org.binas.station.domain.exception.InvalidEmailException;
import org.binas.station.domain.exception.NoBinaAvailException;
import org.binas.station.domain.exception.NoSlotAvailException;
import org.binas.station.domain.exception.UserNotExistsException;
import org.binas.station.domain.exception.InvalidUserReplicException;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(
	endpointInterface = "org.binas.station.ws.StationPortType",
	wsdlLocation = "station.2_0.wsdl",
	name = "StationWebService",
	portName = "StationPort",
	targetNamespace = "http://ws.station.binas.org/",
	serviceName = "StationService"
)
public class StationPortImpl implements StationPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private StationEndpointManager endpointManager;
	private Station station;

	/** Constructor receives a reference to the endpoint manager. */
	public StationPortImpl(StationEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		this.station = Station.getInstance();
	}

	// Main operations -------------------------------------------------------
	
	@Override
	public UserReplicView getBalance(String email) throws UserNotExists_Exception {
		UserReplicView userReplic = null;
		try {
			return buildUserReplicView(station.getBalance(email));	
		} catch (UserNotExistsException unee) {
			throwUserNotExists("User is not registered in this station");
		}
		return userReplic;
	}

	@Override
	public void setBalance(String email, UserReplicView userReplic) throws InvalidEmail_Exception, InvalidUserReplic_Exception {
		try {
			station.setBalance(email, userReplic);
		} catch (InvalidEmailException iee) {
			throwInvalidEmail("Invalid email");
		} catch (InvalidUserReplicException iure) {
			throwInvalidUserReplic("Invalid user replic");
		}
	}

	/** Retrieve information about station. */
	@Override
	public StationView getInfo() {
		return buildStationView(station);
	}

	/** Return a bike to the station. */
	@Override
	public int returnBina() throws NoSlotAvail_Exception {
		int bonus = 0;
		try {
			bonus = station.returnBina();
		} catch (NoSlotAvailException nsae) {
			throwNoSlotAvail("No slots available");
		}
		return bonus;
	}

	/** Take a bike from the station. */
	@Override
	public void getBina() throws NoBinaAvail_Exception {
		try {
			station.getBina();
		} catch (NoBinaAvailException nbae) {
			throwNoBinaAvail("No Binas available");
		}
	}

	// Test Control operations -----------------------------------------------

	/** Diagnostic operation to check if service is running. */
	@Override
	public String testPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// If the station does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Station";

		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}
	
	@Override
	public void testTimeout(Integer timeToWait) throws TimeoutInterruption_Exception {
		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException e) {
			throwTimeoutInterruption(e.getMessage());
		}
	}

	/** Return all station variables to default values. */
	@Override
	public void testClear() {
		Station.getInstance().reset();
	}

	//
	/** Set station variables with specific values. */
	@Override
	public void testInit(int x, int y, int capacity, int returnPrize) throws BadInit_Exception {
		try {
			Station.getInstance().init(x, y, capacity, returnPrize);
		} catch (BadInitException e) {
			throwBadInit("Invalid initialization values!");
		}
	}

	// View helpers----------------------------------------------------------
	
	private UserReplicView buildUserReplicView(UserReplic userReplic) {
		if (userReplic == null) return null;
		
		UserReplicView view = new UserReplicView();
		TagView tagView = new TagView();
		
		synchronized (userReplic) {
			Tag tag = userReplic.getTag();
			tagView.setSeq(tag.getSeq());
			
			view.setValue(userReplic.getValue());
			view.setTag(tagView);
		}
		
		return view;
	}

	/** Helper to convert a domain station to a view. */
	private StationView buildStationView(Station station) {
		StationView view = new StationView();
		
		synchronized (station) {
			view.setId(station.getId());
			view.setCoordinate(buildCoordinatesView(station.getCoordinates()));
			view.setCapacity(station.getMaxCapacity());
			view.setTotalGets(station.getTotalGets());
			view.setTotalReturns(station.getTotalReturns());
			view.setFreeDocks(station.getFreeDocks());
			view.setAvailableBinas(station.getAvailableBinas());			
		}
		
		return view;
	}

	//
	/** Helper to convert a domain coordinates to a view. */
	private CoordinatesView buildCoordinatesView(Coordinates coordinates) {
		CoordinatesView view = new CoordinatesView();
		view.setX(coordinates.getX());
		view.setY(coordinates.getY());
		return view;
	}

	// Exception helpers-----------------------------------------------------

	/** Helper to throw a new NoBinaAvail exception. */
	private void throwNoBinaAvail(final String message) throws NoBinaAvail_Exception {
		NoBinaAvail faultInfo = new NoBinaAvail();
		faultInfo.message = message;
		throw new NoBinaAvail_Exception(message, faultInfo);
	}

	//
	/** Helper to throw a new NoSlotAvail exception. */
	private void throwNoSlotAvail(final String message) throws NoSlotAvail_Exception {
		NoSlotAvail faultInfo = new NoSlotAvail();
		faultInfo.message = message;
		throw new NoSlotAvail_Exception(message, faultInfo);
	}

	//
	/** Helper to throw a new BadInit exception. */
	private void throwBadInit(final String message) throws BadInit_Exception {
		BadInit faultInfo = new BadInit();
		faultInfo.message = message;
		throw new BadInit_Exception(message, faultInfo);
	}

	//
	/** Helper to throw a new UserNotExists exception. */
	private void throwUserNotExists(final String message) throws UserNotExists_Exception {
		UserNotExists faultInfo = new UserNotExists();
		faultInfo.message = message;
		throw new UserNotExists_Exception(message, faultInfo);
	}
	
	//
	/** Helper to throw a new InvalidEmail exception. */
	private void throwInvalidEmail(final String message) throws InvalidEmail_Exception {
		InvalidEmail faultInfo = new InvalidEmail();
		faultInfo.message = message;
		throw new InvalidEmail_Exception(message, faultInfo);
	}

	//
	/** Helper to throw a new InvalidEmail exception. */
	private void throwInvalidUserReplic(final String message) throws InvalidUserReplic_Exception {
		InvalidUserReplic faultInfo = new InvalidUserReplic();
		faultInfo.message = message;
		throw new InvalidUserReplic_Exception(message, faultInfo);
	}

	//
	/** Helper to throw a new InvalidEmail exception. */
	private void throwTimeoutInterruption(final String message) throws TimeoutInterruption_Exception {
		TimeoutInterruption faultInfo = new TimeoutInterruption();
		faultInfo.message = message;
		throw new TimeoutInterruption_Exception(message, faultInfo);
	}

}

package org.binas.ws;

import java.util.List;

import javax.jws.WebService;

import org.binas.domain.BinasManager;
import org.binas.domain.User;
import org.binas.exception.BadInitException;
import org.binas.exception.EmailExistsException;
import org.binas.exception.InvalidEmailException;
import org.binas.ws.BinasPortType;

@WebService(
		endpointInterface = "org.binas.ws.BinasPortType",
		wsdlLocation = "binas.1_0.wsdl",
		name = "BinasWebService",
		portName = "BinasPort",
		targetNamespace = "http://ws.binas.org/",
		serviceName = "BinasService"
	)
public class BinasPortImpl implements BinasPortType {
	
	private BinasEndpointManager endpointManager;
	private BinasManager binasManager;

	public BinasPortImpl(BinasEndpointManager binasEndpointManager) {
		this.endpointManager = binasEndpointManager;
		this.binasManager = BinasManager.getInstance();
	}

	@Override
	public List<org.binas.ws.StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.binas.ws.StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		return buildStationView(binasManager.getStationView(stationId));
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		UserView userView = null;
		try {
			User user = this.binasManager.createAndAddUser(email);
			userView = new UserView();
			userView.setEmail(user.getEmail());
			userView.setCredit(user.getCredit());
			userView.setHasBina(user.hasBina());
		} catch (EmailExistsException eee) {
			throwEmailExists("There's already a user registered with this email");
		} catch (InvalidEmailException iee) {
			throwInvalidEmail("Invalid email: " + email);
		}

		return userView;
	}

	@Override
	public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception,
			NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnBina(String stationId, String email)
			throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		// TODO Auto-generated method stub
		
	}
	
	// Test Control operations -----------------------------------------------

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
		builder.append('\n');
		
		binasManager.getStations();
		
		String stationsResponse = binasManager.pingStations(inputMessage);
		
		if (stationsResponse.length() == 0) {
			builder.append("There are no active stations to ping\n");
		} else {
			builder.append(stationsResponse);			
		}
		
		return builder.toString();
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize)
			throws org.binas.ws.BadInit_Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testClear() {
		BinasManager.getInstance().reset();
		
	}

	@Override
	public void testInit(int userInitialPoints) throws org.binas.ws.BadInit_Exception {
		try {
			BinasManager.getInstance().init(userInitialPoints);
		} catch (BadInitException bie) {
			throwBadInit("User's initial points must be >= 0");
		}
		
	}
	
	// View conversion helpers-----------------------------------------------
	
	private StationView buildStationView(org.binas.station.ws.StationView sv) {		
		StationView stationView = new StationView();
		
		stationView.setId(sv.getId());
		stationView.setCoordinate(buildCoordinatesView(sv.getCoordinate()));
		stationView.setCapacity(sv.getCapacity());
		stationView.setTotalGets(sv.getTotalGets());
		stationView.setTotalReturns(sv.getTotalReturns());
		stationView.setAvailableBinas(sv.getAvailableBinas());
		stationView.setFreeDocks(sv.getFreeDocks());
		
		return stationView;
	}
	
	private CoordinatesView buildCoordinatesView(org.binas.station.ws.CoordinatesView cv) {
		CoordinatesView coordinatesView = new CoordinatesView();
		
		coordinatesView.setX(cv.getX());
		coordinatesView.setY(cv.getY());
		
		return coordinatesView;
	}
	
	// Exception helpers-----------------------------------------------------
	
	/** Helper to throw a new NoBinaAvail exception. */
	private void throwInvalidEmail(final String message) throws InvalidEmail_Exception {
		InvalidEmail faultInfo = new InvalidEmail();
		faultInfo.setMessage(message);
		throw new InvalidEmail_Exception(message, faultInfo);
	}
	
	private void throwEmailExists(final String message) throws EmailExists_Exception {
		EmailExists faultInfo = new EmailExists();
		faultInfo.setMessage(message);
		throw new EmailExists_Exception(message, faultInfo);
	}
	
	private void throwUserNotExists(final String message) throws UserNotExists_Exception {
		UserNotExists faultInfo = new UserNotExists();
		faultInfo.setMessage(message);
		throw new UserNotExists_Exception(message, faultInfo);
	}
	
	private void throwBadInit(final String message) throws BadInit_Exception {
		BadInit faultInfo = new BadInit();
		faultInfo.setMessage(message);
		throw new BadInit_Exception(message, faultInfo);
	}
	
	private void throwAlreadyHasBina(final String message) throws AlreadyHasBina_Exception {
		AlreadyHasBina faultInfo = new AlreadyHasBina();
		faultInfo.setMessage(message);
		throw new AlreadyHasBina_Exception(message, faultInfo);
	}

	private void throwInvalidStation(final String message) throws InvalidStation_Exception {
		InvalidStation faultInfo = new InvalidStation();
		faultInfo.setMessage(message);
		throw new InvalidStation_Exception(message, faultInfo);
	}

	private void throwNoBinaAvail(final String message) throws NoBinaAvail_Exception {
		NoBinaAvail faultInfo = new NoBinaAvail();
		faultInfo.setMessage(message);
		throw new NoBinaAvail_Exception(message, faultInfo);
	}

	private void throwNoCredit(final String message) throws NoCredit_Exception {
		NoCredit faultInfo = new NoCredit();
		faultInfo.setMessage(message);
		throw new NoCredit_Exception(message, faultInfo);
	}
	
}

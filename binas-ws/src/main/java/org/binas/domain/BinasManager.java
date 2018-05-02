package org.binas.domain;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import org.binas.exception.AlreadyHasBinaException;
import org.binas.exception.BadInitException;
import org.binas.exception.EmailExistsException;
import org.binas.exception.FullStationException;
import org.binas.exception.InternalException;
import org.binas.exception.InvalidEmailException;
import org.binas.exception.InvalidStationException;
import org.binas.exception.NoBinaAvailException;
import org.binas.exception.NoBinaRentedException;
import org.binas.exception.NoCreditException;
import org.binas.exception.UserNotExistsException;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.InvalidEmail_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.SetBalanceResponse;
import org.binas.station.ws.StationPortType;
import org.binas.station.ws.StationService;
import org.binas.station.ws.StationView;
import org.binas.station.ws.TagView;
import org.binas.station.ws.UserNotExists_Exception;
import org.binas.station.ws.UserReplicView;

import org.binas.ws.CoordinatesView;

public class BinasManager {

	// Singleton -------------------------------------------------------------

	private BinasManager() {
		reset();
	}
	
	private String uddiURL;
	private String stationsNamePattern;
	private Collection<UDDIRecord> stationsRecord;
	
	private static int USER_INITIAL_POINTS_DEFAULT = 10;
	private int userInitialPoints;
	
	private Collection<StationPortType> stations = Collections.synchronizedList(new ArrayList<StationPortType>());
	private Collection<User> users = Collections.synchronizedSet(new HashSet<User>());
	
	private AtomicInteger quorum = new AtomicInteger(0);
	
	// from callbacks
	static Collection<Response<GetBalanceResponse>> getBalanceResponse = Collections.synchronizedList(new ArrayList<Response<GetBalanceResponse>>());
	static Collection<Response<SetBalanceResponse>> setBalanceResponse = Collections.synchronizedList(new ArrayList<Response<SetBalanceResponse>>());
	
	private boolean verbose = false;
	
	static final int CONN_TIMEOUT = 1000;
	static final int RECV_TIMEOUT = 2000;

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public void uddiLookup() throws UDDINamingException {
		UDDINaming uddiNaming = new UDDINaming(uddiURL);
		this.stationsRecord = uddiNaming.listRecords('%' + this.stationsNamePattern + '%');
	}
	
	public void createStub() {
		StationService service;
		String wsURL;
		StationPortType port;
		
		for(UDDIRecord ur : this.stationsRecord) {
			
			service = new StationService();
			port = service.getStationPort();
			
			wsURL = ur.getUrl();
			
			if (verbose)
				System.out.printf("Setting endpoint address for '%s'%n", wsURL);
			
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsURL);
			
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
			
			this.stations.add(port);
			calculateQuorum();
		}
	}
	
	// Getters -------------------------------------------------------------

	public boolean isVerbose() {
		return verbose;
	}
	
	public int getUserInitialPoints() {
		return this.userInitialPoints;
	}
	
	public Collection<User> getUsers() {
		synchronized (this.users) {
			return this.users;			
		}
	}

	public User getUser(String email) throws UserNotExistsException {
		try {
			User.checkEmail(email);
		} catch (InvalidEmailException iee) {
			throw new UserNotExistsException();
		}
		
		synchronized (this.users) {
			for(User user : this.users) {
				if (user.getEmail().equals(email)) {
					return user;
				};
			}			
			throw new UserNotExistsException();
		}		
	}
	
	private StationPortType getStation(String stationId) throws InvalidStationException {
		if (stationId == null || stationId.length() == 0) throw new InvalidStationException();
		
		synchronized (this.stations) {
			for (StationPortType station : this.stations) {
				if (station.getInfo().getId().equals(stationId)) return station;
			}
			throw new InvalidStationException();			
		}
	}
	
	public org.binas.station.ws.StationView getStationView(String stationId) throws InvalidStationException {
		synchronized (this.stations) {
			return getStation(stationId).getInfo();			
		}
	}
	
	// Setters -------------------------------------------------------------
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public void setUddiUrl(String uddiURL) {
		this.uddiURL = uddiURL;
	}
	
	public void setStationsNamePattern(String stationsNamePattern) {
		this.stationsNamePattern = stationsNamePattern;
	}
	
	// Initialization ------------------------------------------------------
	
	public synchronized void init(int userInitialPoints) throws BadInitException {
		if (userInitialPoints < 0) throw new BadInitException();
		this.userInitialPoints = userInitialPoints;
		emptyStations();
		emptyUsers();
		
		registerStations();
	}
	
	public synchronized void initStation(String stationId, int x, int y, int capacity, int returnPrize) throws BadInitException,
		InvalidStationException {
		try {
			StationPortType station = getStation(stationId);
			station.testInit(x, y, capacity, returnPrize);
		} catch (BadInit_Exception e) {
			throw new BadInitException();
		} catch (InvalidStationException ise) {
			throw new InvalidStationException();
		}
	}
	
	public synchronized void reset() {
		emptyStations();
		emptyUsers();
		this.userInitialPoints = USER_INITIAL_POINTS_DEFAULT;
		this.quorum.set(0);
		getBalanceResponse.clear();
		setBalanceResponse.clear();
	}
	
	public synchronized void testClearStations() {
		for (StationPortType station : this.stations) {
			station.testClear();
		}
	}
	
	// Manager logic  ---------------------------------------------------------
	
	public User createAndAddUser(String email) throws EmailExistsException, InvalidEmailException, InternalException {
		User user = new User(email);
		
		synchronized (this.users) {
			if (!this.users.add(user)) {
				throw new EmailExistsException();
			};			
		}
		
		setBalance(email, this.userInitialPoints);
		
		return user;		
	}
	
	public int getCredit(String email) throws UserNotExistsException, InternalException {
		return getBalance(email).getValue();
	}

	public void registerStations() {
		synchronized (this.stations) {
			emptyStations();
			
			if (verbose)
				System.out.printf("Contacting UDDI at %s%n", uddiURL);	
			try {
				uddiLookup();
				createStub();
			} catch (UDDINamingException une) {
				System.out.printf("Error contacting the stations: %s%n", une);
			}			
		}
	}
	
	public void emptyStations() {
		synchronized (this.stations) {
			this.stations = Collections.synchronizedList(new ArrayList<StationPortType>());			
			this.quorum.set(0);
		}		
	}
	
	public void emptyUsers() {
		synchronized (this.users) {
			this.users = Collections.synchronizedSet(new HashSet<User>());			
		}
	}
	
	public void removeUser(String email) throws UserNotExistsException, InvalidEmailException {
		synchronized (this.users) {
			User user = getUser(email);
			if (user != null) {
				this.users.remove(user);			
			}			
		}
	}
	
 	// Stations -------------------------------------------------------------
	
	public String pingStations(String inputMessage) {
		StringBuilder builder = new StringBuilder();
		
		synchronized (this.stations) {
			for(StationPortType s : this.stations) {
				builder.append(s.testPing(inputMessage));
				builder.append("\n");
			}			
		}
		
		return builder.toString();
	}
	
	public synchronized void rentBina(String stationId, String email) throws AlreadyHasBinaException, InvalidStationException,
		NoBinaAvailException, NoCreditException, UserNotExistsException, InternalException {
		User user = getUser(email);
		
		UserReplicView userReplic = getBalance(email);
		
		StationPortType station = getStation(stationId);
		
		if (user.hasBina()) throw new AlreadyHasBinaException();
		if (userReplic.getValue() < 1) throw new NoCreditException();
		
		try {
			station.getBina();
			setBalance(email, userReplic.getValue() - 1);
			user.takeBina();
		} catch (NoBinaAvail_Exception nbae) {
			throw new NoBinaAvailException();
		} catch (InvalidEmailException iee) {
			// email verification happens above
		}
	}
	
	public synchronized void returnBina(String stationId, String email) throws FullStationException, InvalidStationException,
		NoBinaRentedException, UserNotExistsException, InternalException {
		int bonus = 0;		
		User user = getUser(email);
		
		UserReplicView userReplic = getBalance(email);
		StationPortType station = getStation(stationId);
		
		if(!user.hasBina()) throw new NoBinaRentedException();
		
		try {
			bonus = station.returnBina();
			setBalance(email, userReplic.getValue() + bonus);
			user.returnBina();
		} catch(NoSlotAvail_Exception nsae) {
			throw new FullStationException();
		} catch (InvalidEmailException iee) {
			// email verification happens above
		}	
	}
	
	public List<org.binas.station.ws.StationView> getNearestStationsList(Integer numberOfStations, CoordinatesView coordinates) {
		List<org.binas.station.ws.StationView> stationsList = new ArrayList<org.binas.station.ws.StationView>();
		
		if (numberOfStations <= 0) return stationsList;

		// always looks for new stations
		// this method already gets the mutex for the stations
		registerStations();
		
		Integer xO = coordinates.getX(); Integer yO = coordinates.getY();
		Comparator<StationView> comparator = new Comparator<StationView>() {
			@Override
			public int compare(StationView st1, StationView st2) {
				Integer x1 = st1.getCoordinate().getX(); Integer y1 = st1.getCoordinate().getY(); Double dist1;
				Integer x2 = st2.getCoordinate().getX(); Integer y2 = st1.getCoordinate().getY(); Double dist2;
				
				dist1 = Math.sqrt(Math.pow((xO - x1), 2) + Math.pow((yO - y1), 2));
				dist2 = Math.sqrt(Math.pow((xO - x2), 2) + Math.pow((yO - y2), 2));
				
				return (int) Math.floor(dist1 - dist2);
			}
		};
		
		// get the mutex on this block, only to make sure that the stations
		// that are being contacted do not change during the iteration
		synchronized (this.stations) {
			for (StationPortType station : this.stations) {
				stationsList.add(station.getInfo());
			}			
		}
		
		stationsList.sort(comparator);
		
		if (stationsList.size() < numberOfStations)
			return stationsList;
		
		return stationsList.subList(0, numberOfStations);
	}
	
	public UserReplicView getBalance(String email) throws UserNotExistsException, InternalException {
		UserReplicView user;
		
		while(!this.isGetBalanceFinished()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new InternalException(e.getMessage());
			}
		}
		
		synchronized (this.stations) {
			for (StationPortType station : this.stations) {
				station.getBalanceAsync(email, getBalanceHandler());					
			}
		}
		
		while (!this.isGetBalanceReady()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new InternalException(e.getMessage());
			}
		}
		
		user = getGetBalanceResult();
				
		return user;
	}
	
	public void setBalance(String email, int balance) throws InvalidEmailException, InternalException {
		UserReplicView user = null;
		TagView newTag = new TagView();
		UserReplicView newUser = null;
		
		try {
			user = getBalance(email);

			TagView maxTag = user.getTag();
			
			newTag.setSeq(maxTag.getSeq() + 1);
			
			newUser = new UserReplicView();
			
			newUser.setTag(newTag);
			newUser.setValue(balance);
			
		} catch (UserNotExistsException unee) {
			newUser = new UserReplicView();
			
			newTag.setSeq(0);
			newUser.setTag(newTag);
			newUser.setValue(balance);
		}
		
		while(!this.isSetBalanceFinished()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new InternalException(e.getMessage());
			}
		}
		
		synchronized (this.stations) {
			for (StationPortType station : this.stations) {
				station.setBalanceAsync(email, newUser, setBalanceHandler());
			}
		}
		
		while (!this.isSetBalanceReady()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new InternalException(e.getMessage());
			}
		}
		
		getSetBalanceResult();
	}
	
	// Helpers -------------------------------------------------------------
	
	private void calculateQuorum() { 
		this.quorum.set( (int) (stations.size() / 2) + 1 );
	}
	
	private int getQuorum() {
		return this.quorum.get();
	}
	
	private boolean isGetBalanceReady() {
		return getBalanceResponse.size() >= getQuorum();
	}
	
	private boolean isSetBalanceReady() {
		return setBalanceResponse.size() >= getQuorum();
	}
	
	private boolean isGetBalanceFinished() {
		if (getBalanceResponse.size() == this.stations.size()) {
			getBalanceResponse.clear();
			return true;
		} else if (getBalanceResponse.size() == 0) return true;
		return false;
	}
	
	private boolean isSetBalanceFinished() {
		if (setBalanceResponse.size() == this.stations.size()) {
			setBalanceResponse.clear();
			return true;
		} else if (setBalanceResponse.size() == 0) return true;
		return false;
	}
	
	private UserReplicView getGetBalanceResult() throws UserNotExistsException, InternalException {
		UserReplicView user, maxTagUser = null;
		Throwable exception = null;
		
		synchronized (getBalanceResponse) {

			for (Response<GetBalanceResponse> response : getBalanceResponse) {
				try {
					user = response.get().getUserReplic();
					
					if (maxTagUser == null || user.getTag().getSeq() > maxTagUser.getTag().getSeq()) {
						maxTagUser = user;
					}
					
				} catch (InterruptedException e) {
					throw new InternalException(e.getMessage());
				} catch (ExecutionException e) {
					exception = e.getCause();
				}
			}
		}
				
		if (maxTagUser == null && exception instanceof UserNotExists_Exception) throw new UserNotExistsException();
		if (maxTagUser == null && exception instanceof WebServiceException) throw new InternalException(exception.getMessage());
				
		return maxTagUser;
	}
	
	private void getSetBalanceResult() throws InvalidEmailException, InternalException {
		Throwable exception = null;
		
		synchronized (setBalanceResponse) {
			for(Response<SetBalanceResponse> response : setBalanceResponse) {
				try {
					response.get();
				} catch (InterruptedException e) {
					throw new InternalException(e.getMessage());
				} catch (ExecutionException e) {
					exception = e.getCause();
				}
			}			
		}
		
		if (exception != null && exception instanceof InvalidEmail_Exception) throw new InvalidEmailException();
		if (exception != null && exception instanceof WebServiceException) throw new InternalException(exception.getMessage());
	}
	
	// Handlers for callback -------------------------------------------------------------
	private AsyncHandler<GetBalanceResponse> getBalanceHandler() {
		return new AsyncHandler<GetBalanceResponse>() {
			@Override
			public void handleResponse(Response<GetBalanceResponse> res) {
				getBalanceResponse.add(res);
			}
		};
	}
	
	private AsyncHandler<SetBalanceResponse> setBalanceHandler() {
		return new AsyncHandler<SetBalanceResponse>() {
			@Override
			public void handleResponse(Response<SetBalanceResponse> res) {
				synchronized (setBalanceResponse) {
					setBalanceResponse.add(res);					
				}
			}
		};
	}

}

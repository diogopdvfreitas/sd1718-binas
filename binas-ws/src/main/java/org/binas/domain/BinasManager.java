package org.binas.domain;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import org.binas.exception.AlreadyHasBinaException;
import org.binas.exception.BadInitException;
import org.binas.exception.EmailExistsException;
import org.binas.exception.InvalidEmailException;
import org.binas.exception.InvalidStationException;
import org.binas.exception.NoBinaAvailException;
import org.binas.exception.NoCreditException;
import org.binas.exception.UserNotExistsException;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.StationPortType;
import org.binas.station.ws.StationService;

public class BinasManager {

	// Singleton -------------------------------------------------------------

	private BinasManager() {
		reset();
	}
	
	private String uddiURL;
	private String stationsNamePattern;
	private Collection<StationPortType> stations;
	private Collection<UDDIRecord> stationsRecord;
	
	private int userInitialPoints;
	
	private Collection<User> users;
	
	private boolean verbose = false;

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

	public boolean isVerbose() {
		return verbose;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public void setUddiUrl(String uddiURL) {
		this.uddiURL = uddiURL;
	}
	
	public void setStationsNamePattern(String stationsNamePattern) {
		this.stationsNamePattern = stationsNamePattern;
	}
	
	public synchronized void init(int userInitialPoints) throws BadInitException {
		if (userInitialPoints < 0) throw new BadInitException();
		this.userInitialPoints = userInitialPoints;
		emptyStations();
		emptyUsers();
	}
	
	public synchronized void reset() {
		emptyStations();
		emptyUsers();
	}
	
	public synchronized User createAndAddUser(String email) throws EmailExistsException, InvalidEmailException {
		User user = new User(email, userInitialPoints);
		
		if (!this.users.add(user)) {
			System.out.println("Duplicated");
			throw new EmailExistsException();
		};
		
		return user;
	}

	public void getStations() {
		System.out.printf("Contacting UDDI at %s%n", uddiURL);	
		try {
			uddiLookup();
			createStub();
		} catch (UDDINamingException une) {
			System.out.printf("Error contacting the stations: %s%n", une);
		}
	}
	
	public synchronized void rentBina(String stationId, String email) throws AlreadyHasBinaException, InvalidStationException,
		NoBinaAvailException, NoCreditException, UserNotExistsException {
		
		User user = getUser(email);
		StationPortType station = getStation(stationId);
		
		if (user.hasBina()) throw new AlreadyHasBinaException();
		if (!user.takeBina()) throw new NoCreditException();
		
		try {
			station.getBina();
		} catch (NoBinaAvail_Exception nbae) {
			
			// must increment the credit by 1, because an error occurred and we want to rollback the user state
			user.returnBina(1);
			
			throw new NoBinaAvailException();
		}
	}
	
 	// Getters -------------------------------------------------------------
	
	public synchronized Collection<User> getUsers() {
		return this.users;
	}

	public synchronized User getUser(String email) throws UserNotExistsException {
		for(User user : this.users) {
			if (user.getEmail().equals(email)) {
				return user;
			};
		}
		
		throw new UserNotExistsException();
	}
	
	public synchronized StationPortType getStation(String stationId) throws InvalidStationException {
		for (StationPortType station : stations) {
			if (station.getInfo().getId().equals(stationId)) return station;
		}
		throw new InvalidStationException();
	}
	
	public synchronized org.binas.station.ws.StationView getStationView(String stationId) {
		for (StationPortType station : this.stations) {
			if (station.getInfo().getId() == stationId) {
				return station.getInfo();
			}
		}
			
		return null;
	}
	
	public synchronized List<org.binas.station.ws.StationView> getStationsViewList() {
		List<org.binas.station.ws.StationView> stationsList = new ArrayList<org.binas.station.ws.StationView>();
		for (StationPortType station : this.stations) {
			stationsList.add(station.getInfo());
		}
		
		return stationsList;
	}
	
	public synchronized void emptyStations() {
		this.stations = Collections.synchronizedList(new ArrayList<StationPortType>());
	}
	
	public synchronized void emptyUsers() {
		this.users = Collections.synchronizedSet(new HashSet<User>());
	}
	
	public synchronized void removeUser(String email) throws UserNotExistsException {
		User user = getUser(email);
		if (user != null) {
			this.users.remove(user);			
		}
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
			
			this.stations.add(port);
		}
	}
	
	public String pingStations(String inputMessage) {
		StringBuilder builder = new StringBuilder();
		for(StationPortType s : this.stations) {
			builder.append(s.testPing(inputMessage));
			builder.append("\n");
		}
		return builder.toString();
	}

}

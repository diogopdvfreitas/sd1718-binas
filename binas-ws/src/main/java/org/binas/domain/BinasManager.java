package org.binas.domain;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

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
	
	public void setUddi(String uddiURL) {
		this.uddiURL = uddiURL;
	}
	
	public void setStationsNamePattern(String stationsNamePattern) {
		this.stationsNamePattern = stationsNamePattern;
	}
	
	public synchronized void reset() {
		emptyStations();
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
	
	public synchronized void emptyStations() {
		this.stations = Collections.synchronizedList(new ArrayList<StationPortType>());
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

package org.binas.ws;

import java.util.List;

import javax.jws.WebService;

import org.binas.domain.BinasManager;
import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.StationView;
import org.binas.station.ws.cli.StationClient;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public String testPing(String inputMessage) {
		binasManager.getStations();
		return binasManager.pingStations(inputMessage);
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize)
			throws org.binas.ws.BadInit_Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testClear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testInit(int userInitialPoints) throws org.binas.ws.BadInit_Exception {
		// TODO Auto-generated method stub
		
	}
	
}

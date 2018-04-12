package org.binas.station.ws;

import org.binas.station.domain.Station;
import org.binas.station.domain.exception.BadInitException;

/**
 * The application is where the service starts running. The program arguments
 * are processed here. Other configurations can also be done here.
 */
public class StationApp {
	
	private static void loadProperties(String x, String y, String capacity, String bonus) {
		int intX, intY, intCapacity, intBonus;
		try {
			intX = Integer.parseInt(x);
			intY = Integer.parseInt(y);
			intCapacity = Integer.parseInt(capacity);
			intBonus = Integer.parseInt(bonus);
			Station.getInstance().init(intX, intY, intCapacity, intBonus);
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid arguments: one of the properties is not an integer");
		} catch (BadInitException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
	}

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + StationApp.class.getName() +
					"(wsName wsURL) OR (wsName wsURL uddiURL)  [X Y capacity bonus]");
			return;
		}
		
		String wsName = args[0];
		String wsURL = args[1];
		String uddiURL = null;
		StationEndpointManager endpoint;
		
		int coordinateX, coordinateY, capacity, bonus;
		
		if (args.length == 3 || args.length == 7) {
			uddiURL = args[2];
			
			if (args.length == 7 && args[3] != null) {
				loadProperties(args[3], args[4], args[5], args[6]);
			}
			
			endpoint = new StationEndpointManager(uddiURL, wsName, wsURL);
		} else {
			
			if (args.length == 6 && args[2] != null) {
				loadProperties(args[2], args[3], args[4], args[5]);
			}
			
			System.out.printf("wsName: %s, wsURL: %s%n", wsName, wsURL);
			endpoint = new StationEndpointManager(wsName, wsURL);
		}
		
		Station.getInstance().setId(wsName);

		System.out.println(StationApp.class.getSimpleName() + " running");
		
		System.out.println("=========Station=========");
		System.out.printf("Coordinates:  (%d, %d)\n", Station.getInstance().getCoordinates().getX(), Station.getInstance().getCoordinates().getY());
		System.out.println("Capacity:     " + Station.getInstance().getMaxCapacity());
		System.out.println("Return prize: " + Station.getInstance().getBonus());
		System.out.println("=========================");

		// TODO start Web Service
		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}

	}

}
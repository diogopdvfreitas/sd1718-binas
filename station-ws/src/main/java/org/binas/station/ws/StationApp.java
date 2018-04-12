package org.binas.station.ws;

import org.binas.station.domain.Station;

/**
 * The application is where the service starts running. The program arguments
 * are processed here. Other configurations can also be done here.
 */
public class StationApp {

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
			
			if (args.length == 7) {
				coordinateX = Integer.parseInt(args[3]);
				coordinateY = Integer.parseInt(args[4]);
				capacity = Integer.parseInt(args[5]);
				bonus = Integer.parseInt(args[6]);
				Station.getInstance().init(coordinateX, coordinateY, capacity, bonus);
			}
			
			
			endpoint = new StationEndpointManager(uddiURL, wsName, wsURL);
		} else {
			
			if (args.length == 6) {
				coordinateX = Integer.parseInt(args[2]);
				coordinateY = Integer.parseInt(args[3]);
				capacity = Integer.parseInt(args[4]);
				bonus = Integer.parseInt(args[5]);
				Station.getInstance().init(coordinateX, coordinateY, capacity, bonus);
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
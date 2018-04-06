package org.binas.ws;

import org.binas.domain.BinasManager;

public class BinasApp {

	public static void main(String[] args) throws Exception {
		System.out.println(BinasApp.class.getSimpleName() + " running");
		
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + BinasApp.class.getName() + "uddiURL wsName wsURL");
			return;
		}
		
		String uddiURL = args[0];
		String wsName = args[1];
		String wsURL = args[2];
		BinasEndpointManager endpoint;
		endpoint = new BinasEndpointManager(uddiURL, wsName, wsURL);
	
		BinasManager.getInstance().setUddi(uddiURL);
		BinasManager.getInstance().setVerbose(true);

		// TODO start Web Service
		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}
	}

}
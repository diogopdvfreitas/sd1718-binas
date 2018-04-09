package org.binas.ws.cli;

import org.binas.ws.UserView;

public class BinasClientApp {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length == 0) {
            System.err.println("Argument(s) missing!");
            System.err.println("Usage: java " + BinasClientApp.class.getName()
                    + " wsURL OR uddiURL wsName");
            return;
        }
        String uddiURL = null;
        String wsName = null;
        String wsURL = null;
        if (args.length == 1) {
            wsURL = args[0];
        } else if (args.length >= 2) {
            uddiURL = args[0];
            wsName = args[1];
        }

		System.out.println(BinasClientApp.class.getSimpleName() + " running");

        // Create client
        BinasClient client = null;

        if (wsURL != null) {
            System.out.printf("Creating client for server at %s%n", wsURL);
            client = new BinasClient(wsURL);
        } else if (uddiURL != null) {
            System.out.printf("Creating client using UDDI at %s for server with name %s%n",
                uddiURL, wsName);
            client = new BinasClient(uddiURL, wsName);
        }

        // the following remote invocations are just basic examples
        // the actual tests are made using JUnit

		System.out.println("Invoke ping()...");
		String result = client.testPing("client");
		System.out.print(result);
		
		client.testInit(10);
		
		try {
			UserView userView = client.activateUser("fchamicapereira@gmail.com");
			System.out.println("Email: " + userView.getEmail());
			System.out.println("Credit: " + userView.getCredit());
			System.out.println("Has bina: " + userView.isHasBina());
			client.activateUser("fchamicapereira@gmail.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	 }
}


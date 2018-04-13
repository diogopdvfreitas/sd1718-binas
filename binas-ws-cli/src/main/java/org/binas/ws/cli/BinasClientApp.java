package org.binas.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

public class BinasClientApp {

    public static void main(String[] args) {
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
        BinasClient client = null;
        
        if (args.length == 1) {
            wsURL = args[0];
            
            try {
            		client = new BinasClient(wsURL);            	
            } catch (BinasClientException bce) {
            		System.out.println(bce.getMessage());
            		return;
            }
            
        } else if (args.length >= 2) {
            uddiURL = args[0];
            wsName = args[1];
            
            try {
            		client = new BinasClient(uddiURL, wsName);
            } catch (BinasClientException bce) {
        			return;
            } catch (UDDINamingException une) {
            		return;
			}
        }

		System.out.println(BinasClientApp.class.getSimpleName() + " running");
		
		System.out.println(client.testPing("Eu sou o cliente!"));
		 
		
		try {
			client.activateUser("e@email.com");
			System.out.print("My credit=" + client.getCredit("e@email.com"));			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	 }
}


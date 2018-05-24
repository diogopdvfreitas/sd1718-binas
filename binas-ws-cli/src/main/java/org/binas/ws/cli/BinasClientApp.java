package org.binas.ws.cli;

import java.util.List;
import java.util.Scanner;

import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;

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
			String email = "e@email.com";
			client.activateUser(email);
			
			CoordinatesView cv = new CoordinatesView();
			cv.setX(0);
			cv.setY(0);
			
			List<StationView> stations = client.listStations(1, cv);
			
			client.rentBina(stations.get(0).getId(), email);
			System.out.println("My credit=" + client.getCredit(email));
			
			System.out.println("Input waiting...");
			Scanner reader = new Scanner(System.in);
			reader.next();
			System.out.println("Proceeding");
			
			System.out.println("My credit=" + client.getCredit(email));
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	 }
}


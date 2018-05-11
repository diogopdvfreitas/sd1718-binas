package org.binas.ws;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import org.binas.domain.BinasManager;

import example.ws.handler.KerberosServerHandler;

public class BinasApp {
	private static final String KERBEROS_PROP_FILE = "/kerberos.properties";

	public static void main(String[] args) throws Exception {
		Properties kerberosProps;
    	String user, pass, server;
    	
		System.out.println(BinasApp.class.getSimpleName() + " running");
		
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + BinasApp.class.getName() + "uddiURL wsName wsURL stationsWsNamePattern");
			return;
		}
		
		String uddiURL = args[0];
		String wsName = args[1];
		String wsURL = args[2];
		String stationsNamePattern = args[3];
		
		kerberosProps = new Properties();
        
		try {
			kerberosProps.load(KerberosServerHandler.class.getResourceAsStream(KERBEROS_PROP_FILE));
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", KERBEROS_PROP_FILE);
			System.out.println(msg);
			throw new RuntimeException();
		}
		
		user = kerberosProps.getProperty("user");
		pass = kerberosProps.getProperty("pass");
		server = kerberosProps.getProperty("server");
		
		try {
			KerberosServerHandler.setStaticKerbyProperties(user, pass, server);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BinasEndpointManager endpoint;
		endpoint = new BinasEndpointManager(uddiURL, wsName, wsURL);
	
		BinasManager.getInstance().setUddiUrl(uddiURL);
		BinasManager.getInstance().setStationsNamePattern(stationsNamePattern);
		BinasManager.getInstance().setVerbose(true);
		BinasManager.getInstance().registerStations();
		
		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}
	}

}
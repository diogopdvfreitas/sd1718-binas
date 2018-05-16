package org.binas.ws.cli;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import org.binas.ws.EmailExists_Exception;
import org.binas.ws.Internal_Exception;
import org.binas.ws.InvalidEmail_Exception;

import example.ws.handler.KerberosClientHandler;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

public class BinasClientApp {
	private static final String KERBEROS_PROP_FILE = "/kerberos.properties";
	
    public static void main(String[] args) {
    	Properties kerberosProps;
    	String user, pass, server, kerbyServer;

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

        System.out.println("Getting properties for kerberos...");
        
        kerberosProps = new Properties();
        
		try {
			kerberosProps.load(KerberosClientHandler.class.getResourceAsStream(KERBEROS_PROP_FILE));
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", KERBEROS_PROP_FILE);
			System.out.println(msg);
			throw new RuntimeException();
		}
		
		user = kerberosProps.getProperty("user1");
		pass = kerberosProps.getProperty("pass1");
		server = kerberosProps.getProperty("server");
		kerbyServer = kerberosProps.getProperty("kerbyServer");
		
		try {
			KerberosClientHandler.setStaticKerbyProperties(user, pass, server, kerbyServer);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(BinasClientApp.class.getSimpleName() + " running");
		
			client.testPing("Client");

	 }
}


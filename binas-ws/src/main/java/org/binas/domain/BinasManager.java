package org.binas.domain;

import java.util.Collection;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

public class BinasManager {

	// Singleton -------------------------------------------------------------

	private BinasManager() {
	}
	
	String uddiURL;
	String wsName;

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
	
	public void setUddiAndName(String uddiURL, String wsName) {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
	}

	public void getStations() {
		System.out.printf("Contacting UDDI at %s%n", uddiURL);

		try {
			UDDINaming uddiNaming = new UDDINaming(uddiURL);
			Collection<String> urls = uddiNaming.list(this.wsName + '%');		
			System.out.println(urls);
		} catch (UDDINamingException une) {
			System.out.printf("Error: %s", une);
		}
		
	}
	
	// TODO

}

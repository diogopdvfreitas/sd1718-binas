package org.binas.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.binas.ws.cli.BinasClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;


/*
 * Base class of tests
 * Loads the properties in the file
 */
public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static BinasClient client;
	
	protected static final String STATION1_ID = "A37_Station1";
	protected static final String STATION2_ID = "A37_Station2";
	protected static final String STATION3_ID = "A37_Station3";
	
	protected static final int X1 = 22;
	protected static final int Y1 = 7;
	protected static final int CAPACITY1 = 6;
	protected static final int BONUS1 = 2;
	
	protected static final int X2 = 80;
	protected static final int Y2 = 20;
	protected static final int CAPACITY2 = 12;
	protected static final int BONUS2 = 1;
	
	protected static final int X3 = 50;
	protected static final int Y3 = 50;
	protected static final int CAPACITY3 = 20;
	protected static final int BONUS3 = 0;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps.getProperty("uddi.enabled");
		final String verboseEnabled = testProps.getProperty("verbose.enabled");

		final String uddiURL = testProps.getProperty("uddi.url");
		final String wsName = testProps.getProperty("ws.name");
		final String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client = new BinasClient(uddiURL, wsName);
		} else {
			client = new BinasClient(wsURL);
		}
		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));

	}

	@AfterClass
	public static void cleanup() {
	}

}

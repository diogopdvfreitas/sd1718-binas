package example.ws.handler;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.BadTicketRequest_Exception;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClientException;

import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

/**
 * This SOAPHandler shows how to set/get values from headers in inbound/outbound
 * SOAP messages.
 *
 * A header is created in an outbound message and is read on an inbound message.
 *
 * The value that is read from the header is placed in a SOAP message context
 * property that can be accessed by other handlers or by the application.
 */
public class KerberosClientHandler implements SOAPHandler<SOAPMessageContext> {
	
	public static final String CONTEXT_PROPERTY = "my.property";
	
	public static String user;
	public static Key pass;
	public static String server;
	public static String kerbyServer;
	
	private static SessionKey sessionKey;
	private static CipheredView ticket;
	
	private static final int VALID_DURATION = 30;
	private static final SecureRandom randomGenerator = new SecureRandom();

	//
	// Handler interface implementation
	//

	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		CipheredView auth;

		System.out.println("AddHeaderHandler: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {
				
				requestSessionKeyAndTicket();
				auth = generateAuth(); 
				
				System.out.println("Writing header to OUTbound SOAP message...");
				
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();

				// add auth header
				generateAuthHeader(se, auth);
				
				// add ticket header
				generateTicketHeader(se);
				

			} else {
				System.out.println("Reading header from INbound SOAP message...");

				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();

				// check header
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}

				// get first header element
				Name name = se.createName("myHeader", "d", "http://demo");
				Iterator<?> it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.println("Header element not found.");
					return true;
				}
				SOAPElement element = (SOAPElement) it.next();

				// get header element value
				String valueString = element.getValue();
				int value = Integer.parseInt(valueString);

				// print received header
				System.out.println("Header value is " + value);

				// put header in a property context
				smc.put(CONTEXT_PROPERTY, value);
				// set property scope to application client/server class can
				// access it
				smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);

			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
			throw new RuntimeException();
		}

		return true;
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("Ignoring fault message...");
		return true;
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}
	
	/* ============ Kerby helpers ============  */
	
	private void requestSessionKeyAndTicket() throws KerbyClientException, BadTicketRequest_Exception, KerbyException {
		long nounce = randomGenerator.nextLong();
		
		KerbyClient client = new KerbyClient(kerbyServer);
		SessionKeyAndTicketView sessionAndTicket = client.requestTicket(user, server, nounce, VALID_DURATION);
		
		// saving ciphered ticket
		ticket = sessionAndTicket.getTicket(); 
		
		// decrypting and saving session key
		CipheredView cipheredSessionKey = sessionAndTicket.getSessionKey();
		sessionKey = new SessionKey(cipheredSessionKey, pass);
	}
	
	private CipheredView generateAuth() throws KerbyException {
		Auth auth = new Auth(user, new Date());
		return auth.cipher(sessionKey.getKeyXY());
	}
	
	/* ============ Headers ============  */
	
	private void generateAuthHeader(SOAPEnvelope se, CipheredView auth) throws SOAPException {

		// add header
		SOAPHeader sh = se.getHeader();
		if (sh == null)
			sh = se.addHeader();
		
		// add header element (name, namespace prefix, namespace)
		Name name = se.createName("auth", "kerby", "http://ws.binas.org/");
		SOAPHeaderElement element = sh.addHeaderElement(name);

		// add header element value
		String authHexEncoded = printHexBinary(auth.getData());
		element.addTextNode(authHexEncoded);
	}
	
	private void generateTicketHeader(SOAPEnvelope se) throws SOAPException {

		// add header
		SOAPHeader sh = se.getHeader();
		if (sh == null)
			sh = se.addHeader();
		
		// add header element (name, namespace prefix, namespace)
		Name name = se.createName("ticket", "kerby", "http://ws.binas.org/");
		SOAPHeaderElement element = sh.addHeaderElement(name);

		// add header element value
		String ticketHexEncoded = printHexBinary(ticket.getData());
		element.addTextNode(ticketHexEncoded);
	}
	
	/* ============ Static Setter ============  */
	public static void setStaticKerbyProperties(String _user, String _pass, String _server, String _kerbyServer) throws NoSuchAlgorithmException, InvalidKeySpecException {
		user = _user;
		pass = SecurityHelper.generateKeyFromPassword(_pass);
		server = _server;
		kerbyServer = _kerbyServer;
	}
}
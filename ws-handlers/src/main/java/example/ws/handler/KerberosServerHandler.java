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
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;

import java.security.spec.InvalidKeySpecException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;

/**
 * This SOAPHandler shows how to set/get values from headers in inbound/outbound
 * SOAP messages.
 *
 * A header is created in an outbound message and is read on an inbound message.
 *
 * The value that is read from the header is placed in a SOAP message context
 * property that can be accessed by other handlers or by the application.
 */
public class KerberosServerHandler implements SOAPHandler<SOAPMessageContext> {
	
	public static final String TICKET = "kerby.ticket";
	
	private static String user;
	private static Key pass;
	
	private Key sessionKey;
	private Auth auth;
	
	private static final long TIME_VALIDATION_MARGIN = 2000; // milliseconds 

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

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {
				
				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				
				generateRequestTimeHeader(se, auth);
				
			} else {

				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();

				// check header
				if (sh == null) {
					throw new RuntimeException("Header not found.");
				} 
				
				Ticket ticket = getTicketFromHeader(se, sh);
				validateTicket(ticket);
				
				// put ticket in a property context
				smc.put(TICKET, ticket);
				
				sessionKey = ticket.getKeyXY();
				auth = getAuthFromHeader(se, sh);
				
				validateAuth(auth);

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
	
	private void validateTicket(Ticket ticket) throws KerbyException, RuntimeException {
		ticket.validate();
		
		if (ticket.getTime2().compareTo(new Date()) < 0) {
			throw new RuntimeException("Ticket not valid --- expired");
		}
		
		if (!ticket.getY().equals(user)) {
			throw new RuntimeException("Ticket server does not match");
		}
		
		printTicket(ticket);
	}
	
	private void validateAuth(Auth auth) throws KerbyException, RuntimeException {
		auth.validate();
		RequestTime requestTime = new RequestTime(auth.getTimeRequest());
		long requestTimeDate = requestTime.getTimeRequest().getTime();
		long now = new Date().getTime();
		
		if (requestTimeDate > now + TIME_VALIDATION_MARGIN / 2 || requestTimeDate < now - TIME_VALIDATION_MARGIN / 2) {
			throw new RuntimeException("Invalid request time");
		}
	}	
	
	private void generateRequestTimeHeader(SOAPEnvelope se, Auth auth) throws SOAPException, KerbyException {
		RequestTime requestTime = new RequestTime(auth.getTimeRequest());
		CipheredView requestTimeCiphered = requestTime.cipher(sessionKey);
		String hexEncodedRequestTime = printHexBinary(requestTimeCiphered.getData());
		
		// add header
		SOAPHeader sh = se.getHeader();
		if (sh == null)
			sh = se.addHeader();
		
		// add header element (name, namespace prefix, namespace)
		Name name = se.createName("requestTime", "kerby", "http://ws.binas.org/");
		SOAPHeaderElement element = sh.addHeaderElement(name);

		// add header element value
		element.addTextNode(hexEncodedRequestTime);
	}
	
	/* ============ Headers ============  */
	
	private Ticket getTicketFromHeader(SOAPEnvelope se, SOAPHeader sh) throws KerbyException, RuntimeException, SOAPException {
		Name name = se.createName("ticket", "kerby", "http://ws.binas.org/");
		Iterator<?> it = sh.getChildElements(name);
		
		if (!it.hasNext()) {
			System.out.println("Header element not found.");
			throw new RuntimeException("Ticket not found");
		}
		
		SOAPElement element = (SOAPElement) it.next();

		String hexEncodedTicket = element.getValue();

		CipheredView cipheredTicket = new CipheredView();
				
		cipheredTicket.setData(parseHexBinary(hexEncodedTicket));
		
		Ticket ticket = new Ticket(cipheredTicket, pass);
		return ticket;
	}
	
	private Auth getAuthFromHeader(SOAPEnvelope se, SOAPHeader sh) throws KerbyException, RuntimeException, SOAPException {
		Name name = se.createName("auth", "kerby", "http://ws.binas.org/");
		Iterator<?> it = sh.getChildElements(name);
		
		if (!it.hasNext()) {
			System.out.println("Header element not found.");
			throw new RuntimeException("Auth not found");
		}
		
		SOAPElement element = (SOAPElement) it.next();

		String hexEncodedAuth = element.getValue();

		CipheredView cipheredAuth = new CipheredView();
		
		cipheredAuth.setData(parseHexBinary(hexEncodedAuth));
		
		Auth auth = new Auth(cipheredAuth, sessionKey);
		return auth;
	}
	
	/* ============ Static Setter ============  */
	public static void setStaticKerbyProperties(String _user, String _pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
		user = _user;
		pass = SecurityHelper.generateKeyFromPassword(_pass);
	}
	
	/* ============ Helpers ============  */
	private void printTicket(Ticket ticket) {
		System.out.println("\n============ TICKET ============");
		System.out.println("X:   " + ticket.getX());
		System.out.println("Y:   " + ticket.getY());
		System.out.println("Key: " + printHexBinary(ticket.getKeyXY().getEncoded()));
		System.out.println("T1:  " + ticket.getTime1() + " (" + ticket.getTime1().getTime() + " ms)");
		System.out.println("T2:  " + ticket.getTime2() + " (" + ticket.getTime2().getTime() + " ms)");
		System.out.println("================================\n");
	}
}
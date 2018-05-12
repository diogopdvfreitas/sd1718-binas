package example.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

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

import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;

/**
 * This SOAPHandler shows how to set/get values from headers in inbound/outbound
 * SOAP messages.
 *
 * A header is created in an outbound message and is read on an inbound message.
 *
 * The value that is read from the header is placed in a SOAP message context
 * property that can be accessed by other handlers or by the application.
 */
public class EveSimulatorHandler implements SOAPHandler<SOAPMessageContext> {

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
			if (!outboundElement.booleanValue()) {
				System.out.println("\n-------------------- SNIFF SNIFF I AM EVE AND I AM EVIL SNIFF SNIFF --------------------");
				
				// Message IN
				// Simulate man in the middle

				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();

				// check header
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}
				
				CipheredView cipheredTicket = getCipheredTicketFromHeader(se, sh);
				System.out.println("Got ciphered ticket: " + printHexBinary(cipheredTicket.getData()));

				System.out.println("------------------------------- FINISHED MY EVIL WORK ----------------------------------\n");
			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

		return true;
	}
	
	private CipheredView getCipheredTicketFromHeader(SOAPEnvelope se, SOAPHeader sh) throws KerbyException, RuntimeException, SOAPException {
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
		return cipheredTicket;
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

}
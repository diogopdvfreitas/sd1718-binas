package example.ws.handler;

import java.util.Iterator;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
	
	private final String XPATH_ACTIVATE_USER = "//SOAP-ENV:Envelope/SOAP-ENV:Body/ns2:activateUser/email/text()";
	private final String XPATH_REQUEST_TIME = "//SOAP-ENV:Envelope/SOAP-ENV:Header/kerby:requestTime/text()";
	private final String EVE_EMAIL = "eve@A37.binas.org";
	private static boolean TO_SERVER = false;

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
			System.out.println("\n------------------------------- I AM EVE: CAUGHT MESSAGE -------------------------------\n");
			if (!outboundElement.booleanValue()) {
				if (TO_SERVER) {
					
					// Message IN
					// Simulate man in the middle
					
					SOAPMessage msg = smc.getMessage();
					// SOAPMessage newMsg = temperNodeValueFromXPath(msg, XPATH_ACTIVATE_USER, EVE_EMAIL);
					SOAPMessage newMsg = temperNodeValueFromXPath(msg, XPATH_REQUEST_TIME, EVE_EMAIL);
					if (newMsg != null) {
						msg = newMsg;
					}
					
				} else {					
					SOAPMessage msg = smc.getMessage();
					
					/*
					SOAPMessage newMsg = temperNodeValueFromXPath(msg, XPATH_REQUEST_TIME, EVE_EMAIL);
					if (newMsg != null) {
						msg = newMsg;
					}
					*/
					
				}
			}
			System.out.println("\n------------------------------- FINISHED MY EVIL WORK ----------------------------------\n");
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

		return true;
	}
	
	/* ------------ Tempering helpers --------------- */
	
	private SOAPMessage temperNodeValueFromXPath(SOAPMessage msg, String xPathExpression, String newNodeValue) throws Exception {
		Document document = SOAPMessageToDOMDocument(msg);
		
		// Create XPath object to navigate DOM tree
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        // Define namespace context
        xPath.setNamespaceContext(getNamespaceContext());

        XPathExpression expr = xPath.compile(xPathExpression);

        Object result = expr.evaluate(document, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        
        if (nodes.getLength() > 1 || nodes.getLength() == 0 ||
        		nodes.item(0).getNodeValue() == null || nodes.item(0).getNodeValue().length() == 0) {
        	return null;
        }
        
        System.out.println("Using XPath expression: " + xPathExpression);
        System.out.println("Changing " + nodes.item(0).getNodeValue() + " to " + newNodeValue);
        nodes.item(0).setNodeValue(newNodeValue);
		
		return DOMDocumentToSOAPMessage(document);
	}
	
	/* ----------- XML helpers ------------- */
	
	private Document SOAPMessageToDOMDocument(SOAPMessage msg) throws Exception {

        // SOAPPart implements org.w3c.dom.Document interface
        Document part = msg.getSOAPPart();

        return part;
    }

    private SOAPMessage DOMDocumentToSOAPMessage(Document doc) throws Exception {
        SOAPMessage newMsg = null;

        MessageFactory mf = MessageFactory.newInstance();
        newMsg = mf.createMessage();
        SOAPPart soapPart = newMsg.getSOAPPart();
        soapPart.setContent(new DOMSource(doc));

        return newMsg;
    }
    
    private NamespaceContext getNamespaceContext() {
    	return new NamespaceContext() {

            @Override
            public String getNamespaceURI(String prefix) {
                if (prefix == null) throw new NullPointerException("Null prefix");
                else if ("SOAP-ENV".equals(prefix)) return "http://schemas.xmlsoap.org/soap/envelope/";
                else if ("ns2".equals(prefix)) return "http://ws.binas.org/";
                else if ("kerby".equals(prefix)) return "http://ws.binas.org/";
                else if ("schema".equals(prefix)) return "http://www.w3.org/2001/XMLSchema-instance";
                else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
                return XMLConstants.NULL_NS_URI;
            }

            // This method is not necessary for XPath processing.
            public String getPrefix(String uri) {
                throw new UnsupportedOperationException();
            }

            // This method is not necessary for XPath processing either.
            public Iterator<?> getPrefixes(String uri) {
                throw new UnsupportedOperationException();
            }

        };
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
	
	// Declares if this handler will deal with messages going to the server or to the client
	public static void toServer() {
		TO_SERVER = true;
	}
	
	public static void toClient() {
		TO_SERVER = false;
	}

}
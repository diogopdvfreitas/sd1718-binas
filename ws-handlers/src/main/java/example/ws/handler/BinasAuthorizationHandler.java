package example.ws.handler;

import java.util.Iterator;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.kerby.Ticket;

public class BinasAuthorizationHandler implements SOAPHandler<SOAPMessageContext> {
	
	private final String XPATH_ACTIVATE_USER = "//SOAP-ENV:Envelope/SOAP-ENV:Body/ns2:activateUser/email/text()";
	private final String XPATH_GET_CREDIT = "//SOAP-ENV:Envelope/SOAP-ENV:Body/ns2:getCredit/email/text()";
	private final String XPATH_RENT_BINA = "//SOAP-ENV:Envelope/SOAP-ENV:Body/ns2:rentBina/email/text()";
	private final String XPATH_RETURN_BINA = "//SOAP-ENV:Envelope/SOAP-ENV:Body/ns2:returnBina/email/text()";	
	
	private final String[] XPATH = {XPATH_ACTIVATE_USER, XPATH_GET_CREDIT, XPATH_RENT_BINA, XPATH_RETURN_BINA};

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		try {
			if (!outboundElement.booleanValue()) {

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();								
				Document document = SOAPMessageToDOMDocument(msg);
				
				// Create XPath object to navigate DOM tree
		        XPathFactory xPathFactory = XPathFactory.newInstance();
		        XPath xPath = xPathFactory.newXPath();

		        // Define namespace context
		        xPath.setNamespaceContext(getNamespaceContext());
		        
		        Object result = null;
		        
		        Ticket ticket = (Ticket) smc.get(KerberosServerHandler.TICKET);
		        
		        // Search for user email in SOAP Message
		        for (String xpath : XPATH) {
		        	XPathExpression expr = xPath.compile(xpath);
			        result = expr.evaluate(document, XPathConstants.NODESET);
			        if (result != null) {
			        	NodeList nodes = (NodeList) result;
			            
			            if (nodes.getLength() == 1 && nodes.item(0).getNodeValue() != null && 
			            		nodes.item(0).getNodeValue().length() > 0 && !nodes.item(0).getNodeValue().equals(ticket.getX())) {
			            	throw new RuntimeException("Email doesn't match (XPATH = " + xpath + ").");
			            }
			        }
		        }
		        
		        if (result == null) {
		        	throw new RuntimeException("Couldn't find email in SOAP Message.");
		        }
			}
			
		} catch (Exception e) {
				System.out.print("Caught exception in handleMessage: ");
				System.out.println(e);
				System.out.println("Continue normal processing...");
				throw new RuntimeException();
		}
		
		return true;
	}
	
	private NamespaceContext getNamespaceContext() {
    	return new NamespaceContext() {

            @Override
            public String getNamespaceURI(String prefix) {
                if (prefix == null) throw new NullPointerException("Null prefix");
                else if ("SOAP-ENV".equals(prefix)) return "http://schemas.xmlsoap.org/soap/envelope/";
                else if ("ns2".equals(prefix)) return "http://ws.binas.org/";
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
	
	/* ----------- XML helpers ------------- */
	
	private Document SOAPMessageToDOMDocument(SOAPMessage msg) throws Exception {

        // SOAPPart implements org.w3c.dom.Document interface
        Document part = msg.getSOAPPart();

        return part;
    }
	
	@Override
	public void close(MessageContext arg0) {}

	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("Ignoring fault message...");
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

}

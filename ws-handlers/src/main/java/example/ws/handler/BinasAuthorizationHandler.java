package example.ws.handler;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class BinasAuthorizationHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		try {
			if (outboundElement.booleanValue()) {

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();
			}
			
			else {
				
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();
				
				
			} 
			
		} catch (Exception e) {
				System.out.print("Caught exception in handleMessage: ");
				System.out.println(e);
				System.out.println("Continue normal processing...");
				throw new RuntimeException();
		}
		
		
		
		return true;
	}
	
	@Override
	public void close(MessageContext arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		System.out.println("Ignoring fault message...");
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

}

package example.ws.handler;

import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import java.security.Key;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class MACGeneratorHandler  implements SOAPHandler<SOAPMessageContext>{
	
	public static final String MAC_NAME = "MAC";
	public static final String MAC_PREFIX = "kerby";
	public static final String MAC_NAMESPACE = "http://ws.binas.org/";
	
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {		
		
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		try {
			if (outboundElement.booleanValue()) {
				
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				
				DOMSource source = new DOMSource(se);
				StringWriter stringResult = new StringWriter();
				TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
				String message = stringResult.toString();
				
				Key sessionKey = (Key) smc.get(KerberosClientHandler.SESSION_KEY);
				
				String sessionKeyHexEncoded = printHexBinary(sessionKey.getEncoded());
				String result = message + sessionKeyHexEncoded;
				
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				messageDigest.update(result.getBytes());
				byte[] hmac = messageDigest.digest();
				
				generateMACHeader(se, hmac);
				
			}			
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
			throw new RuntimeException();
		}
		
		return true;
	}
		
	private void generateMACHeader(SOAPEnvelope se, byte[] hmac) throws SOAPException {
		
		// add header
		SOAPHeader sh = se.getHeader();
		if (sh == null)
			sh = se.addHeader();
		
		// add header element (name, namespace prefix, namespace)
		Name name = se.createName(MAC_NAME, MAC_PREFIX, MAC_NAMESPACE);
		SOAPHeaderElement element = sh.addHeaderElement(name);

		// add header element value
		String authHexEncoded = printHexBinary(hmac);
		element.addTextNode(authHexEncoded);
	}
	
	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		System.out.println("Ignoring fault message...");
		return true;
	}

	@Override
	public void close(MessageContext arg0) {}
	
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

}

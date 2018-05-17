package example.ws.handler;

import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
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

import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class MACHandler  implements SOAPHandler<SOAPMessageContext>{
	
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
				
				DOMSource source = new DOMSource(sb);
				StringWriter stringResult = new StringWriter();
				TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
				String message = stringResult.toString();
				
				SessionKey sessionKey = (SessionKey) smc.get(KerberosClientHandler.SESSION_KEY);
				String sessionKeyHexEncoded = printHexBinary(sessionKey.getKeyXY().getEncoded());
				String result = message + sessionKeyHexEncoded;
				
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				messageDigest.update(result.getBytes());
				byte[] hmac = messageDigest.digest();
				
				generateMACHeader(se, hmac);
				
				/*
				 * StringWriter stringResult = new StringWriter();
				TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
				String message = stringResult.toString();
				 */
				
				
				System.out.println("------------------------------------- eu sou a mensagem que tu queres Joao\n" + message);
				
			} else {
				
				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();
				SOAPHeader sh = se.getHeader();
				
				DOMSource source = new DOMSource(sb);
				StringWriter stringResult = new StringWriter();
				TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
				String message = stringResult.toString();
				
				SessionKey sessionKey = (SessionKey) smc.get(KerberosClientHandler.SESSION_KEY);
				String sessionKeyHexEncoded = printHexBinary(sessionKey.getKeyXY().getEncoded());
				String result = message + sessionKeyHexEncoded;
				
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				messageDigest.update(result.getBytes());
				byte[] hmac = messageDigest.digest();
				
				generateMACHeader(se, hmac);
				
				// check header
				if (sh == null) {
					throw new RuntimeException("Header not found.");
				} 
				
				byte[] hmacFromClient = getMACFromHeader(se, sh);
				
				/*
				// put ticket in a property context
				smc.put(byte[], hmacFromClient);
				*/
				
				boolean messageIntegrity =  Arrays.equals(hmac, hmacFromClient);
				
				if(!messageIntegrity) { 
					System.out.println("MAC aren´t the same");
					throw new RuntimeException();
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
		
	private void generateMACHeader(SOAPEnvelope se, byte[] hmac) throws SOAPException {
		
		// add header
		SOAPHeader sh = se.getHeader();
		if (sh == null)
			sh = se.addHeader();
		
		// add header element (name, namespace prefix, namespace)
		Name name = se.createName("MAC", "kerby", "http://ws.binas.org/");
		SOAPHeaderElement element = sh.addHeaderElement(name);

		// add header element value
		String authHexEncoded = printHexBinary(hmac);
		element.addTextNode(authHexEncoded);
	}
	
	private byte[] getMACFromHeader(SOAPEnvelope se, SOAPHeader sh) throws KerbyException, RuntimeException, SOAPException {
		Name name = se.createName("MAC", "kerby", "http://ws.binas.org/");
		Iterator<?> it = sh.getChildElements(name);
		
		if (!it.hasNext()) {
			System.out.println("Header element not found.");
			throw new RuntimeException("Ticket not found");
		}
		
		SOAPElement element = (SOAPElement) it.next();

		String hexEncodedHmac = element.getValue();

		CipheredView cipheredHmac = new CipheredView();
		
		cipheredHmac.setData(parseHexBinary(hexEncodedHmac));
		
		byte[] hmac = cipheredHmac.getData();
		return hmac;
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

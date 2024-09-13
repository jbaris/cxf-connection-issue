package org.example;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import org.apache.cxf.transport.http.HttpConduitConfig;
import org.apache.cxf.transport.http.HttpConduitFeature;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.Map;

public class CxfIssueTest {

    @Test
    public void testApp() throws Exception {
        System.out.println("Start");
        URL wsdlURL = new URL("http://www.google.com:88/calculator.asmx?WSDL");
        //URL wsdlURL = new URL("http://www.dneonline.com/calculator.asmx?WSDL");
        QName serviceName = new QName("http://tempuri.org/", "Calculator");
        QName portName = new QName("http://tempuri.org/", "CalculatorSoap");
        final HttpConduitFeature httpConduitFeature = new HttpConduitFeature();
        final HttpConduitConfig conduitConfig = new HttpConduitConfig();
        final HTTPClientPolicy clientPolicy = new HTTPClientPolicy();
        clientPolicy.setMaxRetransmits(1);
        clientPolicy.setConnectionRequestTimeout(1000);
        clientPolicy.setConnectionTimeout(1500);
        conduitConfig.setClientPolicy(clientPolicy);
        httpConduitFeature.setConduitConfig(conduitConfig);
        Service service = Service.create(wsdlURL, serviceName, httpConduitFeature);
        Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
        SOAPMessage request = createSOAPRequest(10, 5);
        Map<String, Object> requestContext = dispatch.getRequestContext();
        requestContext.put(Dispatch.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
        requestContext.put(Dispatch.SOAPACTION_URI_PROPERTY, "http://tempuri.org/Add");
        SOAPMessage response = dispatch.invoke(request);
        int result = extractResultFromSOAPResponse(response);
        System.out.println("Finish. Addition Result: " + result);
    }

    // Method to create the SOAP request for the 'add' operation
    private static SOAPMessage createSOAPRequest(int intA, int intB) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();
        QName bodyName = new QName("http://tempuri.org/", "Add");
        jakarta.xml.soap.SOAPBodyElement bodyElement = body.addBodyElement(bodyName);
        bodyElement.addChildElement("intA").addTextNode(String.valueOf(intA));
        bodyElement.addChildElement("intB").addTextNode(String.valueOf(intB));
        soapMessage.saveChanges();
        return soapMessage;
    }

    private static int extractResultFromSOAPResponse(SOAPMessage response) throws Exception {
        SOAPBody body = response.getSOAPBody();
        return Integer.parseInt(body.getElementsByTagName("AddResult").item(0).getTextContent());
    }
}

package VPI.VXMLClasses;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import java.io.StringWriter;

@XmlRootElement(name="Envelope")
@XmlType(propOrder = {"header", "body"})
public class XMLEnvelope {

    private XMLHeader header;
    private XMLBody body;

    public XMLEnvelope() {
    }

    @XmlElement(name="Header")
    public XMLHeader getHeader() {
        return header;
    }

    public void setHeader(XMLHeader header) {
        this.header = header;
    }

    @XmlElement(name="Body")
    public XMLBody getBody() {
        return body;
    }

    public void setBody(XMLBody body) {
        this.body = body;
    }

    @Override
    public String toString() {
        try {
            JAXBElement<XMLEnvelope> jaxbElement = new JAXBElement<>(new QName("Envelope"), XMLEnvelope.class, this);
            StringWriter writer = new StringWriter();
            JAXBContext ctx = JAXBContext.newInstance(XMLEnvelope.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(jaxbElement, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "MARSHALLING FAILED BUT THERES SOMETHING HERE";
    }
}

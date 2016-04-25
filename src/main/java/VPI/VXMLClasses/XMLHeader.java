package VPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;
public class XMLHeader {

    private XMLBasicAuth basicAuth;

    public XMLHeader() {
    }

    @XmlElement(name="BasicAuth")
    public XMLBasicAuth getBasicAuth() {
        return basicAuth;
    }

    public void setBasicAuth(XMLBasicAuth basicAuth) {
        this.basicAuth = basicAuth;
    }
}

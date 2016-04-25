package VPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;
public class XMLBasicAuth {

    private String name;
    private String password;

    public XMLBasicAuth() {
    }

    @XmlElement(name="Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="Password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

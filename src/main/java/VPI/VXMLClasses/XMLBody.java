package VPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;
public class XMLBody {

    private XMLQuery query;

    public XMLBody() {
    }

    @XmlElement(name="Query")
    public XMLQuery getQuery() {
        return query;
    }

    public void setQuery(XMLQuery query) {
        this.query = query;
    }
}

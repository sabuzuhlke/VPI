package VPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"selection", "resultdef"})
public class XMLQuery {

    private XMLSelection selection;
    private XMLResultDef resultdef;

    public XMLQuery() {
    }

    @XmlElement(name="Selection")
    public XMLSelection getSelection() {
        return selection;
    }

    public void setSelection(XMLSelection selection) {
        this.selection = selection;
    }

    @XmlElement(name="Resultdef")
    public XMLResultDef getResultdef() {
        return resultdef;
    }

    public void setResultdef(XMLResultDef resultdef) {
        this.resultdef = resultdef;
    }
}

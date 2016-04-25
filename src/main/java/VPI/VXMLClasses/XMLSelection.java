package VPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;

public class XMLSelection {

    private Long objref;

    private String ocl;

    private String sqlWhere;

    private String sqlOrder;

    public XMLSelection() {
    }

    @XmlElement(name="objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }

    @XmlElement(name="ocl")
    public String getOcl() {
        return ocl;
    }

    public void setOcl(String ocl) {
        this.ocl = ocl;
    }

    @XmlElement(name="sqlwhere")
    public String getSqlWhere() {
        return sqlWhere;
    }

    public void setSqlWhere(String sqlWhere) {
        this.sqlWhere = sqlWhere;
    }

    @XmlElement(name="sqlorder")
    public String getSqlOrder() {
        return sqlOrder;
    }

    public void setSqlOrder(String sqlOrder) {
        this.sqlOrder = sqlOrder;
    }
}

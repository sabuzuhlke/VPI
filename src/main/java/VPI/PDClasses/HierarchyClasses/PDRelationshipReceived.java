package VPI.PDClasses.HierarchyClasses;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PDRelationshipReceived {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("rel_owner_org_id")
    private LinkedOrg parent;
    @JsonProperty("rel_linked_org_id")
    private LinkedOrg daughter;

    public PDRelationshipReceived() {
    }

    public PDRelationshipReceived(LinkedOrg parent, LinkedOrg daughter) {
        this.parent = parent;
        this.daughter = daughter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LinkedOrg getParent() {
        return parent;
    }

    public void setParent(LinkedOrg parent) {
        this.parent = parent;
    }

    public LinkedOrg getDaughter() {
        return daughter;
    }

    public void setDaughter(LinkedOrg daughter) {
        this.daughter = daughter;
    }
    @Override
    public String toString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }
}


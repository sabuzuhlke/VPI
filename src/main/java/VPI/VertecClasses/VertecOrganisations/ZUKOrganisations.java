package VPI.VertecClasses.VertecOrganisations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ZUKOrganisations {

    @JsonProperty("organisations")
    private List<JSONOrganisation> organisationList;

    @JsonProperty("danglingContacts")
    private List<JSONContact> danglingContacts;

    public ZUKOrganisations() {
        this.organisationList = new ArrayList<>();
        this.danglingContacts = new ArrayList<>();
    }

    public List<JSONOrganisation> getOrganisationList() {
        return organisationList;
    }

    public void setOrganisationList(List<JSONOrganisation> organisationList) {
        this.organisationList = organisationList;
    }

    public List<JSONContact> getDanglingContacts() {
        return danglingContacts;
    }

    public void setDanglingContacts(List<JSONContact> danglingContacts) {
        this.danglingContacts = danglingContacts;
    }

    public String toPrettyJSON() {
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
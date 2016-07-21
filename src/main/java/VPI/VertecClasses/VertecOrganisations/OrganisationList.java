package VPI.VertecClasses.VertecOrganisations;


import VPI.Entities.Organisation;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class OrganisationList {

    private List<VPI.VertecClasses.VertecOrganisations.Organisation> organisations;

    public OrganisationList() {
    }

    public List<VPI.VertecClasses.VertecOrganisations.Organisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<VPI.VertecClasses.VertecOrganisations.Organisation> organisations) {
        this.organisations = organisations;
    }

    public String toJSONString(){
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

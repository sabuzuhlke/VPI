package VPI.Entities;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OrganisationContainer {
    public Map<Long, Organisation> organisationMap;
    public List<Organisation> orgsWithoutVID;

    public OrganisationContainer(Map<Long, Organisation> organisationMap, List<Organisation> orgsWithoutVID) {
        this.organisationMap = organisationMap;
        this.orgsWithoutVID = orgsWithoutVID;
    }

    public Organisation getByV(Long id) {
        return organisationMap.get(id);
    }

    public Organisation getByP(Long id) {
        List<Organisation> all = new ArrayList<>();
        all.addAll(organisationMap.values());
        all.addAll(orgsWithoutVID);

        for(Organisation org : all){
            if(org.getPipedriveId() == id.longValue()){
                return org;
            }
        }
        return null;
    }

}

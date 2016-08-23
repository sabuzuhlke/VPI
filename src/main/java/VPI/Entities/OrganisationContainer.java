package VPI.Entities;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrganisationContainer {
    public Map<Long, Organisation> orgsWithVIDs;
    public List<Organisation> orgsWithoutVID;

    public OrganisationContainer(Map<Long, Organisation> orgsWithVIDs, List<Organisation> orgsWithoutVID) {
        this.orgsWithVIDs = orgsWithVIDs;
        this.orgsWithoutVID = orgsWithoutVID;
    }

    public Organisation getByV(Long id) {
        return orgsWithVIDs.get(id);
    }

    public Organisation getByP(Long id) {
        List<Organisation> all = getAll();

        for (Organisation org : all) {
            if (org.getPipedriveId() == id.longValue()) {
                return org;
            }
        }
        return null;
    }

    public List<Organisation> getAll() {
        List<Organisation> all = new ArrayList<>();
        all.addAll(orgsWithVIDs.values());
        all.addAll(orgsWithoutVID);
        return all;
    }

}

package VPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 13/04/2016.
 */
public class OrganisationComparator {

    private List<PDOrganisation> PDOrganisations = null;
    private List<ICompany> VOrganisations = null;

    private List<PDOrganisation> putList = null;
    private List<PDOrganisation> postList = null;


    public OrganisationComparator() {
    }


    public void compareOrgs() {
        putList = new ArrayList<>();
        postList = new ArrayList<>();

        for(ICompany c : VOrganisations) {
            Boolean matched = false;
            for(PDOrganisation pd : PDOrganisations) {
                if ((c.getName().equals(pd.getName())) || c.getName().toLowerCase().equals(pd.getName().toLowerCase())) {
                    matched = true;
                    //check if other attirubtes are equal
                    //if so, remove from pd list, else create PUT
                }
            }
            if(!matched){
                PDOrganisation OrgToPost = new PDOrganisation(c);
                postList.add(OrgToPost);
            }
        }
    }

    public void clear(){
        PDOrganisations.clear();
        VOrganisations.clear();
        putList.clear();
        postList.clear();
    }

    public List<PDOrganisation> getPDOrganisations() {
        return PDOrganisations;
    }

    public void setPDOrganisations(List<PDOrganisation> PDOrganisations) {
        this.PDOrganisations = PDOrganisations;
    }

    public List<ICompany> getVOrganisations() {
        return VOrganisations;
    }

    public void setVOrganisations(List<ICompany> VOrganisations) {
        this.VOrganisations = VOrganisations;
    }

    public List<PDOrganisation> getPutList() {
        return putList;
    }

    public void setPutList(List<PDOrganisation> putList) {
        this.putList = putList;
    }

    public List<PDOrganisation> getPostList() {
        return postList;
    }

    public void setPostList(List<PDOrganisation> postList) {
        this.postList = postList;
    }
}

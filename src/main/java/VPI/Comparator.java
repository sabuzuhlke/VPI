package VPI;

import java.util.ArrayList;
import java.util.List;

public class Comparator {

    private List<PDOrganisation> PDOrganisations = null;
    private List<VOrganisation> VOrganisations = null;

    private List<PDOrganisation> putList = null;
    private List<PDOrganisation> postList = null;


    public Comparator() {
    }


    public void compareOrgs() {
        putList = new ArrayList<>();
        postList = new ArrayList<>();

        for(VOrganisation c : VOrganisations) {
            //personList = get(org/c/persons)
            Boolean matched = false;
            Boolean modified = false;
            for(PDOrganisation pd : PDOrganisations) {
                if ((c.getName().equals(pd.getName())) || c.getName().toLowerCase().equals(pd.getName().toLowerCase())) {
                    matched = true;
                    //check if other attirubtes are equal
                    //if so, remove from pd list, else create PUT
                    String cAddress = c.getFormattedAddress();
                    if(!cAddress.equals(pd.getAddress())
                            && (c.getStreet() != null)
                            && (c.getCity() != null)
                            && (c.getZip() != null)
                            && (c.getCountry() != null)){
                        pd.setAddress(cAddress);
                        modified = true;
                    }
                    //More Comparisons
                    if(modified){
                        putList.add(pd);
                    }
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

    public List<VOrganisation> getVOrganisations() {
        return VOrganisations;
    }

    public void setVOrganisations(List<VOrganisation> VOrganisations) {
        this.VOrganisations = VOrganisations;
    }

    public List<PDOrganisation> getPutList() {
        return putList;
    }

    public List<PDOrganisation> getPostList() {
        return postList;
    }

}

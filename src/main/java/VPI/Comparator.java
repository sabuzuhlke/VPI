package VPI;

import VPI.PDClasses.PDContactReceived;
import VPI.PDClasses.PDContactSend;
import VPI.PDClasses.PDOrganisation;
import VPI.VClasses.VContact;
import VPI.VClasses.VOrganisation;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.List;

public class Comparator {

    private List<PDOrganisation> PDOrganisations = null;
    private List<VOrganisation> VOrganisations = null;

    private List<PDOrganisation> organisationPutList = null;
    private List<PDOrganisation> organisationPostList = null;


    private List<VContact> VContacts = null;
    private List<PDContactReceived> PDContacts = null;

    private List<PDContactSend> contactPutList = null;
    private List<PDContactSend> contactPostList = null;


    public Comparator() {
    }


    public void compareOrgs() {
        organisationPutList = new ArrayList<>();
        organisationPostList = new ArrayList<>();

        List<VContact> VorgContactList;
        List<PDContactReceived> PDOrgContactList;

        int vIndex = 0; // represents index into VContacts
        for(VOrganisation c : VOrganisations) {
            //personList = get(org/c/persons)
            Boolean matched = false;
            Boolean modified = false;
            Long currVId = c.getId();
            VorgContactList = new ArrayList<>();
            while ((VContacts.get(vIndex)  != null) && (VContacts.get(vIndex).getOrg_id() == currVId)) {
                VorgContactList.add(VContacts.get(vIndex));
                ++vIndex;
            }
            int pdIndex = 0; // represents index into PDContacts
            for(PDOrganisation pd : PDOrganisations) {
                if ((c.getName().equals(pd.getName())) || c.getName().toLowerCase().equals(pd.getName().toLowerCase())) {
                    matched = true;
                    //check if other attirubtes are equal
                    //if so, remove from pd list, else create PUT
                    //RESOLVE ADDRESS
                    String cAddress = c.getFormattedAddress();
                    if(!cAddress.equals(pd.getAddress())
                            && (c.getStreet() != null)
                            && (c.getCity() != null)
                            && (c.getZip() != null)
                            && (c.getCountry() != null)){
                        pd.setAddress(cAddress);
                        modified = true;
                    }
                    //RESOLVECONTACTS
                    Long currPDId = pd.getId();
                    PDOrgContactList = new ArrayList<>();
                    while (PDContacts.get(pdIndex).getOrg_id().getValue() == currPDId) {
                        PDOrgContactList.add(PDContacts.get(pdIndex));
                        ++pdIndex;
                    }
                    //compare our lists and out put contacts found in v but not in pd to post and contacts with shared fields to put
                    compareContacts(VorgContactList,PDOrgContactList);
                    //More Comparisons
                    if(modified){
                        organisationPutList.add(pd);
                    }
                }
            }
            if(!matched){
                PDOrganisation OrgToPost = new PDOrganisation(c);
                organisationPostList.add(OrgToPost);

                for(VContact vc: VorgContactList) {
                    PDContactSend cnt = new PDContactSend(vc);
                    contactPostList.add(cnt);
                }
                //add all vcontacts for vorg to postlist
            }
        }
    }

    public void clear(){
        PDOrganisations.clear();
        VOrganisations.clear();
        organisationPutList.clear();
        organisationPostList.clear();
    }

    private void compareContacts(List<VContact> vContactList, List<PDContactReceived> pDContactList){

        for(VContact v : vContactList){

            Boolean matched = false;
            Boolean modified = false;
            for(PDContactReceived p : pDContactList){

                if(v.getName().equals(p.getName())) {//match

                    matched = true;
                    if (!v.getName().equals(p.getName())) {
                        modified = true;
                        p.setName(v.getName());
                    }

                    if (modified) {
                        contactPutList.add(new PDContactSend(p));
                        System.out.println("Added CONTACT " + p.getName() + " to PUT");
                    }
                }

            }
            if(!matched && !modified){
                contactPostList.add(new PDContactSend(v));
                System.out.println("Added CONTACT " + v.getName() + " to POST");
            }
        }
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

    public List<PDOrganisation> getOrganisationPutList() {
        return organisationPutList;
    }

    public List<PDOrganisation> getOrganisationPostList() {
        return organisationPostList;
    }

    public List<VContact> getVContacts() {
        return VContacts;
    }

    public void setVContacts(List<VContact> VContacts) {
        this.VContacts = VContacts;
    }

    public List<PDContactReceived> getPDContacts() {
        return PDContacts;
    }

    public void setPDContacts(List<PDContactReceived> PDContacts) {
        this.PDContacts = PDContacts;
    }

    public List<PDContactSend> getContactPutList() {
        return contactPutList;
    }

    public List<PDContactSend> getContactPostList() {
        return contactPostList;
    }
}

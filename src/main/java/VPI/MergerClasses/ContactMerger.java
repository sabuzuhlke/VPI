package VPI.MergerClasses;

import VPI.Entities.Contact;
import VPI.GlobalClass;
import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.Organisation;
import VPI.VertecClasses.VertecService;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.ArrayList;
import java.util.List;

public class ContactMerger {
    public PDService PS;
    public VertecService VS;
    public List<List<Long>> uncertainMerges;
    public List<Long> noMergesFound;

    public ContactMerger(PDService PS, VertecService VS){

        this.PS = PS;
        this.VS = VS;

        this.uncertainMerges = new ArrayList<>();
        this.noMergesFound = new ArrayList<>();
    }

    public void doMerge(){
        DualHashBidiMap<Long, Long> mergedContacts = findVcontactsMergedOnPD();// <merged, surviving>
        for(Long id : mergedContacts.keySet()){
            //VS.mergeTwoContacts(id,mergedContacts.get(id));
        }
        for(Long id : noMergesFound){
            JSONContact c = VS.getContact(id).getBody();
            GlobalClass.log.info("No Merge found for Contact " + c.getFirstName() + " " + c.getSurname() + " (v_id: " + c.getObjid() + ")");
        }
        for(List<Long> conflict : uncertainMerges){
            JSONContact mergedC = VS.getContact(conflict.get(0)).getBody();
            JSONContact survivingC = VS.getContact(conflict.get(1)).getBody();
            GlobalClass.log.info("uncertanty in merge found for missing contact "
                    + mergedC.getFirstName() + " " + mergedC.getSurname() + " (v_id: " + mergedC.getObjid() + ")" +
                    ", potential merge into contact: "
                    + survivingC.getFirstName() + " " + survivingC.getSurname() + " (v_id: " + survivingC.getObjid() + ")");
        }
    }

    private DualHashBidiMap<Long, Long> findVcontactsMergedOnPD() {
        //get all contacts from vertec
        //load idmap of contacts previously posted to vertec
        //get all contacts from pipedrive
        //find ids that are not present on PD but are present in the idmap --> these guys have either been merged or deleted
        //for each missing contact try to find out whom it has been merged into. Best way probably would be to start with e-mail addresses TODO write VRAPI query to get multiple Email addresses
        // , then with activities /Projects and organisations won't provide a definitive mapping on their own (multiple contacts at a company), but might be useful for deciding between uncertain matches./


        return null;
    }
}

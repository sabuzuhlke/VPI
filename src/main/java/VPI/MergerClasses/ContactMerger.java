package VPI.MergerClasses;

import VPI.Entities.Contact;
import VPI.Entities.util.Utilities;
import VPI.GlobalClass;
import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.Organisation;
import VPI.VertecClasses.VertecService;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListTransducedAccessorImpl;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.tomcat.jni.Global;
import org.apache.tomcat.util.modeler.Util;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static VPI.Entities.util.Utilities.loadIdMap;
import static java.util.stream.Collectors.*;

public class ContactMerger {
    public PDService PS;
    public VertecService VS;

    //List of Pairs where fst is VertecId of missing org, sndis VertecId of potential match (emails matched)
    public List<List<Long>> uncertainMerges;
    public List<Long> noMergesFound;

    public ContactMerger(PDService PS, VertecService VS){

        this.PS = PS;
        this.VS = VS;

        this.uncertainMerges = new ArrayList<>();
        this.noMergesFound = new ArrayList<>();
    }

    public void doMerge() throws IOException {
        //get all contacts from vertec --> sure about this?
        //load idmap of contacts previously posted to vertec
        Map<Long,Long> contactsPostedToPD = loadIdMap("productionMaps/productionContactMap");
        //get all contacts from pipedrive
        List<Contact> pipedriveContacts= PS.getAllContacts()
                .getBody()
                .getData()
                .stream()
                .map(c -> new Contact(c, null, null))
                .collect(toList());

        //find ids that are not present on PD but are present in the idmap --> these guys have either been merged or deleted
        List<Long> idsOnPD = pipedriveContacts.stream()
                .map(Contact::getVertecId)
                .collect(toList());

        List<Long> missingIds = contactsPostedToPD.keySet().stream()
        .filter(id -> ! idsOnPD.contains(id))
        .collect(toList());

        System.out.println("Number of missing contacts: " + missingIds.size());

        //TODO get contacts Vrom vertec as Contact based on missingIds

        List<Contact> missingContacts = new ArrayList<>();
        //for each missing contact try to find out whom it has been merged into. Best way probably would be to start with e-mail addresses
        // , then with activities /Projects and organisations won't provide a definitive mapping on their own (multiple contacts at a company), but might be useful for deciding between uncertain matches./
        HashMap<Long, Long> mergedContacts = findVcontactsMergedOnPD(missingContacts, pipedriveContacts);// <merged, surviving>
        for(Long id : mergedContacts.keySet()){
            //VS.mergeTwoContacts(id,mergedContacts.get(id));
        }
        System.out.println("=======> No merges have been found for the following contacts: " + noMergesFound.size());
        for(Long id : noMergesFound){
            JSONContact c = VS.getContact(id).getBody();
            GlobalClass.log.info("No Merge found for Contact " + c.getFirstName() + " " + c.getSurname() + " (v_id: " + c.getObjid() + ")");
        }
        System.out.println("=======> Could not uniquely identify merge by email for the following: " + uncertainMerges.size());
        for(List<Long> conflict : uncertainMerges){
            JSONContact mergedC = VS.getContact(conflict.get(0)).getBody();
            JSONContact survivingC = VS.getContact(conflict.get(1)).getBody();
            GlobalClass.log.info("uncertanty in merge found for missing contact "
                    + mergedC.getFirstName() + " " + mergedC.getSurname() + " (v_id: " + mergedC.getObjid() + ")" +
                    ", potential merge into contact: "
                    + survivingC.getFirstName() + " " + survivingC.getSurname() + " (v_id: " + survivingC.getObjid() + ")");
        }
    }

    public HashMap<Long, Long> findVcontactsMergedOnPD(List<Contact> vertecContacts, List<Contact> pipedriveContacts) {


        HashMap<Long, Long> map = new HashMap<>();
        Map<Long, List<Contact>> countMap = new HashMap<>();
        vertecContacts.forEach(contact -> {

            pipedriveContacts.forEach(pipedrive -> {

               pipedrive.getEmails().forEach(email -> {

                   if (email.getValue().equals(contact.getEmails().get(0).getValue())) {

                       if (countMap.containsKey(contact.getVertecId())) {
                           countMap.get(contact.getVertecId()).add(pipedrive);
                       } else {
                           List<Contact> l = new ArrayList<>();
                           l.add(pipedrive);
                           countMap.put(contact.getVertecId(), l);
                       }
                   }

               });

            });
            List<Contact> matchedContacts = countMap.get(contact.getVertecId()) == null ? new ArrayList<>() : countMap.get(contact.getVertecId());
            if (matchedContacts.size() == 1) {
                System.out.println("Unique match found for contact: " + contact.getFullName() + ", Vertec ID " + contact.getVertecId()
                        + " matched to " + matchedContacts.get(0).getFullName() + " VertecID " + matchedContacts.get(0).getVertecId());
                map.put(contact.getVertecId(), matchedContacts.get(0).getVertecId());
            } else if (matchedContacts.size() == 0) {
                System.out.println("No matched found for contact " + contact.getFullName() + ", Vertec ID " + contact.getVertecId());
                noMergesFound.add(contact.getVertecId());
            } else {
                System.out.println("Mulptiple matches found for contact: " + contact.getFullName() + ", Vertec ID " + contact.getVertecId());
                matchedContacts.forEach(matchedContact -> {
                    System.out.println(matchedContact.getFullName() + ", Vertec ID " + matchedContact.getVertecId());
                });
                matchedContacts.forEach(mc -> uncertainMerges.add(Arrays.asList(contact.getVertecId(), mc.getVertecId())));
            }


        });

        return map;
    }
}

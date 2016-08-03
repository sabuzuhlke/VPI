package VPI.MergerClasses;

import VPI.Entities.Activity;
import VPI.Entities.Contact;
import VPI.GlobalClass;
import VPI.PDClasses.Activities.PDActivityReceived;
import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecActivities.ActivitiesForAddressEntry;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecService;

import java.io.IOException;
import java.util.*;

import static VPI.Entities.util.Utilities.loadIdMap;
import static java.util.stream.Collectors.*;

public class ContactMerger {
    public PDService PS;
    public VertecService VS;

    //List of Pairs where fst is VertecId of missing org, sndis VertecId of potential match (emails matched)
    public List<List<Long>> uncertainMerges;
    public List<Long> noMergesFound;
    public Map<Long, Long> merges;

    public ContactMerger(PDService PS, VertecService VS){

        this.PS = PS;
        this.VS = VS;

        this.uncertainMerges = new ArrayList<>();
        this.noMergesFound = new ArrayList<>();
        this.merges = new HashMap<>();
    }

    public void doMerge() throws IOException {
        //get all contacts from vertec --> sure about this?
        //load idmap of contacts previously posted to vertec
        Map<Long,Long> contactsPostedToPD = loadIdMap("productionMaps/productionContactMap");
        //get all contacts from pipedrive
        List<Contact> pipedriveContacts = PS.getAllContacts()
                .getBody()
                .getData()
                .stream()
                .filter(contact -> contact.getV_id() != null)
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



        List<Contact> missingContacts = VS.getContactList(missingIds);
        //for each missing contact try to find out whom it has been merged into. Best way probably would be to start with e-mail addresses
        // , then with activities /Projects and organisations won't provide a definitive mapping on their own (multiple contacts at a company), but might be useful for deciding between uncertain matches./
         findMatches(missingContacts, pipedriveContacts);// <merged, surviving>

        System.out.println("Unique match found for contacts: " + merges.size());
        for(Long id : merges.keySet()){
            //VS.mergeTwoContacts(id,mergedContacts.get(id));
            List<Contact> mergeNsurvive = VS.getContactList(Arrays.asList(id, merges.get(id)));
            Contact mergedC = mergeNsurvive.get(0);
            Contact survivingC = mergeNsurvive.get(1);
            System.out.println("Contact " + mergedC.getFullName() + "(v_id: " + mergedC.getVertecId()
                    + ") matches survivor : " + survivingC.getFullName() + "(v_id: " + survivingC.getVertecId() + ")");
        }
        System.out.println("=======> No merges have been found for the following contacts: " + noMergesFound.size());
        for(Long id : noMergesFound){
            Contact c = VS.getContactList(Arrays.asList(id)).get(0);
            GlobalClass.log.info("No Merge found for Contact " + c.getFirstName() + " " + c.getSurname() + " (v_id: " + c.getVertecId() + ")");
        }
        System.out.println("=======> Could not uniquely identify merge by email for the following: " + uncertainMerges.size());
        for(List<Long> conflict : uncertainMerges){
            List<Contact> mergeNsurvive = VS.getContactList(conflict);
            Contact mergedC = mergeNsurvive.get(0);
            Contact survivingC = mergeNsurvive.get(1);
            GlobalClass.log.info("uncertanty in merge found for missing contact "
                    + mergedC.getFirstName() + " " + mergedC.getSurname() + " (v_id: " + mergedC.getVertecId() + ")" +
                    ", potential merge into contact: "
                    + survivingC.getFirstName() + " " + survivingC.getSurname() + " (v_id: " + survivingC.getVertecId() + ")");
        }
    }

    public void findMatches(List<Contact> vertecContacts, List<Contact> pipedriveContacts) {
        List<Activity> pa = PS.getAllActivities().stream()
                .map(a -> new Activity(a, null, null, null, null, null, null))
                .filter(a -> a.getVertecId() != null)
                .filter(a -> a.getPipedriveContactLink() != null)
                .collect(toList()); //get activities from pd

        vertecContacts.forEach(vc -> {
            Map<Long, Set<Long>> countMap = new HashMap<>();
            ActivitiesForAddressEntry va = VS.getActivitiesForAddressEntry(vc.getVertecId()).getBody(); //get activities for vertec contact

            pipedriveContacts.forEach(pc -> {
                pc.getEmails().stream()
                        .filter(email -> email.getValue() != null && ! email.getValue().isEmpty())
                        .forEach(email -> {

                            if (email.getValue().equals(vc.getEmails().get(0).getValue())) {
                                if (countMap.containsKey(vc.getVertecId())) {
                                    countMap.get(vc.getVertecId()).add(pc.getVertecId());
                                } else {
                                    Set<Long> l = new HashSet<>();
                                    l.add(pc.getVertecId());
                                    countMap.put(vc.getVertecId(), l);
                                }
                            }
                        });

                pc.getPhones().stream()
                        .filter(Phone -> Phone.getValue() != null && ! Phone.getValue().isEmpty())
                        .forEach(Phone -> {

                            if (Phone.getValue().equals(vc.getPhones().get(0).getValue())) {
                                if (countMap.containsKey(vc.getVertecId())) {
                                    countMap.get(vc.getVertecId()).add(pc.getVertecId());
                                } else {
                                    Set<Long> l = new HashSet<>();
                                    l.add(pc.getVertecId());
                                    countMap.put(vc.getVertecId(), l);
                                }
                            }
                        });

                if (! countMap.keySet().contains(vc.getVertecId())) {

                    va.getActivities().forEach(av -> {

                        pa.stream()
                                .filter(a -> a.getPipedriveContactLink() == pc.getPipedriveId().longValue())
                                .forEach(ap -> {

                                    if (av.getVertecId().longValue() == ap.getVertecId()) {
                                        if (countMap.containsKey(vc.getVertecId())) {
                                            countMap.get(vc.getVertecId()).add(pc.getVertecId());
                                        } else {
                                            Set<Long> l = new HashSet<>();
                                            l.add(pc.getVertecId());
                                            countMap.put(vc.getVertecId(), l);
                                        }

                                    }

                                });
                    });
                }

            });

            Set<Long> set = countMap.get(vc.getVertecId()) == null ? new HashSet<>() : countMap.get(vc.getVertecId());
            List<Long> matchedContacts = new ArrayList<>();
            matchedContacts.addAll(set);
            if (matchedContacts.size() == 1) {
                System.out.println("Unique match found for contact: " + vc.getFullName() + ", Vertec ID " + vc.getVertecId()
                        + " matched to " + " VertecID " + matchedContacts);
                merges.put(vc.getVertecId() ,matchedContacts.get(0));
            } else if (matchedContacts.size() == 0) {
                System.out.println("No matched found for contact " + vc.getFullName() + ", Vertec ID " + vc.getVertecId());
                noMergesFound.add(vc.getVertecId());
            } else {
                System.out.println("Mulptiple matches found for contact: " + vc.getFullName() + ", Vertec ID "
                        + vc.getVertecId() + " email: " + vc.getEmails().get(0).getValue());
                matchedContacts.forEach(matchedContact -> {
                    System.out.println("Vertec ID " + matchedContact);
                });
                matchedContacts.forEach(mc -> uncertainMerges.add(Arrays.asList(vc.getVertecId(), mc)));
            }


        });


    }

    public HashMap<Long, Long> findMergesByEmail(List<Contact> vertecContacts, List<Contact> pipedriveContacts) {


        HashMap<Long, Long> map = new HashMap<>();
        Map<Long, List<Contact>> countMap = new HashMap<>();
        vertecContacts.forEach(contact -> {

            pipedriveContacts.forEach(pipedrive -> {
//
//               pipedrive.getEmails().stream()
//                .filter(email -> email.getValue() != null && ! email.getValue().isEmpty())
//                .forEach(email -> {
//
//                   if (email.getValue().equals(contact.getEmails().get(0).getValue())) {
//
//                       if (countMap.containsKey(contact.getVertecId())) {
//                           countMap.get(contact.getVertecId()).add(pipedrive);
//                       } else {
//                           List<Contact> l = new ArrayList<>();
//                           l.add(pipedrive);
//                           countMap.put(contact.getVertecId(), l);
//                       }
//                   }
//               });

            });


            List<Contact> matchedContacts = countMap.get(contact.getVertecId()) == null ? new ArrayList<>() : countMap.get(contact.getVertecId());
            if (matchedContacts.size() == 1) {
                System.out.println("Unique match found for contact: " + contact.getFullName() + ", Vertec ID " + contact.getVertecId()
                        + " matched to " + matchedContacts.get(0).getFullName() + " VertecID " + matchedContacts.get(0).getVertecId());
                merges.put(contact.getVertecId() ,matchedContacts.get(0).getVertecId());

                map.put(contact.getVertecId(), matchedContacts.get(0).getVertecId());
            } else if (matchedContacts.size() == 0) {
                System.out.println("No matched found for contact " + contact.getFullName() + ", Vertec ID " + contact.getVertecId());
                noMergesFound.add(contact.getVertecId());
            } else {
                System.out.println("Mulptiple matches found for contact: " + contact.getFullName() + ", Vertec ID "
                        + contact.getVertecId() + " email: " + contact.getEmails().get(0).getValue());
                matchedContacts.forEach(matchedContact -> {
                    System.out.println(matchedContact.getFullName() + ", Vertec ID " + matchedContact.getVertecId()
                            + " email: " + matchedContact.getEmails().get(0).getValue());
                });
                matchedContacts.forEach(mc -> uncertainMerges.add(Arrays.asList(contact.getVertecId(), mc.getVertecId())));
            }


        });

        return map;
    }

    public List<Long> findMergesByActivity(ActivitiesForAddressEntry afc, List<Activity> pdActivities, int logCounter){
        System.out.println("------NEW PAIR TO MATCH----------");
        HashMap<Long,Long> matches = new HashMap<>();

        for(VPI.VertecClasses.VertecActivities.Activity act : afc.getActivities()){
            for(Activity pAct : pdActivities) {

                Long pContId = pAct.getVertecContactLink();
                if(act.getVertecId().longValue() == pAct.getVertecId()){

                    if(! matches.containsKey(pContId)) {
                        matches.put(pContId, 1L);
                    }
                    else matches.replace(pContId, matches.get(pContId) + 1);

                }
            }
        }

        if(matches.size() == 1){

            GlobalClass.log.info(logCounter + ")   Contact " + afc.getName() + " (vid:" + afc.getId() + ")"
                    + " -> " + matches.keySet().toArray()[0] + " with 100% certainty");

            List<Long> pair = new ArrayList<>();
            pair.add(afc.getId());
            pair.add((Long) matches.keySet().toArray()[0]);
            merges.put(afc.getId(), (Long) matches.keySet().toArray()[0]);
            return pair;
        }
        if(matches.size() > 1) {

            //logging
            Long total = 0L;
            for(Long value : matches.values()){
                total += value;
            }
            for(Long cont : matches.keySet()){

                GlobalClass.log.info(logCounter + ") Contact " + afc.getName() + " (vid:" + afc.getId() + ")"
                        + " -> " + cont + " with " + ((matches.get(cont).floatValue()/total) * 100) + " % certainty");

                List<Long> pair = new ArrayList<>();
                pair.add(afc.getId());
                pair.add((Long) matches.keySet().toArray()[0]);
                uncertainMerges.add(pair);
            }

            return new ArrayList<>();
        } else{
            //log
            GlobalClass.log.info(logCounter + ") Could not find Surviving organisation on PipeDrive for " + afc.getName() +  " (vid:" + afc.getId() + ")");

            noMergesFound.add(afc.getId());
            return new ArrayList<>();
        }
    }


}

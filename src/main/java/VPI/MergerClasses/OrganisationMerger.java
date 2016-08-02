package VPI.MergerClasses;

import VPI.Entities.Activity;
import VPI.Entities.Contact;
import VPI.GlobalClass;
import VPI.VertecClasses.VertecOrganisations.Organisation;
import VPI.Entities.util.Utilities;
import VPI.PDClasses.Activities.PDActivityReceived;
import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecActivities.ActivitiesForOrganisation;
import VPI.VertecClasses.VertecService;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

public class OrganisationMerger {
    public PDService PS;
    public VertecService VS;
    public List<List<Long>> uncertainMerges;
    public List<Long> noMergesFound;

    public OrganisationMerger(PDService PS, VertecService VS) {
        this.PS = PS;
        this.VS = VS;

        this.uncertainMerges = new ArrayList<>();
        this.noMergesFound = new ArrayList<>();
    }

    public void doMerge() throws IOException {
        DualHashBidiMap<Long, Long> mergedOrgs = findVorgsMergedOnPD();
        for (Long mergeID : mergedOrgs.keySet()) {
            VS.mergeTwoOrganisations(mergeID, mergedOrgs.get(mergeID));
        }
        for (Long id: noMergesFound) {
            Organisation o = VS.getOrganisationCommonRep(id).getBody();
            GlobalClass.log.info("No Merge Found for Organisation: " + o.getName());
        }
        for (List<Long> mergeConflicts : uncertainMerges) {
            Organisation mo = VS.getOrganisationCommonRep(mergeConflicts.get(0)).getBody();
            Organisation so = VS.getOrganisationCommonRep(mergeConflicts.get(1)).getBody();
            GlobalClass.log.info("Uncertainty in merge found for missing org: " + mo.getName() +", potential org to merge into: " + so.getName());
        }
    }

    public DualHashBidiMap<Long,Long> findVorgsMergedOnPD() throws IOException { //Map<Lost, Surviving>

        //find out which organisations have been merged

        DualHashBidiMap<Long, Long> mergedOrganisations = new DualHashBidiMap<>();
        //Load in idmaps v to pd
        DualHashBidiMap<Long,Long> orgIdMap = Utilities.loadIdMap("productionMaps/productionOrganisationMap");

        System.out.println("OrgidMap.size(): " + orgIdMap.size());


        //get all organisations from pd
        List<PDOrganisationReceived> pOrganisations = PS.getAllOrganisations()
                .getBody()
                .getData();

        List<Long> pdIds = pOrganisations.stream()
                .filter(org -> org.getV_id() != null)
                .map(PDOrganisationReceived::getV_id)
                .collect(toList());
        //find ids that are present in map but not present on pd
        Set<Long> missingIds = orgIdMap.keySet().stream()
                .filter(id -> ! pdIds.contains(id))
                .collect(toSet());

        System.out.println("Pdorganisations.size(): " + pdIds.size());

        System.out.println(" NUMBER OF MISSING ORGANISATIONS::::: " + missingIds.size());

        //foreach missing id: getActivites linked to that org from vertec
        // missingOrgsWithActivities has an entry for each missing organisation
        List<ActivitiesForOrganisation> missingOrgsWithActivities = missingIds.stream()
                .map(VS::getActivitiesForOrganisation)
                .map(HttpEntity::getBody)
                .collect(toList());

        //List<JSONOrganisation> vOrgs = VS.

        //get all activities from pd as its faster than getting activities for individual pdOrgs
        List<PDActivityReceived> pdActivityReceiveds = PS.getAllActivities();

        List<Activity> pActivities = pdActivityReceiveds.stream()
                .map(activity -> {
                   return new Activity(activity,orgIdMap.getKey(activity.getOrg_id()),null,null,null,null,null);
                })
                .collect(toList());

        int i = 1;
        for(ActivitiesForOrganisation org : missingOrgsWithActivities){
            List<Long> pair = findMergedOrganisationPair(org, pActivities, i);
            if(!pair.isEmpty()) mergedOrganisations.put(pair.get(0), pair.get(1));
            i++;
        }

        return mergedOrganisations;
    }


    public List<Long> findMergedOrganisationPair(ActivitiesForOrganisation afo, List<Activity> pdActivities, int logCounter){
        System.out.println("------NEW PAIR TO MATCH----------");
        HashMap<Long,Long> matches = new HashMap<>();

        for(VPI.VertecClasses.VertecActivities.Activity act : afo.getActivitiesForOrganisation()){
            for(Activity pAct : pdActivities) {

                Long vOrgid = act.getVertecOrganisationLink();
                Long pOrgid = pAct.getVertecOrganisationLink();
                if(act.getVertecId().longValue() == pAct.getVertecId()){

                    if(! matches.containsKey(pOrgid)) {
                        matches.put(pOrgid, 1L);
                    }
                    else matches.replace(pOrgid, matches.get(pOrgid) + 1);

                }
            }
        }

        if(matches.size() == 1){

            GlobalClass.log.info(logCounter + ")   Organisation " + afo.getName() + " (vid:" + afo.getOrganisationId() + ")"
                    + " -> " + matches.keySet().toArray()[0] + " with 100% certainty");

            List<Long> pair = new ArrayList<>();
            pair.add(afo.getOrganisationId());
            pair.add((Long) matches.keySet().toArray()[0]);
            return pair;
        }
        if(matches.size() > 1) {

            //logging
            Long total = 0L;
            for(Long value : matches.values()){
                total += value;
            }
            for(Long org : matches.keySet()){

                GlobalClass.log.info(logCounter + ") Organisation " + afo.getName() + " (vid:" + afo.getOrganisationId() + ")"
                        + " -> " + org + " with " + ((matches.get(org).floatValue()/total) * 100) + " % certainty");

                List<Long> pair = new ArrayList<>();
                pair.add(afo.getOrganisationId());
                pair.add((Long) matches.keySet().toArray()[0]);
                uncertainMerges.add(pair);
            }

            return new ArrayList<>();
        } else{
            //log
            GlobalClass.log.info(logCounter + ") Could not find Surviving organisation on PipeDrive for " + afo.getName() +  " (vid:" + afo.getOrganisationId() + ")");

            noMergesFound.add(afo.getOrganisationId());
            return new ArrayList<>();
        }
    }
}

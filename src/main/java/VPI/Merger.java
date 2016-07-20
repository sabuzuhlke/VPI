package VPI;

import VPI.Entities.util.Utilities;
import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecOrganisations.Organisation;
import VPI.VertecClasses.VertecService;

import java.util.HashMap;
import java.util.Map;

public class Merger {
    public PDService PS;
    public VertecService VS;

    public Merger(PDService PS, VertecService VS) {
        this.PS = PS;
        this.VS = VS;
    }


    public Map<Long,Long> findVorgsMergedOnPD(){ //Map<Lost, Surviving>

        //find out which organisations have been merged

        //Load in idmaps v to pd
        Map<Long,Long> orgIdMap = Utilities.loadIdMap("productionMaps/organisationIdMaps");
        //ger all organisations from pd
        //find ids that are present in map but not present on pd
        //foreach -> getActivites linked to that org from vertec

        //get all activities from pd
        //foreach mergedorg from vertec -> find which pdorg one of its activities are linked to, get that vId
        //based on vid construct list of merged vids
        //output orgs that are missing from pd but we couldnt merge

        return new HashMap<>();
    }

    public void applyPdOrgMegesToVertec(){


    }
}

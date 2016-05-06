package VPI.InsightClasses;

import VPI.PDClasses.PDOrganisation;
import VPI.InsightClasses.VOrganisation;
import VPI.PDClasses.PDOrganisationSend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 15/04/2016.
 */
public class Organisations {

    public List<VOrganisation> vOrganisations;
    public List<PDOrganisation> pdOrganisations;

    public List<PDOrganisationSend> postList;
    public List<PDOrganisationSend> putList;

    public List<VOrganisation> matchedList;

    public Organisations() {
        this.pdOrganisations = new ArrayList<>();
        this.vOrganisations  = new ArrayList<>();
        this.postList        = new ArrayList<>();
        this.putList         = new ArrayList<>();
        this.matchedList     = new ArrayList<>();
    }
}

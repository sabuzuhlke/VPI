package VPI.InsightClasses;

import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDOrganisationSend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 15/04/2016.
 */
public class Organisations {

    public List<VOrganisation> vOrganisations;
    public List<PDOrganisationReceived> pdOrganisations;

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

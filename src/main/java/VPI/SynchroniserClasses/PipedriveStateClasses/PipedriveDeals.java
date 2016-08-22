package VPI.SynchroniserClasses.PipedriveStateClasses;

import VPI.PDClasses.Deals.PDDealReceived;

import java.util.List;

public class PipedriveDeals {

    private List<PDDealReceived> deals;

    public PipedriveDeals() {
    }

    public List<PDDealReceived> getDeals() {
        return deals;
    }

    public void setDeals(List<PDDealReceived> deals) {
        this.deals = deals;
    }
}

package VPI.SynchroniserClasses;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;

/**
 * Class for storing all maps of vertec_id <-> pipederive_id, posted previously
 */
public class SynchroniserState {

    private DualHashBidiMap<Long, Long> organisationMap;

    public SynchroniserState() {
        this.organisationMap = loadOrganisationIdMap();
    }

    private DualHashBidiMap<Long,Long> loadOrganisationIdMap() {
        //code to get map;
        return new DualHashBidiMap<>();
    }

    public DualHashBidiMap<Long, Long> getOrganisationMap() {
        return organisationMap;
    }
}

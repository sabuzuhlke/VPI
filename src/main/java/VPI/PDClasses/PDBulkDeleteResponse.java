package VPI.PDClasses;

import java.util.Collection;
import java.util.List;

/**
 * Created by sabu on 14/04/2016.
 */
public class PDBulkDeleteResponse extends PDResponse {

    private PDBulkDeletedIds data;

    public PDBulkDeleteResponse() {
    }

    public PDBulkDeletedIds getData() {
        return data;
    }

    public void setData(PDBulkDeletedIds data) {
        this.data = data;
    }

    class PDBulkDeletedIds {

        private List<String> id;

        public PDBulkDeletedIds() {
        }

        public List<String> getId() {
            return id;
        }

        public void setId(List<String> id) {
            this.id = id;
        }
    }

    class PDBulkDeletedIdsReq {

        private Collection<Long> ids;

        public PDBulkDeletedIdsReq() {
        }

        public Collection<Long> getIds() {
            return ids;
        }

        public void setIds(Collection<Long> ids) {
            this.ids = ids;
        }
    }
}


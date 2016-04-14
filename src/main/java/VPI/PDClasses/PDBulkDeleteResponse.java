package VPI.PDClasses;

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

        private List<Long> ids;

        public PDBulkDeletedIdsReq() {
        }

        public List<Long> getIds() {
            return ids;
        }

        public void setIds(List<Long> ids) {
            this.ids = ids;
        }
    }
}


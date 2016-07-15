package VPI.VertecStateClasses;

import VPI.VertecClasses.VertecOrganisations.JSONContact;

import java.util.List;

public class VertecContacts {

    private List<JSONContact> contacts;

    public VertecContacts() {
    }

    public List<JSONContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<JSONContact> contacts) {
        this.contacts = contacts;
    }
}

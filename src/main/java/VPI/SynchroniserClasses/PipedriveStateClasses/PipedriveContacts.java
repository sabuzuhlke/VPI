package VPI.SynchroniserClasses.PipedriveStateClasses;

import VPI.PDClasses.Contacts.PDContactReceived;

import java.util.List;

public class PipedriveContacts {

    private List<PDContactReceived> contacts;

    public PipedriveContacts() {
    }

    public List<PDContactReceived> getContacts() {
        return contacts;
    }

    public void setContacts(List<PDContactReceived> contacts) {
        this.contacts = contacts;
    }
}

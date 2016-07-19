package VPI.Entities;

import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.Entities.util.ContactDetail;
import VPI.PDClasses.Contacts.PDContactSend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 18/07/2016.
 */

//TODO Once VRAPI Supports handling contacts add conversion functions to/from vertec
public class Contact {

    private Long vertecId;
    private Long pipedriveId;

    private Long vertecOrgLink;
    private Long pipedriveOrgLink;

    private String ownerEmail;

    private String firstName;
    private String surname;
    private Boolean active;

   private  List<ContactDetail> emails;
    private List<ContactDetail> phones; // TODO figure out how phone vs mobile differentiation applies

    private Integer visible_to;

    private String creationTime;
    private String modifiedTime;

    private String ownedOnVertecBy;

    private String position;

    private List<String> followers;

    public Contact(){
        this.emails = new ArrayList<>();
        this.phones = new ArrayList<>();
        this.followers = new ArrayList<>();
    }

    public Contact(PDContactReceived pdr, Long vertecOrgLink, List<String> followerEmails){
        this.visible_to = 3;
        this.vertecId = pdr.getV_id();
        this.pipedriveId = pdr.getId();
        this.active = pdr.getActive_flag();

        this.firstName = pdr.getFirst_name();
        this.surname = pdr.getLast_name();

        this.ownedOnVertecBy = pdr.getOwnedBy();
        this.creationTime = pdr.getCreationTime();
        this.modifiedTime = pdr.getModifiedTime();
        this.ownerEmail= pdr.getOwner_id().getEmail();

        this.emails = pdr.getEmail();
        this.phones = pdr.getPhone();

        this.pipedriveOrgLink = pdr.getOrg_id().getValue();
        this.vertecOrgLink = vertecOrgLink;

        this.position = pdr.getPosition();

        this.followers = followerEmails;
    }

    /**
     * Does not post the followers, they need to be handled separately !!
     */
    public PDContactSend toPDSend(Long pdOwnerId){
        PDContactSend pds = new PDContactSend();

        pds.setId(this.pipedriveId);
        pds.setV_id(this.vertecId);

        pds.setOwner_id(pdOwnerId);

        pds.setPosition(this.position);
        pds.setName(this.getFullName());
        pds.setEmail(emails);
        pds.setPhone(phones);
        pds.setCreationTime(creationTime);
        pds.setModifiedTime(modifiedTime);
        pds.setOwnedBy(ownedOnVertecBy);
        pds.setActive_flag(active);

        pds.setOrg_id(pipedriveOrgLink);

        return pds;
    }

    public String getFullName(){
        String Name = "";
        if(firstName != null && !firstName.isEmpty()) Name = firstName;
        if((firstName != null && !firstName.isEmpty()) && (surname != null && ! surname.isEmpty())) Name += " ";
        if(surname != null && ! surname.isEmpty()) Name += surname;
        return Name;
    }



    public Long getVertecId() {
        return vertecId;
    }

    public void setVertecId(Long vertecId) {
        this.vertecId = vertecId;
    }

    public Long getPipedriveId() {
        return pipedriveId;
    }

    public void setPipedriveId(Long pipedriveId) {
        this.pipedriveId = pipedriveId;
    }

    public Long getVertecOrgLink() {
        return vertecOrgLink;
    }

    public void setVertecOrgLink(Long vertecOrgLink) {
        this.vertecOrgLink = vertecOrgLink;
    }

    public Long getPipedriveOrgLink() {
        return pipedriveOrgLink;
    }

    public void setPipedriveOrgLink(Long pipedriveOrgLink) {
        this.pipedriveOrgLink = pipedriveOrgLink;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<ContactDetail> getEmails() {
        return emails;
    }

    public void setEmails(List<ContactDetail> emails) {
        this.emails = emails;
    }

    public List<ContactDetail> getPhones() {
        return phones;
    }

    public void setPhones(List<ContactDetail> phones) {
        this.phones = phones;
    }

    public Integer getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(Integer visible_to) {
        this.visible_to = visible_to;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getOwnedOnVertecBy() {
        return ownedOnVertecBy;
    }

    public void setOwnedOnVertecBy(String ownedOnVertecBy) {
        this.ownedOnVertecBy = ownedOnVertecBy;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}

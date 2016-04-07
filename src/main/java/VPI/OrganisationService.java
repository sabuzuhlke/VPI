package VPI;

import org.springframework.web.client.RestTemplate;

/**
 * Created by sabu on 07/04/2016.
 */
public class OrganisationService {

    private RestTemplate restTemplate;
    private String server;
    private String apiKey;

    public OrganisationService(RestTemplate restTemplate, String server, String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.server = server;
    }

    public PDResponse post(String companyName, Integer visibleTo) {
        Organisation org = null;
        try {
            OrganisationPost post = new OrganisationPost(companyName, visibleTo);
            String uri = server + "organizations" + apiKey;
            org = restTemplate.postForObject(uri, post, Organisation.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return org;
    }

    public PDResponse get(Long id) {
        Organisation org = null;
        try {
            String uri = server + "organizations/" + id + apiKey;
            org = restTemplate.getForObject(uri, Organisation.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return org;
    }

    public void delete(Long id){
        try {
            restTemplate.delete(server + "organizations/" + id + apiKey);
        } catch (Exception e) {
            System.out.println("DELETE Exception: " + e.toString());
        }
    }

    public void updateAddress(Long id, String address) {
        Organisation org;
        try {
            org = (Organisation) this.get(id);
            org.getData().setAddress(address);
            OrgData od = new OrgData(
                    org.getData().getId(),
                    org.getData().getCompany_id(),
                    org.getData().getOwner_id(),
                    org.getData().getActive_flag(),
                    org.getData().getName(),
                    address);
            Organisation newOrg = new Organisation(od);
            System.out.println(newOrg.toString());
            String uri = server + "organizations/" + id + apiKey;
            restTemplate.put(uri, newOrg);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}

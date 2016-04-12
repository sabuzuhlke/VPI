package VPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by sabu on 06/04/2016.
 */
@SpringBootApplication
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String server = "https://api.pipedrive.com/v1/";
    private static final String apiKey = "?api_token=eefa902bdca498a342552b837663f38b566bce5a";
    private static final String insightServer = "http://insight.zuehlke.com";
    private RestTemplate restTemplate;
    private OrganisationService OS;
    private InsightService IS;
    private MyCredentials credentials;

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) throws Exception {
        this.credentials = new MyCredentials();
        restTemplate = new RestTemplate();
        OS = new OrganisationService(restTemplate, server, apiKey);
        IS = new InsightService(restTemplate,insightServer,this.credentials.getUserName(),this.credentials.getPass());

        try {
            //IS.getOrganisation(53);
            IS.getAllOrganisations();
        } catch (Exception e) {
            log.info("HELP HELP IVE HIT AN EXCEPTION" + e.toString());
        }
    }

}

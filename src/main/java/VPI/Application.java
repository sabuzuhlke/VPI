package VPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*import org.springframework.http.MediaType;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

*/
/**
 * Created by sabu on 06/04/2016.
 */
@SpringBootApplication
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String server = "https://api.pipedrive.com/v1/";
    private static final String apiKey = "?api_token=eefa902bdca498a342552b837663f38b566bce5a";
    private RestTemplate restTemplate;
    private OrganisationService OS;

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) throws Exception {
        restTemplate = new RestTemplate();
        OS = new OrganisationService(restTemplate, server, apiKey);
        try {
            Long id = new Long(1);
            Organisation o = (Organisation) OS.get(id);

            log.info(o.toString());

        } catch (Exception e) {
            log.info("HELP HELP IVE HIT AN EXCEPTION" + e.toString());
        }
    }

}

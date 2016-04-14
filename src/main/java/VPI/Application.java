package VPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SpringBootApplication
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String server = "https://api.pipedrive.com/v1/";
    private static final String apiKey = "";
    private static final String insightServer = "http://insight.zuehlke.com";

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) throws Exception {
        MyCredentials credentials = new MyCredentials();
        RestTemplate restTemplate = new RestTemplate();
        PDService PS = new PDService(restTemplate, server, credentials.getApiKey());
        InsightService IS = new InsightService(
                restTemplate,
                insightServer,
                credentials.getUserName(),
                credentials.getPass()
        );

        try {
            //DO STUFF HERE
        } catch (Exception e) {
            log.info("HELP HELP IVE HIT AN EXCEPTION" + e.toString());
        }
    }

}

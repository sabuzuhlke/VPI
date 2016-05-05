package VPI;

import VPI.PDClasses.PDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String server = "https://api.pipedrive.com/v1/";

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }

    private PDService PDS;

    @Override
    public void run(String... args) throws Exception {

        MyCredentials creds = new MyCredentials();

        RestTemplate restTemplate = new RestTemplate();

        PDS = new PDService(restTemplate, server, creds.getApiKey());

        try {

            //TODO: fix
            //InsightSynchroniser synchroniser =  new InsightSynchroniser(server, insightServer);

            Timer timer = new Timer();
            TimerTask t = new TimerTask() {
                @Override
                public void run() {

                }
            };
            //
            timer.schedule(t, 0l, 1000*60*60);

            //DO STUFF HERE
            //synchroniser.importOrganisations();
        } catch (Exception e) {
            log.info("HELP HELP IVE HIT AN EXCEPTION" + e.toString());
        }
    }

}

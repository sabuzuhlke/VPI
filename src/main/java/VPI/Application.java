package VPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String server = "https://api.pipedrive.com/v1/";
    private static final String insightServer = "http://insight.zuehlke.com";

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) throws Exception {

        try {

            Synchroniser synchroniser =  new Synchroniser(server, insightServer);

            Timer timer = new Timer();
            TimerTask t = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Running...");
                    synchroniser.importToPipedrive();
                }
            };

            timer.schedule(t, 0l, 1000*60);

            //DO STUFF HERE
            //synchroniser.importOrganisations();
        } catch (Exception e) {
            log.info("HELP HELP IVE HIT AN EXCEPTION" + e.toString());
        }
    }

}

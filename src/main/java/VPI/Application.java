package VPI;

import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is the entry point to the program, at the moment it doesn't do anything, but later will shedule a synchronisation
 * every day
 */
@SpringBootApplication
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application implements CommandLineRunner {

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }

    private VertecSynchroniser VS;

    @Override
    public void run(String... args) throws Exception {

        this.VS = new VertecSynchroniser();
        MyCredentials creds = new MyCredentials();
        PDService PD = new PDService("https://api.pipedrive.com/v1/", creds.getApiKey());

        try {

            Timer timer = new Timer();
            TimerTask t = new TimerTask() {
                @Override
                public void run() {
                    //Place code to run repeatedly here
                }
            };
            //schedules task to run every hour
            timer.schedule(t, 0L, 1000*60*60*24);

        } catch (Exception e) {
            GlobalClass.log.info("HELP HELP IVE HIT AN EXCEPTION" + e.toString());
        }
    }

}

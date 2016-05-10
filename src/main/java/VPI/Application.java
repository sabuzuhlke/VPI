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

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }

    private VertecSynchroniser VS;

    @Override
    public void run(String... args) throws Exception {

        this.VS = new VertecSynchroniser();

        try {

            Timer timer = new Timer();
            TimerTask t = new TimerTask() {
                @Override
                public void run() {
                    //Place code to run repeatedly here
                }
            };
            //schedules task to run every hour
            timer.schedule(t, 0L, 1000*60*60);

        } catch (Exception e) {
            log.info("HELP HELP IVE HIT AN EXCEPTION" + e.toString());
        }
    }

}

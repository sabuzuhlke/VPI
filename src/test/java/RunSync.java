import VPI.Keys.DevelopmentKeys;
import VPI.Keys.ProductionKeys;
import VPI.PDClasses.PDService;
import VPI.SynchroniserClasses.Synchroniser;
import VPI.VertecClasses.VertecService;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class RunSync {
    @Test @Ignore
    public void run() throws IOException {
        Synchroniser s = new Synchroniser(new PDService("https://api.pipedrive.com/v1/", DevelopmentKeys.key), new VertecService("localhost:9999"));
    }
}

package CurrentTests;

import VPI.Entities.util.SyncLog;
import VPI.Entities.util.SyncLogList;
import VPI.Entities.util.Utilities;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;


public class UtilitiesTest {
    @Test
    public void canLoadIdMap() throws IOException {
        Map<Long, Long> idMap = Utilities.loadIdMap("productionMaps/organisationIdMap.txt");

       assertEquals(908L, idMap.get(21768524L).longValue()); //both are read from given path
       assertEquals(1329L, idMap.get(27652536L).longValue()); //both are read from given path
    }
    @Test
    public void canGetCurrentTime(){
        System.out.println(Utilities.getCurrentTime());
    }

    @Test
    public void canWriteLogToFile() throws IOException {
        SyncLogList ll = new SyncLogList("logs/test");
        ll.add("TEST", "TEST", "NAME", 2L, 12L);
        ll.save();
    }
}

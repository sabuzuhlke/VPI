package VPI.Entities.util;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A list of updates, created for each run of the synchroniser
 * A seperate list will be created for Vertec and PD
 */
public class SyncLogList {

    private String filePath;
    private List<SyncLog> log;

    public SyncLogList(String filePath) {
        this.filePath = filePath;
        log = new ArrayList<>();
    }

    public void save() throws IOException {
        String timestring = Utilities.getCurrentTime().replace(":", "-");
        System.out.println(timestring);
         new File(filePath + "_" + timestring + ".log").createNewFile();
        FileWriter file = new FileWriter(filePath + "_" + timestring + ".log", true);

        log.forEach(element -> {
            try {
                file.write(element.toString());
            } catch (IOException e) {
                throw new RuntimeException("could not write log element to file: " + element);
            }
        });

        file.close();
    }

    public void add(SyncLog element) {
        log.add(element);
    }

    public void add(String type, String objectType,  String name, Long vertecId, Long pipedriveId) {
        SyncLog element = new SyncLog(type, objectType, name, vertecId, pipedriveId, Utilities.getCurrentTime());

        log.add(element);
    }

    //TODO
//    public void load() throws FileNotFoundException {
//        File f = new File(filePath);
//        FileReader reader = new FileReader(filePath);
//        BufferedReader file = new BufferedReader(reader);
//
//        while()
//    }

}

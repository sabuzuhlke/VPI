package VPI.Entities.util;

import VPI.Entities.Organisation;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A list of updates, created for each run of the synchroniser
 * A seperate list will be created for Vertec and PD
 */

/**
 * These log-lists are to be used to keep track of all the changes we apply to either Vertec or Pipedrive
 */
public class SyncLogList {

    private String filePath;
    public String name;
    private List<SyncLog> log;

    public SyncLogList() {
        this.filePath = "";
        this.name = "";
        this.log = new ArrayList<>();
    }

    public SyncLogList(String filePath, String name) {
        this.filePath = filePath;
        this.name = name;
        log = new ArrayList<>();
    }

    public void save() throws IOException {
        if (this.log.isEmpty()) return;
        String timestring = Utilities.getCurrentTime().replace(":", "-");
        System.out.println(timestring);
        String logTitle = filePath + name + "_" + timestring + ".log";
        new File(logTitle).createNewFile();
        FileWriter file = new FileWriter(logTitle, true);

        log.forEach(element -> {
            try {
                file.write(element.toString());
            } catch (IOException e) {
                throw new RuntimeException("could not write log element to file: " + element);
            }
        });

        file.close();

        Utilities.overwriteSingleLineFile(logTitle, "latest_" + name + "_log");
    }

    public void add(SyncLog element) {
        log.add(element);
    }

    public void add(String type, String objectType, String name, Long vertecId, Long pipedriveId) {
        SyncLog element = new SyncLog(type, objectType, name, vertecId, pipedriveId, Utilities.getCurrentTime());

        log.add(element);
    }

    public void add(String type, String objectType, String name, Long vertecId, Long pipedriveId, String time) {
        SyncLog element = new SyncLog(type, objectType, name, vertecId, pipedriveId, time);

        log.add(element);
    }


    public static SyncLogList load(String filePath) throws IOException {
        File f = new File(filePath);
        FileReader reader = new FileReader(filePath);
        BufferedReader file = new BufferedReader(reader);
        String line;

        SyncLogList logList = new SyncLogList(filePath, "");
        while ((line = file.readLine()) != null) {
            String[] parts = line.split(" :: ");
            if (parts[4] == null) parts[4] = "-1";
            if (parts[5] == null) parts[5] = "-1";
            if (parts[0] == null) parts[0] = "";
            logList.add(parts[1], parts[2], parts[3], Long.parseLong(parts[4]), Long.parseLong(parts[5]), parts[0]);
        }
        reader.close();
        return logList;
    }

    public String contains(Organisation org) {
        for (SyncLog element : log) {
            if (element.getVertecId().longValue() == org.getVertecId() || element.getPipedriveId().longValue() == org.getPipedriveId())
                return element.getTimeStamp();
        }
        return "";
    }

    public List<SyncLog> getLog() {
        return log;
    }

}

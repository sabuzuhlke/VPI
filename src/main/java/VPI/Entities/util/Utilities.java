package VPI.Entities.util;

import java.io.*;
import java.text.Bidi;
import java.time.LocalDate;
import java.util.*;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.*;

public class Utilities {

     static  public String formatVertecDate(String vDate){
         try{
             if(vDate != null){
                 if (vDate.contains("1900-01-01")) {
                     return "1900-01-01 00:00:00";
                 } else {
                     String[] dateFormatter = vDate.split("T");
                     String date = dateFormatter[0];
                     String time = dateFormatter[1];
                     return date + " " + time;
                 }
             }
         } catch (Exception e){
             System.out.println("Could not split date of: " + vDate);
         }
         return null;
     }
     
     static public String formatToVertecDate(String pDate){
         try{
             if(pDate != null){
                 if (pDate.contains("1900-01-01")) {
                     return "1900-01-01T00:00:00";
                 } else {
                     String[] dateFormatter = pDate.split(" ");
                     String date = dateFormatter[0];
                     String time = dateFormatter[1];
                     return date + " " + time;
                 }
             }
         } catch (Exception e){
             System.out.println("Could not split date: " + pDate);
         }
         return null;
     }
     

    static public String formatVertecAddress(VPI.VertecClasses.VertecOrganisations.Organisation org){
         String address = "";
         if(org.getBuildingName() != null && !org.getBuildingName().isEmpty()){
             address += org.getBuildingName() + ", ";
         }

         if (org.getStreet_no() != null && !org.getStreet_no().isEmpty()) {
             address += org.getStreet_no() + " ";
         }
         if (org.getStreet() != null && !org.getStreet().isEmpty()) {
             address += org.getStreet() + ", ";
         }

         if (org.getCity() != null && !org.getCity().isEmpty()) {
             address += org.getCity() + ", ";
         }
         if (org.getZip() != null && !org.getZip().isEmpty()) {
             address += org.getZip() + ", ";
         }
         if (org.getCountry() != null && !org.getCountry().isEmpty()) {
             address += org.getCountry();
         }
         return address;
     }

     static public String createFullAddress(String BuildingName, String Street_no, String Street, String City, String Zip, String Country){
         String address = "";
         if(BuildingName != null && !BuildingName.isEmpty()){
             address += BuildingName + ", ";
         }

         if (Street_no != null && !Street_no.isEmpty()) {
             address += Street_no + " ";
         }
         if (Street != null && !Street.isEmpty()) {
             address += Street + ", ";
         }

         if (City != null && !City.isEmpty()) {
             address += City + ", ";
         }
         if (Zip != null && !Zip.isEmpty()) {
             address += Zip + ", ";
         }
         if (Country != null && !Country.isEmpty()) {
             address += Country;
         }
         return address;
     }

     static public String extractNoteFromNoteWithVID(String note) {
         if (extractVID(note) == -1) {
             return note;
         } else {
             String[] vIdAndRest = note.split("#");
             return vIdAndRest[1];
         }
     }

     static public  Long extractVID(String note) {
         if (note.contains("V_ID:")) {
             String[] vIdAndRest = note.split("#");
             String vIdString = vIdAndRest[0];
             String[] keyValue = vIdString.split(":");
             if (keyValue.length == 2) {
                 return Long.parseLong(keyValue[1]);
             }
         }
         return -1L;
     }

     //TODO add the reverse of this function
     public static String reformatToHtml(String s) { //makes content of note field legible on pipedrive
         String[] parts = s.split("\n");

         String formattedString = "";

         for (String part : parts) {
             formattedString += part + "<br>";
         }

         String[] parts2 = formattedString.split("\t");


         formattedString = "";
         for(String part2 : parts2){
             formattedString+= part2 + "    ";//four spaces to replace tabs
         }
         return formattedString;
     }

    static public boolean isInThePast(String dateTime) {
        if(dateTime == null) return false;
        if (dateTime.length() >= 10) {
            String date = dateTime.substring(0, 10);
            LocalDate d = LocalDate.parse(date);
            LocalDate now = LocalDate.now();
            return d.isBefore(now);
        } else {
            return false;
        }

    }

    static public DualHashBidiMap<Long, Long> loadIdMap(String filename) throws IOException {
        String line;

        DualHashBidiMap<Long, Long> idMap = new DualHashBidiMap<>();

            File file = new File(filename);

            FileReader reader = new FileReader(file.getAbsolutePath());
            BufferedReader breader = new BufferedReader(reader);

            while((line = breader.readLine()) != null){
                String[] ids = line.split(",");
                String key = ids[0];
                String value = ids[1];

                idMap.put(Long.parseLong(key),Long.parseLong(value));

            }
        reader.close();
        return idMap;
    }

    static public List<Long> loadIdList(String filename) throws IOException {
        List<Long> ids = new ArrayList<>();
        File file = new File(filename);
        String line;

        FileReader reader = new FileReader(file.getAbsolutePath());
        BufferedReader breader = new BufferedReader(reader);

        while((line = breader.readLine()) != null){
            ids.add(Long.parseLong(line));
        }
        return ids;
    }

   static  public String idsAsString(List<Long> ids) {
        String idsAsString = "";
        for(int i = 0; i < ids.size(); i++) {
            if (i < ids.size() -1) {
                idsAsString += ids.get(i) + ",";
            } else {
                idsAsString += ids.get(i);
            }
        }
        return idsAsString;
    }
}



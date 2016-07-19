package VPI.Entities.util;

 public class Formatter {

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
}

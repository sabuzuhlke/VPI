package VPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.SequenceInputStream;
import java.net.URL;

/**
 * Created by gebo on 12/04/2016.
 */
public class MyCredentials {

    private String pass = "";
    private String userName = "";

    public MyCredentials() {
        String line;
        try{

            File file = new File("creds.txt");
            System.out.println("Found file: " + file.getAbsolutePath());

            FileReader reader = new FileReader(file.getAbsolutePath());
            BufferedReader breader = new BufferedReader(reader);
            if((line = breader.readLine()) != null){
                userName = line;
                if((line = breader.readLine()) != null){
                    pass = line;
                }
                else System.out.println("Couldnt read password.");
            }
            else System.out.println("Couldnt read username nor Password.");
        }
        catch(Exception e){
            System.out.println("Could not open file: " +e.toString());
        }

    }

    public String getPass() {
        return pass;
    }

    public String getUserName() {
        return userName;
    }
}

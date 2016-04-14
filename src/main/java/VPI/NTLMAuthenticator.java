package VPI;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class NTLMAuthenticator extends Authenticator{
    private final String userName;
    private final char[] pwd;

    public NTLMAuthenticator(final String userName, final String pwd) {
        super();
        this.userName = userName;
        this.pwd = pwd.toCharArray();
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(this.userName,this.pwd);
    }
}


package VPI.VXMLClasses;

import VPI.MyCredentials;
import VPI.NTLMAuthenticator;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.Authenticator;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Created by sabu on 20/04/2016.
 */
public class XMLService {

    private RestTemplate restTemplate;
    private String ipAddress;
    private String portNo;
    private Unmarshaller jm;
    private String username;
    private String pwd;

    public XMLService(String ipAddress, String portNo) {
        this.restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new FormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        restTemplate.setMessageConverters(converters);
        this.ipAddress = ipAddress;
        this.portNo = portNo;
        MyCredentials creds = new MyCredentials();
        this.username = creds.getUserName();
        this.pwd = creds.getPass();
    }

    public void testGet() {
        RequestEntity<String> req;
        ResponseEntity<String> res;
        try {
            String s = getXMLString();
            req = new RequestEntity<>(s, HttpMethod.POST, new URI(ipAddress + ":" + portNo + "/xml"));

            res = restTemplate.exchange(req, String.class);

            System.out.println(res.toString());


        } catch (Exception e) {
            System.out.println("Exception: ");
            e.printStackTrace();
        }

    }

    public void objectToXML(){
        XMLBasicAuth ba = new XMLBasicAuth();
        ba.setName("me");
        ba.setPassword("LETMEIN");

        XMLHeader head = new XMLHeader();
        head.setBasicAuth(ba);

        XMLResultDef rd = new XMLResultDef();
        String[] stra = {"habbab","babbab"};
        rd.setMembers(stra);

        XMLSelection sel = new XMLSelection();
        sel.setObjref(553L);
        sel.setOcl("this please");
        sel.setSqlOrder("id");
        sel.setSqlWhere("WHERE id IS NOT NULL");

        XMLQuery q = new XMLQuery();
        q.setResultdef(rd);
        q.setSelection(sel);

        XMLBody bod = new XMLBody();
        bod.setQuery(q);


        XMLEnvelope env = new XMLEnvelope();
        env.setBody(bod);
        env.setHeader(head);

        try {
            JAXBElement<XMLEnvelope> jaxbElement = new JAXBElement<>(new QName("Envelope"), XMLEnvelope.class, env);
            //StringWriter writer = new StringWriter();
            JAXBContext ctx = JAXBContext.newInstance(XMLEnvelope.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(jaxbElement,System.out);
            //System.out.println(writer.toString());

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    public void xmlToObject() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(XMLEnvelope.class);
            Unmarshaller um = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(getXMLString());
            XMLEnvelope envelope = (XMLEnvelope) um.unmarshal(reader);
            System.out.println(envelope.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public String getXMLString() {
        return "<Envelope>\n" +
                "    <Header>\n" +
                "        <BasicAuth>\n" +
                "            <Name>"+ this.username +"</Name>\n" +
                "            <Password>" + this.pwd + "</Password>\n" +
                "        </BasicAuth>\n" +
                "    </Header>\n" +
                "    <Body>\n" +
                "        <Query>\n" +
                "            <Selection>\n" +
                "                <objref>12329705</objref>\n" +
                "            </Selection>\n" +
                "            <Resultdef>\n" +
                "                <member>modifieddatetime</member>\n" +
                "                <member>projekt</member>\n" +
                "                <member>parentphase</member>\n" +
                "            </Resultdef>\n" +
                "        </Query>\n" +
                "    </Body>\n" +
                "</Envelope>";
    }

}

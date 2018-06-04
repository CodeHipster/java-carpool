package thijs.oostdam.carpool.email;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStreamReader;

public class MailGunEmailService {

    private String apiKey;
    private String domain = "mg.oostd.am";
    private String fromAddress;

    /**
     *
     * @param apiKey
     * @param fromAddress in the form of "Mail <mail@test.com>"
     */
    public MailGunEmailService(String apiKey, String fromAddress){
        this.apiKey = apiKey;
        this.fromAddress = fromAddress;
    }

    public void sendEmail(Email email) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", apiKey));
        WebResource webResource = client.resource("https://api.mailgun.net/v3/"+domain+"/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", fromAddress);
        formData.add("to", email.getToAddress());
        formData.add("subject", email.getSubject());
        formData.add("text", email.getBody());
        ClientResponse response = webResource
                .type(MediaType.APPLICATION_FORM_URLENCODED)
                .post(ClientResponse.class, formData);

        if(response.getStatus() != 200){
            try {
                String responseBody = CharStreams.toString(new InputStreamReader(
                        response.getEntityInputStream(), Charsets.UTF_8));
                throw new RuntimeException(String.format("Request to mailgun failed. StatusCode: {}, response: {}", response.getStatus(), responseBody));
            } catch (IOException e) {
                throw new RuntimeException("Could not read response body of failed request from mailgun.");
            }
        }
    }
}

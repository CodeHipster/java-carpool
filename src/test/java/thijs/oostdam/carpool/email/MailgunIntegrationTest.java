package thijs.oostdam.carpool.email;


import org.junit.Before;
import org.junit.Test;



public class MailgunIntegrationTest {

    private MailGunEmailService service;

    @Before
    public void before(){
        service = new MailGunEmailService("insert api key", "test@oostd.am");
    }

    @Test
    public void sendMailSuccesfully(){
        Email email = new Email("oostdam+test@gmail.com", "subject", "some body");
        service.sendEmail(email);
    }


    @Test(expected = RuntimeException.class)
    public void sendMailUnSuccesfully(){
        Email email = new Email("oostdam+test", "subject", "some body");
        service.sendEmail(email);
    }
}

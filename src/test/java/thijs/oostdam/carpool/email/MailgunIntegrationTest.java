package thijs.oostdam.carpool.email;


import org.junit.Before;
import org.junit.Test;



public class MailgunIntegrationTest {

    private MailGunEmailService service;

    @Before
    public void before(){
        service = new MailGunEmailService("a94f9f7e38db60d491f690b28ed5e6ce-b6183ad4-7714d989", "test@oostd.am");
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

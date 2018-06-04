package thijs.oostdam.carpool.authentication.services;

import thijs.oostdam.carpool.authentication.domain.EmailAddress;
import thijs.oostdam.carpool.authentication.domain.Login;
import thijs.oostdam.carpool.authentication.domain.LoginToken;
import thijs.oostdam.carpool.email.Email;
import thijs.oostdam.carpool.email.MailGunEmailService;

public class EmailService {

    private MailGunEmailService mailService;

    public EmailService(MailGunEmailService mailService){
        this.mailService = mailService;
    }

    /**
     * Send an address with the verification code(TODO: make it a link?)
     * @param verificationCode
     * @param email
     */
    public void sendVerificationCode(String verificationCode, EmailAddress email){
        Email carpool_verification_code = new Email(email.address, "Carpool verification code", verificationCode);
        mailService.sendEmail(carpool_verification_code);
    }

    /**
     * Send an address with the password in plain text
     * @param login
     */
    public void sendNewLogin(Login login) {
        Email email = new Email(login.email.address, "Carpool: new password", login.password);
        mailService.sendEmail(email);
    }

    /**
     * Send an address containing a link to the main page + authtoken as parameter.
     * @param loginToken
     */
    public void sendLoginLink(LoginToken loginToken) {
        Email email = new Email(loginToken.email.address,"Carpool: login link", "https://oostd.am/carpool?token="+loginToken.token);
        mailService.sendEmail(email);
    }
}

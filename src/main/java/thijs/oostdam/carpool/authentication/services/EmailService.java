package thijs.oostdam.carpool.authentication.services;

import thijs.oostdam.carpool.authentication.domain.Email;
import thijs.oostdam.carpool.authentication.domain.Login;
import thijs.oostdam.carpool.authentication.domain.LoginToken;

public class EmailService {

    /**
     * Send an email with the verification code(TODO: make it a link?)
     * @param verificationCode
     * @param email
     */
    public void sendVerificationCode(String verificationCode, Email email){

    }

    /**
     * Send an email with the password in plain text
     * @param login
     */
    public void sendNewLogin(Login login) {

    }

    /**
     * Send an email containing a link to the main page + authtoken as parameter.
     * @param loginToken
     */
    public void sendLoginLink(LoginToken loginToken) {

    }
}

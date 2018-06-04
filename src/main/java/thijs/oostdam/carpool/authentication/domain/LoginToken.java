package thijs.oostdam.carpool.authentication.domain;

public class LoginToken {
    public String token;
    public EmailAddress email;
    public LoginToken(EmailAddress email, String token){
        this.token = token;
        this.email = email;
    }
}

package thijs.oostdam.carpool.authentication.domain;

public class LoginToken {
    public String token;
    public Email email;
    public LoginToken(Email email, String token){
        this.token = token;
        this.email = email;
    }
}

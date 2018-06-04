package thijs.oostdam.carpool.authentication.domain;

public class Login {
    public EmailAddress email;
    public String password;

    public Login(EmailAddress email, String password){
        this.password = password;
        this.email = email;
    }
}

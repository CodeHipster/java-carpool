package thijs.oostdam.carpool.authentication.domain;

public class Registration {
    public EmailAddress email;
    public String password;

    public Registration(String email, String password){
        this.email = new EmailAddress(email);
        this.password = password;
    }
}

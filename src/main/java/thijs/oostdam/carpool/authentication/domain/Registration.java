package thijs.oostdam.carpool.authentication.domain;

public class Registration {
    public Email email;
    public String password;

    public Registration(String email, String password){
        this.email = new Email(email);
        this.password = password;
    }
}

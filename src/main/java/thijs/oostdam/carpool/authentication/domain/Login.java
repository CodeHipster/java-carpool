package thijs.oostdam.carpool.authentication.domain;

public class Login {
    public Login(Email email, String password){
        this.password = password;
        this.email = email;
    }
    public Email email;
    public String password;
}

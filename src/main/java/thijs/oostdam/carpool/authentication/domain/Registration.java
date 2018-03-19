package thijs.oostdam.carpool.authentication.domain;

public class Registration {
    public String email;
    public String password;

    public Registration(String email, String password){
        this.email = email;
        this.password = password;
    }
}

package thijs.oostdam.carpool.authentication.transport;

public class LoginHttp {
    public String email;
    public String password;

    public LoginHttp(String email, String password){
        this.password = password;
        this.email = email;
    }
}

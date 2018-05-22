package thijs.oostdam.carpool.authentication.transport;

public class VerificationCodeHttp {
    public String code;
    public String email;

    public VerificationCodeHttp(String email, String code){
        this.code= code;
        this.email = email;
    }
}

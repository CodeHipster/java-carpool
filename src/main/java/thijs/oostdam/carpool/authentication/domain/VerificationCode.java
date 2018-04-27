package thijs.oostdam.carpool.authentication.domain;

public class VerificationCode {
    public String code;
    public Email email;

    public VerificationCode(Email email, String code){
        this.code= code;
        this.email = email;
    }
}

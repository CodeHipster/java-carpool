package thijs.oostdam.carpool.authentication.domain;

public class VerificationCode {
    public String code;
    public EmailAddress email;

    public VerificationCode(EmailAddress email, String code){
        this.code= code;
        this.email = email;
    }
}

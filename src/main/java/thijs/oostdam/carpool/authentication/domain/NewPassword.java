package thijs.oostdam.carpool.authentication.domain;

public class NewPassword {
    public EmailAddress email;
    public String oldPassword;
    public String newPassword;

    public NewPassword(EmailAddress email, String oldPassword, String newPassword){
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}

package thijs.oostdam.carpool.authentication.domain;

public class NewPassword {
    public Email email;
    public String oldPassword;
    public String newPassword;

    public NewPassword(Email email, String oldPassword, String newPassword){
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}

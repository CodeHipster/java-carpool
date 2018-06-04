package thijs.oostdam.carpool.authentication.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.*;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;

public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    private PasswordRepository repository;
    private EmailService emailService;
    private PasswordHasher passwordHasher;
    private SecureRandom random;
    private ReadableCodeGenerator codeGenerator;
    private TokenBuilder tokenBuilder;

    public AuthenticationService(KeyPairProvider keyPairProvider, PasswordRepository repository, EmailService emailService){
        this.repository = repository;
        this.emailService = emailService;
        this.random = new SecureRandom();
        this.passwordHasher = new PasswordHasher(random);
        this.codeGenerator = new ReadableCodeGenerator();
        this.tokenBuilder = new TokenBuilder(keyPairProvider);
    }

    public void register(Registration registration){
        //Check if user doesnt already have an account
        Optional<PasswordHash> password = repository.getPassword(registration.email);
        if(password.isPresent()){
            throw new RuntimeException("user already exists.");
        }

        PasswordHash passwordHash = passwordHasher.hashPassword(registration.password);

        repository.addPassword(registration.email, passwordHash);

        String verificationCode = codeGenerator.generateCode(7);

        //Not hashing the verificationCode, little impact(potential ghost account) if code is compromised.
        //Store verification code in db
        repository.addVerificationCode(new VerificationCode(registration.email, verificationCode));

        //address verification code
        emailService.sendVerificationCode(verificationCode, registration.email);
    }

    public LoginToken validateToken(String token){
        return tokenBuilder.buildToken(token);
    }

    public void verifyCode(VerificationCode code){
        //verify code with repo
        Optional<VerificationCode> storedCode = repository.getVerificationCode(code);
        if(!storedCode.isPresent()){
            throw new RuntimeException("There is no code to verify.");
        }

        if(!code.code.equals(storedCode.get().code)){
            throw new RuntimeException("Code is not valid.");
        }

        //change role if valid
        //roles? guest or member?
        repository.setVerified(code.email);

        //remove verification code
        repository.removeVerificationCode(code);
    }

    public LoginToken getLoginToken(Login login){

        //verify login
        verifyPassword(login.email, login.password);

        //generate token
        return tokenBuilder.buildToken(login.email);
    }

    public void resetPassword(EmailAddress email){
        //generate new password
        String password = codeGenerator.generateCode(8);

        Login login = new Login(email, password);

        PasswordHash hashedPassword = passwordHasher.hashPassword(password);

        //store password in db
        repository.updatePassword(email, hashedPassword);

        //send address with new password and link to change it
        emailService.sendNewLogin(login);
    }

    public void changePassword(NewPassword newPassword){
        verifyPassword(newPassword.email, newPassword.oldPassword);

        PasswordHash passwordHash = passwordHasher.hashPassword(newPassword.newPassword);

        repository.updatePassword(newPassword.email, passwordHash);
    }

    private void verifyPassword(EmailAddress email, String password){

        PasswordHash storedPassword = repository.getPassword(email)
                .orElseThrow(() -> new RuntimeException("address not known."));

        //use the salt that belongs to the password.
        PasswordHash passwordHash = passwordHasher.hashPassword(password, storedPassword.getSalt());

        if(!Arrays.equals(passwordHash.getHash(), storedPassword.getHash())){
            throw new RuntimeException("Incorrect password.");
        }
    }

    public void loginViaEmail(Login login) {
        LoginToken loginToken = tokenBuilder.buildToken(login.email);

        //address login token
        emailService.sendLoginLink(loginToken);
    }
}



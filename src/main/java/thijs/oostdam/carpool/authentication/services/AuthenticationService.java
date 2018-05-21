package thijs.oostdam.carpool.authentication.services;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.*;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

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

    //TODO: separation of concerns
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

        //email verification code
        emailService.sendVerificationEmail(verificationCode, registration.email);
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

    public LoginToken login(Login login){

        //verify login
        Optional<PasswordHash> storedPassword = repository.getPassword(login.email);
        if(!storedPassword.isPresent()){
            throw new RuntimeException("email not known.");
        }

        //verify password, add the salt and then compare to hash stored in db.
        byte[] givenPassword = login.password.getBytes();
        byte[] saltedGivenPassword = Bytes.concat(givenPassword, storedPassword.get().getSalt());
        HashCode givenPasswordHash = Hashing.sha256().hashBytes( saltedGivenPassword );

        if(!givenPasswordHash.equals(HashCode.fromBytes(storedPassword.get().getHash()))){
            throw new RuntimeException("Bad password.");
        }

        //generate token
        return tokenBuilder.buildToken(login.email);
    }

    public void resetPassword(Email email){
        //generate new password
        byte[] passwordBytes = new byte[8];
        new Random().nextBytes(passwordBytes);
        for(int i = 0 ; i < passwordBytes.length; i++){
            passwordBytes[i] = (byte)(passwordBytes[i] % 26 + 65);
        }
        String password = new String(passwordBytes);

        Login login = new Login(email, password);

        PasswordHash hashedPassword = passwordHasher.hashPassword(password);

        //store password in db
        repository.updatePassword(email, hashedPassword);

        //send email with new password and link to change it
        emailService.sendNewLogin(login);
    }

    public void changePassword(NewPassword newPassword){
        //verify password
        //change password
        //hash and salt
    }
}



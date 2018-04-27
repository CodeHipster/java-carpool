package thijs.oostdam.carpool.authentication.services;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.*;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;
import java.util.Optional;

public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    private KeyPairProvider keyPairProvider;
    private PasswordRepository repository;

    public AuthenticationService(KeyPairProvider keyPairProvider, PasswordRepository repository){
        this.keyPairProvider = keyPairProvider;
        this.repository = repository;
    }

    //TODO: separation of concerns
    public void register(Registration registration){
        //Check if user doesnt already have an account
        Optional<PasswordHash> password = repository.getPassword(registration.email);
        if(password.isPresent()){
            throw new RuntimeException("user already exists.");
        }

        //calculate hash
        //add salt to password
        //calculate hash
        SecureRandom random = new SecureRandom();
        byte salt[] = new byte[4];
        random.nextBytes(salt);

        byte[] saltedPassword = Bytes.concat(registration.password.getBytes(), salt);
        HashCode saltedPasswordHash = Hashing.sha256().hashBytes( saltedPassword );
        PasswordHash passwordHash = new PasswordHash(saltedPasswordHash.asBytes(), salt);

        repository.addPassword(registration.email, passwordHash);

        //generate verification code
        byte[] code = new byte[7];
        for(int i = 0; i < code.length; i++){
            //https://en.wikipedia.org/wiki/Basic_Latin_(Unicode_block)
            code[i] = (byte)(random.nextInt(26) +65); //No need to use secure random
        }

        String verificationCode = new String(code, Charsets.UTF_8);

        //Store verification code in db
        repository.addVerificationCode(new VerificationCode(registration.email, verificationCode));

        //email verification code

    }


    //test no header
    //test empty header
    //test no . in header
    //test only . in header
    public Email validateToken(String token){
        //get the part after the dot. which is the signature.
        //verify the part before the dot.
        String[] split = token.split(".");
        byte[] id = Base64.getDecoder().decode(split[0]);
        byte[] signature = Base64.getDecoder().decode(split[1]);

        try {
            Signature sig = null;
            sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(keyPairProvider.getKeyPair().getPublic());
            sig.update(id);
            if(sig.verify(signature)){
                return new Email(new String(id,"utf-8"));
            }
            else{
                throw new RuntimeException("Signature is not valid.");
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | SignatureException e) {
            LOG.error("Something went wrong: {}", e.getMessage(), e);
            throw new RuntimeException("could not validate token",e);
        }
    }

    public void verifyCode(VerificationCode code){
        //verify code with repo
        //change role if valid
        //remove verification code



        //roles? guest or member?
    }

    public LoginToken login(Login login){
        //verify login
        //generate token

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

        //sign the email.
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(keyPairProvider.getKeyPair().getPrivate());
            sig.update(login.email.email.getBytes("UTF8"));
            byte[] signatureBytes = sig.sign();
            String signature = Base64.getEncoder().encodeToString(signatureBytes);
            String signedToken = Base64.getEncoder().encodeToString(login.email.email.getBytes("UTF8")) + "." + signature;
            LOG.debug(signedToken);
            return new LoginToken(signedToken);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException("exception when when generating login token.", e);
        }
    }

    public void resetPassword(Email email){
        //generate new password
        //store password in db
        //send email with new password and link to change it
    }

    public void changePassword(NewPassword newPassword){
        //verify password
        //change password
            //hash and salt
    }
}



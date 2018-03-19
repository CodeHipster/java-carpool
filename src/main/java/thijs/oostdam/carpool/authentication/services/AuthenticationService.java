package thijs.oostdam.carpool.authentication.services;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.*;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.text.MessageFormat;
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

    public void register(Registration login){
        //Check if user doesnt already have an account

        //Add user and password
            //hash and salt

        //generate verification code

        //email verification code
    }

    public String validateToken(String token){
        //get the part after the dot. which is the signature.
        //verify the part before the dot.
        String[] split = token.split(".");
        byte[] id = Base64.getDecoder().decode(split[0]);
        byte[] signature = Base64.getDecoder().decode(split[1]);

        //OutputStream is incompatible with java7 try with resources.
        OutputStream os = exchange.getResponseBody();
        try {
            Signature sig = null;
            sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(keyPairProvider.getKeyPair().getPublic());
            sig.update(id);
            if(!sig.verify(signature)) throw new RuntimeException("Signature is not valid.");
            exchange.setAttribute("id",new String(id,"utf-8"));
            next.handle(exchange);
        } catch (Exception e) {
            LOG.error("Something went wrong: {}", e.getMessage(), e);
            byte[] response = MessageFormat.format(ERROR_TEMPLATE, e.getMessage()).getBytes();
            exchange.sendResponseHeaders(500, response.length);
            os.write(response);
            os.close();
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
            sig.update(login.email.getBytes("UTF8"));
            byte[] signatureBytes = sig.sign();
            String signature = Base64.getEncoder().encodeToString(signatureBytes);
            String signedToken = Base64.getEncoder().encodeToString(login.email.getBytes("UTF8")) + "." + signature;
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



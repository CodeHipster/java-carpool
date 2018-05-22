package thijs.oostdam.carpool.authentication.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.Email;
import thijs.oostdam.carpool.authentication.domain.LoginToken;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

public class TokenBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(TokenBuilder.class);
    private KeyPairProvider keyPairProvider;

    public TokenBuilder(KeyPairProvider keyPairProvider){
        this.keyPairProvider = keyPairProvider;
    }


    //test no header
    //test empty header
    //test no . in header
    //test only . in header
    public LoginToken buildToken(String token){
        //get the part after the dot. which is the signature.
        //verify the part before the dot.
        String[] split = token.split("\\.");
        byte[] id = Base64.getDecoder().decode(split[0]);
        byte[] signature = Base64.getDecoder().decode(split[1]);

        try {
            Signature sig = null;
            sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(keyPairProvider.getKeyPair().getPublic());
            sig.update(id);
            if(sig.verify(signature)){
                return new LoginToken(new Email(new String(id,"utf-8")), token);
            }
            else{
                throw new RuntimeException("Signature is not valid.");
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | SignatureException e) {
            LOG.error("Something went wrong: {}", e.getMessage(), e);
            throw new RuntimeException("could not validate token",e);
        }
    }

    public LoginToken buildToken(Email email){
        //sign the email.
        try {
            byte[] emailBytes = email.email.getBytes("UTF8");
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(keyPairProvider.getKeyPair().getPrivate());
            sig.update(emailBytes);
            byte[] signatureBytes = sig.sign();
            String signature = Base64.getEncoder().encodeToString(signatureBytes);
            String signedToken = Base64.getEncoder().encodeToString(emailBytes) + "." + signature;
            LOG.debug(signedToken);
            return new LoginToken(email, signedToken);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException("exception when when generating login token.", e);
        }
    }
}

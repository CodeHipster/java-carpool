package thijs.oostdam.carpool.authentication;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyPairProvider {

    private final KeyPair keyPair;
    public KeyPairProvider(){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            keyPair = keyGen.generateKeyPair();
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException();
        }
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}

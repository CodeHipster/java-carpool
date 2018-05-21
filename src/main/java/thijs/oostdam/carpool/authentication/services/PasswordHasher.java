package thijs.oostdam.carpool.authentication.services;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import thijs.oostdam.carpool.authentication.domain.PasswordHash;

import java.security.SecureRandom;

/**
 * Hashes and salts passwords. With different salt for each password.
 * This allows us to work with the password without knowing the password.
 *
 * It protects the password against pre hashed dictionary attacks.
 * By using a different salt for each password it protects against pre hashed dictionary attacks for against all passwords.
 *
 * It does NOT protect against dictionary attacks for a single specific password.
 * (as an attacker can hash a dictionary with the salt.)
 *
 */
public class PasswordHasher {
    private SecureRandom random;

    public PasswordHasher(SecureRandom random){
        this.random = random;
    }

    public PasswordHash hashPassword(String password){

        //add salt to password
        byte salt[] = new byte[4];
        random.nextBytes(salt);
        byte[] saltedPassword = Bytes.concat(password.getBytes(), salt);

        //calculate hash
        HashCode saltedPasswordHash = Hashing.sha256().hashBytes( saltedPassword );
        return new PasswordHash(saltedPasswordHash.asBytes(), salt);
    }
}

package thijs.oostdam.carpool.authentication.domain;

public class PasswordHash {

    private final byte[] saltedPasswordHash;
    private final byte[] salt;

    public PasswordHash(byte[] hash, byte[] salt){

        this.saltedPasswordHash = hash;
        this.salt = salt;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getHash() {
        return saltedPasswordHash;
    }
}

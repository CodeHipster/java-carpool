package thijs.oostdam.carpool.authentication;

public class Password {

    private final byte[] passwordHash;
    private final byte[] salt;

    public Password(byte[] hash, byte[] salt){
        this.passwordHash = hash;
        this.salt = salt;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }
}

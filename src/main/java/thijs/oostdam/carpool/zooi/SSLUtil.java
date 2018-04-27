package thijs.oostdam.carpool.zooi;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

public class SSLUtil {

    public SSLContext getSSLContext(){
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    }
}

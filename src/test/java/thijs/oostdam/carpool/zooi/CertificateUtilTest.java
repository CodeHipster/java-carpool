package thijs.oostdam.carpool.zooi;

import org.junit.jupiter.api.Test;

import java.security.cert.X509Certificate;

class CertificateUtilTest {

    @Test
    void getRandomCertificate() {
        CertificateUtil util = new CertificateUtil();
        X509Certificate randomCertificate = util.getRandomCertificate();
    }
}
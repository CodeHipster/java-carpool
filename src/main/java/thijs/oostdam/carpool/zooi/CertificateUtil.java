package thijs.oostdam.carpool.zooi;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import thijs.oostdam.carpool.authentication.services.KeyPairProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateUtil {

    private int counter = 0;

    public X509Certificate getRandomCertificate() {

        KeyPairProvider kpp = new KeyPairProvider();
        KeyPair keyPair = kpp.getKeyPair();

        AlgorithmIdentifier algorithmIdentifier = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA256withRSA");

        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
                new X500Name("name"),
                new BigInteger(""+counter),
                new Date(),
                new Date(),
                new X500Name("subject"),
                new SubjectPublicKeyInfo(algorithmIdentifier,keyPair.getPublic().getEncoded())
        );
        counter++;
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find("SHA256withRSA");
        AsymmetricKeyParameter privateKeyAsymKeyParam = null;
        try {
            privateKeyAsymKeyParam = PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        ContentSigner sigGen;
        try {
            sigGen = new BcRSAContentSignerBuilder(algorithmIdentifier, digAlgId).build(privateKeyAsymKeyParam);
        } catch (OperatorCreationException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        X509CertificateHolder bouncyCert = builder.build(sigGen);
        try {
            return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(bouncyCert);
        } catch (CertificateException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}

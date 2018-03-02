package thijs.oostdam.carpool.jwt.handlers;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.*;
import java.text.MessageFormat;
import java.util.Base64;

public class LoginHandler implements HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);
    private static final String ERROR_TEMPLATE = "'{'\"message\":\"{0}\"'}'";

    private static final KeyPair keyPair;
    static {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            keyPair = keyGen.generateKeyPair();
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException();
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        InputStreamReader r = new InputStreamReader(exchange.getRequestBody(), Charsets.UTF_8);
        Login login = new Gson().fromJson(r, Login.class);

        LOG.info("User({}) logging in.",login);

        //OutputStream is incompatible with java7 try with resources.
        OutputStream os = exchange.getResponseBody();
        try {
            //sign the email.
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(keyPair.getPrivate());
            sig.update(login.email.getBytes("UTF8"));
            byte[] signatureBytes = sig.sign();
            String signature = Base64.getEncoder().encodeToString(signatureBytes);
            String signedToken = Base64.getEncoder().encodeToString(login.email.getBytes("UTF8")) + "." + signature;
            LOG.debug(signedToken);

//            sig.initVerify(keyPair.getPublic());
//            sig.update(login.email.getBytes());
//            System.out.println(sig.verify(Base64.getDecoder().decode(signature)));

            //Put it in the header
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("signed-token", signedToken);
            exchange.sendResponseHeaders(204,0);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException("There was a problem security problem.", e);
        }catch (Exception e) {
            LOG.error("Something went wrong: {}", e.getMessage(), e);
            String response = MessageFormat.format(ERROR_TEMPLATE, e.getMessage());
            exchange.sendResponseHeaders(500, response.getBytes().length);
            os.write(response.getBytes());
        } finally {
            os.close();
        }

        return;
    }
}

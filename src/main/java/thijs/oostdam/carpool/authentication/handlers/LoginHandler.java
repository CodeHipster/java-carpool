package thijs.oostdam.carpool.authentication.handlers;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.Login;
import thijs.oostdam.carpool.authentication.Password;
import thijs.oostdam.carpool.authentication.PasswordRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.Signature;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Optional;

public class LoginHandler implements HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);
    private static final String ERROR_TEMPLATE = "'{'\"message\":\"{0}\"'}'";
    private final PrivateKey key;
    private final PasswordRepository passwordRepository;

    public LoginHandler(PrivateKey key, PasswordRepository passwordRepository){
        this.key = key;
        this.passwordRepository = passwordRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        InputStreamReader r = new InputStreamReader(exchange.getRequestBody(), Charsets.UTF_8);
        Login login = new Gson().fromJson(r, Login.class);

        LOG.info("User({}) logging in.",login);
        Optional<Password> password = passwordRepository.getPassword(login.email);
        if(!password.isPresent()){
            //return bad request. email not known.
        }

        //verify password, add the salt and then compare to hash stored in db.
        byte[] passwordBytes = login.password.getBytes();
        byte[] saltedpassword = Bytes.concat(passwordBytes, password.get().getSalt());
        HashCode hash = Hashing.sha256().hashBytes( saltedpassword );

        if(!hash.equals(HashCode.fromBytes(password.get().getPasswordHash()))){
            //return bad request incorrect password.
        }

        //OutputStream is incompatible with java7 try with resources.
        OutputStream os = exchange.getResponseBody();
        try {
            //sign the email.
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(key);
            sig.update(login.email.getBytes("UTF8"));
            byte[] signatureBytes = sig.sign();
            String signature = Base64.getEncoder().encodeToString(signatureBytes);
            String signedToken = Base64.getEncoder().encodeToString(login.email.getBytes("UTF8")) + "." + signature;
            LOG.debug(signedToken);

            //Put it in the header
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("signed-id", signedToken);
            exchange.sendResponseHeaders(204,0);
        }catch (Exception e) {
            LOG.error("Something went wrong: {}", e.getMessage(), e);
            byte[] response = MessageFormat.format(ERROR_TEMPLATE, e.getMessage()).getBytes();
            exchange.sendResponseHeaders(500, response.length);
            os.write(response);
        } finally {
            os.close();
        }

        return;
    }
}

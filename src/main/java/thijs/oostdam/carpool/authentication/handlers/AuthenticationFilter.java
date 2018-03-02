package thijs.oostdam.carpool.authentication.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.text.MessageFormat;
import java.util.Base64;

public class AuthenticationFilter implements HttpHandler{

    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);
    private static final String ERROR_TEMPLATE = "'{'\"message\":\"{0}\"'}'";

    private PublicKey key;
    private HttpHandler next;

    public AuthenticationFilter(PublicKey key, HttpHandler next){
        this.key = key;
        this.next = next;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String idToken = exchange.getRequestHeaders().getFirst("signed-id");

        //get the part after the dot. which is the signature.
        //verify the part before the dot.
        String[] split = idToken.split(".");
        byte[] id = Base64.getDecoder().decode(split[0]);
        byte[] signature = Base64.getDecoder().decode(split[1]);

        //OutputStream is incompatible with java7 try with resources.
        OutputStream os = exchange.getResponseBody();
        try {
            Signature sig = null;
            sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(key);
            sig.update(id);
            if(!sig.verify(signature)) throw new RuntimeException("Signature is not valid.");
            exchange.setAttribute("id",new String(id,"utf-8"));
            next.handle(exchange);
        } catch (Exception e) {
            LOG.error("Something went wrong: {}", e.getMessage(), e);
            byte[] response = MessageFormat.format(ERROR_TEMPLATE, e.getMessage()).getBytes();
            exchange.sendResponseHeaders(500, response.length);
            os.write(response);
            os.close();
        }
    }
}
//test no header
//test empty header
//test no . in header
//test only . in header
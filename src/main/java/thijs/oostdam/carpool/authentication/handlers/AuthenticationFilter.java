package thijs.oostdam.carpool.authentication.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.LoginToken;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

//TODO: differentiate between user and system errors.
public class AuthenticationFilter implements HttpHandler{

    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);
    private static final String ERROR_TEMPLATE = "'{'\"message\":\"{0}\"'}'";

    private AuthenticationService service;
    private HttpHandler next;

    public AuthenticationFilter(AuthenticationService service, HttpHandler next){
        this.service = service;
        this.next = next;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String idToken = exchange.getRequestHeaders().getFirst("login-token");

        //OutputStream is incompatible with java7 try with resources.
        OutputStream os = exchange.getResponseBody();
        try {
            LoginToken token = service.validateToken(idToken);
            exchange.setAttribute("email",token.email.email);
            next.handle(exchange);
        } catch (Exception e) {
            //TODO: differentiate between user error and system error.
            LOG.error("Something went wrong: {}", e.getMessage(), e);
            byte[] response = MessageFormat.format(ERROR_TEMPLATE, e.getMessage()).getBytes();
            exchange.sendResponseHeaders(500, response.length);
            os.write(response);
            os.close();
        }
    }
}
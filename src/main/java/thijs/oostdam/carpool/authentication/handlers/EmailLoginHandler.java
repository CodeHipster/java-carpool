package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.Email;
import thijs.oostdam.carpool.authentication.domain.Login;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.authentication.transport.LoginHttp;
import thijs.oostdam.carpool.generic.Response;

import java.util.List;

/**
 * Class for handling logging in through email.
 */
public class EmailLoginHandler extends JsonHandler<LoginHttp, Void> {

    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);
    private final AuthenticationService service;

    public EmailLoginHandler(AuthenticationService service){
        super(LoginHttp.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(LoginHttp login, List<NameValuePair> headers) {

        LOG.info("User({}) logging in via email.",login.email);
        service.loginViaEmail(
                new Login(
                        new Email(login.email),login.password));

        return new Response<>(null,null);
    }
}

package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.Login;
import thijs.oostdam.carpool.authentication.domain.LoginToken;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.generic.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LoginHandler extends JsonHandler<Login, Void> {

    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);
    private final AuthenticationService service;

    public LoginHandler(AuthenticationService service){
        super(Login.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(Login login, List<NameValuePair> headers) throws IOException {

        LOG.info("User({}) logging in.",login);
        LoginToken loginToken = service.login(login);

        List<NameValuePair> responseHeaders = Arrays.asList(new BasicNameValuePair("login-token", loginToken.token));

        return new Response<Void>(null, headers);
    }
}

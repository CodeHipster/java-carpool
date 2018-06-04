package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.EmailAddress;
import thijs.oostdam.carpool.authentication.domain.Login;
import thijs.oostdam.carpool.authentication.domain.LoginToken;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.authentication.transport.LoginHttp;
import thijs.oostdam.carpool.generic.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LoginHandler extends JsonHandler<LoginHttp, Void> {

    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);
    private final AuthenticationService service;

    public LoginHandler(AuthenticationService service){
        super(LoginHttp.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(LoginHttp login, List<NameValuePair> headers) throws IOException {

        LOG.info("User({}) logging in.", login.email);

        if (login.password != null) {
            LOG.info("Using password.", login.email);
            LoginToken loginToken = service.getLoginToken(
                    new Login(
                            new EmailAddress(login.email), login.password));

            List<NameValuePair> responseHeaders = Arrays.asList(new BasicNameValuePair("login-token", loginToken.token));
            return new Response<>(null, responseHeaders);
        }else{
            LOG.info("Using address.",login.email);
            service.loginViaEmail(
                    new Login(
                            new EmailAddress(login.email),login.password));

            return new Response<>(null,null);
        }
    }
}

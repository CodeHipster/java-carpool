package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.Email;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.generic.Response;

import java.util.List;

public class ResetPasswordHandler extends JsonHandler<Email, Void>{

    private static final Logger LOG = LoggerFactory.getLogger(RegisterHandler.class);

    private AuthenticationService service;

    public ResetPasswordHandler(AuthenticationService service){
        super(Email.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(Email email, List<NameValuePair> queryParams){

        service.resetPassword(email);
        return new Response<>(null, null);
    }
}

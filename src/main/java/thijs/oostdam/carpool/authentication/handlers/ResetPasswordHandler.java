package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.EmailAddress;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.generic.Response;

import java.util.List;

public class ResetPasswordHandler extends JsonHandler<EmailAddress, Void>{

    private static final Logger LOG = LoggerFactory.getLogger(RegisterHandler.class);

    private AuthenticationService service;

    public ResetPasswordHandler(AuthenticationService service){
        super(EmailAddress.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(EmailAddress email, List<NameValuePair> queryParams){

        service.resetPassword(email);
        return new Response<>(null, null);
    }
}

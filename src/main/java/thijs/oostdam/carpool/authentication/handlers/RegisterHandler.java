package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.Registration;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.authentication.transport.RegistrationHttp;
import thijs.oostdam.carpool.generic.Response;

import java.util.List;

public class RegisterHandler extends JsonHandler<RegistrationHttp, Void> {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterHandler.class);
    private static final String ERROR_TEMPLATE = "'{'\"message\":\"{0}\"'}'";

    private AuthenticationService service;

    public RegisterHandler(AuthenticationService service){
        super(RegistrationHttp.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(RegistrationHttp registration, List<NameValuePair> queryParams){

        service.register(new Registration(registration.email, registration.password));
        return new Response<>(null, null);
    }
}

package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.Registration;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.generic.Response;

import java.util.List;

public class RegisterHandler extends JsonHandler<Registration, Void> {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterHandler.class);
    private static final String ERROR_TEMPLATE = "'{'\"message\":\"{0}\"'}'";

    private AuthenticationService service;

    public RegisterHandler(AuthenticationService service){
        super(Registration.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(Registration registration, List<NameValuePair> queryParams){

        service.register(registration);
        return new Response<>(null, null);
    }
}

package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.VerificationCode;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.generic.Response;

import java.util.List;

public class RegistrationVerificationHandler extends JsonHandler<VerificationCode, Void> {
    //receive a code and an email
    //if code matches verification then email is verified

    private static final Logger LOG = LoggerFactory.getLogger(RegisterHandler.class);
    private static final String ERROR_TEMPLATE = "'{'\"message\":\"{0}\"'}'";

    private AuthenticationService service;

    public RegistrationVerificationHandler(AuthenticationService service){
        super(VerificationCode.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(VerificationCode code, List<NameValuePair> queryParams){

        service.verifyCode(code);
        return new Response<>(null, null);
    }
}
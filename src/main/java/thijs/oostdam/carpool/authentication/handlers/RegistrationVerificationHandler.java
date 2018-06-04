package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.EmailAddress;
import thijs.oostdam.carpool.authentication.domain.VerificationCode;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.authentication.transport.VerificationCodeHttp;
import thijs.oostdam.carpool.generic.Response;

import java.util.List;

public class RegistrationVerificationHandler extends JsonHandler<VerificationCodeHttp, Void> {
    //receive a code and an address
    //if code matches verification then address is verified

    private static final Logger LOG = LoggerFactory.getLogger(RegisterHandler.class);
    private static final String ERROR_TEMPLATE = "'{'\"message\":\"{0}\"'}'";

    private AuthenticationService service;

    public RegistrationVerificationHandler(AuthenticationService service){
        super(VerificationCodeHttp.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(VerificationCodeHttp code, List<NameValuePair> queryParams){

        service.verifyCode(
                new VerificationCode(
                        new EmailAddress(code.email), code.code));

        return new Response<>(null, null);
    }
}

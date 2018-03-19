package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.NewPassword;
import thijs.oostdam.carpool.authentication.domain.Registration;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.generic.Response;

import java.util.List;

public class ChangePasswordHandler extends JsonHandler<NewPassword, Void>{

    private static final Logger LOG = LoggerFactory.getLogger(RegisterHandler.class);

    private AuthenticationService service;

    public ChangePasswordHandler(AuthenticationService service){
        super(Registration.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(NewPassword newPassword, List<NameValuePair> queryParams){

        service.changePassword(newPassword);
        return new Response<>(null, null);
    }

}

package thijs.oostdam.carpool.authentication.handlers;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.authentication.domain.Email;
import thijs.oostdam.carpool.authentication.domain.NewPassword;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.authentication.transport.NewPasswordHttp;
import thijs.oostdam.carpool.generic.Response;

import java.util.List;

public class ChangePasswordHandler extends JsonHandler<NewPasswordHttp, Void>{

    private static final Logger LOG = LoggerFactory.getLogger(RegisterHandler.class);

    private AuthenticationService service;

    public ChangePasswordHandler(AuthenticationService service){
        super(NewPasswordHttp.class);
        this.service = service;
    }

    @Override
    public Response<Void> post(NewPasswordHttp newPassword, List<NameValuePair> queryParams){

        service.changePassword(new NewPassword(new Email(newPassword.email), newPassword.oldPassword, newPassword.newPassword));
        return new Response<>(null, null);
    }

}

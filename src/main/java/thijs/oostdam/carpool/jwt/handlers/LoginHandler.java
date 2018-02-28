package thijs.oostdam.carpool.jwt.handlers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.core.handlers.dto.TripHttp;
import thijs.oostdam.carpool.core.handlers.resources.JsonHandler;
import thijs.oostdam.carpool.core.handlers.resources.PassengerHandler;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class LoginHandler extends JsonHandler<Login, Void>{

    private static final Logger LOG = LoggerFactory.getLogger(PassengerHandler.class);

    private SecretKey key;

    public LoginHandler(){
        super(TripHttp.class);
        key = MacProvider.generateKey();
    }

    @Override
    public Void post(Login login, List<NameValuePair> queryParams) throws IOException {
        LOG.info("User({}) logging in.",login);

        String sjwt = Jwts.builder()
                .setSubject(login.email)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        //Set it in the header

        return null;
    }
}

package thijs.oostdam.carpool.jwt.handlers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;

public class JwtFilter {
    private void verify(String compactJwt){
        Jwts.parser().setSigningKey(getKey()).parseClaimsJws(compactJwt);
    }

    private Key getKey(){
        return MacProvider.generateKey();
    }
}

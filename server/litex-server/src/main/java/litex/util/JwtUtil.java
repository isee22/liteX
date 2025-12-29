package litex.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;

public class JwtUtil {
    static final String SECRET = "litex-secret";
    static final long EXPIRE = 7 * 24 * 60 * 60 * 1000;

    public static String generateToken(Long userId) {
        return JWT.create()
            .withClaim("userId", userId)
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE))
            .sign(Algorithm.HMAC256(SECRET));
    }

    public static Long getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
            return jwt.getClaim("userId").asLong();
        } catch (Exception e) {
            return null;
        }
    }
}

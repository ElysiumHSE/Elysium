package hse.elysium.serverspring.auth;

import hse.elysium.entities.User;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.web.header.Header;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;


@Service
public class JwtService {

    private final String SECRET_KEY;
    private final Logger log = LogManager.getLogger(JwtService.class);

    JwtService() {
        String SECRET_KEY1;
        Properties prop = new Properties();
        InputStream input;
        try {
            input = new FileInputStream("gradle.properties");
            prop.load(input);
            SECRET_KEY1 = prop.getProperty("JWT_SECRET_KEY");
        } catch (IOException ex) {
            SECRET_KEY1 = "";
            log.error("not found gradle.properties");
        }
        SECRET_KEY = SECRET_KEY1;
        log.info("JWT secret key fetched");
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Integer extractUserId(String token) {
        return extractClaim(token, (claims -> claims.get("UserId", Integer.class)));
    }

    public String extractIssuer(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user, int userId) {
        return generateToken(new HashMap<>(), user, userId);
    }

    public String generateToken(Map<String, Object> extraClaims, User user, int userId) {
        extraClaims.put("UserId", userId);
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getLogin())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer("Elysium")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // Token lives for 1 week
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    Boolean isTokenValid(String token, String username) {
        return (extractUsername(token).equals(username) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}

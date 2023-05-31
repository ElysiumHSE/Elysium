package hse.elysium.serverspring.auth;

import hse.elysium.databaseInteractor.TokenService;
import hse.elysium.entities.Token;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;


@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@ComponentScan("hse.elysium")
public final class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final Logger log = LogManager.getLogger(AuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("AuthenticationFilter.doFilterInternal");
        final String token = request.getHeader("Authorization");
        log.info(token);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtService.extractUsername(token);
            String issuer = jwtService.extractIssuer(token);
            if (username != null && issuer.equals("Elysium") &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Token tokenEntity = tokenService.getTokenWithTokenValue(token);
                Boolean isValid = jwtService.isTokenValid(token, username);
                if (isValid && !tokenEntity.getExpired() && !tokenEntity.getRevoked()) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, null);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    request.setAttribute("UserId", jwtService.extractUserId(token));
                    log.info("Authentication set");
                }
            }
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | SignatureException e) {
            log.info("Incorrect jwt token");
            }
        filterChain.doFilter(request, response);
    }

}

package hse.elysium.serverspring.auth;

import hse.elysium.databaseInteractor.TokenService;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ComponentScan("hse.elysium")
public class LogoutService implements LogoutHandler {

    private final TokenService tokenService;
    private final Logger log = LogManager.getLogger(LogoutService.class);


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("LogoutService.logout");
        String token = request.getHeader("Authorization");
        log.info(token);

        try {
            tokenService.setRevokedWithTokenValue(token);
        } catch (NoResultException e){
            log.info("Incorrect token in logout");
        }
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}

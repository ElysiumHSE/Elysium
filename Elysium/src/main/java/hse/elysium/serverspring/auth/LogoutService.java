package hse.elysium.serverspring.auth;

import hse.elysium.databaseInteractor.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        System.out.println("LogoutService.logout");
        String token = request.getHeader("Authorization");
        System.out.println(token);

        tokenService.setRevokedWithTokenValue(token);
        SecurityContextHolder.getContext().setAuthentication(null);

        System.out.println();
    }
}

package hse.elysium.serverspring.auth;

import hse.elysium.databaseInteractor.TokenService;
import hse.elysium.databaseInteractor.UserService;
import hse.elysium.entities.User;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@ComponentScan("hse.elysium")
public class AuthenticationService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final Logger log = LogManager.getLogger(AuthenticationService.class);


    public String register(String username, String password) {
        log.info("AuthenticationService.register");
        log.info(username);
        log.info(password);

        try {
            userService.addNewUserWithLoginPassword(username, passwordEncoder.encode(password));
            log.info("Success");
            return "Success";
        } catch (PersistenceException e) {
            log.info("Already exists");
            return "Already exists";
        }
    }

    public String login(String username, String password) {
        log.info("AuthenticationService.login");
        log.info(username + " " + password);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        try {
            int userId = userService.getUserIdWithLogin(username);
            User user = userService.getUserWithUserId(userId);
            String token = jwtService.generateToken(user);

            tokenService.addNewTokenWithTokenValueUserId(token, userId);

            log.info(token);
            return token;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<UserLoginPasswordForm> changePassword(String token, String newPassword) {
        log.info("AuthenticationService.changePassword");

        String username = jwtService.extractUsername(token);
        try {
            int userId = userService.getUserIdWithLogin(username);
            userService.changePasswordWithUserIdPassword(userId, passwordEncoder.encode(newPassword));
            tokenService.setRevokedForUserChangedPasswordWithUserId(userId);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, newPassword));

            return Optional.of(new UserLoginPasswordForm(username, newPassword));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}

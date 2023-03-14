package hse.elysium.serverspring.auth;

import hse.elysium.databaseInteractor.TokenService;
import hse.elysium.databaseInteractor.UserService;
import hse.elysium.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@ComponentScan("hse.elysium")
public class AuthenticationService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenService tokenService;

    public String register(String username, String password) {
        System.out.println("AuthenticationService.register");
        System.out.println(username);
        System.out.println(password);

        int userId = userService.addNewUserWithLoginPassword(username, passwordEncoder.encode(password));
        if (userId == -1){
            return "Already exists";
        }
        System.out.println();
        return "Success";
    }

    public String login(String username, String password) {
        System.out.println("AuthenticationService.login");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));

        int userId = userService.getUserIdWithLogin(username);
        if (userId == -1) return null;
        User user = userService.getUserWithUserId(userId);
        String token = jwtService.generateToken(user);

        tokenService.addNewTokenWithTokenValueUserId(token, userId);

        System.out.println(token);
        System.out.println(user.getLogin());
        System.out.println();
        return token;
    }

    public Optional<UserLoginPasswordForm> changePassword(String token, String newPassword){
        System.out.println("AuthenticationService.changePassword");

        String username = jwtService.extractUsername(token);
        int userId = userService.getUserIdWithLogin(username);
        if (userId == -1){
            return Optional.empty();
        }
        userService.changePasswordWithUserIdPassword(userId, passwordEncoder.encode(newPassword));
        tokenService.setRevokedForUserChangedPasswordWithUserId(userId);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,newPassword));

        return Optional.of(new UserLoginPasswordForm(username, newPassword));
    }

}

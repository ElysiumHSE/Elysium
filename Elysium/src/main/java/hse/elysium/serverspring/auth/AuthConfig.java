package hse.elysium.serverspring.auth;

import hse.elysium.databaseInteractor.UserService;
import hse.elysium.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@Configuration
@RequiredArgsConstructor
@ComponentScan("hse.elysium")
public class AuthConfig {

    private final UserService userService;

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> {
//            int userId = userService.getUserIdWithLogin(username);
//            if (userId == -1){
//                throw new UsernameNotFoundException("User not found");
//            }
//            User user = userService.getUserWithUserId(userId);
//            return new org.springframework.security.core.userdetails.User(
//                    user.getLogin(), user.getPassword(),
//                    new ArrayList<>()
//            );
//        };
//    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

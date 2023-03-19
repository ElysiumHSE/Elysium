package hse.elysium.serverspring.controller;

import hse.elysium.databaseInteractor.UserService;
import hse.elysium.entities.User;
import hse.elysium.serverspring.auth.AuthenticationService;
import hse.elysium.serverspring.auth.JwtService;
import hse.elysium.serverspring.auth.UserLoginPasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ComponentScan("hse.elysium")
@RequestMapping("/elysium")
@RequiredArgsConstructor
public class Controller {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/test")
    ResponseEntity<String> testResponse(){
        return ResponseEntity.ok("Authorized");
    }

    @GetMapping("/getUserInfo")
    ResponseEntity<User> getUserInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String username = jwtService.extractUsername(token);
        int user_id = userService.getUserIdWithLogin(username);
        return new ResponseEntity<>(userService.getUserWithUserId(user_id), HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    ResponseEntity<UserLoginPasswordForm> changePassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody PasswordForm passwordForm) {
        var result = authenticationService.changePassword(token, passwordForm.getPassword());
        if (result.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(result.get(), HttpStatus.OK);
        }
    }
}

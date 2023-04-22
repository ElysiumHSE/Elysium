package hse.elysium.serverspring.controller;

import hse.elysium.databaseInteractor.UserService;
import hse.elysium.entities.User;
import hse.elysium.serverspring.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@ComponentScan("hse.elysium")
@RequestMapping("/elysium/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/getUserInfo")
    ResponseEntity<User> getUserInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String username = jwtService.extractUsername(token);
        int user_id = userService.getUserIdWithLogin(username);
        return new ResponseEntity<>(userService.getUserWithUserId(user_id), HttpStatus.OK);
    }


}

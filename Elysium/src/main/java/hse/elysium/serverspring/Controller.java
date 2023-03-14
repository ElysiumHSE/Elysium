package hse.elysium.serverspring;

import hse.elysium.entities.User;
import hse.elysium.serverspring.auth.AuthenticationService;
import hse.elysium.serverspring.auth.UserLoginPasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import hse.elysium.databaseInteractor.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/elysium")
@RequiredArgsConstructor
public class Controller {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/test")
    ResponseEntity<String> testResponse(){
        return ResponseEntity.ok("Authorized");
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

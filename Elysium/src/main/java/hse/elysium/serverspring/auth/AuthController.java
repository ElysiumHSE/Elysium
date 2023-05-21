package hse.elysium.serverspring.auth;

import hse.elysium.serverspring.forms.UserLoginPasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/elysium/auth")
@RequiredArgsConstructor
@ComponentScan("hse.elysium")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserLoginPasswordForm userRegisterForm) {
        String res = authenticationService.register(userRegisterForm.getLogin(), userRegisterForm.getPassword());
        if (res.equals("Already exists")) {
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginPasswordForm userRegisterForm) {
        return ResponseEntity.ok(authenticationService.login(userRegisterForm.getLogin(), userRegisterForm.getPassword()));
    }

}

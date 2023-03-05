package hse.elysium.serverspring;

import hse.elysium.entities.TrackEntity;
import hse.elysium.entities.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/elysium")
public class Controller {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    ResponseEntity<List<UserEntity>> getUser1() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

    @GetMapping("/user/all")
    ResponseEntity<List<UserEntity>> getUser() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

//    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
//    private final EntityManager entityManager;
//
//    Controller() {
//        entityManager = entityManagerFactory.createEntityManager();
//    }
//
//    @PostMapping("/user/register")
//    ResponseEntity<UserEntity> registerUser(@RequestBody UserRegisterForm userRegisterForm) {
//        EntityTransaction transaction = entityManager.getTransaction();
//        UserEntity newUser = null;
//        try {
//            transaction.begin();
//            Random rand = new Random();
//            newUser = new UserEntity();
//            newUser.setUserId(rand.nextInt());
//            newUser.setFavourites("");
//            newUser.setLogin(userRegisterForm.getLogin());
//            newUser.setPassword(userRegisterForm.getPassword());
//            entityManager.merge(newUser);
//            transaction.commit();
//        } finally {
//            if (transaction.isActive()) {
//                transaction.rollback();
//                newUser = null;
//            }
//        }
//        if (newUser == null) {
//            return new ResponseEntity<UserEntity>(newUser, HttpStatus.SERVICE_UNAVAILABLE);
//        } else {
//            return new ResponseEntity<UserEntity>(newUser, HttpStatus.OK);
//        }
//    }
//
//    @GetMapping("/user/{id}")
//    ResponseEntity<UserEntity> getUser(@PathVariable Integer id) {
//        EntityTransaction transaction = entityManager.getTransaction();
//        UserEntity user;
//        try {
//            transaction.begin();
//
//            user = entityManager.find(UserEntity.class, id);
//
//            transaction.commit();
//        } finally {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//        }
//        if (user == null) {
//            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
//        } else {
//            return new ResponseEntity<>(user, HttpStatus.OK);
//        }
//    }
//
//    @GetMapping("/track/{id}")
//    ResponseEntity<TrackEntity> getTrack(@PathVariable Integer id){
//        EntityTransaction transaction = entityManager.getTransaction();
//        TrackEntity track;
//        try {
//            transaction.begin();
//
//            track = entityManager.find(TrackEntity.class, id);
//
//            transaction.commit();
//        } finally {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//        }
//        if (track == null) {
//            return new ResponseEntity<>(track, HttpStatus.BAD_REQUEST);
//        } else {
//            return new ResponseEntity<>(track, HttpStatus.OK);
//        }
//    }


}

class UserRegisterForm {
    private final String login;
    private final String password;

    public UserRegisterForm(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}

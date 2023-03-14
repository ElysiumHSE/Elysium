package hse.elysium.serverspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EntityScan("hse.elysium")
public class ServerSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerSpringApplication.class, args);
    }
}

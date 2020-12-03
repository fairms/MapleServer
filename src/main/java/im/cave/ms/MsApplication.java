package im.cave.ms;

import im.cave.ms.net.server.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsApplication.class, args);
        Server.getInstance().init();
    }
}

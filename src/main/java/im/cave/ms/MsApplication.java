package im.cave.ms;

import im.cave.ms.network.server.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MsApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(MsApplication.class, args);
        Server.getInstance().init();
        Runtime rt = Runtime.getRuntime();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            rt.exec("gsudo netsh int i add addr 1 221.231.130.70");
        }
    }
}
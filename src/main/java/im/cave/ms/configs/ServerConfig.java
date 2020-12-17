package im.cave.ms.configs;

import com.esotericsoftware.yamlbeans.YamlReader;
import im.cave.ms.MsApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.config
 * @date 11/19 23:36
 */
public class ServerConfig {
    public static ServerConfig config = loadConfig("server-config.yml");
    public boolean AUTOMATIC_REGISTER;
    public List<Short> CLOSED_JOBS;


    public static ServerConfig loadConfig(String filename) {
        try {
            URL resource = MsApplication.class.getClassLoader().getResource(filename);
            String path = resource.getPath();
            File file = new File("resource/" + filename);
            YamlReader reader = new YamlReader(new FileReader(new File(path)));
            ServerConfig config = reader.read(ServerConfig.class);
            reader.close();
            return config;
        } catch (FileNotFoundException e) {
            String message = "Could not read config file " + filename + ": " + e.getMessage();
            throw new RuntimeException(message);
        } catch (IOException e) {
            String message = "Could not successfully parse config file " + filename + ": " + e.getMessage();
            throw new RuntimeException(message);
        }
    }

}

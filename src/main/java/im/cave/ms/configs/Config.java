package im.cave.ms.configs;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider
 * @date 12/28 16:01
 */
public class Config {
    public static ServerConfig serverConfig = (ServerConfig) loadConfig("server.yml", ServerConfig.class);
    public static WorldConfig worldConfig = (WorldConfig) loadConfig("worldId.yml", WorldConfig.class);

    public static Object loadConfig(String filename, Class<?> clazz) {
        try {
            File file = new File("configs/" + filename);
            YamlReader reader = new YamlReader(new FileReader(file));
            Object config = reader.read(clazz);
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

    public static void reload() {
        serverConfig = (ServerConfig) loadConfig("server-config.yml", ServerConfig.class);
        worldConfig = (WorldConfig) loadConfig("worldId-config.yml", WorldConfig.class);
    }
}

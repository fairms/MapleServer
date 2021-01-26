package im.cave.ms.scripting;

import im.cave.ms.client.MapleClient;
import im.cave.ms.constants.ServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;


public abstract class AbstractScriptManager {
    protected static final Logger log = LoggerFactory.getLogger(AbstractScriptManager.class);
    private final ScriptEngineManager sem;
    protected ScriptEngine engine;

    protected AbstractScriptManager() {
        sem = new ScriptEngineManager();
    }

    protected Invocable getInvocable(String path, MapleClient c) {
        try {
            path = ServerConstants.SCRIPT_DIR + path;
            engine = null;
            if (c != null) {
                engine = c.getScriptEngine(path);
            }
            if (engine == null) {
                File scriptFile = new File(path);
                if (!scriptFile.exists()) {
                    return null;
                }
                engine = sem.getEngineByName("graal.js");
                if (c != null) {
                    c.setScriptEngine(path, engine);
                }
                final FileReader fr;
                fr = new FileReader(scriptFile);
                if (engine == null) {
                    fr.close();
                    return null;
                }
                Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
                bindings.put("polyglot.js.allowAllAccess", true);
                engine.eval(fr);
                fr.close();
            }
            return (Invocable) engine;
        } catch (Exception e) {
            log.error("Error executing script. Script file: " + path + ".", e);
            return null;
        }
    }

    protected void resetContext(String path, MapleClient c) {
        path = "scripts/" + path;
        c.removeScriptEngine(path);
    }
}

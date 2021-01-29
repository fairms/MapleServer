package im.cave.ms.tools;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Stat;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.scripting.npc.NpcConversationManager;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.tools
 * @date 1/7 10:05
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(JobConstants.getJobLevel((short) 110));
//        try {
//            scriptTest();
//        } catch (FileNotFoundException | ScriptException | NoSuchMethodException e) {
//            e.printStackTrace();
//        }
    }


    public static void scriptTest() throws FileNotFoundException, ScriptException, NoSuchMethodException {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("graal.js");
        String path = "scripts/npc/test.js";
        File scriptFile = new File(path);
        final FileReader fr;
        fr = new FileReader(scriptFile);
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("polyglot.js.allowAllAccess", true);
        engine.eval(fr);
        MapleClient mapleClient = new MapleClient(null, 0, 0);
        MapleCharacter chr = new MapleCharacter();
        chr.setId(1000000);
        mapleClient.setPlayer(chr);
        NpcConversationManager cm = new NpcConversationManager(mapleClient, 100000, null, path);
        engine.put("cm", cm);
        ((Invocable) engine).invokeFunction("start");
    }


}

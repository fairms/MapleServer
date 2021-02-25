package im.cave.ms.scripting.portal;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.Portal;
import im.cave.ms.connection.packet.MessagePacket;
import im.cave.ms.enums.BroadcastMsgType;
import im.cave.ms.scripting.AbstractScriptManager;

import javax.script.Invocable;
import javax.script.ScriptException;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.scripting.portal
 * @date 11/28 14:16
 */
public class PortalScriptManager extends AbstractScriptManager {
    private static final PortalScriptManager instance = new PortalScriptManager();

    public static PortalScriptManager getInstance() {
        return instance;
    }

    private PortalScriptManager() {
        super();
    }

    public boolean executePortalScript(Portal portal, MapleClient client) {
        String scriptPath = null;
        try {
            scriptPath = String.format("portal/%s.js", portal.getScript());
            Invocable iv = getInvocable(scriptPath, client);
            if (iv != null) {
                return (boolean) iv.invokeFunction("enter", new PortalPlayerInteraction(client, portal));
            } else {
                MapleCharacter player = client.getPlayer();
                client.getPlayer().dropMessage("地图:" + player.getMapId() + " 传送口:" + portal.getScript());
            }
        } catch (NoSuchMethodException | ScriptException e) {
            log.error(scriptPath);
            client.announce(MessagePacket.broadcastMsg("脚本执行错误", BroadcastMsgType.ALERT));
            client.removeScriptEngine(scriptPath);
            client.getPlayer().enableAction();
            e.printStackTrace();
        }
        return false;
    }

}

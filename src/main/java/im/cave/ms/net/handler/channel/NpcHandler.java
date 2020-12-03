package im.cave.ms.net.handler.channel;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.Npc;
import im.cave.ms.enums.NpcMessageType;
import im.cave.ms.provider.data.NpcData;
import im.cave.ms.provider.service.EventManager;
import im.cave.ms.scripting.npc.NpcConversationManager;
import im.cave.ms.scripting.npc.NpcScriptManager;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

import java.awt.*;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/30 19:10
 */
public class NpcHandler {
    public static void handleUserSelectNpc(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int objId = slea.readInt();
        MapleMap map = player.getMap();
        MapleMapObj obj = map.getObj(objId);
        Position playerPos = slea.readPos();
        if (!(obj instanceof Npc)) {
            player.chatMessage();
            return;
        }
        Npc npc = (Npc) obj;
        int npcId = npc.getTemplateId();
        String script = npc.getScripts().get(0);
        if (script == null) {
            if (false) {
                System.out.println("打开商店");
                NpcData.getShopById(npcId);
            } else {
                script = String.valueOf(npcId);
            }
        }
        String finalScript = script;
        EventManager.addEvent(() -> NpcScriptManager.getInstance().start(c, npcId, finalScript), 0);

    }


    public static void handleAction(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        NpcConversationManager cm = NpcScriptManager.getInstance().getCM(c);
        if (cm == null) {
            return;
        }
        byte lastType = slea.readByte();
        byte action = slea.readByte();
        if (action == 0 || action == -1) {
            cm.dispose();
            return;
        }
        NpcMessageType messageType = cm.getNpcScriptInfo().getMessageType();
        switch (messageType) {
            case AskText:
                if (action == 1) {
                    String response = slea.readMapleAsciiString();
                    cm.getNpcScriptInfo().addResponse(response);
                }
                break;
            case AskYesNo:
                if (action == 1) {
                    cm.getNpcScriptInfo().addResponse(1);
                }
                break;
            default:
                cm.dispose();
        }
    }
}

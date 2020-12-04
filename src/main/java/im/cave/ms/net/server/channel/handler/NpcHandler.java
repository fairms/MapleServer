package im.cave.ms.net.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.Npc;
import im.cave.ms.client.movement.Movement;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.NpcMessageType;
import im.cave.ms.net.packet.NpcPacket;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.provider.data.NpcData;
import im.cave.ms.provider.service.EventManager;
import im.cave.ms.scripting.npc.NpcConversationManager;
import im.cave.ms.scripting.npc.NpcScriptManager;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

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
            player.chatMessage(ChatType.Purple, "Unknown Error");
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

    public static void handleNpcAnimation(SeekableLittleEndianAccessor slea, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.NPC_ANIMATION.getValue());
        int objectID = slea.readInt();
        byte oneTimeAction = slea.readByte();
        byte chatIdx = slea.readByte();
        int duration = slea.readInt();
        byte keyPadState = 0;
        MovementInfo movement = null;
        MapleMapObj obj = c.getPlayer().getMap().getObj(objectID);
        if (obj instanceof Npc && ((Npc) obj).isMove()) {
            Npc npc = (Npc) obj;
            if (slea.available() > 0) {
                movement = new MovementInfo(npc.getPosition(), npc.getVPosition());
                movement.decode(slea);
                for (Movement m : movement.getMovements()) {
                    Position pos = m.getPosition();
                    Position vPos = m.getVPosition();
                    if (pos != null) {
                        npc.setPosition(pos);
                    }
                    if (vPos != null) {
                        npc.setVPosition(vPos);
                    }
                    npc.setMoveAction(m.getMoveAction());
                    npc.setFh(m.getFh());
                }
                if (slea.available() > 0) {
                    keyPadState = slea.readByte();
                }
            }
        }
        c.announce(NpcPacket.npcMove(objectID, oneTimeAction, chatIdx, duration, movement, keyPadState));
    }
}

package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.client.field.obj.npc.shop.NpcShop;
import im.cave.ms.client.movement.Movement;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.NpcMessageType;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.NpcPacket;
import im.cave.ms.network.packet.WorldPacket;
import im.cave.ms.network.packet.opcode.SendOpcode;
import im.cave.ms.network.server.service.EventManager;
import im.cave.ms.provider.data.NpcData;
import im.cave.ms.scripting.npc.NpcConversationManager;
import im.cave.ms.scripting.npc.NpcScriptManager;
import im.cave.ms.scripting.quest.QuestActionManager;
import im.cave.ms.scripting.quest.QuestScriptManager;
import im.cave.ms.tools.Position;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler.channel
 * @date 11/30 19:10
 */
public class NpcHandler {

    public static void handleUserSelectNPC(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int objId = inPacket.readInt();
        MapleMap map = player.getMap();
        MapleMapObj obj = map.getObj(objId);
        if (!(obj instanceof Npc)) {
            player.chatMessage(ChatType.Purple, "Unknown Error");
            return;
        }
        Npc npc = (Npc) obj;
        userSelectNpc(player, npc);
    }

    public static void talkToNPC(MapleCharacter chr, int npcId) {
        Npc npc = NpcData.getNpc(npcId);
        if (npc != null) {
            userSelectNpc(chr, npc);
        }
    }

    public static void userSelectNpc(MapleCharacter chr, Npc npc) {
        int npcId = npc.getTemplateId();
        String script = npc.getScripts().get(0);
        if (npc.getTrunkPut() > 0 || npc.getTrunkGet() > 0) {
            chr.announce(WorldPacket.openTrunk(npcId, chr.getAccount()));
            return;
        }
        if (script == null) {
            NpcShop shop = NpcData.getShopById(npcId);
            if (shop != null) {
                chr.setShop(shop);
                chr.announce(NpcPacket.openShop(npcId, 0, shop));
                chr.chatMessage(String.format("Opening shop %s", npc.getTemplateId()));
                return;
            } else {
                script = String.valueOf(npcId);
            }
        }
        String finalScript = script;
        EventManager.addEvent(() -> NpcScriptManager.getInstance().start(chr.getClient(), npcId, finalScript), 0);
    }

    public static void handleUserScriptMessageAnswer(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        NpcConversationManager cm = NpcScriptManager.getInstance().getCM(c);
        QuestActionManager qm = QuestScriptManager.getInstance().getQM(c);
        if (cm == null && qm == null) {
            return;
        }
        if (cm == null) {
            cm = qm;
        }
        byte lastType = inPacket.readByte();
        byte action = inPacket.readByte();
        if (action == -1) {
            cm.dispose();
            return;
        }
        NpcMessageType messageType = cm.getNpcScriptInfo().getMessageType();
        switch (messageType) {
            case AskText:
                if (action == 1) {
                    String response = inPacket.readMapleAsciiString();
                    cm.getNpcScriptInfo().addResponse(response);
                } else {
                    cm.dispose();
                }
                break;
            case AskYesNo:
                cm.getNpcScriptInfo().addResponse(action);
                break;
            case AskMenu:
                if (action == 0) {
                    cm.getNpcScriptInfo().addResponse(-1);
                    return;
                }
                int select = inPacket.readInt();
                cm.getNpcScriptInfo().addResponse(select);
                break;
            case AskAvatar:
                if (inPacket.available() >= 4) {
                    inPacket.readShort();
                    byte option = inPacket.readByte();
                    byte submit = inPacket.readByte();
                    if (submit == 1) {
                        cm.getNpcScriptInfo().addResponse(((int) option));
                    } else {
                        cm.getNpcScriptInfo().addResponse(-1);
                    }
                } else {
                    cm.getNpcScriptInfo().addResponse(-1);
                }
                break;
            default:
                cm.dispose();
        }
    }

    public static void handleNpcAnimation(InPacket inPacket, MapleClient c) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.NPC_ANIMATION.getValue());
        int objectID = inPacket.readInt();
        byte oneTimeAction = inPacket.readByte();
        byte chatIdx = inPacket.readByte();
        int duration = inPacket.readInt();
        byte keyPadState = 0;
        MovementInfo movement = null;
        MapleMapObj obj = c.getPlayer().getMap().getObj(objectID);
        if (obj instanceof Npc && ((Npc) obj).isMove()) {
            Npc npc = (Npc) obj;
            if (inPacket.available() > 0) {
                movement = new MovementInfo(npc.getPosition(), npc.getVPosition());
                movement.decode(inPacket);
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
                if (inPacket.available() > 0) {
                    keyPadState = inPacket.readByte();
                }
            }
        }
        c.announce(NpcPacket.npcAnimation(objectID, oneTimeAction, chatIdx, duration, movement, keyPadState));
    }
}

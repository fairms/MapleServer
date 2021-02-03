package im.cave.ms.connection.server.channel;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.field.Foothold;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.MessagePacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.connection.server.Server;
import im.cave.ms.connection.server.channel.handler.NpcHandler;
import im.cave.ms.connection.server.world.World;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.BroadcastMsgType;
import im.cave.ms.enums.Command;
import im.cave.ms.enums.UIType;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.MobData;
import im.cave.ms.provider.data.NpcData;
import im.cave.ms.scripting.quest.QuestScriptManager;
import im.cave.ms.tools.HexTool;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.StringUtil;
import im.cave.ms.tools.Util;

import java.util.List;

import static im.cave.ms.enums.ChatType.Notice;
import static im.cave.ms.enums.InventoryOperationType.REMOVE;
import static im.cave.ms.enums.InventoryOperationType.UPDATE_QUANTITY;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler
 * @date 12/1 13:38
 */
public class CommandExecutor {
    public static void handle(MapleCharacter player, String msg) {
        boolean gm = player.isGm();
        boolean gmCommand = msg.startsWith("!");
        String[] params = msg.substring(1).split(" ");
        int paramsSize = params.length - 1; //参数数量
        Command cmd = Command.getByName(params[0]);
        if (cmd == null || (cmd.getReqGm() != 0 && !gm)) {
            player.chatMessage(Notice, "指令不存在");
            return;
        } else if (cmd.getReqLev() > player.getLevel()) {
            player.chatMessage(Notice, String.format("等级不足,无法使用该命令 要求等级:%d", cmd.getReqLev()));
            return;
        }
        checkParam(cmd, params);
        switch (cmd) {
            case SAVE:
                long start = System.currentTimeMillis();
                DataBaseManager.saveToDB(player);
                long end = System.currentTimeMillis();
                player.dropMessage(String.format("保存成功 耗时:%d", end - start));
                break;
            case EA:
                player.setConversation(false);
                player.announce(UserPacket.enableActions());
                player.announce(MessagePacket.broadcastMsg("[提示] 解卡操作已处理", BroadcastMsgType.NOTICE_WITH_OUT_PREFIX));
                break;
            case SPAWN:
                if (paramsSize < 2) {
                    player.chatMessage(Notice, "请检查参数 !Spawn <id> <quantity> ");
                }
                String id = params[1];
                String quantity = params[2];
                break;
            case WARP:
                if (paramsSize < 1) {
                    player.chatMessage(Notice, "请检查参数 !Spawn <id> <quantity> ");
                }
                String mapId = params[1];
                if (StringUtil.isNumber(mapId)) {
                    player.changeMap(Integer.parseInt(mapId));
                } else {
                    player.chatMessage(Notice, "指令格式错误,请检查");
                }
                break;
            case FAMILIAR:
                player.announce(WorldPacket.openUI(UIType.UI_FAMILIAR));
                break;
            case JOB:
                if (paramsSize < 1) {
                    player.chatMessage(Notice, "请检查参数 !Spawn <id> <quantity> ");
                }
                player.changeJob(Integer.parseInt(params[1]));
                break;
            case DROP:
                if (paramsSize < 1 || !StringUtil.isNumber(params[1])) {
                    player.chatMessage(Notice, "请检查参数 !drop <itemId> [quantity] ");
                    return;
                }
                String itemId = params[1];
                Item item = ItemData.getItemCopy(Integer.parseInt(itemId), false);
                Drop drop = new Drop(item.getItemId(), item);
                MapleMap map = player.getMap();
                Position position = player.getPosition();
                Foothold fh = map.findFootHoldBelow(new Position(position.getX(), position.getY() - GameConstants.DROP_HEIGHT));
                drop.setCanBePickedUpByPet(false);
                map.drop(drop, position, new Position(position.getX(), fh.getYFromX(position.getX())), true);
                break;
            case NPC:
                NpcHandler.talkToNPC(player, 9220050);
                break;
        }
    }

    private static void checkParam(Command cmd, String[] params) {

    }
}
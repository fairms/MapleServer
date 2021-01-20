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
                player.announce(WorldPacket.openUI(UIType.FAMILIAR));
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
        }
    }
}

//
//public class CommandHandler {
//    public static void handle(MapleClient c, String content) {
//        String[] s = content.substring(1).split(" ");
//        switch (s[0]) {
//            case "aa": {
//                MapleCharacter player = c.getPlayer();
//                Equip item = ((Equip) player.getEquipInventory().getItem((short) 1));
//                item.updateToChar(player);
//                break;
//            }
//            case "save":
//                c.getPlayer().saveToDB();
//                c.announce(WorldPacket.chatMessage("保存角色", Notice));
//                break;
//            case "chat":
//                if (s.length < 2) {
//                    return;
//                }
//                int type = Integer.parseInt(s[1]);
//                ChatType chatType = ChatType.getByVal(type);
//                if (chatType == null) {
//                    return;
//                }
//                c.announce(WorldPacket.chatMessage("3:3:3" + chatType, chatType));
//                break;
//            case "mob":
//                if (s.length < 2) {
//                    return;
//                }
//                if (s[1] != null && !s[1].equals("") && Util.isNumber(s[1])) {
//                    Mob mob = MobData.getMob(Integer.parseInt(s[1]));
//                    if (mob == null) {
//                        return;
//                    }
//                    for (int i = 0; i < 1; i++) {
//                        Mob copy = mob.deepCopy();
//                        copy.setHp(mob.getMaxHp());
//                        copy.setMp(mob.getMaxMp());
//                        copy.setPosition(c.getPlayer().getPosition());
//                        c.getPlayer().getMap().spawnObj(copy, c.getPlayer());
//                    }
//                }
//                break;
//            case "npc":
//                if (s.length < 2) {
//                    return;
//                }
//                if (s[1] != null && !s[1].equals("") && Util.isNumber(s[1])) {
//                    Npc npc = NpcData.getNpc(Integer.parseInt(s[1])).deepCopy();
//                    if (npc == null) {
//                        return;
//                    }
//                    npc.setPosition(c.getPlayer().getPosition());
//                    npc.setRx0(npc.getPosition().getX() - 50);
//                    npc.setRx1(npc.getPosition().getX() + 50);
//                    npc.setCy(npc.getPosition().getY());
//                    npc.setFh(c.getPlayer().getFoothold());
//                    c.getPlayer().getMap().spawnObj(npc, c.getPlayer());
//
//                }
//                break;
//            case "ea":
//                c.getPlayer().setConversation(false);
//                QuestScriptManager.getInstance().dispose(c);
//                c.announce(UserPacket.enableActions());
//                c.announce(MessagePacket.broadcastMsg("[提示] 解卡操作已处理", BroadcastMsgType.NOTICE_WITH_OUT_PREFIX));
//                break;
//            case "item":
//                if (s.length < 2) {
//                    return;
//                }
//                int itemId = Integer.parseInt(s[1]);
//                if (ItemConstants.isEquip(itemId)) {
//                    Equip equip = ItemData.getEquipDeepCopyFromID(itemId, false);
//                    if (equip == null) {
//                        return;
//                    }
//                    c.getPlayer().addItemToInv(equip);
//
//                } else {
//                    Item item = ItemData.getItemCopy(itemId, false);
//                    if (item == null) {
//                        return;
//                    }
//                    item.setQuantity(1);
//                    c.getPlayer().addItemToInv(item);
//                }
//                break;
//            case "t":
//                if (s.length < 2) {
//                    return;
//                }
//                int i = content.indexOf(" ");
//                String substring = content.substring(i);
//                byte[] byteArrayFromHexString = HexTool.getByteArrayFromHexString(substring);
//                OutPacket out = new OutPacket();
//                out.write(byteArrayFromHexString);
//                c.announce(out);
//                break;
//            case "debug":
//                MapleCharacter player = c.getPlayer();
//                player.chatMessage(ChatType.Tip, Server.getInstance().onlinePlayer());
//                break;
//            case "maps":
//                int num = 0;
//                List<World> worlds = Server.getInstance().getWorlds();
//                for (World world : worlds) {
//                    for (MapleChannel channel : world.getChannels()) {
//                        for (MapleMap map : channel.getMaps()) {
//                            num += map.getCharacters().size();
//                        }
//                    }
//                }
//                c.getPlayer().chatMessage(ChatType.Tip, "所有地图在线" + num);
//                break;
//            case "warp":
//                if (s.length < 2 || !StringUtil.isNumber(s[1])) {
//                    return;
//                }
//                c.getPlayer().changeMap(Integer.parseInt(s[1]));
//                break;
//            case "job":
//                if (s.length < 2) {
//                    return;
//                }
//                c.getPlayer().changeJob(Integer.parseInt(s[1]));
//                break;
//            case "notice":
//                if (s.length < 2) {
//                    return;
//                }
//                c.getPlayer().dropMessage(s[1]);
//                break;
//
//            case "mobs":
//                c.getPlayer().dropMessage("当前地图OBJ总数:" + c.getPlayer().getMap().getObjs().size());
//                c.getPlayer().dropMessage("可见OBJ数目:" + c.getPlayer().getVisibleMapObjs().size());
//                break;
//            case "reload":
//                c.getEngines().clear();
//                NpcData.refreshShop();
//                break;
//            case "em":
//                c.getAccount().addPoint(100000);
//                c.getMapleChannel().broadcast(WorldPacket.eventMessage("测试测试", 2, 3000));
//                break;
//            case "meso":
//                if (s.length < 2) {
//                    return;
//                }
//                c.getPlayer().addMeso(Long.parseLong(s[1]));
//                break;


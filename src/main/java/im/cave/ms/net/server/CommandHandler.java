package im.cave.ms.net.server;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.obj.Npc;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Item;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.ChatType;
import im.cave.ms.net.packet.ChannelPacket;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.net.server.world.World;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.MobData;
import im.cave.ms.provider.data.NpcData;
import im.cave.ms.scripting.quest.QuestScriptManager;
import im.cave.ms.tools.HexTool;
import im.cave.ms.tools.StringUtil;
import im.cave.ms.tools.Util;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

import java.util.List;

import static im.cave.ms.enums.ChatType.Notice;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.handler
 * @date 12/1 13:38
 */
public class CommandHandler {
    public static void handle(MapleClient c, String content) {
        String[] s = content.substring(1).split(" ");
        switch (s[0]) {
            case "save":
                c.getPlayer().saveToDB();
                c.announce(ChannelPacket.chatMessage("保存角色", Notice));
                break;
            case "chat":
                if (s.length < 2) {
                    return;
                }
                int type = Integer.parseInt(s[1]);
                ChatType chatType = ChatType.getByVal(type);
                if (chatType == null) {
                    return;
                }
                c.announce(ChannelPacket.chatMessage("3:3:3" + chatType, chatType));
                break;
            case "mob":
                if (s.length < 2) {
                    return;
                }
                if (s[1] != null && !s[1].equals("") && Util.isNumber(s[1])) {
                    Mob mob = MobData.getMob(Integer.parseInt(s[1]));
                    if (mob == null) {
                        return;
                    }
                    mob.setHp(mob.getMaxHp());
                    mob.setMp(mob.getMaxMp());
                    mob.setPosition(c.getPlayer().getPosition());
                    for (int i = 0; i < 100; i++) {
                        Mob copy = mob.deepCopy();
                        c.getPlayer().getMap().addObj(copy);
                        c.announce(MaplePacketCreator.spawnMob(copy));
                        c.announce(MaplePacketCreator.mobChangeController(copy));
                    }
                }
                break;
            case "npc":
                if (s.length < 2) {
                    return;
                }
                if (s[1] != null && !s[1].equals("") && Util.isNumber(s[1])) {
                    Npc npc = NpcData.getNpcDataFromWz(Integer.parseInt(s[1]));
                    if (npc == null) {
                        return;
                    }
                    npc.setPosition(c.getPlayer().getPosition());
                    npc.setRx0(npc.getPosition().getX() - 50);
                    npc.setRx1(npc.getPosition().getX() + 50);
                    npc.setCy(npc.getPosition().getY());
                    c.getPlayer().getMap().addObj(npc);
                    npc.setFh(c.getPlayer().getFoothold());
                    c.announce(MaplePacketCreator.spawnNpc(npc));
                    c.announce(MaplePacketCreator.spawnNpcController(npc));
                }
                break;
            case "ea":
                c.getPlayer().setConversation(false);
                QuestScriptManager.getInstance().dispose(c);
                c.announce(MaplePacketCreator.enableActions());
                break;
            case "item":
                if (s.length < 2) {
                    return;
                }
                int itemId = Integer.parseInt(s[1]);
                if (ItemConstants.isEquip(itemId)) {
                    Equip equip = ItemData.getEquipFromWz(itemId);
                    if (equip == null) {
                        return;
                    }
                    c.getPlayer().putItem(equip);

                } else {
                    Item item = ItemData.getItemCopy(itemId, false);
                    if (item == null) {
                        return;
                    }
                    item.setQuantity(100);
                    c.getPlayer().putItem(item);
                }
                break;
            case "t":
                if (s.length < 2) {
                    return;
                }
                int i = content.indexOf(" ");
                String substring = content.substring(i);
                byte[] byteArrayFromHexString = HexTool.getByteArrayFromHexString(substring);
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.write(byteArrayFromHexString);
                c.announce(mplew);
                break;
            case "debug":
                MapleCharacter player = c.getPlayer();
                player.chatMessage(ChatType.Tip, Server.getInstance().onlinePlayer());
                break;
            case "maps":
                int num = 0;
                List<World> worlds = Server.getInstance().getWorlds();
                for (World world : worlds) {
                    for (MapleChannel channel : world.getChannels()) {
                        for (MapleMap map : channel.getMaps()) {
                            num += map.getCharacters().size();
                        }
                    }
                }
                c.getPlayer().chatMessage(ChatType.Tip, "所有地图在线" + num);
                break;
            case "warp":
                if (s.length < 2 || !StringUtil.isNumber(s[1])) {
                    return;
                }
                c.getPlayer().changeMap(Integer.parseInt(s[1]));
                break;
            case "job":
                if (s.length < 2) {
                    return;
                }
                c.getPlayer().changeJob(Integer.parseInt(s[1]));
                break;
            case "notice":
                if (s.length < 2) {
                    return;
                }
                c.getPlayer().dropMessage(s[1]);
                break;
        }
    }
}

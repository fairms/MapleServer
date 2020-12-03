package im.cave.ms.net.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.field.obj.mob.Mob;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Item;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.enums.ChatType;
import im.cave.ms.net.packet.ChannelPacket;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.packet.MobPacket;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.MobData;
import im.cave.ms.tools.HexTool;
import im.cave.ms.tools.Util;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

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
                    Mob mob = MobData.getMobFromWz(Integer.parseInt(s[1]));
                    if (mob == null) {
                        return;
                    }
                    c.getPlayer().getMap().addLife(mob);
                    c.announce(MaplePacketCreator.spawnMob(mob));
                    c.announce(MaplePacketCreator.mobChangeController(mob));
                }
                break;
            case "ea":
                c.getPlayer().setConversation(false);
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
                    Item item = ItemData.getItemCopy(itemId);
                    if (item == null) {
                        return;
                    }
                    item.setQuantity(1);
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

        }
    }
}

package im.cave.ms.net.server.login.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.net.netty.InPacket;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.provider.data.ItemData;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:46
 */
public class CreateCharHandler {
    public static void afterCreate(InPacket inPacket, MapleClient c) {

    }

    public static void checkName(InPacket inPacket, MapleClient c) {
        String name = inPacket.readMapleAsciiString();
        int state = MapleCharacter.nameCheck(name);
        c.announce(LoginPacket.checkNameResponse(name, (byte) state));
    }

    public static void createChar(InPacket inPacket, MapleClient c) {
        String name;
        byte gender, skin;
        short subcategory;
        name = inPacket.readMapleAsciiString();
        int keyMode = inPacket.readInt(); //默认键盘布局
        if (keyMode != 0 && keyMode != 1) {
            c.announce(LoginPacket.checkNameResponse(name, (byte) 3));
            return;
        }
        inPacket.skip(4);
        int curSelectedRace = inPacket.readInt();
        JobConstants.JobEnum job = JobConstants.LoginJob.getLoginJobById(curSelectedRace).getBeginJob();
        subcategory = inPacket.readShort();
        gender = inPacket.readByte();
        skin = inPacket.readByte();
        byte itemLength = inPacket.readByte();
        int[] items = new int[itemLength];
        for (byte i = 0; i < itemLength; i++) {
            items[i] = inPacket.readInt();
        }
        if (job == null) {
            return;
        }
        for (int item : items) {
            if (!ItemData.startItems.contains(item)) {
                c.close();
            }
        }
        MapleCharacter chr = MapleCharacter.getDefault(job);
        chr.setAccId(c.getAccount().getId());
        chr.setWorld(c.getWorld());
        chr.setGender(gender);
        chr.setSkin(skin);
        chr.setName(name);
        chr.setGm(c.getAccount().getGm());
        chr.setChannel(c.getChannel());
        chr.getKeyMap().setDefault(keyMode == 0);
        chr.setFace(items[0]);
        chr.setHair(items[1]);
        chr.setClient(c);
        chr.setAccount(c.getAccount());
        Inventory equippedIv = chr.getInventory(InventoryType.EQUIPPED);
        chr.getAccount().addChar(chr);
        chr.saveToDB();  //先保存角色
        for (int id : items) {
            Equip equip = ItemData.getEquipById(id);
            if (equip != null && ItemConstants.isEquip(id)) {
                equip.setPos(ItemConstants.getBodyPartFromItem(id, gender));
                equip.setQuantity(1);
                equippedIv.addItem(equip);
            }
        }
        chr.changeChannel(c.getChannel());
    }
}

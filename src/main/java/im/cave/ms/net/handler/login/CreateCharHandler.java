package im.cave.ms.net.handler.login;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.net.server.Server;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.net.server.world.World;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:46
 */
public class CreateCharHandler {
    public static void afterCreate(SeekableLittleEndianAccessor slea, MapleClient c) {

    }

    public static void checkName(SeekableLittleEndianAccessor slea, MapleClient c) {
        String name = slea.readMapleAsciiString();
        int state = MapleCharacter.nameCheck(name);
        c.announce(LoginPacket.checkNameResponse(name, (byte) state));
    }

    public static void createChar(SeekableLittleEndianAccessor slea, MapleClient c) {

        String name;
        byte gender, skin;
        short subcategory;

        name = slea.readMapleAsciiString();
        int keyMode = slea.readInt(); //默认键盘布局
        if (keyMode != 0 && keyMode != 1) {
            c.announce(LoginPacket.checkNameResponse(name, (byte) 3));
            return;
        }
        slea.skip(4);
        int curSelectedRace = slea.readInt();
        JobConstants.JobEnum job = JobConstants.LoginJob.getLoginJobById(curSelectedRace).getBeginJob();
        subcategory = slea.readShort();
        gender = slea.readByte();
        skin = slea.readByte();
        byte itemLength = slea.readByte();
        int[] items = new int[itemLength];
        for (byte i = 0; i < itemLength; i++) {
            items[i] = slea.readInt();
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
        chr.getKeyBinding().setDefault(keyMode == 0);
        chr.setFace(items[0]);
        chr.setHair(items[1]);
        chr.setClient(c);
        chr.setAccount(c.getAccount());
        Inventory equippedIv = chr.getInventory(InventoryType.EQUIPPED);

        for (int id : items) {
            Equip equip = ItemData.getEquipById(id);
            if (equip != null && ItemConstants.isEquip(id)) {
                equip.setPos(ItemConstants.getBodyPartFromItem(id, gender));
                equip.setQuantity(1);
                equippedIv.addItem(equip);
            }
        }
        chr.saveToDB();
        chr.getAccount().addChar(chr);
        World world = Server.getInstance().getWorldById(c.getWorld());
        MapleChannel channel = world.getChannel(c.getChannel());
        channel.addPlayer(chr);
        c.setLoginStatus(LoginStatus.SERVER_TRANSITION);
        chr.setChangingChannel(true);

    }
}

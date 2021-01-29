package im.cave.ms.connection.server.login.handler;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.Inventory;
import im.cave.ms.configs.Config;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.LoginPacket;
import im.cave.ms.connection.packet.MessagePacket;
import im.cave.ms.connection.server.Server;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.BroadcastMsgType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.JobEnum;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.LoginType;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.DateUtil;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 21:46
 */
public class CharOperationHandler {
    public static void handleSelectChar(InPacket in, MapleClient c) {
        int charId = in.readInt();
        byte invisible = in.readByte();
        if (c.getLoginStatus().equals(LoginStatus.LOGGEDIN) && c.getAccount().getCharacter(charId) != null) {
            MapleCharacter player = c.getAccount().getCharacter(charId);
//            c.setPlayer(player);
            c.setLoginStatus(LoginStatus.SERVER_TRANSITION);
            Server.getInstance().addClientInTransfer(c.getChannelId(), charId, c);
            int port = Server.getInstance().getChannel(c.getWorldId(), c.getChannelId()).getPort();
            c.announce(LoginPacket.selectCharacterResult(LoginType.Success, (byte) 0, port, charId));
        } else {
            c.announce(LoginPacket.selectCharacterResult(LoginType.UnauthorizedUser, (byte) 0, 0, 0));
        }
    }

    public static void handleUserDeleteChar(InPacket in, MapleClient c) {
        in.readByte();
        in.readByte();
        int charId = in.readInt();
        MapleCharacter character = c.getAccount().getCharacter(charId);
        long deleteTime = LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        character.setDeleteTime(DateUtil.getFileTime(deleteTime));
        c.announce(LoginPacket.deleteTime(charId));
    }

    public static void handleUserConfirmDeleteChar(InPacket in, MapleClient c) {
        in.readByte();
        in.readByte();
        int charId = in.readInt();
        MapleCharacter character = c.getAccount().getCharacter(charId);
        character.setDeleted(true);
        character.saveToDB();
        c.getAccount().removeChar(charId);
        c.announce(LoginPacket.deleteTime(charId));
        c.announce(MessagePacket.broadcastMsg("删除角色成功，请回到首页重新进入。", BroadcastMsgType.ALERT));
    }

    public static void handleCancelDelete(InPacket in, MapleClient c) {
        int charId = in.readInt();
        MapleCharacter character = c.getAccount().getCharacter(charId);
        character.setDeleteTime(0L);
        c.announce(LoginPacket.cancelDeleteChar(charId));
    }

    public static void handleAccountCharSlotsExpand(InPacket in, MapleClient c) {
        int accId = in.readInt();
        int i = in.readInt(); //3
        int i1 = in.readInt(); //
        int sn = in.readInt();
        boolean cash = in.readByte() == 0;
        Account account = c.getAccount();
        if (accId != account.getId() || account.getMaplePoint() < 3000) {
            return;
        }
        account.addMaplePoint(-3000);
        account.addSlot(1);
        c.announce(LoginPacket.characterSlotsExpandResult(i1, account.getMaplePoint(), cash));
    }

    public static void handleCheckDuplicatedId(InPacket in, MapleClient c) {
        String name = in.readMapleAsciiString();
        int state = MapleCharacter.nameValidate(name);
        c.announce(LoginPacket.checkDuplicatedIDResult(name, (byte) state));
    }

    public static void handleCreateCharRequest(InPacket in, MapleClient c) {
        String name;
        byte gender, skin;
        short subJob;
        name = in.readMapleAsciiString();
        int keyMode = in.readInt();
        if (keyMode != 0 && keyMode != 1) {
            c.announce(LoginPacket.checkDuplicatedIDResult(name, (byte) 3));
            return;
        }
        in.skip(4);
        int curSelectedRace = in.readInt();
        JobEnum job = JobConstants.LoginJob.getLoginJobById(curSelectedRace).getBeginJob();
        int mapId = JobConstants.LoginJob.getLoginJobById(curSelectedRace).getBeginMap();
        if (job == null) {
            c.announce(MessagePacket.broadcastMsg("未开放创建的职业", BroadcastMsgType.ALERT));
            return;
        }
        subJob = in.readShort();
        gender = in.readByte();
        skin = in.readByte();
        byte itemLength = in.readByte();
        int[] items = new int[itemLength];
        for (byte i = 0; i < itemLength; i++) {
            items[i] = in.readInt();
        }
        for (int item : items) {
            if (!ItemData.getStartItems().contains(item)) {
                c.announce(MessagePacket.broadcastMsg("检测到非法初始道具!", BroadcastMsgType.ALERT));
                c.close();
            }
        }
        MapleCharacter chr = MapleCharacter.getDefault(job.getJob());
        if (chr == null) {
            c.announce(MessagePacket.broadcastMsg("角色创建异常,请联系管理员", BroadcastMsgType.ALERT));
            return;
        }
        {
            chr.setAccId(c.getAccount().getId());
            chr.setWorld(c.getWorldId());
            chr.setSubJob(subJob);
            chr.setGender(gender);
            chr.setSkin(skin);
            chr.setName(name);
            chr.setGm(c.getAccount().isGm());
            chr.setChannel(c.getChannelId());
            chr.getKeyMap().setDefault(keyMode != 0);
            chr.setFace(items[0]);
            chr.setHair(items[1]);
            chr.setCreatedTime(DateUtil.getFileTime(System.currentTimeMillis()));
            chr.setMapId(mapId);
        }
        chr.setClient(c);
        chr.setAccount(c.getAccount());
        Inventory equippedIv = chr.getInventory(InventoryType.EQUIPPED);
        chr.getAccount().addChar(chr);
        chr.saveToDB(); //先保存角色
        for (int id : items) {
            Equip equip = ItemData.getEquipDeepCopyFromID(id, false);
            if (equip != null && ItemConstants.isEquip(id)) {
                equip.setPos(ItemConstants.getBodyPartFromItem(id, gender));
                equip.setQuantity(1);
                equippedIv.addItem(equip);
            }
        }
        c.announce(LoginPacket.createCharacterResult(LoginType.Success, chr));
    }


    public static void handleAfterCreateChar(InPacket in, MapleClient c) {
        int charId = in.readInt();
        if (Config.serverConfig.MIGRATE_IN_AFTER_CREATE_CHAR) {
            Account account = c.getAccount();
            MapleCharacter chr = account.getCharacter(charId);
            account.setOnlineChar(chr);
            c.setLoginStatus(LoginStatus.SERVER_TRANSITION);
//            c.setPlayer(chr);
            Server.getInstance().addClientInTransfer(c.getChannelId(), charId, c);
            c.announce(LoginPacket.selectCharacterResult(LoginType.Success, (byte) 0, c.getMapleChannel().getPort(), chr.getId()));
        }
    }
}

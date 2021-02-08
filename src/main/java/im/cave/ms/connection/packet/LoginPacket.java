package im.cave.ms.connection.packet;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.configs.Config;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.connection.server.Server;
import im.cave.ms.connection.server.channel.MapleChannel;
import im.cave.ms.connection.server.login.LoginServer;
import im.cave.ms.connection.server.world.World;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.LoginType;
import im.cave.ms.enums.PrivateStatusIDFlag;
import im.cave.ms.enums.ServerType;
import im.cave.ms.tools.DateUtil;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static im.cave.ms.constants.ServerConstants.PATH;
import static im.cave.ms.constants.ServerConstants.VERSION;
import static im.cave.ms.enums.ServerType.LOGIN;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.handler.login
 * @date 11/20 17:07
 */
public class LoginPacket {

    public static OutPacket sendStart() {
        OutPacket out = new OutPacket(SendOpcode.CLIENT_START);
        out.write(0);
        return out;
    }

    public static OutPacket getHello(MapleClient c, ServerType type) {
        OutPacket out = new OutPacket();
        if (type == LOGIN) {
            out.writeShort(0x1E);
            for (int i = 0; i < 2; i++) {
                out.writeShort(VERSION);
                out.writeMapleAsciiString(PATH);
                out.writeInt(c.getRecvIv());
                out.writeInt(c.getSendIv());
                out.writeShort(4);
            }
        } else {
            out.writeShort(0x0E);
            out.writeShort(VERSION);
            out.writeMapleAsciiString(PATH);
            out.writeInt(c.getRecvIv());
            out.writeInt(c.getSendIv());
            out.write(4);
        }
        return out;
    }

    public static OutPacket getOpenCreateChar() {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.OPEN_CREATE_CHAR_LAYOUT_RESULT.getValue());
        out.write(4); // 09 do nothing / 07 validate code
        return out;
    }

    public static OutPacket checkDuplicatedIDResult(String name, byte state) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CHECK_DUPLICATED_ID_RESULT.getValue());
        out.writeMapleAsciiString(name);
        out.write(state);
        return out;
    }

    public static OutPacket loginResult(MapleClient c, LoginType result) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.LOGIN_STATUS.getValue());
        if (result == LoginType.Success) {
            out.write(0);
            out.writeMapleAsciiString(c.getAccount().getAccount());
            out.writeLong(0);
            out.writeInt(c.getAccount().getId());
            out.writeBool(false);
            out.writeLong(128);
            out.writeShort(3);
            out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
            out.writeZeroBytes(11);
            out.writeShort(8449);
            for (JobConstants.LoginJob job : JobConstants.LoginJob.values()) {
                out.write(Config.serverConfig.CLOSED_JOBS.contains(job.getBeginJob().getJob()) ? 0 : job.getFlag());
                out.writeShort(1);
            }
            out.write(0);
            out.writeInt(-1);
            out.writeBool(true);
            out.writeShort(0);
            out.writeMapleAsciiString(c.getAccount().getAccount());
            out.write(1);
            out.write(1);
            out.write(1);
            out.write(0);
        } else {
            out.write(result.getValue());
        }
        return out;
    }

    public static OutPacket serverListBg() {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SERVER_LIST_BG.getValue());
        out.writeBool(true); //unk
        out.writeMapleAsciiString("default");
        out.writeBool(true); //unk
        out.writeLong(0);
        return out;
    }

    public static List<OutPacket> serverList() {
        Server server = Server.getInstance();
        List<World> worlds = server.getWorlds();
        List<OutPacket> serverList = new ArrayList<>();
        for (World world : worlds) {
            OutPacket out = new OutPacket();
            out.writeShort(SendOpcode.SERVERLIST.getValue());
            out.writeShort(world.getId());
            out.writeMapleAsciiString("World-" + world.getId());
            out.writeZeroBytes(8);
            out.writeShort(512);
            out.writeMapleAsciiString(world.getEventMessage());
            out.write(world.getChannelsSize());
            out.writeInt(500);
            List<MapleChannel> worldChannels = world.getChannels();
            for (MapleChannel ch : worldChannels) {
                out.writeMapleAsciiString("World-" + world.getId() + "-" + ch.getChannelId());
                out.writeInt(ch.getChannelCapacity());
                out.write(world.getId());
                out.writeShort(ch.getChannelId());
            }
            out.writeZeroBytes(12);
            serverList.add(out);
        }
        return serverList;

    }

    public static OutPacket serverListEnd() {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SERVERLIST.getValue());
        out.writeShort(-1);
        out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        out.writeLong(0);
        out.writeShort(0);
        return out;
    }

    public static OutPacket serverStatus(int worldId) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SERVERSTATUS.getValue());
        out.write(0);
        out.writeInt(worldId);
        out.writeInt(worldId);
        return out;
    }

    public static OutPacket account(Account account) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SET_ACCOUNT_INFO.getValue());
        out.writeLong(0);
        out.writeMapleAsciiString(account.getAccount());
        return out;
    }

    public static OutPacket charactersList(MapleClient c, List<MapleCharacter> characters, int status) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CHARLIST.getValue());
        out.write(status);
        out.writeMapleAsciiString("normal");
        out.writeMapleAsciiString("normal");
        out.writeZeroBytes(6);
        List<MapleCharacter> deletedChars = characters.stream().filter(chr -> chr.getDeleteTime() != 0).collect(Collectors.toList());
        out.writeInt(deletedChars.size());
        out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        for (MapleCharacter deletedChar : deletedChars) {
            out.writeInt(deletedChar.getId());
            out.writeLong(deletedChar.getDeleteTime());
        }
        out.write(0);
        out.writeInt(characters.size());
        for (MapleCharacter character : characters) {
            out.writeInt(character.getId());
        }
        out.writeZeroBytes(9);
        out.write(characters.size());
        characters.forEach(chr -> PacketHelper.addCharEntry(out, chr));
        out.write(0); // bLoginOpt
        out.write(0); // bQuerySSNOnCreateNewCharacter
        out.writeInt(c.getAccount().getCharacterSlots());
        out.writeInt(0); // buying char slots
        out.writeInt(-1); // nEventNewCharJob
        out.writeReversedLong(DateUtil.getFileTime(System.currentTimeMillis()));
        out.write(0); //
        out.write(1);
        out.write(0);
        out.writeZeroBytes(8);
        out.writeInt(327680);
        out.writeInt(553713664); // 00 00 01 21
        for (JobConstants.LoginJob job : JobConstants.LoginJob.values()) {
            out.write(Config.serverConfig.CLOSED_JOBS.contains(job.getBeginJob().getJob()) ? 0 : job.getFlag());
            out.writeShort(1);
        }
        return out;
    }

    public static OutPacket selectCharacterResult(LoginType loginType, byte errorCode, int port, int charId) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SELECT_CHARACTER_RESULT.getValue());
        out.write(loginType.getValue());
        out.write(errorCode);
        if (loginType.equals(LoginType.Success)) {
            out.write(ServerConstants.NEXON_IP);
            out.writeShort(port);
            out.writeInt(0);
            out.writeInt(charId);
            out.writeMapleAsciiString("normal");
            out.writeMapleAsciiString("normal");
            out.writeInt(0);
            out.writeShort(5120);
            out.writeLong(1000);
        }
        return out;
    }

    public static OutPacket ping(ServerType type) {
        OutPacket out = new OutPacket();
        if (type == LOGIN) {
            out.writeShort(SendOpcode.PING.getValue());
        } else {
            out.writeShort(SendOpcode.CPING.getValue());
        }
        return out;
    }

    public static OutPacket changePlayer(MapleClient c) {
        OutPacket out = new OutPacket();
        char[] ss = new char[256];
        int i = 0;
        while (i < ss.length) {
            int f = (int) (Math.random() * 3);
            if (f == 0) {
                ss[i] = (char) ('A' + Math.random() * 26);
            } else if (f == 1) {
                ss[i] = (char) ('a' + Math.random() * 26);
            } else {
                ss[i] = (char) ('0' + Math.random() * 10);
            }
            i++;
        }
        String key = new String(ss);
        LoginServer.getInstance().putLoginAuthKey(key, c.getAccount().getAccount(), c.getChannelId());
        out.writeShort(SendOpcode.CHANGE_CHAR_KEY.getValue());
        out.writeMapleAsciiString(key);
        return out;
    }

    public static OutPacket authSuccess(MapleClient c) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.AUTH_SUCCESS.getValue());
        out.write(0);//succeed
        out.writeInt(c.getAccount().getId());
        out.write(0);
        out.writeInt(PrivateStatusIDFlag.NONE.getFlag()); //ACCOUNT TYPE
        out.writeInt(0);
        out.write(3);
        out.write(0);
        out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        out.writeLong(0);
        out.write(0);
        out.writeMapleAsciiString(c.getAccount().getAccount());
        out.writeShort(0);
        out.write(1);
        out.write(33);
        for (JobConstants.LoginJob job : JobConstants.LoginJob.values()) {
            out.write(Config.serverConfig.CLOSED_JOBS.contains(job.getBeginJob().getJob()) ? 0 : job.getFlag());
            out.writeShort(1);
        }
        out.write(0);
        out.writeInt(-1);
        out.write(1);
        return out;
    }

    public static OutPacket deleteTime(int charId) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.DELETE_CHAR_TIME.getValue());
        out.writeInt(charId);
        out.write(0);
        long deleteTime = LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        out.writeLong(DateUtil.getFileTime(deleteTime));
        return out;
    }

    public static OutPacket cancelDeleteChar(int charId) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CANCEL_DELETE_CHAR.getValue());
        out.writeInt(charId);
        out.write(0);
        return out;
    }

    public static OutPacket characterSlotsExpandResult(int i1, int point, boolean cash) {
        OutPacket out = new OutPacket();
        out.writeInt(SendOpcode.CHAR_SLOTS_EXPAND_RESULT.getValue());
        out.writeInt(i1);
        out.writeInt(60);
        out.writeInt(point);
        out.write(1);
        out.writeShort(cash ? 0 : 1);
        return out;
    }

    public static OutPacket createCharacterResult(LoginType type, MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.CREATE_NEW_CHARACTER_RESULT);
        out.write(type.getValue());
        if (type == LoginType.Success) {
            PacketHelper.addCharEntry(out, chr);
        }
        return out;
    }
}

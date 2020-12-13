package im.cave.ms.net.packet;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.config.ServerConfig;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.LoginType;
import im.cave.ms.enums.ServerType;
import im.cave.ms.net.netty.OutPacket;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.net.server.Server;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.net.server.login.LoginServer;
import im.cave.ms.net.server.world.World;
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


    public static OutPacket clientAuth() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(0x2C);
        outPacket.write(0);
        return outPacket;
    }


    public static OutPacket getHello(int sendIv, int recvIv, ServerType type) {
        OutPacket outPacket = new OutPacket();
        if (type == LOGIN) {
            outPacket.writeShort(0x1E);
            for (int i = 0; i < 2; i++) {
                outPacket.writeShort(VERSION);
                outPacket.writeMapleAsciiString(PATH);
                outPacket.writeInt(recvIv);
                outPacket.writeInt(sendIv);
                outPacket.writeShort(4);
            }
        } else if (type == ServerType.CHANNEL) {
            outPacket.writeShort(0x0E);
            outPacket.writeShort(VERSION);
            outPacket.writeMapleAsciiString(PATH);
            outPacket.writeInt(recvIv);
            outPacket.writeInt(sendIv);
            outPacket.write(4);
        }
        return outPacket;
    }


    public static OutPacket getOpenCreateChar() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.OPEN_CREATE_CHAR.getValue());
        outPacket.write(4);
        return outPacket;
    }

    public static OutPacket checkNameResponse(String name, byte state) {
        final OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHAR_NAME_RESPONSE.getValue());
        outPacket.writeMapleAsciiString(name);
        outPacket.write(state);
        return outPacket;
    }

    private static int num = 1;

    public static OutPacket loginResult(MapleClient c, LoginType result) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.LOGIN_STATUS.getValue());
        if (result == LoginType.Success) {
            outPacket.write(0);
            outPacket.writeMapleAsciiString(c.getAccount().getAccount());
            outPacket.writeLong(0);
            outPacket.writeInt(c.getAccount().getId());
            outPacket.writeBool(false);
            outPacket.writeLong(128);
            outPacket.writeShort(3);
            outPacket.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
            outPacket.writeZeroBytes(11);
            outPacket.writeShort(8449);
            for (JobConstants.LoginJob job : JobConstants.LoginJob.values()) {
                outPacket.write(ServerConfig.config.CLOSED_JOBS.contains(job.getBeginJob().getJobId()) ? 0 : job.getFlag());
                outPacket.writeShort(1);
            }
            outPacket.write(0);
            outPacket.writeInt(-1);
            outPacket.writeBool(true);
            outPacket.writeShort(0);
            outPacket.writeMapleAsciiString(c.getAccount().getAccount());
            outPacket.write(1);
            outPacket.write(1);
            outPacket.write(1);
            outPacket.write(0);
        } else {
            outPacket.write(result.getValue());
        }
        return outPacket;
    }

    public static OutPacket serverListBg() {
        final OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SERVER_LIST_BG.getValue());
        outPacket.write(0);
        outPacket.writeMapleAsciiString("default");
        outPacket.write(1);
        outPacket.writeLong(0);
        return outPacket;
    }

    public static List<OutPacket> serverList() {
        Server server = Server.getInstance();
        List<World> worlds = server.getWorlds();
        List<OutPacket> serverList = new ArrayList<>();
        for (World world : worlds) {
            OutPacket outPacket = new OutPacket();
            outPacket.writeShort(SendOpcode.SERVERLIST.getValue());
            outPacket.writeShort(world.getId());
            outPacket.writeMapleAsciiString("World-" + world.getId());
            outPacket.writeZeroBytes(8);
            outPacket.writeShort(512);
            outPacket.writeMapleAsciiString(world.getEventMessage());
            outPacket.write(world.getChannelsSize());
            outPacket.writeInt(500);
            List<MapleChannel> worldChannels = world.getChannels();
            for (MapleChannel ch : worldChannels) {
                outPacket.writeMapleAsciiString("World-" + world.getId() + "-" + ch.getChannelId());
                outPacket.writeInt(ch.getChannelCapacity());
                outPacket.write(world.getId());
                outPacket.writeShort(ch.getChannelId());
            }
            outPacket.writeZeroBytes(12);
            serverList.add(outPacket);
        }
        return serverList;

    }

    public static OutPacket serverListEnd() {
        final OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SERVERLIST.getValue());
        outPacket.writeShort(-1);
        outPacket.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        outPacket.writeLong(0);
        outPacket.writeShort(0);
        return outPacket;
    }

    public static OutPacket serverStatus(int worldId) {
        final OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SERVERSTATUS.getValue());
        outPacket.write(0);
        outPacket.writeInt(worldId);
        outPacket.writeInt(2);
        return outPacket;
    }

    public static OutPacket account(Account account) {
        final OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.ACCOUNT.getValue());
        outPacket.writeLong(0);
        outPacket.writeMapleAsciiString(account.getAccount());
        return outPacket;
    }

    public static OutPacket charList(MapleClient c, List<MapleCharacter> characters, int status) {
        final OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHARLIST.getValue());
        outPacket.write(status);
        outPacket.writeMapleAsciiString("Normal");
        outPacket.writeMapleAsciiString("Normal");
        outPacket.writeZeroBytes(6);
        List<MapleCharacter> deletedChars = characters.stream().filter(MapleCharacter::isDeleted).collect(Collectors.toList());
        outPacket.writeInt(deletedChars.size());
        outPacket.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        for (MapleCharacter deletedChar : deletedChars) {
            outPacket.writeInt(deletedChar.getId());
            outPacket.writeLong(DateUtil.getFileTime(deletedChar.getDeleteTime()));
        }
        outPacket.write(0);
        outPacket.writeInt(characters.size());
        for (MapleCharacter character : characters) {
            outPacket.writeInt(character.getId());
        }
        outPacket.writeZeroBytes(9);
        outPacket.write(characters.size());
        characters.forEach(chr -> PacketHelper.addCharEntry(outPacket, chr));
        outPacket.writeShort(0);
        outPacket.writeLong(c.getAccount().getCharacterSlots());
        outPacket.writeInt(-1);
        outPacket.writeReversedLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        outPacket.write(0);
        outPacket.write(1);
        outPacket.write(0);
        outPacket.writeZeroBytes(8);
        outPacket.writeInt(327680);
        outPacket.writeInt(553713664);
        for (JobConstants.LoginJob job : JobConstants.LoginJob.values()) {
            outPacket.write(ServerConfig.config.CLOSED_JOBS.contains(job.getBeginJob().getJobId()) ? 0 : job.getFlag());
            outPacket.writeShort(1);
        }
        return outPacket;
    }

    public static OutPacket selectCharacterResult(LoginType loginType, byte errorCode, int port, int charId) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SERVER_IP.getValue());
        outPacket.write(loginType.getValue());
        outPacket.write(errorCode);
        if (loginType.equals(LoginType.Success)) {
            outPacket.write(ServerConstants.NEXON_IP);
            outPacket.writeShort(port);
            outPacket.writeInt(0);
            outPacket.writeInt(charId);
            outPacket.writeInt(0);
            outPacket.writeShort(5120);
            outPacket.writeShort(1000);
            outPacket.writeZeroBytes(6);
        }
        return outPacket;
    }

    public static OutPacket ping(ServerType type) {
        OutPacket outPacket = new OutPacket();
        if (type == LOGIN) {
            outPacket.writeShort(SendOpcode.PING.getValue());
        } else {
            outPacket.writeShort(SendOpcode.CPING.getValue());
        }
        return outPacket;
    }

    public static OutPacket changePlayer(MapleClient c) {
        OutPacket outPacket = new OutPacket();
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
        LoginServer.getInstance().putLoginAuthKey(key, c.getAccount().getAccount(), c.getChannel());
        outPacket.writeShort(SendOpcode.CHANGE_CHAR_KEY.getValue());
        outPacket.writeMapleAsciiString(key);
        return outPacket;
    }

    public static OutPacket authSuccess(MapleClient c) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.AUTH_SUCCESS.getValue());
        outPacket.write(0);
        outPacket.writeInt(c.getAccount().getId());
        outPacket.write(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.write(3);
        outPacket.write(0);
        outPacket.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        outPacket.writeLong(0);
        outPacket.write(0);
        outPacket.writeMapleAsciiString(c.getAccount().getAccount());
        outPacket.writeShort(0);
        outPacket.write(1);
        outPacket.write(33);
        for (JobConstants.LoginJob job : JobConstants.LoginJob.values()) {
            outPacket.write(ServerConfig.config.CLOSED_JOBS.contains(job.getBeginJob().getJobId()) ? 0 : job.getFlag());
            outPacket.writeShort(1);
        }
        outPacket.write(0);
        outPacket.writeInt(-1);
        outPacket.write(1);
        return outPacket;
    }

    public static OutPacket deleteTime(int charId) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.DELETE_CHAR_TIME.getValue());
        outPacket.writeInt(charId);
        outPacket.write(0);
        long deleteTime = LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        outPacket.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        outPacket.writeLong(DateUtil.getFileTime(deleteTime));
        return outPacket;
    }

    public static OutPacket cancelDeleteChar(int charId) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CANCEL_DELETE_CHAR.getValue());
        outPacket.writeInt(charId);
        outPacket.write(0);
        return outPacket;
    }
}

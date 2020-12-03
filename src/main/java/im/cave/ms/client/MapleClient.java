package im.cave.ms.client;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.LoginStatus;
import im.cave.ms.enums.LoginType;
import im.cave.ms.enums.ServerType;
import im.cave.ms.net.packet.LoginPacket;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.server.Server;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.net.server.world.World;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client
 * @date 11/19 17:16
 */
public class MapleClient {

    public static final AttributeKey<MapleClient> CLIENT_KEY = AttributeKey.newInstance("Client");
    public Map<Integer, Integer> mEncryptedOpcode = new LinkedHashMap<>();
    private int sendIv;
    private int recvIv;
    private final Channel ch;
    private final ReentrantLock lock = new ReentrantLock(true);
    private Account account;
    private byte world;
    private byte channel = -1;
    private byte[] machineID;
    private MapleCharacter player;
    private int storeLength = -1;
    private LoginStatus loginStatus = LoginStatus.NOTLOGGEDIN;
    private long lastPong;
    private final Map<String, NashornScriptEngine> engines = new HashMap<>();

    public MapleClient(Channel ch, int sendIv, int recvIv) {
        this.ch = ch;
        this.sendIv = sendIv;
        this.recvIv = recvIv;
    }


    public ReentrantLock getLock() {
        return lock;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public byte getChannel() {
        return channel;
    }

    public void setChannel(byte channel) {
        this.channel = channel;
    }

    public void setChannel(int channel) {
        this.channel = (byte) channel;
    }

    public byte[] getMachineID() {
        return machineID;
    }

    public void setMachineID(byte[] machineID) {
        this.machineID = machineID;
    }

    public MapleCharacter getPlayer() {
        return player;
    }

    public void setPlayer(MapleCharacter player) {
        this.player = player;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

    public byte getWorld() {
        return world;
    }

    public void setWorld(byte world) {
        this.world = world;
    }

    public void setWorld(int world) {
        this.world = (byte) world;
    }

    public int getRecvIv() {
        return recvIv;
    }

    public void setRecvIv(int recvIv) {
        this.recvIv = recvIv;
    }

    public int getSendIv() {
        return sendIv;
    }

    public void setSendIv(int sendIv) {
        this.sendIv = sendIv;
    }

    public int getStoreLength() {
        return storeLength;
    }

    public void setStoreLength(int storeLength) {
        this.storeLength = storeLength;
    }

    public void close() {
        ch.close();
    }

    public void acquireEncoderState() {
        lock.lock();
    }

    public void releaseEncoderState() {
        lock.unlock();
    }

    public int getPort() {
        return Integer.parseInt(ch.localAddress().toString().split(":")[1]);
    }

    public void announce(MaplePacketLittleEndianWriter mplew) {
        ch.writeAndFlush(mplew);
    }

    public void pongReceived() {
        lastPong = System.currentTimeMillis();
    }

    public Channel getCh() {
        return ch;
    }

    public LoginType login(String username, String password) {
        Account account = Account.getFromDB(username);
        if (account == null) {
            return LoginType.NotRegistered;
        }
        String hashPwd = account.getPassword();
        if (BCrypt.checkpw(password, hashPwd)) {
            if (Server.getInstance().isAccountLoggedIn(account)) {
                return LoginType.AlreadyConnected;
            }
            setAccount(account);
            setLoginStatus(LoginStatus.LOGGEDIN);
            return LoginType.Success;
        }
        return LoginType.IncorrectPassword;
    }

    public List<MapleCharacter> loadCharacters(int worldId, boolean channelServer) {
        return new ArrayList<>(this.getAccount().getCharacters());
    }

    public NashornScriptEngine getScriptEngine(String name) {
        return engines.getOrDefault(name, null);
    }

    public void setScriptEngine(String path, NashornScriptEngine engine) {
        engines.put(path, engine);
    }

    public void removeScriptEngine(String name) {
        engines.remove(name);
    }

    public long getLastPong() {
        return lastPong;
    }

    public void setLastPong(long lastPong) {
        this.lastPong = lastPong;
    }

    public void changeChannel(int channel) {
        Server server = Server.getInstance();
        World world = server.getWorldById(this.world);
        MapleChannel tChannel = world.getChannel(channel);
        player.getMap().removePlayer(player);
        player.setChangingChannel(true);
        tChannel.addPlayer(player);
        announce(MaplePacketCreator.getChannelChange(tChannel.getPort()));
    }


    public void sendPing() {
        announce(LoginPacket.ping(channel == -1 ? ServerType.LOGIN : ServerType.CHANNEL));
    }


    public MapleChannel getMapleChannel() {
        Server server = Server.getInstance();
        World wd = server.getWorldById(world);
        return wd.getChannel(this.channel);
    }
}

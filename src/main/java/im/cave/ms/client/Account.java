package im.cave.ms.client;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.multiplayer.friend.Friend;
import im.cave.ms.client.storage.Locker;
import im.cave.ms.client.storage.Storage;
import im.cave.ms.client.storage.Trunk;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.server.Server;
import im.cave.ms.enums.MessageType;
import im.cave.ms.enums.PrivateStatusIDFlag;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static im.cave.ms.constants.GameConstants.DEFAULT_CHARACTER_SLOTS;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client
 * @date 11/19 17:28
 */
@Getter
@Setter
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String account, password;
    private int characterSlots;
    private int cash, maplePoint, point;
    private boolean gm;
    private boolean isBanned;
    private String banReason;
    private long lastLogin;
    @Column(name = "flag")
    private PrivateStatusIDFlag accountFlag;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "accId")
    private Set<MapleCharacter> characters;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "accId")
    private Set<Storage> storages;
    @ElementCollection
    @CollectionTable(name = "quest_ex_shared", joinColumns = @JoinColumn(name = "accId"))
    @MapKeyColumn(name = "qrKey")
    @Column(name = "qrValue")
    private Map<Integer, String> sharedQuestExStorage;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "accId")
    private Set<Friend> friends;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "accId")
    private Set<Record> records;
    @Transient
    private Map<Integer, Map<String, String>> sharedQuestEx;
    @Transient
    private MapleCharacter onlineChar;
    @Transient
    private RecordManager recordManager;

    public void cleanTemp() {
        if (records != null) {
            records.removeIf(record -> record.getType().isTransition());
        }
    }

    public static Account createAccount(String name, String password) {
        Account acc = new Account();
        acc.storages = new HashSet<>();
        acc.storages.add(new Trunk());
        acc.storages.add(new Locker());
        acc.account = name;
        acc.password = password;
        return acc;
    }

    public Account() {
        characterSlots = DEFAULT_CHARACTER_SLOTS;
        recordManager = new RecordManager(this);
        sharedQuestEx = new HashMap<>();
    }

    public static Account getFromDB(String username) {
        Object account = DataBaseManager.getObjFromDB(Account.class, "account", username);
        return ((Account) account);
    }

    public void save() {
        cleanTemp();
        buildQuestExStorage();
        DataBaseManager.saveToDB(this);
    }

    public void logout() {
        Server.getInstance().removeAccount(this);
        save();
    }

    public void addChar(MapleCharacter chr) {
        characters.add(chr);
    }

    public MapleCharacter getCharacter(int charId) {
        return characters.stream().filter(character -> character.getId() == charId).findAny().orElse(null);
    }

    public void buildSharedQuestEx() {
        getSharedQuestExStorage().forEach((qrKey, qrValue) ->
                {
                    HashMap<String, String> value = new HashMap<>();
                    for (String option : qrValue.split(";")) {
                        String[] pair = option.split("=");
                        value.put(pair[0], pair[1]);
                    }
                    addSharedQuestEx(qrKey, value, false);
                }
        );
    }

    public void buildQuestExStorage() {
        getSharedQuestEx().forEach((questId, options) -> {
            StringBuilder value = new StringBuilder();
            for (String key : options.keySet()) {
                value.append(key).append("=").append(options.get(key)).append(";");
            }
            sharedQuestExStorage.put(questId, value.substring(0, value.length() - 1));
        });
    }

    public void addSharedQuestEx(int questId, Map<String, String> value, boolean sendPacket) {
        Map<String, String> options = sharedQuestEx.getOrDefault(questId, null);
        if (options == null) {
            sharedQuestEx.put(questId, value);
        } else {
            sharedQuestEx.get(questId).putAll(value);
        }
        if (sendPacket && getOnlineChar() != null) {
            getOnlineChar().announce(UserPacket.message(MessageType.WORLD_SHARE_RECORD_MESSAGE, questId,
                    getSharedQuestExStorage().get(questId), (byte) 0));
        }
    }

    public Friend getFriendByAccId(int accId) {
        return getFriends().stream().filter(f -> f.getFriendAccountId() == accId).findAny().orElse(null);
    }

    public void addFriend(Friend friend) {
        if (getFriendByAccId(friend.getFriendAccountId()) == null) {
            getFriends().add(friend);
        }
    }

    public void removeChar(int charId) {
        getCharacters().removeIf(character -> character.getId() == charId);
    }

    public void addMaplePoint(int amount) {
        maplePoint += amount;
    }

    public void addPoint(int amount) {
        point += amount;
    }

    public void addCash(int amount) {
        cash += amount;
    }

    public void addSlot(int i) {
        characterSlots += i;
    }

    public Locker getLocker() {
        for (Storage storage : storages) {
            if (storage instanceof Locker) {
                return ((Locker) storage);
            }
        }
        Locker locker = new Locker();
        storages.add(locker);
        return locker;
    }

    public Trunk getTrunk() {
        for (Storage storage : storages) {
            if (storage instanceof Trunk) {
                return ((Trunk) storage);
            }
        }
        Trunk trunk = new Trunk();
        storages.add(trunk);
        return trunk;
    }
}

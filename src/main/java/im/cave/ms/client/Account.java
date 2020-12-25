package im.cave.ms.client;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.network.db.DataBaseManager;
import im.cave.ms.network.server.Server;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private int characterSlots = 6;
    private int point, voucher;
    private byte gender, gm;
    private boolean isBanned;
    private String banReason;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "accId")
    private Set<MapleCharacter> characters = new HashSet<>();
    private Long lastLogin;
    @JoinColumn(name = "trunkId")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Trunk trunk;
    @ElementCollection
    @CollectionTable(name = "shared_quest_ex", joinColumns = @JoinColumn(name = "accId"))
    @MapKeyColumn(name = "qrKey")
    @Column(name = "qrValue")
    private Map<Integer, String> sharedQuestExStorage;
    @Transient
    private Map<Integer, Map<String, String>> sharedQuestEx = new HashMap<>();

    public Account(String account, String password) {
        this.account = account;
        this.password = password;
        lastLogin = System.currentTimeMillis();
        trunk = new Trunk((byte) 20);
    }

    public Account() {

    }

    public static Account getFromDB(String username) {
        Object account = DataBaseManager.getObjFromDB(Account.class, "account", username);
        return ((Account) account);
    }

    public void saveToDb() {
        DataBaseManager.saveToDB(this);
    }

    public void logout() {
        Server.getInstance().removeAccount(this);
        DataBaseManager.saveToDB(this);
//        saveToDb();
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
        buildQuestExStorage();
        if (sendPacket) {
            //todo
        }
    }

}

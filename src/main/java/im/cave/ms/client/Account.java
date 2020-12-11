package im.cave.ms.client;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.net.db.DataBaseManager;
import im.cave.ms.net.server.Server;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.util.HashSet;
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
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Trunk trunk;

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

    @Transactional
    public void saveToDb() {
        DataBaseManager.saveToDB(this);
    }

    public void logout() {
        Server.getInstance().removeAccount(this);
        saveToDb();
    }

    public void addChar(MapleCharacter chr) {
        characters.add(chr);
    }

    public MapleCharacter getCharacter(int charId) {
        return characters.stream().filter(character -> character.getId() == charId).findAny().orElse(null);
    }

}

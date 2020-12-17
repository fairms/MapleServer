package im.cave.ms.client.character;

import im.cave.ms.constants.GameConstants;
import im.cave.ms.network.netty.OutPacket;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 11/22 12:52
 */
@Data
@Table(name = "keymap")
@Entity
public class MapleKeyMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "keymapId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeyBind> keymap = new ArrayList<>();

    @Transient
    private static final int MAX_KEYBINDS = 89;


    public MapleKeyMap() {
    }

    public void encode(OutPacket outPacket) {
        if (getKeymap().size() == 0) {
            outPacket.writeBool(true);
        } else {
            outPacket.writeBool(false);
            for (int i = 0; i < MAX_KEYBINDS; i++) {
                KeyBind tuple = getMappingAt(i);
                if (tuple == null) {
                    outPacket.write(0);
                    outPacket.writeInt(0);
                } else {
                    outPacket.write(tuple.getType());
                    outPacket.writeInt(tuple.getAction());
                }
            }
        }

    }


    public KeyBind getMappingAt(int key) {
        for (KeyBind km : getKeymap()) {
            if (km.getKey() == key) {
                return km;
            }
        }
        return null;
    }

    public void putKeyBinding(int key, byte type, int action) {
        KeyBind km = getMappingAt(key);
        if (km == null) {
            km = new KeyBind();
            km.setKey(key);
            km.setType(type);
            km.setAction(action);
            getKeymap().add(km);
        } else {
            km.setType(type);
            km.setAction(action);
        }
    }

    public void setDefault(boolean custom) {
        byte[] key;
        byte[] type;
        int[] action;
        if (custom) {
            key = GameConstants.CUSTOM_KEY;
            type = GameConstants.CUSTOM_TYPE;
            action = GameConstants.CUSTOM_ACTION;
        } else {
            key = GameConstants.DEFAULT_KEY;
            type = GameConstants.DEFAULT_TYPE;
            action = GameConstants.DEFAULT_ACTION;
        }
        for (int i = 0; i < key.length; i++) {
            keymap.add(new KeyBind(key[i], type[i], action[i]));
        }
    }
}

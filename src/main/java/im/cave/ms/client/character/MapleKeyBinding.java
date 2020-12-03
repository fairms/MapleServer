package im.cave.ms.client.character;

import im.cave.ms.constants.GameConstants;
import im.cave.ms.tools.Pair;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 11/22 12:52
 */
@Data
public class MapleKeyBinding {
    private int id;
    private int charId;
    private final Map<Byte, Pair<Byte, Integer>> keymap;
    private boolean changed = false;


    public MapleKeyBinding() {
        keymap = new HashMap<>();
    }

    public MapleKeyBinding(Map<Byte, Pair<Byte, Integer>> keymap) {
        this.keymap = keymap;
        changed = false;
    }


    public void put(Byte key, Pair<Byte, Integer> bind) {
        keymap.put(key, bind);
        changed = true;
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
            keymap.put(key[i], new Pair<>(type[i], action[i]));
        }
        changed = true;
    }


}

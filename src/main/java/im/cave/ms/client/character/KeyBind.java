package im.cave.ms.client.character;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 12/5 18:58
 */
@Data
@Table(name = "keybinding")
@Entity
public class KeyBind {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "`key`")
    private int key;
    private byte type;
    private int action;

    public KeyBind(int key, byte type, int action) {
        this.key = key;
        this.type = type;
        this.action = action;
    }

    public KeyBind() {

    }
}

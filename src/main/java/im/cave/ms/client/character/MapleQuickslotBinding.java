package im.cave.ms.client.character;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 11/22 12:52
 */
@Entity
@Table(name = "quickbind")
@Data
public class MapleQuickslotBinding {
    @Transient
    public static final int QUICKSLOT_SIZE = 32;

    @Transient
    public static final byte[] DEFAULT_QUICKSLOTS =
            {
                    0x2A, 0x52, 0x47, 0x49, 0x1D, 0x53, 0x4F, 0x51,
                    0x02, 0x03, 0x04, 0x05, 0x10, 0x11, 0x12, 0x13,
                    0x06, 0x07, 0x08, 0x09, 0x14, 0x1e, 0x1f, 0x20,
                    0x0A, 0x0B, 0x21, 0x22, 0x25, 0x26, 0x31, 0x32
            };
    @Column(name = "keymap")
    private final byte[] m_aQuickslotKeyMapped;
    @Id
    private int id;
    private int charId;

    public MapleQuickslotBinding(byte[] aKeys) {

        if (aKeys.length != QUICKSLOT_SIZE) {
            throw new IllegalArgumentException(String.format("aKeys' size should be %d", QUICKSLOT_SIZE));
        }
        this.m_aQuickslotKeyMapped = aKeys.clone();
    }

    public MapleQuickslotBinding() {
        m_aQuickslotKeyMapped = new byte[QUICKSLOT_SIZE];
    }


    public byte[] GetKeybindings() {
        return m_aQuickslotKeyMapped;
    }


}

package im.cave.ms.client.character;

import im.cave.ms.constants.ItemConstants;
import im.cave.ms.network.netty.OutPacket;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 4/5/2018.
 */
@Entity
@Table(name = "damage_skins")
public class DamageSkinSaveData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int damageSkinId;
    private int itemId;
    private boolean notSave;
    private String description;

    public DamageSkinSaveData() {
        this.damageSkinId = -1;
        this.notSave = true;
    }

    public DamageSkinSaveData(int damageSkinID, int itemID, boolean notSave, String description) {
        this.damageSkinId = damageSkinID;
        this.itemId = itemID;
        this.notSave = notSave;
        this.description = description;
    }

    public static DamageSkinSaveData getByItemID(int itemID) {
        return new DamageSkinSaveData(ItemConstants.getDamageSkinIDByItemID(itemID), itemID, true,
                ""); // desc = StringData.getItemStringById(itemID)
    }

    public void encode(OutPacket outPacket) {
        outPacket.writeInt(getDamageSkinID());
        outPacket.writeInt(getItemID());
        outPacket.writeBool(!isNotSave());
        outPacket.writeMapleAsciiString(getDescription());
        outPacket.writeInt(0);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDamageSkinID() {
        return damageSkinId;
    }

    public void setDamageSkinID(int damageSkinID) {
        this.damageSkinId = damageSkinID;
    }

    public int getItemID() {
        return itemId;
    }

    public void setItemID(int itemID) {
        this.itemId = itemID;
    }

    public boolean isNotSave() {
        return notSave;
    }

    public void setNotSave(boolean notSave) {
        this.notSave = notSave;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

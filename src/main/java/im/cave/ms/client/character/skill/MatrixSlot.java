package im.cave.ms.client.character.skill;

import im.cave.ms.connection.netty.OutPacket;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "matrix_slot")
@Getter
@Setter
public class MatrixSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int slotId;//0-19
    private long equippedSkill = -1;
    private int enhanceLevel;


    public void encode(OutPacket out) {
        out.writeInt((int) getEquippedSkill());
        out.writeInt(getSlotId());
        out.writeInt(getEnhanceLevel());
        out.write(0);
    }
}

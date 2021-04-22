package im.cave.ms.client.character.skill;

import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.enums.MatrixStateType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "matrix_skill")
@Getter
@Setter
public class MatrixSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int slot = -1; //未装备
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "state")
    private MatrixStateType state;
    private int coreId; //核心ID
    private int skill1;
    private int skill2;
    private int skill3;
    private int level;
    private int masterLevel;
    private int experience;
    private long expirationDate = ServerConstants.MAX_TIME;


    public void encode(OutPacket out){
        out.writeLong(getId());
        out.writeInt(getCoreId());
        out.writeInt(getLevel());
        out.writeInt(getExperience());
        out.writeInt(getState().getVal());
        out.writeInt(getSkill1());
        out.writeInt(getSkill2());
        out.writeInt(getSkill3());
        out.writeInt(getSlot());
        out.writeLong(getExpirationDate());
    }

}

package im.cave.ms.client.character.skill;

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
}

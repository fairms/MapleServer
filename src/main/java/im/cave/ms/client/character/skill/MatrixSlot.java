package im.cave.ms.client.character.skill;

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
    private long equippedSkill;
    private int enhanceLevel;
}

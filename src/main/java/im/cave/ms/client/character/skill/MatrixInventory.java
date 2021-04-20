package im.cave.ms.client.character.skill;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@Entity
@Table(name = "matrix_inventory")
public class MatrixInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "matrixId")
    private List<MatrixSkill> skills;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "matrixId")
    private List<MatrixSlot> slots;

    public MatrixInventory() {
        this.skills = new CopyOnWriteArrayList<>();
        this.slots = new CopyOnWriteArrayList<>();
    }


    public static MatrixInventory getDefault() {
        MatrixInventory ret = new MatrixInventory();
        for (int i = 0; i < 19; i++) {
            MatrixSlot slot = new MatrixSlot();
            slot.setSlotId(i);
            slot.setEquippedSkill(-1);
            ret.slots.add(slot);
        }
        return ret;
    }

}

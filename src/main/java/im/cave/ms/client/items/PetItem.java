package im.cave.ms.client.items;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.Pet;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.pet
 * @date 1/1 21:41
 */
@Entity
@Table(name = "pets")
@PrimaryKeyJoinColumn(name = "itemId")
@Getter
@Setter
public class PetItem extends Item {
    private String name;
    private byte level;
    private short tameness; // 亲密度
    private byte repleteness; // hungry thing
    private short petAttribute;
    private int petSkill;
    private long deadDate;
    private int remainLife;
    private short attribute;
    private byte activeState;
    private int autoBuffSkill;
    private int petHue;
    private short giantRate;

    @Override
    public Type getType() {
        return Type.PET;
    }

    public Pet createPet(MapleCharacter chr) {
        Pet pet = new Pet(getItemId(), chr.getId());
        pet.setFh(chr.getFoothold());
        pet.setPosition(chr.getPosition());
        int chosenIdx = chr.getFirstPetIdx();
        if (chosenIdx == -1) {
            chr.dropMessage("Tried to create a pet while 3 pets already exist.");
        }
        pet.setIdx(chosenIdx);
        pet.setName(getName());
        pet.setPetLockerSN(getId());
        pet.setHue(getPetHue());
        pet.setGiantRate(getGiantRate());
        pet.setPetItem(this);
        return pet;
    }
}

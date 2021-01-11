package im.cave.ms.client.character.items;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.Pet;
import im.cave.ms.enums.PetSkill;
import im.cave.ms.network.netty.OutPacket;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.pet
 * @date 1/1 21:41
 */
@Entity
@Table(name = "pet")
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
    @OneToMany
    @JoinColumn(name = "petId", referencedColumnName = "itemId")
    private List<ExceptionItem> exceptionList;

    public PetItem() {

    }

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

    public void addPetSkill(PetSkill petSkill) {
        setPetSkill(getPetSkill() | petSkill.getVal());
    }

    @Override
    public void encode(OutPacket out) {
        super.encode(out);
        out.writeAsciiString(getName(), 13);
        out.write(getLevel());
        out.writeShort(getTameness());
        out.write(getRepleteness());
        out.writeLong(getDeadDate());
        out.writeShort(getPetAttribute()); // 0
        out.writeShort(getPetSkill()); //
        out.writeInt(getRemainLife()); // 0
        out.writeShort(getAttribute()); // 2 0
        out.write(getActiveState());
        out.writeInt(getAutoBuffSkill());
        out.writeInt(getPetHue());
        out.writeShort(getGiantRate());
        out.writeZeroBytes(14);
    }

    public void setExceptionList(List<ExceptionItem> items) {
        if (exceptionList == null) {
            exceptionList = new ArrayList<>();
        } else {
            exceptionList.clear();
        }
        exceptionList.addAll(items);
    }

    public boolean hasPetSkill(PetSkill petSkill) {
        return (getPetSkill() & petSkill.getVal()) != 0;
    }
}

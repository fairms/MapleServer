package im.cave.ms.client.character;

import im.cave.ms.client.character.items.Inventory;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.BodyPart;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.character
 * @date 1/4 10:26
 */
@Entity
@Table(name = "charlook")
@Getter
@Setter
public class CharLook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charId")
    private MapleCharacter chr;
    private byte gender; //0:Male 1:Female
    private byte skin;
    private int hair;
    private int face;
    private int mark;
    private byte hairColorBase;
    private byte hairColorMixed;
    private byte hairColorProb;
    private boolean zero; //isZero
    private int ears;
    private int tail;
    @Transient
    private boolean mega;
    @Transient
    private int job;
    @Transient
    private Map<Integer, Integer> hairEquips;
    @Transient
    private Map<Integer, Integer> unseenEquips;
    @Transient
    private Map<Integer, Integer> totems;
    @Transient
    private int weaponStickerId;
    @Transient
    private int weaponId;
    @Transient
    private int subWeaponId;

    @PostLoad
    public void initEquippedItems() {
        Inventory iv = chr.getEquippedInventory();
        List<Item> equips = iv.getItems();
        equips.sort(Comparator.comparingInt(Item::getPos));
        for (Item equip : equips) {
            int pos = equip.getPos();
            if (pos > BodyPart.BPBase.getVal() && pos < BodyPart.BPEnd.getVal()) {
                if (pos == BodyPart.Weapon.getVal()) {
                    weaponId = equip.getItemId();
                } else if (pos == BodyPart.Shield.getVal()) {
                    subWeaponId = equip.getItemId();
                }
                hairEquips.put(pos, equip.getItemId());
            } else if (pos > BodyPart.CBPBase.getVal() && pos < BodyPart.CBPEnd.getVal()) {
                pos -= BodyPart.BPEnd.getVal();
                if (pos == BodyPart.Weapon.getVal()) {
                    weaponStickerId = equip.getItemId();
                    continue;
                } else if (pos == BodyPart.Shield.getVal()) {
                    subWeaponId = equip.getItemId();
                }
                if (hairEquips.containsKey(pos)) {
                    unseenEquips.put(pos, hairEquips.get(pos));
                    hairEquips.put(pos, equip.getItemId());
                }
            } else if (pos > BodyPart.TotemBase.getVal() && pos < BodyPart.TotemEnd.getVal()) {
                totems.put(pos, equip.getItemId());
            }
        }
    }

    public CharLook() {
        hairColorBase = -1;
        hairEquips = new LinkedHashMap<>();
        unseenEquips = new LinkedHashMap<>();
        totems = new LinkedHashMap<>();
    }

    //fallback
    public static CharLook defaultLook(short job) {
        CharLook charLook = new CharLook();
        byte gender = JobConstants.getJobDefaultGender(job);
        charLook.setGender(gender);
        charLook.setHair(gender == 0 ? 35180 : 38430);
        charLook.setFace(gender == 0 ? 20038 : 21036);
        return charLook;
    }

    public void encode(OutPacket out) {
        out.write(getGender());
        out.write(getSkin());
        out.writeInt(getFace());
        out.writeInt(getJob());
        out.writeBool(isMega());
        out.writeInt(getHair());
        //显示的装备
        getHairEquips().forEach((pos, itemId) -> {
            out.write(pos);
            out.writeInt(itemId);
        });
        out.write(-1);
        //被遮挡的装备
        getUnseenEquips().forEach((pos, itemId) -> {
            out.write(pos);
            out.writeInt(itemId);
        });
        out.write(-1);
        //未知
        out.write(-1);

        //图腾开始
        getTotems().forEach((pos, itemId) -> {
            out.write(pos - BodyPart.TotemBase.getVal());
            out.writeInt(itemId);
        });
        out.write(-1);

        out.writeInt(getWeaponStickerId());
        out.writeInt(getWeaponId());
        out.writeInt(getSubWeaponId());

        out.writeLong(0);
        out.write(0); //boolean

        //三个宠物的ID
        for (int i = 0; i < GameConstants.MAX_PET_AMOUNT; i++) {
            if (chr.getPets().size() > i) {
                out.writeInt(chr.getPets().get(i).getTemplateId());
            } else {
                out.writeInt(0);
            }
        }

        if (JobConstants.isXenon((short) getJob()) || JobConstants.isDemon((short) getJob())) {
            out.writeInt(getMark());
        }
        out.writeZeroBytes(7);
        out.writeInt(0);
    }
}

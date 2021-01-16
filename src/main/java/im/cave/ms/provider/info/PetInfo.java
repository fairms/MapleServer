package im.cave.ms.provider.info;

import im.cave.ms.client.character.items.PetItem;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.PetSkill;
import im.cave.ms.provider.data.StringData;
import im.cave.ms.tools.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.info
 * @date 1/5 22:21
 */
@Getter
@Setter
public class PetInfo {
    private int itemID;
    private InventoryType invType;
    private int life;
    private int setItemID;
    private int limitedLife;
    private int evolutionID;
    private int type;
    private int evolReqItemID;
    private int evolNo;
    private int evol1;
    private int evol2;
    private int evol3;
    private int evol4;
    private int evol5;
    private int probEvol1;
    private int probEvol2;
    private int probEvol3;
    private int probEvol4;
    private int probEvol5;
    private int evolReqPetLvl;
    private boolean allowOverlappedSet;
    private boolean noRevive;
    private boolean noScroll;
    private boolean cash;
    private boolean giantPet;
    private boolean permanent;
    private boolean pickupItem;
    private boolean interactByUserAction;
    private boolean longRange;
    private boolean multiPet;
    private boolean autoBuff;
    private boolean starPlanetPet;
    private boolean evol;
    private boolean autoReact;
    private boolean pickupAll;
    private boolean sweepForDrop;
    private boolean consumeMP;
    private String runScript;

    public PetItem createPetItem() {
        PetItem petItem = new PetItem();
        petItem.setRepleteness((byte) 100);
        petItem.setItemId(getItemID());
        petItem.setInvType(getInvType());
        petItem.setQuantity(1);
        petItem.setName(StringData.getPetNameById(getItemID()));
        petItem.setLevel((byte) 1);
        petItem.setPetHue(-1);
        long expireTime = LocalDateTime.now().plusDays(getLife()).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        petItem.setExpireTime(getLife() == 0 ? MAX_TIME : DateUtil.getFileTime(expireTime));
        if (isPickupItem()) {
            petItem.addPetSkill(PetSkill.ITEM_PICKUP);
        }
        if (isAutoBuff()) {
            petItem.addPetSkill(PetSkill.AUTO_BUFF);
        }
        if (isAutoReact()) {
            petItem.addPetSkill(PetSkill.AUTO_FEED); // correct one?
        }
        if (isSweepForDrop()) {
            petItem.addPetSkill(PetSkill.AUTO_MOVE);
        }
        if (isLongRange()) {
            petItem.addPetSkill(PetSkill.EXPANDED_AUTO_MOVE);
        }

        return petItem;
    }
}

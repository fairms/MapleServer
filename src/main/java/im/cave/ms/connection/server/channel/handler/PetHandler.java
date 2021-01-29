package im.cave.ms.connection.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.ExceptionItem;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.items.PetItem;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.Pet;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.PetPacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.QuestConstants;
import im.cave.ms.enums.FieldOption;
import im.cave.ms.enums.InventoryOperationType;
import im.cave.ms.enums.PetSkill;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.ItemInfo;
import im.cave.ms.provider.info.SkillInfo;
import im.cave.ms.tools.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.server.channel.handler
 * @date 1/1 22:08
 */
public class PetHandler {
    public static void handleUserActivatePetRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        short pos = in.readShort();
        Item item = player.getCashInventory().getItem(pos);
        if (!(item instanceof PetItem)) {
            item = player.getConsumeInventory().getItem(pos);
        }
        // Two of the same condition, as item had to be re-assigned
        if (!(item instanceof PetItem)) {
            player.chatMessage(String.format("Could not find a pet on that slot (slot %s).", pos));
            return;
        }
        PetItem petItem = (PetItem) item;
        if (petItem.getActiveState() == 0) {
            if (player.getPets().size() >= GameConstants.MAX_PET_AMOUNT) {
                return;
            }
            Pet pet = petItem.createPet(player);
            petItem.setActiveState((byte) (pet.getIdx() + 1));
            player.addPet(pet);
            player.getMap().broadcastMessage(PetPacket.petActivateChange(pet, true, (byte) 0));
            if (petItem.getExceptionList() != null) {
                player.announce(PetPacket.initPetExceptionList(pet));
            }
        } else {
            Pet pet = player.getPets()
                    .stream()
                    .filter(p -> p.getPetItem().getActiveState() == petItem.getActiveState())
                    .findFirst().orElse(null);
            if (pet == null) {
                player.enableAction();
                return;
            }
            petItem.setActiveState((byte) 0);
            player.removePet(pet);
            player.getMap().broadcastMessage(PetPacket.petActivateChange(pet, false, (byte) 1));
        }
        petItem.updateToChar(player);

    }

    public static void handlePetActionSpeak(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int index = in.readInt();
        in.readInt();//tick
        short unk = in.readShort();
        String msg = in.readMapleAsciiString();
        player.getMap().broadcastMessage(player, PetPacket.petActionSpeak(player.getId(), index, unk, msg), false);
    }

    public static void handleUserPetFoodItemUseRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        in.readInt();
        short uPos = in.readShort();
        int itemId = in.readInt();
        Item item = player.getConsumeInventory().getItem(uPos);
        if (item == null) {
            item = player.getCashInventory().getItem(uPos);
        }
        if (item != null) {
            ItemInfo ii = ItemData.getItemInfoById(itemId);
            int incRepleteness = ii.getIncRepleteness();
            int incTameness = ii.getIncTameness();
            List<Integer> limitedPets = ii.getLimitedPets();
            List<Pet> pets = player.getPets();
            List<Pet> feedPets = pets.stream().filter(pet -> limitedPets.contains(pet.getPetItem().getItemId())).sorted(Comparator.comparingInt(o -> o.getPetItem().getRepleteness())).collect(Collectors.toList());
            if (feedPets.size() == 0) {
                return;
            }
            Pet pet = feedPets.get(0); //获取饱腹值最少的
            if (pet.getPetItem().getRepleteness() >= 100) {
                player.chatMessage("您的宠物的饥饿感是满值，如果继续使用将会有50%的几率减少1点亲密度。");
            } else {
                PetItem petItem = pet.getPetItem();
                petItem.setRepleteness((byte) Math.min(100, petItem.getRepleteness() + incRepleteness));
                petItem.setTameness((short) Math.max(Short.MAX_VALUE, petItem.getTameness() + incTameness));
                player.getMap().broadcastMessage(PetPacket.petActionCommand(player.getId(), petItem.getActiveState(), 2, 1, itemId));
                c.announce(UserPacket.inventoryOperation(true,
                        InventoryOperationType.ADD, (short) petItem.getPos(), (short) 0, 0, petItem));
            }
            player.consumeItem(item);
        }
    }

    public static void handlePetMove(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int index = in.readInt();
        in.readByte();
        MovementInfo movementInfo = new MovementInfo(in);
        Pet pet = player.getPetByIdx(index);
        if (pet != null) {
            movementInfo.applyTo(pet);
            player.getMap().broadcastMessage(player, PetPacket.petMove(player.getId(), index, movementInfo), false);
        }
    }

    public static void handleUserCashPetSkillSetting(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        short skill = in.readShort();
        in.readShort();
        if (skill == PetSkill.AUTO_FEED.getVal()) {
            boolean enable = in.readByte() != 0;
            Map<String, String> options = new HashMap<>();
            if (enable) {
                options.put("autoEat", "1");
            } else {
                options.put("autoEat", "0");
            }
            player.addQuestExAndSendPacket(QuestConstants.QUEST_EX_PET_AUTO_EAT_FOOD, options);
        }
    }

    public static void handleUserCashPetPickUpOnOffRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(in.readInt());
        boolean on = in.readByte() != 0;
        boolean channelChange = in.readByte() != 0;
        for (Pet pet : player.getPets()) {
            PetItem petItem = pet.getPetItem();
            petItem.setAttribute((short) (on ? 0 : 2));
            petItem.updateToChar(player);
        }
        player.announce(PetPacket.cashPetPickUpOnOffResult(true, on));
    }

    public static void handlePetSetExceptionList(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int idx = in.readInt();
        Pet pet = player.getPetByIdx(idx);
        byte size = in.readByte();
        List<ExceptionItem> items = new ArrayList<>();
        for (byte i = 0; i < size; i++) {
            items.add(new ExceptionItem(in.readInt()));
        }
        pet.getPetItem().setExceptionList(items);
    }

    public static void handleUserRegisterPetAutoBuffRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int idx = in.readInt();
        int skillId = in.readInt();
        SkillInfo si = SkillData.getSkillInfo(skillId);
        Skill skill = player.getSkill(skillId);
        Pet pet = player.getPetByIdx(idx);
        int coolTime = si == null ? 0 : si.getValue(SkillStat.cooltime, 1);
        if (skillId != 0 && (si == null || pet == null || !pet.getPetItem().hasPetSkill(PetSkill.AUTO_BUFF) ||
                skill == null || skill.getCurrentLevel() == 0 || coolTime > 0)) {
            player.chatMessage("Something went wrong when adding the pet skill.");
            player.enableAction();
            return;
        }
        pet.getPetItem().setAutoBuffSkill(skillId);
        pet.getPetItem().updateToChar(player);
    }

    public static void handlePetFoodItemUse(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int idx = in.readInt();
        player.setTick(in.readInt());
        short uPos = in.readShort();
        int itemId = in.readInt();
        Item item = player.getConsumeInventory().getItem(uPos);
        Pet pet = player.getPetByIdx(idx);
        if (pet == null || item == null || item.getItemId() != itemId || item.getQuantity() < 1) {
            return;
        }
        PetItem petItem = pet.getPetItem();
        ItemInfo ii = ItemData.getItemInfoById(itemId);
        int incRepleteness = ii.getIncRepleteness();
        petItem.setRepleteness((byte) Math.max(100, petItem.getRepleteness() + incRepleteness));
        petItem.updateToChar(player);
        player.consumeItem(itemId, 1);
        player.announce(PetPacket.petActionCommand(player.getId(), idx, 2, 1, itemId));
    }

    public static void handlePetPickUpRequest(InPacket in, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        MapleMap map = player.getMap();
        if ((map.getFieldLimit() & FieldOption.NoPet.getVal()) > 0) {
            return;
        }
        int idx = in.readInt();
        in.readByte(); //field key
        player.setTick(in.readInt());
        Position pos = in.readPosition(); //todo check
        int dropId = in.readInt();
        MapleMapObj obj = map.getObj(dropId);
        if (obj instanceof Drop) {
            Drop drop = (Drop) obj;
            boolean success = drop.isCanBePickedUpByPet() && drop.canBePickedUpBy(player) && player.addDrop(drop);
            if (success) {
                map.removeDrop(dropId, player.getId(), false, idx);
            }
        }
    }
}

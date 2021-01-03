package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.Pet;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.items.ItemInfo;
import im.cave.ms.client.items.PetItem;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.enums.InventoryOperationType;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.PetPacket;
import im.cave.ms.network.packet.UserPacket;
import im.cave.ms.provider.data.ItemData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.server.channel.handler
 * @date 1/1 22:08
 */
public class PetHandler {
    public static void handleUserActivatePetRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        player.setTick(inPacket.readInt());
        short pos = inPacket.readShort();
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
            if (player.getPets().size() > GameConstants.MAX_PET_AMOUNT) {
                return;
            }
            Pet pet = petItem.createPet(player);
            petItem.setActiveState((byte) (pet.getIdx() + 1));
            player.addPet(pet);
            petItem.updateToChar(player);
            player.getMap().broadcastMessage(PetPacket.petActivateChange(pet, true, (byte) 0));
        } else {
            byte index = petItem.getActiveState();
            Pet pet = player.getPetByIdx(index);
            petItem.setActiveState((byte) 0);
            petItem.updateToChar(player);
            player.getMap().broadcastMessage(PetPacket.petActivateChange(pet, false, (byte) 0));
        }
    }

    public static void handlePetActionSpeak(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int index = inPacket.readInt();
        inPacket.readInt();//tick
        short unk = inPacket.readShort();
        String msg = inPacket.readMapleAsciiString();
        player.getMap().broadcastMessage(player, PetPacket.petActionSpeak(player.getId(), index, unk, msg), true);
    }

    public static void handleUserPetFoodItemUseRequest(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        inPacket.readInt();
        short uPos = inPacket.readShort();
        int itemId = inPacket.readInt();
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
                player.getMap().broadcastMessage(PetPacket.petActionCommand(player.getId(), petItem.getActiveState()));
                c.announce(UserPacket.inventoryOperation(true,
                        InventoryOperationType.ADD, (short) petItem.getPos(), (short) 0, 0, petItem));
            }
            player.consumeItem(item);
        }
    }

    public static void handlePetMove(InPacket inPacket, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int index = inPacket.readInt();
        inPacket.readByte();
        MovementInfo movementInfo = new MovementInfo(inPacket);
        Pet pet = player.getPetByIdx(index);
        if (pet != null) {
            movementInfo.applyTo(pet);
            player.getMap().broadcastMessage(player, PetPacket.petMove(player.getId(), index, movementInfo), false);
        }
    }
}

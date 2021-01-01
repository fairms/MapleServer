package im.cave.ms.network.server.channel.handler;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.obj.Pet;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.pet.PetItem;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.network.netty.InPacket;
import im.cave.ms.network.packet.PetPacket;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.network.server.channel.handler
 * @date 1/1 22:08
 */
public class PetHandler {
    public static void handleUserActivatePetReuqest(InPacket inPacket, MapleClient c) {
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
            player.getMap().broadcastMessage(PetPacket.petActivateChange(pet, true, (byte) 0));
        } else {

        }
    }
}

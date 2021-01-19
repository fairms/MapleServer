package im.cave.ms.connection.server.channel.handler;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.field.obj.Familiar;
import im.cave.ms.connection.packet.FamiliarPacket;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.info.FamiliarInfo;
import im.cave.ms.tools.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.server.channel.handler
 * @date 1/19 19:36
 */
public class ItemHandler {

    public static void familiarCard(MapleCharacter chr) {
        Map<Integer, FamiliarInfo> familiars = ItemData.getFamiliars();
        List<Familiar> familiarList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Integer familiar = Util.getRandomFromCollection(familiars.keySet());
            Item item = ItemData.getItemCopy(familiar, false);
            item.setFamiliar(Familiar.generate(item.getItemId()));
            chr.addItemToInv(item);
            familiarList.add(item.getFamiliar());
        }
        chr.announce(FamiliarPacket.familiarResult(chr, (byte) 5, FamiliarPacket.revealFamiliars(familiarList), null));
    }
}

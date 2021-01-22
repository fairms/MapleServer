package im.cave.ms.connection.server.channel.handler;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.obj.Familiar;
import im.cave.ms.connection.packet.FamiliarPacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.packet.UserRemote;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.info.FamiliarInfo;
import im.cave.ms.provider.info.ItemInfo;
import im.cave.ms.provider.info.ItemRewardInfo;
import im.cave.ms.tools.Pair;
import im.cave.ms.tools.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.server.channel.handler
 * @date 1/19 19:36
 */
public class ItemActuator {

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

    public static void avatarCoupon() {

    }

    public static boolean rewardItem(Item item, MapleCharacter chr) {
        ItemInfo ii = ItemData.getItemInfoById(item.getItemId());
        Set<ItemRewardInfo> itemRewardInfos = ii.getItemRewardInfos();
        if (itemRewardInfos.size() <= 0) {
            return false;
        }
        List<Pair<Double, ItemRewardInfo>> list = new ArrayList<>();
        for (ItemRewardInfo itemRewardInfo : itemRewardInfos) {
            list.add(new Pair<>(itemRewardInfo.getProb(), itemRewardInfo));
        }
        ItemRewardInfo itemRewardInfo = Util.random(list);
        if (itemRewardInfo == null) {
            return false;
        }
        Item reward = itemRewardInfo.getItem();
        boolean canHold = false;
        if (reward.getQuantity() == 1) {
            canHold = chr.canHold(reward.getItemId());

        } else {
            canHold = chr.canHold(Collections.singletonList(reward));
        }
        if (canHold) {
            chr.addItemToInv(reward);
            chr.announce(UserPacket.effect(Effect.avatarOriented(itemRewardInfo.getEffect())));
            chr.getMap().broadcastMessage(UserRemote.effect(chr.getId(), Effect.avatarOriented(itemRewardInfo.getEffect())));
            return true;
        } else {
            //todo
//            chr.announce() //背包空间不足
            return false;
        }
    }
}

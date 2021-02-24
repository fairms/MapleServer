package im.cave.ms.connection.server.channel.handler;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.obj.Familiar;
import im.cave.ms.connection.netty.InPacket;
import im.cave.ms.connection.packet.FamiliarPacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.packet.UserRemote;
import im.cave.ms.constants.QuestConstants;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.info.FamiliarInfo;
import im.cave.ms.provider.info.ItemInfo;
import im.cave.ms.provider.info.ItemRewardInfo;
import im.cave.ms.tools.Pair;
import im.cave.ms.tools.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.server.channel.handler
 * @date 1/19 19:36
 */
//todo
public class CashItemActuator {
    //怪怪卡包
    public static void familiarPack(MapleCharacter chr) {
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

    //理发、美容卡
    public static void avatarCoupon() {

    }

    //兑换类/随机 道具
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
        ItemRewardInfo itemRewardInfo = Util.randomPick(list);
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


    //附加魔方
    public static boolean additionalCube(Item item, MapleCharacter chr, InPacket in) {
        short ePos = in.readShort();
        Equip equip = (Equip) chr.getEquipInventory().getItem(ePos);


        chr.announce(UserPacket.additionalCubeResult(chr.getId(), false, item, equip));
        chr.getMap().broadcastMessage(UserRemote.showItemAdditionalReleaseEffect(chr.getId(), item.getItemId()));
        return true;
    }

    //附加记忆魔方
    public static boolean additionalMemorialCube(Item item, MapleCharacter chr, InPacket in) {
        short ePos = in.readShort();
        Equip equip = (Equip) chr.getEquipInventory().getItem(ePos);
        Map<String, String> values = new HashMap<>();
        boolean lvup = Util.succeedProp(10, 100);
        List<Integer> options = equip.getOptions();
        values.put("dst", String.valueOf(ePos));
        values.put("pot0", String.valueOf(options.get(3)));
        values.put("pot1", String.valueOf(options.get(4)));
        values.put("pot2", String.valueOf(options.get(5) == 0 ? -1 : options.get(5)));
        values.put("add", "1");
        values.put("lvup", String.valueOf(lvup));
        chr.addQuestExAndSendPacket(QuestConstants.QUEST_EX_MEMORIAL_CUBE, values);
        chr.announce(UserPacket.memorialCubeResult(item, equip));
        chr.getMap().broadcastMessage(UserRemote.showItemAdditionalReleaseEffect(chr.getId(), item.getItemId()));
        return true;
    }
}
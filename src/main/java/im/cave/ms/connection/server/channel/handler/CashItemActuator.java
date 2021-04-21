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
import im.cave.ms.connection.server.service.EventManager;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.QuestConstants;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.ScrollStat;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.info.FamiliarInfo;
import im.cave.ms.provider.info.ItemInfo;
import im.cave.ms.provider.info.ItemRewardInfo;
import im.cave.ms.tools.Pair;
import im.cave.ms.tools.Randomizer;
import im.cave.ms.tools.Util;

import java.util.*;

import static im.cave.ms.enums.EquipBaseStat.iuc;
import static im.cave.ms.enums.EquipBaseStat.tuc;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.server.channel.handler
 * @date 1/19 19:36
 */
public class CashItemActuator {

    public static boolean mapleAnyDoor(Item item, MapleCharacter chr, InPacket in) {
        byte b = in.readByte();
        int mapId = in.readInt();
        chr.changeMap(mapId);
        return true;
    }


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

    //闪炫魔方
    public static boolean hexagonalCube(Item item, MapleCharacter chr, InPacket in) {
        short ePos = in.readShort();
        Equip equip = (Equip) chr.getEquipInventory().getItem(ePos);
        //QuestEx 52998
        //lines=6;opt1=40042;opt2=30086;opt3=30046;opt4=30043;opt5=40086;opt6=30041;lisn=661760072317140996;dst=49;grade=4
        List<Integer> options = new ArrayList<>();
        int level = 0;

        chr.announce(UserPacket.hexagonalCubeResult(level, options));
        chr.getMap().broadcastMessage(UserRemote.showCubeEffect(chr.getId(), item.getItemId()));
        return true;
    }

    public static boolean uniqueCube(Item item, MapleCharacter chr, InPacket in) {
        short ePos = in.readShort();
        Equip equip = (Equip) chr.getEquipInventory().getItem(ePos);
        //QuestEx 53089
        //dst=49;idx=2;opt=30046
        int line = Randomizer.rand(0, 3);

        chr.announce(UserPacket.uniqueCubeResult(line));

        chr.getMap().broadcastMessage(UserRemote.showCubeEffect(chr.getId(), item.getItemId()));
        return true;
    }


    /*

    OUT 00 01 00 00 00 00 00 00 00 00 00 00 00 00 失败
    IN 01 00 00 00 00 00 00 00
    OUT 00 02 01 00 00 00 00 00 00 00 00 00 00 00

    OUT 00 00 00 00 00 00 00 00 00 00 00 00 00 00 成功
    IN 00 00 00 00 00 00 00 00
    out 00 02 00 00 00 00 00 00 00 00 00 00 00 00

    OUT 00 00 00 00 00 00 00 00 00 00 00 00 00 00 成功
    IN 00 00 00 00 00 00 00 00
    OUT 00 02 00 00 00 00 00 00 00 00 00 00 00 00

    OUT 00 03 02 00 00 00 00 00 00 00 00 00 00 00 MAX
     */
    public static boolean goldHammer(Item hammer, MapleCharacter chr, InPacket in) {
        int i = in.readInt();
        int ePos = in.readInt();
        byte b = in.readByte();

        final int delay = 2700; //金锤子动画时间

        EventManager.addEvent(() -> {
            Equip equip = (Equip) chr.getInventory(InventoryType.EQUIP).getItem((short) ePos);

            if (equip == null || !ItemConstants.canEquipGoldHammer(equip) ||
                    hammer == null || !ItemConstants.isGoldHammer(hammer)) {
                chr.announce(UserPacket.goldHammerItemUpgradeResult((byte) 1, 0));
                return;
            }

            Map<ScrollStat, Integer> vals = ItemData.getItemInfoById(hammer.getItemId()).getScrollStats();

            if (vals.size() > 0) {
                if (equip.getBaseStat(iuc) >= ItemConstants.MAX_HAMMER_SLOTS) {
                    return;
                }

                boolean success = Util.succeedProp(vals.getOrDefault(ScrollStat.success, 100));

                if (success) {
                    equip.addStat(iuc, 1); // +1 hammer used
                    equip.addStat(tuc, 1); // +1 upgrades available
                    equip.updateToChar(chr);
                    chr.chatMessage(String.format("Successfully expanded upgrade slots. (%d/%d)", equip.getIuc(), ItemConstants.MAX_HAMMER_SLOTS));
                    chr.write(UserPacket.goldHammerItemUpgradeResult((byte) 1, 0));
                } else {
                    chr.chatMessage(String.format("Failed to expand upgrade slots. (%d/%d)", equip.getIuc(), ItemConstants.MAX_HAMMER_SLOTS));
                    chr.write(UserPacket.goldHammerItemUpgradeResult((byte) 1, 0));
                }

                chr.consumeItem(hammer.getItemId(), 1);
            }
        }, delay);

        return true;
    }

    //内在还原器
    public static boolean miracleCirculator(Item item, MapleCharacter chr) {

        chr.announce(UserPacket.miracleCirculatorResult(null, item));
        return true;
    }


    public static boolean dispatch(Item item, MapleCharacter chr, InPacket in) {
        return true;
    }


}
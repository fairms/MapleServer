package im.cave.ms.network.packet;

import im.cave.ms.client.Account;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.field.Familiar;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.quest.Quest;
import im.cave.ms.client.quest.QuestManager;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.EnchantStat;
import im.cave.ms.enums.EquipBaseStat;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.MapleTraitType;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.tools.DateUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static im.cave.ms.enums.BodyPart.APBase;
import static im.cave.ms.enums.BodyPart.APEnd;
import static im.cave.ms.enums.BodyPart.ASBase;
import static im.cave.ms.enums.BodyPart.ASEnd;
import static im.cave.ms.enums.BodyPart.BPBase;
import static im.cave.ms.enums.BodyPart.BPEnd;
import static im.cave.ms.enums.BodyPart.BitsBase;
import static im.cave.ms.enums.BodyPart.BitsEnd;
import static im.cave.ms.enums.BodyPart.CBPBase;
import static im.cave.ms.enums.BodyPart.CBPEnd;
import static im.cave.ms.enums.BodyPart.DUBase;
import static im.cave.ms.enums.BodyPart.DUEnd;
import static im.cave.ms.enums.BodyPart.EvanBase;
import static im.cave.ms.enums.BodyPart.EvanEnd;
import static im.cave.ms.enums.BodyPart.HakuEnd;
import static im.cave.ms.enums.BodyPart.HakuStart;
import static im.cave.ms.enums.BodyPart.MBPBase;
import static im.cave.ms.enums.BodyPart.MBPEnd;
import static im.cave.ms.enums.BodyPart.MechBase;
import static im.cave.ms.enums.BodyPart.MechEnd;
import static im.cave.ms.enums.BodyPart.TotemBase;
import static im.cave.ms.enums.BodyPart.TotemEnd;
import static im.cave.ms.enums.BodyPart.ZeroBase;
import static im.cave.ms.enums.BodyPart.ZeroEnd;
import static im.cave.ms.enums.InventoryType.EQUIPPED;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.packet
 * @date 11/21 16:22
 */
public class PacketHelper {
    public static final long MAX_TIME = 150842304000000000L;
    public static final long ZERO_TIME = 94354848000000000L;
    public static final long PERMANENT = 150841440000000000L;

    public static void addCharEntry(OutPacket outPacket, MapleCharacter chr) {
        addCharStats(outPacket, chr);
        outPacket.writeZeroBytes(12);
        addCharLook(outPacket, chr, true, false);
        outPacket.write(0);
    }

    public static void addCharLook(OutPacket outPacket, MapleCharacter chr, boolean mega, boolean second) {
//      outPacket.write(second ? chr.getSecondGender() : chr.getGender());
        outPacket.write(chr.getGender());
        outPacket.write(chr.getSkin());
        outPacket.writeInt(chr.getFace());
//        outPacket.writeInt(second ? chr.getSecondFace() : chr.getFace());
        outPacket.writeInt(chr.getJob().getJobId());
        outPacket.write(mega ? 0 : 1);
//        outPacket.writeInt(second ? chr.getSecondHair() : chr.getHair());
        outPacket.writeInt(chr.getHair());

        Map<Byte, Integer> myEquip = new LinkedHashMap<>();
        Map<Byte, Integer> maskedEquip = new LinkedHashMap<>();
        Map<Byte, Integer> totemEquip = new LinkedHashMap<>();
        Inventory equip = chr.getInventory(EQUIPPED);

        for (Item item : equip.getItems()) {
            if (item.getPos() >= 5000 && item.getPos() < 5003) {
                byte pos = (byte) (item.getPos() - 5000); //定义图腾装备的位置
                totemEquip.computeIfAbsent(pos, k -> item.getItemId());
            }
            if (item.getPos() > 128) { //not visible
                continue;
            }

            byte pos = (byte) (item.getPos()); //定义装备的位置pos
            if (pos < 100 && myEquip.get(pos) == null) {
                Equip skin = (Equip) item;
                myEquip.put(pos, skin.getEquipSkin() % 10000 > 0 ? skin.getEquipSkin() : item.getItemId());
            } else if ((pos > 100 || pos == -128) && pos != 111) {
                pos = (byte) (pos == -128 ? 28 : pos - 100);
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, item.getItemId());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, item.getItemId());
            }
        }
        /*
         * 神之子主手和副手处理
         * 1572000 太刀类型 主手
         * 1562000 太剑类型 副手
         * 10 = 盾牌
         * 11 = 武器
         */
//        boolean zero = JobConstants.is神之子(chr.getJob());
//        if (zero) {
//            if (second && myEquip.containsKey((byte) 10)) {
//                int itemId = myEquip.remove((byte) 10); //删除盾牌
//                myEquip.put((byte) 11, itemId); //将盾牌装备放到主手
//            }
//        }
        //遍历玩家身上装备的位置
        for (Map.Entry<Byte, Integer> entry : myEquip.entrySet()) {
            outPacket.write(entry.getKey()); //装备的位置
            outPacket.writeInt(entry.getValue()); //装备ID
            //System.err.println("身上装备 - > " + entry.getKey() + " " + entry.getValue());
        }
        outPacket.write(0xFF); // 加载身上装备结束
        //背包里的装备
        for (Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            outPacket.write(entry.getKey()); //装备栏的位置
            outPacket.writeInt(entry.getValue()); //装备ID
            //System.err.println("背包装备 - > " + entry.getKey() + " " + entry.getValue());
        }
        outPacket.write(0xFF); // 加载背包装备结束
        //加载玩家图腾信息 图腾的KEY位置从0开始计算 0 1 2 共三个
        for (Map.Entry<Byte, Integer> entry : totemEquip.entrySet()) {
            outPacket.write(entry.getKey()); //装备的位置
            outPacket.writeInt(entry.getValue()); //装备ID
            //System.err.println("图腾装备 - > " + entry.getKey() + " " + entry.getValue());
        }
        outPacket.write(0xFF); // 图腾
        //todo 神秘珠子
        outPacket.write(0xFF);

        //点装武器
        Item cWeapon = equip.getItem((byte) 111);
        outPacket.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
        //角色武器
        Item weapon = equip.getItem(second ? (byte) 10 : (byte) 11); //神之子第2角色 显示的武器是盾牌的
        outPacket.writeInt(weapon != null ? weapon.getItemId() : 0);
        //角色副手或者盾牌
        Item subWeapon = equip.getItem((byte) 10);
        outPacket.writeInt(subWeapon != null ? subWeapon.getItemId() : 0);
        //是否显示精灵耳朵
        outPacket.writeZeroBytes(20);

        if (JobConstants.isXenon(chr.getJobId()) || JobConstants.isDemon(chr.getJobId())) {
            outPacket.writeInt(chr.getDecorate());
        }
        outPacket.writeLong(0);
        //todo
//        if (MapleJob.is林之灵(chr.getJob())) {
//            chr.checkTailAndEar();
//            outPacket.write(Integer.valueOf(chr.getOneInfo(59300, "bEar")));
//            outPacket.writeInt(Integer.valueOf(chr.getOneInfo(59300, "EarID")));
//            outPacket.write(Integer.valueOf(chr.getOneInfo(59300, "bTail")));
//            outPacket.writeInt(Integer.valueOf(chr.getOneInfo(59300, "TailID")));
//        }


    }

    private static void addCharStats(OutPacket outPacket, MapleCharacter chr) {
        outPacket.writeInt(chr.getId());
        outPacket.writeInt(chr.getId());
        outPacket.writeInt(chr.getWorld());
        outPacket.writeAsciiString(chr.getName(), 13);
        outPacket.write(chr.getGender());
        outPacket.write(chr.getSkin());
        outPacket.writeInt(chr.getFace());
        outPacket.writeInt(chr.getHair());

        outPacket.write(chr.getHairColorBase());
        outPacket.write(chr.getHairColorMixed());
        outPacket.write(chr.getHairColorProb());

        outPacket.writeInt(chr.getLevel());
        outPacket.writeShort(chr.getJob().getJobId());

        outPacket.writeShort(chr.getStr());
        outPacket.writeShort(chr.getDex());
        outPacket.writeShort(chr.getInt_());
        outPacket.writeShort(chr.getLuk());

        outPacket.writeInt(chr.getHp());
        outPacket.writeInt(chr.getMaxHP());
        outPacket.writeInt(chr.getMp());
        outPacket.writeInt(chr.getMaxMP());

        outPacket.writeShort(chr.getRemainingAp());
        addCharSP(outPacket, chr);
        outPacket.writeLong(chr.getExp());
        outPacket.writeInt(chr.getFame());
        outPacket.writeInt(chr.getWeaponPoint());
        outPacket.writeLong(0); // gach exp  pos map
        outPacket.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        outPacket.writeInt(chr.getMapId());
        outPacket.write(chr.getSpawnPoint()); //portal
        outPacket.writeShort(0); //sub job
        if (JobConstants.isXenon(chr.getJobId()) || JobConstants.isDemon(chr.getJobId())) {
            outPacket.writeInt(chr.getDecorate());
        }
        outPacket.write(0);
        outPacket.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis())); //账号创建时间?
        outPacket.writeShort(chr.getFatigue());  //2
        outPacket.writeInt(DateUtil.getTime()); // 年月日时  //上次登录时间? //lastfatigueupdatetime
        /*
         * charisma, sense, insight, volition, hands, charm;
         */
        for (MapleTraitType t : MapleTraitType.values()) {
            outPacket.writeInt(chr.getTraitTotalExp(t));
        }
        outPacket.writeZeroBytes(13); //getNonCombatStatDayLimit
        outPacket.writeLong(ZERO_TIME);
        /*
         * PVP
         */
        outPacket.writeInt(0); //pvp exp
        outPacket.write(10); //pvp grade
        outPacket.writeInt(0); // pvp point
        outPacket.write(5); // unk
        outPacket.write(5); // pvp mode type
        outPacket.writeInt(0); //event point

        outPacket.writeReversedLong(getTime(System.currentTimeMillis())); //last log out
        outPacket.writeLong(MAX_TIME);
        outPacket.writeLong(ZERO_TIME);
        // todo
        outPacket.writeZeroBytes(14);
        outPacket.writeInt(-1);
        outPacket.writeInt(0);

    }

    public static void addCharSP(OutPacket outPacket, MapleCharacter chr) {
        int[] remainingSps = chr.getRemainingSps();
        if (JobConstants.isExtendSpJob(chr.getJobId())) {
            outPacket.write(chr.getRemainingSpsSize());
            for (int i = 0; i < remainingSps.length; i++) {
                if (remainingSps[i] > 0) {
                    outPacket.write(i + 1);
                    outPacket.writeInt(remainingSps[i]);
                }
            }
        } else {
            outPacket.writeShort(remainingSps[0]);
        }
    }

    public static void addCharInfo(OutPacket outPacket, MapleCharacter chr) {
        outPacket.writeLong(-1); // 开始生成角色信息 mask  0xFFFFFFFFFFFFFFFFL
        outPacket.write(0); //getCombatOrders
        outPacket.writeInt(-1); // pet getActiveSkillCoolTime
        outPacket.writeInt(-1);
        outPacket.writeInt(-1);
        outPacket.writeZeroBytes(6);
        //角色信息
        addCharStats(outPacket, chr);
        outPacket.write(chr.getBuddyCapacity()); //friend
        // 精灵的祝福
        if (chr.getBlessOfFairyOrigin() != null) {
            outPacket.write(1);
            outPacket.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
        } else {
            outPacket.write(0);
        }
        // 女皇的祝福
        if (chr.getBlessOfEmpressOrigin() != null) {
            outPacket.write(1);
            outPacket.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
        } else {
            outPacket.write(0);
        }
        //终极冒险家
        outPacket.writeBool(false);

        //未知
        outPacket.writeInt(0);
        outPacket.write(-1);
        outPacket.writeInt(0);
        outPacket.write(-1);

        outPacket.writeLong(chr.getMeso());
        outPacket.writeInt(chr.getId());
        outPacket.writeInt(0); //打豆豆.豆子
        outPacket.writeInt(0);
        outPacket.writeInt(0);

        outPacket.writeInt(0); //todo
//        outPacket.writeInt(chr.getPotionPot() != null ? 1 : 0); //药剂罐信息
//        if (chr.getPotionPot() != null) {
//            addPotionPotInfo(outPacket, chr.getPotionPot());
//        }

        //背包容量
        outPacket.write(chr.getInventory(InventoryType.EQUIP).getSlots());
        outPacket.write(chr.getInventory(InventoryType.CONSUME).getSlots());
        outPacket.write(chr.getInventory(InventoryType.INSTALL).getSlots());
        outPacket.write(chr.getInventory(InventoryType.ETC).getSlots());
        outPacket.write(chr.getInventory(InventoryType.CASH).getSlots());

//        //扩充吊坠栏
//        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122700));
//        if (stat != null && stat.getCustomData() != null && Long.parseLong(stat.getCustomData()) > System.currentTimeMillis()) {
//            outPacket.writeLong(getTime(Long.parseLong(stat.getCustomData())));
//        } else {
//            outPacket.writeLong(ZERO_TIME);
//        }
        outPacket.writeLong(ZERO_TIME); //todo


        outPacket.write(0); //未知
        addInventoryInfo(outPacket, chr);
        addSkillInfo(outPacket, chr);
        addQuestInfo(outPacket, chr);
        addRingsInfo(outPacket, chr); //CoupleRecord
        addTRocksInfo(outPacket, chr); //MapTransfer

        // QuestEx
        outPacket.writeShort(chr.getQuestsExStorage().size());
        chr.getQuestsExStorage().forEach((qrKey, qrValue) -> {
            outPacket.writeInt(qrKey);
            outPacket.writeMapleAsciiString(qrValue);
        });

        outPacket.writeInt(0); //unk
        outPacket.write(1);
        outPacket.writeInt(0);
        outPacket.writeInt(1);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(-1);
        outPacket.writeInt(0);
        for (int i = 0; i < 20; i++) {
            outPacket.writeInt(0);
        }
        //内在能力
        outPacket.writeShort(chr.getPotentials().size());
        for (CharacterPotential characterPotential : chr.getPotentials()) {
            characterPotential.encode(outPacket);
        }

        outPacket.writeShort(0);
        outPacket.writeInt(1); //荣耀等级
        outPacket.writeInt(chr.getStats().getHonerPoint()); // 声望
        outPacket.write(1);
        outPacket.writeShort(0);
        outPacket.write(0);
        // 天使变身外观
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.write(0);
        outPacket.writeInt(-1);
        outPacket.writeInt(0);
        outPacket.writeInt(0);

        outPacket.writeLong(0);
        outPacket.writeLong(ZERO_TIME);

        // 未知
        outPacket.writeZeroBytes(33);

        outPacket.writeLong(ZERO_TIME);
        outPacket.writeInt(0);
        outPacket.writeInt(chr.getId());
        outPacket.writeZeroBytes(12);
        outPacket.writeLong(ZERO_TIME);
        outPacket.writeInt(0x0A);
        outPacket.writeZeroBytes(20);

        //角色共享任务数据
        Account account = chr.getAccount();
        Map<Integer, String> sharedQuestExStorage = account.getSharedQuestExStorage();
        outPacket.writeShort(sharedQuestExStorage.size());
        sharedQuestExStorage.forEach((qrKey, qrValue) -> {
            outPacket.writeInt(qrKey);
            outPacket.writeMapleAsciiString(qrValue);
        });

        outPacket.writeZeroBytes(13);

        //未知
        int ffff = 19;
        outPacket.writeInt(ffff);
        for (int i = 0; i < ffff; i++) {
            outPacket.writeInt(-1);
            outPacket.write(i);
            outPacket.writeLong(0);
        }

        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        outPacket.writeInt(0);
        outPacket.writeLong(ZERO_TIME);

        outPacket.writeZeroBytes(82); //幻影窃取的技能

        outPacket.writeShort(ItemConstants.COMMODITY.length);
        long time = getTime(System.currentTimeMillis());
        for (int i : ItemConstants.COMMODITY) {
            outPacket.writeInt(i);
            outPacket.writeInt(0);
            outPacket.writeLong(time);
        }

        outPacket.writeZeroBytes(69);
        outPacket.writeLong(getTime(System.currentTimeMillis()));
        outPacket.write(0);
        outPacket.writeBool(true); //Can Use Familiar ?
        outPacket.writeInt(0);
        outPacket.writeInt(chr.getAccId());
        outPacket.writeInt(chr.getId());
        int[] idarr = new int[]{9410165, 9410166, 9410167, 9410168, 9410198};
        outPacket.writeLong(idarr.length);
        for (int i : idarr) {
            outPacket.writeInt(i);
            outPacket.writeInt(0);
        }
        outPacket.writeZeroBytes(22);
        outPacket.writeLong(ZERO_TIME);

        outPacket.write(0);
        outPacket.write(0);
        outPacket.write(1);
    }

    private static void addTRocksInfo(OutPacket outPacket, MapleCharacter chr) {
        for (int i = 0; i < 5; i++) {
            outPacket.writeInt(999999999);
        }
        for (int i = 0; i < 10; i++) {
            outPacket.writeInt(999999999);
        }
        for (int i = 0; i < 13; i++) {
            outPacket.writeInt(999999999);
        }
    }

    private static void addRingsInfo(OutPacket outPacket, MapleCharacter chr) {
        outPacket.writeShort(0); //couple

        outPacket.writeShort(0); //friend

        outPacket.writeShort(0); //marriage
    }

    private static void addQuestInfo(OutPacket outPacket, MapleCharacter chr) {
        outPacket.writeBool(true);//started quests
        QuestManager questManager = chr.getQuestManager();
        int size = questManager.getQuestsInProgress().size();
        outPacket.writeShort(size);
        for (Quest quest : questManager.getQuestsInProgress()) {
            outPacket.writeInt(quest.getQrKey());
            outPacket.writeMapleAsciiString(quest.getQRValue());
        }
        outPacket.writeBool(true); //completed quest
        Set<Quest> completedQuests = questManager.getCompletedQuests();
        outPacket.writeShort(completedQuests.size());
        for (Quest quest : completedQuests) {
            outPacket.writeInt(quest.getQrKey());
            outPacket.writeLong(getTime(quest.getCompletedTime()));
        }
        outPacket.writeShort(0); //mini game
    }

    private static void addSkillInfo(OutPacket outPacket, MapleCharacter chr) {
        outPacket.write(1); //mask
//        outPacket.writeShort(0); // skills size       short size = (short) (getSkills().size() + linkSkills.size());
        Set<Skill> skills = chr.getSkills();
        outPacket.writeShort(skills.size());
        for (Skill skill : skills) {
            outPacket.writeInt(skill.getSkillId());
            outPacket.writeInt(skill.getCurrentLevel());
            outPacket.writeLong(MAX_TIME);
            if (SkillConstants.isSkillNeedMasterLevel(skill.getSkillId())) {
                outPacket.writeInt(skill.getMasterLevel());
            }
        }

        outPacket.writeShort(0); //link skill

        outPacket.writeInt(0); //son of linked skill

        outPacket.writeShort(0); //skills in cd  size
    }

    private static void addInventoryInfo(OutPacket outPacket, MapleCharacter chr) {
        Inventory iv = chr.getInventory(EQUIPPED);
        List<Item> equippedItems = new ArrayList<>(iv.getItems());
        equippedItems.sort(Comparator.comparingInt(Item::getPos));
        List<Item> normalEquip = new ArrayList<>();
        List<Item> cashEquip = new ArrayList<>();
        List<Item> evanEquip = new ArrayList<>();
        List<Item> petConsumeEquip = new ArrayList<>();
        List<Item> androidEquip = new ArrayList<>();
        List<Item> angelicBusterEquip = new ArrayList<>();
        List<Item> bits = new ArrayList<>();
        List<Item> zeroEquip = new ArrayList<>();
        List<Item> monsterBattleEquip = new ArrayList<>();
        List<Item> arcaneSymbol = new ArrayList<>();
        List<Item> totems = new ArrayList<>();
        List<Item> hakuEquip = new ArrayList<>();
        List<Item> virtualEquip = new ArrayList<>();
        for (Item item : equippedItems) {
            if (item.getPos() > BPBase.getVal() && item.getPos() < BPEnd.getVal()) {
                normalEquip.add(item);
            } else if (item.getPos() >= CBPBase.getVal() && item.getPos() <= CBPEnd.getVal()) {
                cashEquip.add(item);
            } else if (item.getPos() >= EvanBase.getVal() && item.getPos() < EvanEnd.getVal()) {
                evanEquip.add(item);
            } else if (item.getPos() >= 200 && item.getPos() <= 300) {
                petConsumeEquip.add(item);
            } else if (item.getPos() >= APBase.getVal() && item.getPos() <= APEnd.getVal()) {
                androidEquip.add(item);
            } else if (item.getPos() >= DUBase.getVal() && item.getPos() < DUEnd.getVal()) {
                angelicBusterEquip.add(item);
            } else if (item.getPos() >= BitsBase.getVal() && item.getPos() < BitsEnd.getVal()) {
                bits.add(item);
            } else if (item.getPos() >= ZeroBase.getVal() && item.getPos() < ZeroEnd.getVal()) {
                zeroEquip.add(item);
            } else if (item.getPos() >= MBPBase.getVal() && item.getPos() < MBPEnd.getVal()) {
                monsterBattleEquip.add(item);
            } else if (item.getPos() >= ASBase.getVal() && item.getPos() < ASEnd.getVal()) {
                arcaneSymbol.add(item);
            } else if (item.getPos() >= TotemBase.getVal() && item.getPos() < TotemEnd.getVal()) {
                totems.add(item);
            } else if (item.getPos() >= HakuStart.getVal() && item.getPos() < HakuEnd.getVal()) {
                hakuEquip.add(item);
            } else if (item.getPos() >= MechBase.getVal() && item.getPos() < MechEnd.getVal()) {
                virtualEquip.add(item);
            }
        }
        for (Item item : normalEquip) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.writeShort(0);

        for (Item item : cashEquip) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.writeShort(0);

        for (Item item : chr.getInventory(InventoryType.EQUIP).getItems()) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.writeShort(0);

        for (Item item : evanEquip) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.writeShort(0);

        for (Item item : petConsumeEquip) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.writeShort(0);

        for (Item item : androidEquip) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.writeShort(0);

        for (Item item : totems) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.writeShort(0);

        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);
        //todo
        outPacket.writeShort(0);


        for (Item item : chr.getConsumeInventory().getItems()) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.write(0);

        for (Item item : chr.getInstallInventory().getItems()) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.write(0);

        for (Item item : chr.getEtcInventory().getItems()) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.write(0);

        for (Item item : chr.getCashInventory().getItems()) {
            addItemPos(outPacket, item, false, false);
            addItemInfo(outPacket, item);
        }
        outPacket.write(0);

        //todo 矿物背包
        outPacket.writeZeroBytes(12);

        outPacket.write(0);
        outPacket.writeLong(0);
    }

    private static void addItemPos(OutPacket outPacket, Item item, boolean trade, boolean bag) {
        if (item == null) {
            outPacket.write(0);
            return;
        }
        short pos = (short) item.getPos();
        if (item instanceof Equip) {
            if (pos >= 100 && pos < 1000) {
                pos -= 100;
            }
        }
        if (bag) {
            outPacket.writeInt(pos % 100 - 1);
        } else if (!trade && item.getType() == Item.Type.EQUIP) {
            outPacket.writeShort(pos);
        } else {
            outPacket.write(pos);
        }
    }

    public static void addItemInfo(OutPacket outPacket, Item item) {
        outPacket.write(item.getType().getVal());
        outPacket.writeInt(item.getItemId());
        boolean hasSn = item.getCashItemSerialNumber() > 0;
        outPacket.writeBool(hasSn);
        if (hasSn) {
            outPacket.writeLong(item.getId());
        }
        outPacket.writeLong(item.getExpireTime());
        outPacket.writeInt(-1);
        outPacket.write(0);
        if (item instanceof Equip) {
            Equip equip = (Equip) item;
            outPacket.writeInt(equip.getEquipStatMask(0));
            if (equip.hasStat(EquipBaseStat.tuc)) {
                outPacket.write(equip.getTuc());
            }
            if (equip.hasStat(EquipBaseStat.cuc)) {
                outPacket.write(equip.getCuc());
            }
            if (equip.hasStat(EquipBaseStat.iStr)) {
                outPacket.writeShort(equip.getIStr() + equip.getFSTR() + equip.getEnchantStat(EnchantStat.STR));
            }
            if (equip.hasStat(EquipBaseStat.iDex)) {
                outPacket.writeShort(equip.getIDex() + equip.getFDEX() + equip.getEnchantStat(EnchantStat.DEX));
            }
            if (equip.hasStat(EquipBaseStat.iInt)) {
                outPacket.writeShort(equip.getIInt() + equip.getFINT() + equip.getEnchantStat(EnchantStat.INT));
            }
            if (equip.hasStat(EquipBaseStat.iLuk)) {
                outPacket.writeShort(equip.getILuk() + equip.getFLUK() + equip.getEnchantStat(EnchantStat.LUK));
            }
            if (equip.hasStat(EquipBaseStat.iMaxHP)) {
                outPacket.writeShort(equip.getIMaxHp() + equip.getFHP() + equip.getEnchantStat(EnchantStat.MHP));
            }
            if (equip.hasStat(EquipBaseStat.iMaxMP)) {
                outPacket.writeShort(equip.getIMaxMp() + equip.getFMP() + equip.getEnchantStat(EnchantStat.MMP));
            }
            if (equip.hasStat(EquipBaseStat.iPAD)) {
                outPacket.writeShort(equip.getIPad() + equip.getFATT() + equip.getEnchantStat(EnchantStat.PAD));
            }
            if (equip.hasStat(EquipBaseStat.iMAD)) {
                outPacket.writeShort(equip.getIMad() + equip.getFMATT() + equip.getEnchantStat(EnchantStat.MAD));
            }
            if (equip.hasStat(EquipBaseStat.iPDD)) {
                outPacket.writeShort(equip.getIPDD() + equip.getFDEF() + equip.getEnchantStat(EnchantStat.PDD));
            }
            if (equip.hasStat(EquipBaseStat.iCraft)) {
                outPacket.writeShort(equip.getICraft());
            }
            if (equip.hasStat(EquipBaseStat.iSpeed)) {
                outPacket.writeShort(equip.getISpeed() + equip.getFSpeed() + equip.getEnchantStat(EnchantStat.SPEED));
            }
            if (equip.hasStat(EquipBaseStat.iJump)) {
                outPacket.writeShort(equip.getIJump() + equip.getFJump() + equip.getEnchantStat(EnchantStat.JUMP));
            }
            if (equip.hasStat(EquipBaseStat.attribute)) {
                outPacket.writeInt(equip.getAttribute());
            }
            if (equip.hasStat(EquipBaseStat.levelUpType)) {
                outPacket.write(equip.getLevelUpType());
            }
            if (equip.hasStat(EquipBaseStat.level)) {
                outPacket.write(equip.getLevel());
            }
            if (equip.hasStat(EquipBaseStat.exp)) {
                outPacket.writeLong(equip.getExp());
            }
            if (equip.hasStat(EquipBaseStat.durability)) {
                outPacket.writeInt(equip.getDurability());
            }
            if (equip.hasStat(EquipBaseStat.iuc)) {
                outPacket.writeInt(equip.getIuc()); // 金锤子
            }
            if (equip.hasStat(EquipBaseStat.iReduceReq)) {
                byte bLevel = (byte) (equip.getIReduceReq() + equip.getFLevel());
                if (equip.getRLevel() + equip.getIIncReq() - bLevel < 0) {
                    bLevel = (byte) (equip.getRLevel() + equip.getIIncReq());
                }
                outPacket.write(bLevel);
            }
            if (equip.hasStat(EquipBaseStat.specialAttribute)) {
                outPacket.writeShort(equip.getSpecialAttribute());
            }
            if (equip.hasStat(EquipBaseStat.durabilityMax)) {
                outPacket.writeInt(equip.getDurabilityMax());
            }
            if (equip.hasStat(EquipBaseStat.iIncReq)) {
                outPacket.write(equip.getIIncReq());
            }
            if (equip.hasStat(EquipBaseStat.growthEnchant)) {
                outPacket.write(equip.getGrowthEnchant()); // ygg
            }
            if (equip.hasStat(EquipBaseStat.psEnchant)) {
                outPacket.write(equip.getPsEnchant()); // final strike
            }
            if (equip.hasStat(EquipBaseStat.bdr)) {
                outPacket.write(equip.getBdr() + equip.getFBoss()); // bd
            }
            if (equip.hasStat(EquipBaseStat.imdr)) {
                outPacket.write(equip.getImdr()); // ied
            }
            //28 00 00 00  14 00 00 00
            outPacket.writeInt(equip.getEquipStatMask(1)); // mask 2
            if (equip.hasStat(EquipBaseStat.damR)) {
                outPacket.write(equip.getDamR() + equip.getFDamage());
            }
            if (equip.hasStat(EquipBaseStat.statR)) {
                outPacket.write(equip.getStatR() + equip.getFAllStat());
            }
            if (equip.hasStat(EquipBaseStat.cuttable)) {
                outPacket.write(equip.getCuttable());  //剪刀 FF
            }
            if (equip.hasStat(EquipBaseStat.flame)) {
                outPacket.writeLong(equip.getFlame());  //
            }
            if (equip.hasStat(EquipBaseStat.itemState)) {
                outPacket.writeInt(equip.getItemState());  // 00 01 00 00
            }

            outPacket.writeMapleAsciiString(equip.getOwner());
            outPacket.write(equip.getGrade());
            outPacket.write(equip.getChuc());
            for (int i = 0; i < 7; i++) {
                outPacket.writeShort(equip.getOptions().get(i)); // 7x, last is fusion anvil
            }
            // sockets
            outPacket.writeShort(0); //mask
            outPacket.writeShort(-1); //socket 1
            outPacket.writeShort(-1);
            outPacket.writeShort(-1); //socket 3

            outPacket.writeInt(0);
            outPacket.writeLong(equip.getId());
            outPacket.writeLong(ZERO_TIME);
            outPacket.writeInt(-1);
            outPacket.writeLong(0); //0
            outPacket.writeLong(ZERO_TIME);
            outPacket.writeZeroBytes(16); //grade

            outPacket.writeShort(equip.getSoulOptionId());
            outPacket.writeShort(equip.getSoulSocketId());
            outPacket.writeShort(equip.getSoulOption());

            if (equip.getItemId() / 10000 == 171) {
                outPacket.writeShort(0); //ARC
                outPacket.writeInt(0); //ARC EXP
                outPacket.writeShort(0); //ARC LEVEL
            }
            outPacket.writeShort(-1);
            outPacket.writeLong(MAX_TIME);
            outPacket.writeLong(ZERO_TIME);
            outPacket.writeLong(MAX_TIME);
            outPacket.writeLong(equip.getLimitBreak());
        } else {
            outPacket.writeShort(item.getQuantity());
            outPacket.writeMapleAsciiString(item.getOwner());
            outPacket.writeInt(item.getFlag());
            if (ItemConstants.isThrowingStar(item.getItemId()) || ItemConstants.isBullet(item.getItemId()) ||
                    ItemConstants.isFamiliar(item.getItemId()) || item.getItemId() == 4001886) {
                outPacket.writeLong(item.getId());
            }
            outPacket.writeInt(0);
            if (ItemConstants.isFamiliar(item.getItemId()) && item.getFamiliar() == null) {
                int familiarID = ItemData.getFamiliarId(item.getItemId());
                Familiar familiar = new Familiar(familiarID);
                item.setFamiliar(familiar);
            }
            Familiar familiar = item.getFamiliar();
            outPacket.writeInt(familiar != null ? familiar.getFamiliarId() : 0);
            outPacket.writeShort(familiar != null ? familiar.getLevel() : 0);
            outPacket.writeShort(familiar != null ? familiar.getSkill() : 0);
            outPacket.writeShort(familiar != null ? familiar.getLevel() : 0);
            outPacket.writeShort(familiar != null ? familiar.getOption(0) : 0);
            outPacket.writeShort(familiar != null ? familiar.getOption(1) : 0);
            outPacket.writeShort(familiar != null ? familiar.getOption(2) : 0);
            outPacket.write(familiar != null ? familiar.getGrade() : 0);     //品级 0=C 1=B 2=A 3=S 4=SS
        }
    }

    /**
     * 时间
     */
    public static long getTime(long realTimestamp) {

        if (realTimestamp == -1) {
            return MAX_TIME; //00 80 05 BB 46 E6 17 02
        } else if (realTimestamp == -2) {
            return ZERO_TIME; //00 40 E0 FD 3B 37 4F 01
        } else if (realTimestamp == -3) {
            return PERMANENT; //00 C0 9B 90 7D E5 17 02
        }

        return DateUtil.getFileTimestamp(realTimestamp);
    }

}


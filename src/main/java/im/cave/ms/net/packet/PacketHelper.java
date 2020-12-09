package im.cave.ms.net.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.quest.Quest;
import im.cave.ms.client.quest.QuestManager;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.EnchantStat;
import im.cave.ms.enums.EquipBaseStat;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.MapleTraitType;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

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


    public static void addCharEntry(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        addCharStats(mplew, chr);
        addCharLook(mplew, chr, true, false);
    }

    private static void addCharLook(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean mega, boolean second) {
        mplew.writeZeroBytes(12);
        //todo
//      mplew.write(second ? chr.getSecondGender() : chr.getGender());
        mplew.write(chr.getGender());
        mplew.write(chr.getSkin());
        mplew.writeInt(chr.getFace());
//        mplew.writeInt(second ? chr.getSecondFace() : chr.getFace());
        mplew.writeInt(chr.getJob().getJobId());
        mplew.write(mega ? 0 : 1);
//        mplew.writeInt(second ? chr.getSecondHair() : chr.getHair());
        mplew.writeInt(chr.getHair());


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
            mplew.write(entry.getKey()); //装备的位置
            mplew.writeInt(entry.getValue()); //装备ID
            //System.err.println("身上装备 - > " + entry.getKey() + " " + entry.getValue());
        }
        mplew.write(0xFF); // 加载身上装备结束
        //背包里的装备
        for (Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey()); //装备栏的位置
            mplew.writeInt(entry.getValue()); //装备ID
            //System.err.println("背包装备 - > " + entry.getKey() + " " + entry.getValue());
        }
        mplew.write(0xFF); // 加载背包装备结束
        //加载玩家图腾信息 图腾的KEY位置从0开始计算 0 1 2 共三个
        for (Map.Entry<Byte, Integer> entry : totemEquip.entrySet()) {
            mplew.write(entry.getKey()); //装备的位置
            mplew.writeInt(entry.getValue()); //装备ID
            //System.err.println("图腾装备 - > " + entry.getKey() + " " + entry.getValue());
        }
        mplew.write(0xFF); // 图腾
        //todo 神秘珠子
        mplew.write(0xFF);

        //点装武器
        Item cWeapon = equip.getItem((byte) 111);
        mplew.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
        //角色武器
        Item weapon = equip.getItem(second ? (byte) 10 : (byte) 11); //神之子第2角色 显示的武器是盾牌的
        mplew.writeInt(weapon != null ? weapon.getItemId() : 0);
        //角色副手或者盾牌
        Item subWeapon = equip.getItem((byte) 10);
        mplew.writeInt(subWeapon != null ? subWeapon.getItemId() : 0);
        //是否显示精灵耳朵
        mplew.writeBool(false);
        mplew.writeZeroBytes(20);

        if (JobConstants.isXenon(chr.getJobId()) || JobConstants.isDemon(chr.getJobId())) {
            mplew.writeInt(chr.getDecorate());
        }
        mplew.writeLong(0);
        //todo
//        if (MapleJob.is林之灵(chr.getJob())) {
//            chr.checkTailAndEar();
//            mplew.write(Integer.valueOf(chr.getOneInfo(59300, "bEar")));
//            mplew.writeInt(Integer.valueOf(chr.getOneInfo(59300, "EarID")));
//            mplew.write(Integer.valueOf(chr.getOneInfo(59300, "bTail")));
//            mplew.writeInt(Integer.valueOf(chr.getOneInfo(59300, "TailID")));
//        }

    }

    private static void addCharStats(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getWorld());
        mplew.writeAsciiString(chr.getName(), 13);
        mplew.write(chr.getGender());
        mplew.write(chr.getSkin());
        mplew.writeInt(chr.getFace());
        mplew.writeInt(chr.getHair());

        mplew.write(chr.getHairColorBase());
        mplew.write(chr.getHairColorMixed());
        mplew.write(chr.getHairColorProb());

        mplew.writeInt(chr.getLevel());
        mplew.writeShort(chr.getJob().getJobId());

        mplew.writeShort(chr.getStr());
        mplew.writeShort(chr.getDex());
        mplew.writeShort(chr.getInt_());
        mplew.writeShort(chr.getLuk());

        mplew.writeInt(chr.getHp());
        mplew.writeInt(chr.getMaxHP());
        mplew.writeInt(chr.getMp());
        mplew.writeInt(chr.getMaxMP());

        mplew.writeShort(chr.getRemainingAp());
        addCharSP(mplew, chr);
        mplew.writeLong(chr.getExp());
        mplew.writeInt(chr.getFame());
        mplew.writeInt(chr.getWeaponPoint());
        mplew.writeLong(0); // gach exp  pos map
        mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        mplew.writeInt(chr.getMapId());
        mplew.write(chr.getSpawnPoint()); //portal
        mplew.writeShort(0); //sub job
        if (JobConstants.isXenon(chr.getJobId()) || JobConstants.isDemon(chr.getJobId())) {
            mplew.writeInt(chr.getDecorate());
        }
        mplew.write(0);
        mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis())); //账号创建时间?
        mplew.writeShort(chr.getFatigue());  //2
        mplew.writeInt(DateUtil.getTime()); // 年月日时  //上次登录时间? //lastfatigueupdatetime
        /*
         * charisma, sense, insight, volition, hands, charm;
         */
        for (MapleTraitType t : MapleTraitType.values()) {
            mplew.writeInt(chr.getTraitTotalExp(t));
        }
        mplew.writeZeroBytes(13); //getNonCombatStatDayLimit
        mplew.writeLong(getTime(-2));
        /*
         * PVP
         */
        mplew.writeInt(0); //pvp exp
        mplew.write(10); //pvp grade
        mplew.writeInt(0); // pvp point
        mplew.write(5); // unk
        mplew.write(5); // pvp mode type
        mplew.writeInt(0); //event point

        mplew.writeReversedLong(getTime(System.currentTimeMillis())); //last log out
        mplew.writeLong(getTime(-1));
        mplew.writeLong(getTime(-2));
        // todo
        mplew.writeZeroBytes(14);
        mplew.writeInt(-1);
        mplew.writeInt(0);

    }

    //todo
    public static void addCharSP(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        int[] remainingSps = chr.getRemainingSps();
        if (JobConstants.isExtendSpJob(chr.getJobId())) {
            mplew.write(chr.getRemainingSpsSize());
            for (int i = 0; i < remainingSps.length; i++) {
                if (remainingSps[i] > 0) {
                    mplew.write(i + 1);
                    mplew.writeInt(remainingSps[i]);
                }
            }
        } else {
            mplew.writeShort(remainingSps[0]);
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

    public static void addCharInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeLong(-1); // 开始生成角色信息 mask  0xFFFFFFFFFFFFFFFFL
        mplew.write(0); //getCombatOrders
        mplew.writeInt(-1); // pet getActiveSkillCoolTime
        mplew.writeInt(-1);
        mplew.writeInt(-1);
        mplew.writeZeroBytes(6);
        //角色信息
        addCharStats(mplew, chr);
        mplew.write(chr.getBuddyCapacity()); //friend
        // 精灵的祝福
        if (chr.getBlessOfFairyOrigin() != null) {
            mplew.write(1);
            mplew.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
        } else {
            mplew.write(0);
        }
        // 女皇的祝福
        if (chr.getBlessOfEmpressOrigin() != null) {
            mplew.write(1);
            mplew.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
        } else {
            mplew.write(0);
        }
        //终极冒险家
        mplew.writeBool(false);

        //未知
        mplew.writeInt(0);
        mplew.write(-1);
        mplew.writeInt(0);
        mplew.write(-1);

        mplew.writeLong(chr.getMeso());
        mplew.writeInt(chr.getId());
        mplew.writeInt(0); //打豆豆.豆子
        mplew.writeInt(0);
        mplew.writeInt(0);

        mplew.writeInt(0); //todo
//        mplew.writeInt(chr.getPotionPot() != null ? 1 : 0); //药剂罐信息
//        if (chr.getPotionPot() != null) {
//            addPotionPotInfo(mplew, chr.getPotionPot());
//        }

        //背包容量
        mplew.write(chr.getInventory(InventoryType.EQUIP).getSlots());
        mplew.write(chr.getInventory(InventoryType.CONSUME).getSlots());
        mplew.write(chr.getInventory(InventoryType.INSTALL).getSlots());
        mplew.write(chr.getInventory(InventoryType.ETC).getSlots());
        mplew.write(chr.getInventory(InventoryType.CASH).getSlots());

//        //扩充吊坠栏
//        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122700));
//        if (stat != null && stat.getCustomData() != null && Long.parseLong(stat.getCustomData()) > System.currentTimeMillis()) {
//            mplew.writeLong(getTime(Long.parseLong(stat.getCustomData())));
//        } else {
//            mplew.writeLong(getTime(-2));
//        }
        mplew.writeLong(getTime(-2)); //todo


        mplew.write(0); //未知
        addInventoryInfo(mplew, chr);
        addSkillInfo(mplew, chr);
        addQuestInfo(mplew, chr);
        addRingsInfo(mplew, chr); //CoupleRecord
        addTRocksInfo(mplew, chr); //MapTransfer

        // QuestInfo
        mplew.writeShort(0); // quest complete old

        //未知
        mplew.writeInt(0); //quest ex
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeInt(chr.getAccId());
        mplew.writeInt(-1);
        mplew.writeInt(0);
        for (int i = 0; i < 20; i++) {
            mplew.writeInt(0);
        }

        mplew.writeShort(0); // 内在能力 todo

        mplew.writeShort(0);
        mplew.writeInt(1); //荣耀等级
        mplew.writeInt(0); // 声望
        mplew.write(1);
        mplew.writeShort(0);
        mplew.write(0);
        // 天使变身外观
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.writeInt(0);

        mplew.writeLong(0);
        mplew.writeLong(getTime(-2));

        // 未知
        mplew.writeZeroBytes(33);

        mplew.writeLong(getTime(-2));
        mplew.writeInt(0);
        mplew.writeInt(chr.getId());
        mplew.writeZeroBytes(12);
        mplew.writeLong(getTime(-2));
        mplew.writeInt(0x0A);
        mplew.writeZeroBytes(20);

        //角色共享任务数据
        mplew.writeShort(0);

        mplew.writeZeroBytes(13);

        //未知
        int ffff = 19;
        mplew.writeInt(ffff);
        for (int i = 0; i < ffff; i++) {
            mplew.writeInt(-1);
            mplew.write(i);
            mplew.writeLong(0);
        }


        mplew.writeInt(chr.getAccId());
        mplew.writeInt(chr.getId());
        mplew.writeInt(0);
        mplew.writeLong(getTime(-2));

        mplew.writeZeroBytes(82);

        mplew.writeShort(0);
//        mplew.writeShort(ItemConstants.COMMODITY.length);
//        for (int i : ItemConstants.COMMODITY) {
//            mplew.writeInt(i);
//            mplew.writeInt(0);
//            mplew.writeLong(getTime(System.currentTimeMillis()));
//        }

        mplew.writeZeroBytes(69);
        mplew.writeLong(getTime(System.currentTimeMillis()));
        mplew.write(0);
        mplew.writeBool(true); //Can Use Familiar ?
        mplew.writeInt(0);
        mplew.writeInt(chr.getAccId());
        mplew.writeInt(chr.getId());
        int[] idarr = new int[]{9410165, 9410166, 9410167, 9410168, 9410198};
        mplew.writeLong(idarr.length);
        for (int i : idarr) {
            mplew.writeInt(i);
            mplew.writeInt(0);
        }
        mplew.writeZeroBytes(22);
        mplew.writeLong(getTime(-2));
        mplew.write(0);
        mplew.write(0);
        mplew.write(1);
        mplew.write(1);
        mplew.write(0);
        mplew.writeLong(getTime(-2));
        mplew.writeZeroBytes(16);
    }

    private static void addTRocksInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        for (int i = 0; i < 5; i++) {
            mplew.writeInt(999999999);
        }
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(999999999);
        }
        for (int i = 0; i < 13; i++) {
            mplew.writeInt(999999999);
        }
    }

    private static void addRingsInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeShort(0); //couple

        mplew.writeShort(0); //friend

        mplew.writeShort(0); //marriage
    }

    private static void addQuestInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeBool(true);//started quests
        QuestManager questManager = chr.getQuestManager();
        int size = questManager.getQuestsInProgress().size();
        mplew.writeShort(size);
        for (Quest quest : questManager.getQuestsInProgress()) {
            mplew.writeInt(quest.getQrKey());
            mplew.writeMapleAsciiString(quest.getQrValue());
        }
        mplew.writeBool(true); //completed quest
        Set<Quest> completedQuests = questManager.getCompletedQuests();
        mplew.writeShort(completedQuests.size());
        for (Quest quest : completedQuests) {
            mplew.writeInt(quest.getQrKey());
            mplew.writeLong(getTime(quest.getCompletedTime()));
        }
        mplew.writeShort(0); //mini game
    }

    private static void addSkillInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.write(1); //mask
//        mplew.writeShort(0); // skills size       short size = (short) (getSkills().size() + linkSkills.size());
        Set<Skill> skills = chr.getSkills();
        mplew.writeShort(skills.size());
        for (Skill skill : skills) {
            mplew.writeInt(skill.getSkillId());
            mplew.writeInt(skill.getCurrentLevel());
            mplew.writeLong(DateUtil.getFileTime(-1));
            if (SkillConstants.isSkillNeedMasterLevel(skill.getSkillId())) {
                mplew.writeInt(skill.getMasterLevel());
            }
        }

        mplew.writeShort(0); //link skill

        mplew.writeInt(0); //son of linked skill

        mplew.writeShort(0); //skills in cd  size
    }

    private static void addInventoryInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
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
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.writeShort(0);

        for (Item item : cashEquip) {
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.writeShort(0);

        for (Item item : chr.getInventory(InventoryType.EQUIP).getItems()) {
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.writeShort(0);

        for (Item item : evanEquip) {
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.writeShort(0);

        for (Item item : petConsumeEquip) {
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.writeShort(0);

        for (Item item : androidEquip) {
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.writeShort(0);

        for (Item item : totems) {
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.writeShort(0);

        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);
        //todo
        mplew.writeShort(0);


        for (Item item : chr.getConsumeInventory().getItems()) {
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.write(0);

        for (Item item : chr.getInstallInventory().getItems()) {
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.write(0);

        for (Item item : chr.getEtcInventory().getItems()) {
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.write(0);

        for (Item item : chr.getCashInventory().getItems()) {
            addItemPos(mplew, item, false, false);
            addItemInfo(mplew, item);
        }
        mplew.write(0);

        //todo 矿物背包
        mplew.writeZeroBytes(12);

        mplew.write(0);
        mplew.writeLong(0);
    }

    private static void addItemPos(MaplePacketLittleEndianWriter mplew, Item item, boolean trade, boolean bag) {
        if (item == null) {
            mplew.write(0);
            return;
        }
        short pos = (short) item.getPos();
        if (item instanceof Equip) {
            if (pos >= 100 && pos < 1000) {
                pos -= 100;
            }
        }
        if (bag) {
            mplew.writeInt(pos % 100 - 1);
        } else if (!trade && item.getType() == Item.Type.EQUIP) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
    }

    public static void addItemInfo(MaplePacketLittleEndianWriter mplew, Item item) {
        mplew.write(item.getType().getVal());
        mplew.writeInt(item.getItemId());
        mplew.write(0); //hasSN
//        mplew.writeLong(item.getId());
        mplew.writeLong(item.getExpireTime());
        mplew.writeInt(-1);//pos?
        mplew.write(0);
        if (item instanceof Equip) {
            Equip equip = (Equip) item;
            mplew.writeInt(equip.getEquipStatMask(0));
            if (equip.hasStat(EquipBaseStat.tuc)) {
                mplew.write(equip.getTuc());
            }
            if (equip.hasStat(EquipBaseStat.cuc)) {
                mplew.write(equip.getCuc());
            }
            if (equip.hasStat(EquipBaseStat.iStr)) {
                mplew.writeShort(equip.getIStr() + equip.getFSTR() + equip.getEnchantStat(EnchantStat.STR));
            }
            if (equip.hasStat(EquipBaseStat.iDex)) {
                mplew.writeShort(equip.getIDex() + equip.getFDEX() + equip.getEnchantStat(EnchantStat.DEX));
            }
            if (equip.hasStat(EquipBaseStat.iInt)) {
                mplew.writeShort(equip.getIInt() + equip.getFINT() + equip.getEnchantStat(EnchantStat.INT));
            }
            if (equip.hasStat(EquipBaseStat.iLuk)) {
                mplew.writeShort(equip.getILuk() + equip.getFLUK() + equip.getEnchantStat(EnchantStat.LUK));
            }
            if (equip.hasStat(EquipBaseStat.iMaxHP)) {
                mplew.writeShort(equip.getIMaxHp() + equip.getFHP() + equip.getEnchantStat(EnchantStat.MHP));
            }
            if (equip.hasStat(EquipBaseStat.iMaxMP)) {
                mplew.writeShort(equip.getIMaxMp() + equip.getFMP() + equip.getEnchantStat(EnchantStat.MMP));
            }
            if (equip.hasStat(EquipBaseStat.iPAD)) {
                mplew.writeShort(equip.getIPad() + equip.getFATT() + equip.getEnchantStat(EnchantStat.PAD));
            }
            if (equip.hasStat(EquipBaseStat.iMAD)) {
                mplew.writeShort(equip.getIMad() + equip.getFMATT() + equip.getEnchantStat(EnchantStat.MAD));
            }
            if (equip.hasStat(EquipBaseStat.iPDD)) {
                mplew.writeShort(equip.getIPDD() + equip.getFDEF() + equip.getEnchantStat(EnchantStat.PDD));
            }
            if (equip.hasStat(EquipBaseStat.iCraft)) {
                mplew.writeShort(equip.getICraft());
            }
            if (equip.hasStat(EquipBaseStat.iSpeed)) {
                mplew.writeShort(equip.getISpeed() + equip.getFSpeed() + equip.getEnchantStat(EnchantStat.SPEED));
            }
            if (equip.hasStat(EquipBaseStat.iJump)) {
                mplew.writeShort(equip.getIJump() + equip.getFJump() + equip.getEnchantStat(EnchantStat.JUMP));
            }
            if (equip.hasStat(EquipBaseStat.attribute)) {
                mplew.writeInt(equip.getAttribute());
            }
            if (equip.hasStat(EquipBaseStat.levelUpType)) {
                mplew.write(equip.getLevelUpType());
            }
            if (equip.hasStat(EquipBaseStat.level)) {
                mplew.write(equip.getLevel());
            }
            if (equip.hasStat(EquipBaseStat.exp)) {
                mplew.writeLong(equip.getExp());
            }
            if (equip.hasStat(EquipBaseStat.durability)) {
                mplew.writeInt(equip.getDurability());
            }
            if (equip.hasStat(EquipBaseStat.iuc)) {
                mplew.writeInt(equip.getIuc()); // hammer
            }
//            if (equip.hasStat(EquipBaseStat.iPvpDamage)) {
//                mplew.writeShort(equip.getIPvpDamage());
//            }
            if (equip.hasStat(EquipBaseStat.iReduceReq)) {
                byte bLevel = (byte) (equip.getIReduceReq() + equip.getFLevel());
                if (equip.getRLevel() + equip.getIIncReq() - bLevel < 0) {
                    bLevel = (byte) (equip.getRLevel() + equip.getIIncReq());
                }
                mplew.write(bLevel);
            }
            if (equip.hasStat(EquipBaseStat.specialAttribute)) {
                mplew.writeShort(equip.getSpecialAttribute());
            }
            if (equip.hasStat(EquipBaseStat.durabilityMax)) {
                mplew.writeInt(equip.getDurabilityMax());
            }
            if (equip.hasStat(EquipBaseStat.iIncReq)) {
                mplew.write(equip.getIIncReq());
            }
            if (equip.hasStat(EquipBaseStat.growthEnchant)) {
                mplew.write(equip.getGrowthEnchant()); // ygg
            }
            if (equip.hasStat(EquipBaseStat.psEnchant)) {
                mplew.write(equip.getPsEnchant()); // final strike
            }
            if (equip.hasStat(EquipBaseStat.bdr)) {
                mplew.write(equip.getBdr() + equip.getFBoss()); // bd
            }
            if (equip.hasStat(EquipBaseStat.imdr)) {
                mplew.write(equip.getImdr()); // ied
            }
            //28 00 00 00
            mplew.writeInt(equip.getEquipStatMask(1)); // mask 2
            if (equip.hasStat(EquipBaseStat.damR)) {
                mplew.write(equip.getDamR() + equip.getFDamage()); // td
            }
            if (equip.hasStat(EquipBaseStat.statR)) {
                mplew.write(equip.getStatR() + equip.getFAllStat()); // as
            }
            if (equip.hasStat(EquipBaseStat.cuttable)) {
                mplew.write(equip.getCuttable()); // sok //剪刀
            }
            if (equip.hasStat(EquipBaseStat.exGradeOption)) {
                mplew.writeLong(equip.getExGradeOption());  //?
            }
            if (equip.hasStat(EquipBaseStat.itemState)) {
                mplew.writeInt(equip.getItemState());
            }


            mplew.writeMapleAsciiString(equip.getOwner());
            mplew.write(equip.getGrade());
            mplew.write(equip.getChuc());
            for (int i = 0; i < 7; i++) {
                mplew.writeShort(equip.getOptions().get(i)); // 7x, last is fusion anvil
            }
            // sockets
            mplew.writeShort(0); //mask
            mplew.writeShort(-1); //socket 1
            mplew.writeShort(-1);
            mplew.writeShort(-1); //socket 3


            mplew.writeInt(0);
            mplew.writeLong(equip.getId());
            mplew.writeLong(getTime(-2));
            mplew.writeInt(-1);
            mplew.writeLong(equip.getCashItemSerialNumber());
            mplew.writeLong((getTime(equip.getExpireTime())));
            mplew.writeZeroBytes(16); //grade

            mplew.writeShort(equip.getSoulOptionId());
            mplew.writeShort(equip.getSoulSocketId());
            mplew.writeShort(equip.getSoulOption());
            if (equip.getItemId() / 10000 == 171) {
                mplew.writeShort(0); //ARC
                mplew.writeInt(0); //ARC EXP
                mplew.writeShort(0); //ARC LEVEL
            }
            mplew.writeShort(-1);
            mplew.writeLong(getTime(-1));
            mplew.writeLong(getTime(-2));
            mplew.writeLong(getTime(-1));
            mplew.writeLong(equip.getLimitBreak());
        } else {
            mplew.writeShort(item.getQuantity());
            mplew.writeMapleAsciiString(item.getOwner());
            mplew.writeShort(0);
            mplew.writeZeroBytes(23);
        }
    }
}


package im.cave.ms.connection.packet;

import im.cave.ms.client.Account;
import im.cave.ms.client.character.CharLook;
import im.cave.ms.client.character.CharStats;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Inventory;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.quest.Quest;
import im.cave.ms.client.quest.QuestManager;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.tools.DateUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.constants.ServerConstants.ZERO_TIME;
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
    public static void addCharEntry(OutPacket out, MapleCharacter chr) {
        addCharStats(out, chr);
        out.writeInt(0); //0 18
        out.writeLong(0);
        out.writeInt(0);
        chr.getCharLook().encode(out);
        out.write(0);
    }

    public static void addCharStats(OutPacket out, MapleCharacter chr) {
        out.writeInt(chr.getId());
        out.writeInt(chr.getId());
        out.writeInt(chr.getWorld());
        out.writeAsciiString(chr.getName(), 13);
        CharLook charLook = chr.getCharLook();
        out.write(charLook.getGender());
        out.write(charLook.getSkin());
        out.writeInt(charLook.getFace());
        out.writeInt(charLook.getHair());
        out.write(charLook.getHairColorBase());
        out.write(charLook.getHairColorMixed());
        out.write(charLook.getHairColorProb());
        CharStats stats = chr.getStats();
        stats.encode(out);
        out.writeLong(0);
        out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        out.writeInt(chr.getMapId());
        out.write(chr.getSpawnPoint());
        out.writeShort(stats.getSubJob());
        if (JobConstants.isXenon(chr.getJob()) || JobConstants.isDemon(chr.getJob())) {
            out.writeInt(charLook.getMark());
        }
        out.write(0);
        out.writeLong(ZERO_TIME);
        out.writeShort(stats.getFatigue());
        out.writeInt(stats.getFatigueUpdated() == 0 ? DateUtil.getTime() : stats.getFatigueUpdated());
        /*
         * charisma, sense, insight, volition, hands, charm;
         */
        out.writeInt(stats.getCharismaExp());
        out.writeInt(stats.getInsightExp());
        out.writeInt(stats.getWillExp());
        out.writeInt(stats.getCraftExp());
        out.writeInt(stats.getSenseExp());
        out.writeInt(stats.getCharmExp());
        stats.getNonCombatStatDayLimit().encode(out);
        /*
         * PVP
         */
        out.writeInt(0); //pvp exp
        out.write(10); //pvp grade
        out.writeInt(0); // pvp maplePoint
        out.write(5); // unk
        out.write(5); // pvp mode type
        out.writeInt(0); //event maplePoint

        out.writeReversedLong(chr.getLastLogout());
        out.writeLong(MAX_TIME);
        out.writeLong(ZERO_TIME);
        out.writeZeroBytes(14);
        //斗燃
        //begin level
        //end level
        //00 00 00 00
        //02 00 level added
        out.writeInt(-1);
        out.writeInt(0); //bBurning
    }


    public static void addCharInfo(OutPacket out, MapleCharacter chr) {
        out.writeLong(-1); // 开始生成角色信息 mask  0xFFFFFFFFFFFFFFFFL
        //begin character
        out.write(0); //getCombatOrders
        out.writeInt(-1); // pet getActiveSkillCoolTime
        out.writeInt(-1);
        out.writeInt(-1);
        out.writeZeroBytes(6);
        //角色信息
        addCharStats(out, chr);
        out.write(chr.getBuddyCapacity()); //friend
        // 精灵的祝福
        if (chr.getBlessOfFairyOrigin() != null) {
            out.write(1);
            out.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
        } else {
            out.write(0);
        }
        // 女皇的祝福
        if (chr.getBlessOfEmpressOrigin() != null) {
            out.write(1);
            out.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
        } else {
            out.write(0);
        }
        //终极冒险家
        out.writeBool(false);

        //未知
        out.writeInt(0);
        out.write(-1);
        out.writeInt(0);
        out.write(-1);
        //end character
        out.writeLong(chr.getMeso());
        out.writeInt(chr.getId());
        out.writeInt(0); //打豆豆.豆子
        out.writeInt(0);
        out.writeInt(0);

        out.writeInt(chr.getPotionPot() != null);
        if (chr.getPotionPot() != null) {
            chr.getPotionPot().encode(out);
        }
        //背包容量
        out.writeInt(chr.getInventory(InventoryType.EQUIP).getSlots());
        out.writeInt(chr.getInventory(InventoryType.CONSUME).getSlots());
        out.writeInt(chr.getInventory(InventoryType.INSTALL).getSlots());
        out.writeInt(chr.getInventory(InventoryType.ETC).getSlots());
        out.writeInt(chr.getInventory(InventoryType.CASH).getSlots());
        out.writeInt(chr.getInventory(InventoryType.CASH_EQUIP).getSlots());

        if (chr.getExtendedPendant() > 0 && chr.getExtendedPendant() > DateUtil.getFileTime(System.currentTimeMillis())) {
            out.writeLong(chr.getExtendedPendant());
        } else {
            out.writeLong(ZERO_TIME);

        }
        out.write(0); //END

        addInventoryInfo(out, chr);
        addSkillInfo(out, chr);
        addQuestInfo(out, chr);
        addRingsInfo(out, chr); //CoupleRecord
        addTRocksInfo(out, chr); //MapTransfer

        // QuestEx
        out.writeShort(chr.getQuestsExStorage().size());
        chr.getQuestsExStorage().forEach((qrKey, qrValue) -> {
            out.writeInt(qrKey);
            out.writeMapleAsciiString(qrValue);
        });

        out.writeInt(0); //unk
        out.write(1);
        out.writeInt(0);
        out.writeInt(1);
        out.writeInt(chr.getAccId());
        out.writeInt(-1);


        out.writeShort(0);// ShopBuyLimit
        //01 00
        //03 00
        //9010109
            //9010109
            //06 00
            //2023660
            //03 00
            //time

        out.writeShort(0);//Unk5


        for (int i = 0; i < 20; i++) { //StolenSkills and ChosenSkills
            out.writeInt(0);
        }
        //内在能力
        out.writeShort(chr.getPotentials().size()); //CharacterPotentialSkill
        for (CharacterPotential characterPotential : chr.getPotentials()) {
            characterPotential.encode(out);
        }

        out.writeShort(0); //Character
        out.writeInt(1); //荣耀等级
        out.writeInt(chr.getStats().getHonerPoint()); // 声望
        out.write(1);
        out.writeShort(0);
        out.write(0);
        // 天使变身外观//DressUpInfo
        out.writeInt(0);
        out.writeInt(0);
        out.writeInt(0);
        out.write(0);
        out.writeInt(-1);
        out.writeInt(0);
        out.writeInt(0);

        out.writeLong(0);
        out.writeLong(ZERO_TIME);

        // 未知
        out.writeZeroBytes(33);

        out.writeLong(ZERO_TIME);
        out.writeInt(0);
        out.writeInt(chr.getId());
        out.writeZeroBytes(12);
        out.writeLong(ZERO_TIME);

        out.writeInt(0x0A); // xxx size
        out.writeZeroBytes(20); // xxx

        //角色共享任务数据
        Account account = chr.getAccount();
        Map<Integer, String> sharedQuestExStorage = account.getSharedQuestExStorage();
        out.writeShort(sharedQuestExStorage.size());
        sharedQuestExStorage.forEach((qrKey, qrValue) -> {
            out.writeInt(qrKey);
            out.writeMapleAsciiString(qrValue);
        });

        out.writeShort(0); //未知Quest数据
        //100000
        //0=800004000000000000000000000000000000000000000000

        out.writeZeroBytes(7);
        out.writeInt(0); //五转核心数目
        //idx
        //201327833
        //sn
        //level
        //0
        //2
        //skillId
        //8个0
        //位置序号
        //max_TIme


        //未知 可能和五转的强化有关
        int ffff = 20;
        out.writeInt(ffff);
        for (int i = 0; i < ffff; i++) {
            out.writeInt(-1);
            out.write(i);
            out.writeLong(0);
        }

        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        out.writeInt(0);
        out.writeLong(ZERO_TIME);
        //todo
        out.writeZeroBytes(82); //幻影窃取的技能
        //00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
        // 08 00 00 00 00 00 00 00 00 00 00 00 00
        //还有个8？

        out.writeShort(ItemConstants.COMMODITY.length);
        long time = DateUtil.getFileTime(System.currentTimeMillis());
        for (int i : ItemConstants.COMMODITY) {
            out.writeInt(i);
            out.writeInt(0);
            out.writeLong(time);
        }
        out.writeZeroBytes(69);
        out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
        out.write(0);
        out.writeBool(true); //Can Use Familiar ?
        out.writeInt(0);
        out.writeInt(chr.getAccId());
        out.writeInt(chr.getId());
        int[] idarr = new int[]{9410165, 9410166, 9410167, 9410168, 9410198};
        out.writeLong(idarr.length);
        for (int i : idarr) {
            out.writeInt(i);
            out.writeInt(0);
        }
        out.writeZeroBytes(22);
        out.writeLong(ZERO_TIME);

        out.write(0);
        out.write(0);
        out.write(1); //也可能是0
    }

    private static void addTRocksInfo(OutPacket out, MapleCharacter chr) {
        for (int i = 0; i < 5; i++) {
            out.writeInt(999999999);
        }
        for (int i = 0; i < 10; i++) {
            out.writeInt(999999999);
        }
        for (int i = 0; i < 13; i++) {
            out.writeInt(999999999);
        }
    }

    private static void addRingsInfo(OutPacket out, MapleCharacter chr) {
        out.writeShort(0); //couple

        out.writeShort(0); //friend

        out.writeShort(0); //marriage
    }

    private static void addQuestInfo(OutPacket out, MapleCharacter chr) {
        out.writeBool(true);//started quests
        QuestManager questManager = chr.getQuestManager();
        int size = questManager.getQuestsInProgress().size();
        out.writeShort(size);
        for (Quest quest : questManager.getQuestsInProgress()) {
            out.writeInt(quest.getQrKey());
            out.writeMapleAsciiString(quest.getQRValue());
        }
        out.writeBool(true); //completed quest
        Set<Quest> completedQuests = questManager.getCompletedQuests();
        out.writeShort(completedQuests.size());
        for (Quest quest : completedQuests) {
            out.writeInt(quest.getQrKey());
            out.writeLong(quest.getCompletedTime());
        }
        out.writeShort(0); //mini game
    }

    private static void addSkillInfo(OutPacket out, MapleCharacter chr) {
        out.write(1); //mask
//        out.writeShort(0); // skills size       short size = (short) (getSkills().size() + linkSkills.size());
        Set<Skill> skills = chr.getSkills();
        out.writeShort(skills.size());
        for (Skill skill : skills) {
            out.writeInt(skill.getSkillId());
            out.writeInt(skill.getCurrentLevel());
            out.writeLong(MAX_TIME);
            if (SkillConstants.isSkillNeedMasterLevel(skill.getSkillId())) {
                out.writeInt(skill.getMasterLevel());
            }
        }

        out.writeShort(0); //link skill

        out.writeInt(0); //son of linked skill

        out.writeShort(0); //skills in cd  size
    }

    private static void addInventoryInfo(OutPacket out, MapleCharacter chr) {
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
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0);  //装备

        for (Item item : chr.getInventory(InventoryType.EQUIP).getItems()) {
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0); //装备栏
/////////////////////////////////////////////
        for (Item item : evanEquip) {
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0);

        for (Item item : petConsumeEquip) {
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0);
///////////////////////////////
        for (Item item : totems) {
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0);
///////////////////////////////
        //todo
        out.writeShort(0); //1
        //todo
        out.writeShort(0); //2
        //todo
        out.writeShort(0); //3
        //todo
        out.writeShort(0); //4
        //todo
        out.writeShort(0); //5
        //todo
        out.writeShort(0); //6
        //todo
        out.writeShort(0); //7
        ////////////////////
        //机器人
        //todo
        out.writeInt(0); //背包中机器人的数目...
        //60 C8 8E 26 00 00 00 00 id
        //0a 00 type
        // short hair
        // short face
        // name
        // 12个0
        ///////////////////////////

        //todo
        out.writeShort(0); //8
        //todo
        out.writeShort(0); //9

        out.write(0);
        for (Item item : cashEquip) {
            out.writeShort(item.getPos() - 100);
            item.encode(out);
        }
        out.writeShort(0);
        for (Item item : chr.getCashEquipInventory().getItems()) {
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0);
        //todo
        for (Item item : androidEquip) {
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0);

        //todo
        out.writeShort(0);
        //todo
        out.writeShort(0);

        for (Item item : chr.getConsumeInventory().getItems()) {
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0);

        for (Item item : chr.getInstallInventory().getItems()) {
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0);

        for (Item item : chr.getEtcInventory().getItems()) {
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0);

        for (Item item : chr.getCashInventory().getItems()) {
            out.writeShort(item.getPos());
            item.encode(out);
        }
        out.writeShort(0);

        out.writeLong(0);
        out.writeInt(0); //bag quantity

        /*
           bags.for {
            bag.index int
            bagItem.id
            bagItems.encode{
           bagItemId
           {
           item.pos int
           item.encode
            }
    }
    -1 int
        }
         */
        out.write(0);
        out.writeLong(0);
    }
}


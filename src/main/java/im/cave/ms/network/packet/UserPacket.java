package im.cave.ms.network.packet;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleStat;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.items.ScrollUpgradeInfo;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.enums.DamageSkinType;
import im.cave.ms.enums.EquipmentEnchantType;
import im.cave.ms.enums.InventoryOperation;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.MessageType;
import im.cave.ms.network.crypto.TripleDESCipher;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.opcode.RecvOpcode;
import im.cave.ms.network.packet.opcode.SendOpcode;
import im.cave.ms.tools.Randomizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static im.cave.ms.constants.ServerConstants.DESKEY;
import static im.cave.ms.enums.InventoryType.EQUIPPED;
import static im.cave.ms.network.packet.PacketHelper.MAX_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet.opcode
 * @date 11/29 22:25
 */
public class UserPacket {
    public static final Map<MapleStat, Long> EMPTY_STATUS = Collections.emptyMap();

    public static OutPacket enableActions() {
        return updatePlayerStats(EMPTY_STATUS, true, null);
    }

    public static OutPacket updatePlayerStats(Map<MapleStat, Long> stats, MapleCharacter chr) {
        return updatePlayerStats(stats, false, chr);
    }

    public static OutPacket updatePlayerStats(Map<MapleStat, Long> stats, boolean enableActions, MapleCharacter chr) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.UPDATE_STATS.getValue());
        outPacket.write(enableActions ? 1 : 0);

        outPacket.write(0); //unk

        long mask = 0;
        for (MapleStat stat : stats.keySet()) {
            mask |= stat.getValue();
        }
        outPacket.writeLong(mask);
        Comparator<MapleStat> comparator = Comparator.comparingLong(MapleStat::getValue);
        TreeMap<MapleStat, Long> sortedStats = new TreeMap<>(comparator);
        sortedStats.putAll(stats);
        for (Map.Entry<MapleStat, Long> entry : sortedStats.entrySet()) {
            MapleStat stat = entry.getKey();
            long value = entry.getValue();
            switch (stat) {
                case SKIN:
                    outPacket.write((byte) value);
                    break;
                case FACE:
                case HAIR:
                case HP:
                case MAXHP:
                case MP:
                case MAXMP:
                case FAME:
                case CHARISMA:
                case CHARM:
                case WILL:
                case SENSE:
                case INSIGHT:
                case CRAFT:
                case LEVEL:
                case ICE_GAGE:
                case JOB:
                    outPacket.writeInt((int) value);
                    break;
                case STR:
                case DEX:
                case INT:
                case LUK:
                case AVAILABLEAP:
                case FATIGUE:
                    outPacket.writeShort((int) value);
                    break;
                case AVAILABLESP:
                    PacketHelper.addCharSP(outPacket, chr);
                    break;
                case EXP:
                case MESO:
                    outPacket.writeLong(value);
                    break;
                case TODAYS_TRAITS:
                    outPacket.writeZeroBytes(21); //限制
                    break;
            }
        }
        outPacket.write(chr != null ? chr.getHairColorBase() : -1);
        outPacket.write(chr != null ? chr.getHairColorMixed() : 0);
        outPacket.write(chr != null ? chr.getHairColorProb() : 0);


        outPacket.write(0);
        if (mask == 0 || !enableActions) { //unknown
            outPacket.write(0);
        }
        outPacket.write(0);

        return outPacket;

    }

    public static OutPacket inventoryOperation(boolean exclRequestSent, boolean notRemoveAddInfo,
                                               InventoryOperation type,
                                               short oldPos, short newPos,
                                               int bagPos, Item item) {
        OutPacket outPacket = new OutPacket();
        InventoryType invType = item.getInvType();
        if ((oldPos > 0 && newPos < 0 && invType == EQUIPPED) ||
                (invType == EQUIPPED && oldPos < 0)) {
            invType = InventoryType.EQUIP;
        }
        outPacket.writeShort(SendOpcode.INVENTORY_OPERATION.getValue());
        outPacket.writeBool(exclRequestSent);
        outPacket.write(1); // size
        outPacket.writeBool(notRemoveAddInfo);
        byte equipMove = 0;
        boolean addMovementInfo = false;
        outPacket.write(type.getVal());
        outPacket.write(invType.getVal());
        outPacket.writeShort(oldPos);
        switch (type) {
            case ADD:
                PacketHelper.addItemInfo(outPacket, item);
                addMovementInfo = true;
                break;
            case UPDATE_QUANTITY:
                outPacket.writeShort(item.getQuantity());
                break;
            case MOVE:
                outPacket.writeShort(newPos);
                if (invType == InventoryType.EQUIP && (oldPos < 0 || newPos < 0)) {
                    addMovementInfo = true;
                    if (oldPos < 0) {
                        equipMove = 2;
                    } else {
                        equipMove = 1;
                    }
                }
                break;
            case REMOVE:
                if (invType == InventoryType.EQUIP && (oldPos < 0 || newPos < 0)) {
                    addMovementInfo = true;
                }
                break;
            case ITEM_EXP:
                outPacket.writeLong(((Equip) item).getExp());
                break;
            case UPDATE_BAG_POS:
                outPacket.writeInt(bagPos);
                break;
            case UPDATE_BAG_QUANTITY:
                outPacket.writeShort(newPos);
                break;
            case UNK_1:
            case UNK_3:
                break;
            case UNK_2:
                outPacket.writeShort(bagPos); // ?
                break;
            case UPDATE_ITEM_INFO:
                PacketHelper.addItemInfo(outPacket, item);
                break;
        }
        if (addMovementInfo) {
            outPacket.write(equipMove);
        }
        return outPacket;
    }

    public static OutPacket move(MapleCharacter player, MovementInfo movementInfo) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.REMOTE_MOVE.getValue());
        outPacket.writeInt(player.getId());
        movementInfo.encode(outPacket);
        return outPacket;
    }

    public static OutPacket lockUI(boolean enable) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.LOCK_UI.getValue());
        outPacket.writeBool(enable);
        return outPacket;
    }

    public static OutPacket disableUI(boolean disable) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.DISABLE_UI.getValue());
        outPacket.writeBool(disable);
        outPacket.writeBool(disable);
        if (disable) {
            outPacket.writeBool(false);
            outPacket.writeBool(false);
        }
        return outPacket;
    }

    public static OutPacket updateVoucher(MapleCharacter chr) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.UPDATE_VOUCHER.getValue());
        outPacket.writeInt(chr.getId());
        outPacket.writeInt(chr.getAccount().getVoucher());
        return outPacket;
    }

    public static OutPacket cancelChair(int charId, short id) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CANCEL_CHAIR.getValue());
        outPacket.writeInt(charId);
        if (id != -1) {
            outPacket.write(1);
            outPacket.writeShort(id);
        } else {
            outPacket.write(0);
        }
        return outPacket;
    }

    public static OutPacket hiddenEffectEquips(MapleCharacter player) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.HIDDEN_EFFECT_EQUIP.getValue());
        outPacket.writeInt(player.getId());
        List<Item> items = player.getEquippedInventory().getItems();
        List<Item> equips = items.stream().filter(item -> !((Equip) item).isShowEffect()).collect(Collectors.toList());
        outPacket.writeInt(equips.size());
        for (Item equip : equips) {
            outPacket.writeInt(equip.getPos());
        }
        outPacket.writeBool(false);
        return outPacket;
    }

    public static OutPacket sendRebirthConfirm(boolean onDeadRevive, boolean onDeadProtectForBuff, boolean onDeadProtectBuffMaplePoint,
                                               boolean onDeadProtectExpMaplePoint, boolean anniversary, int reviveType, int protectType) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.DEATH_CONFIRM.getValue());
        int reviveMask = 0;
        if (onDeadRevive) {
            reviveMask |= 0x1;
        }
        if (onDeadProtectForBuff) {
            reviveMask |= 0x2;
        }
        if (onDeadProtectBuffMaplePoint) {
            reviveMask |= 0x4;
        }
        if (onDeadProtectExpMaplePoint) {
            reviveMask |= 0x8;
        }
        outPacket.writeInt(reviveMask);
        outPacket.writeBool(anniversary);
        outPacket.writeInt(reviveType);
        if (onDeadProtectForBuff || onDeadProtectExpMaplePoint) {
            outPacket.writeInt(protectType);
        }
        return outPacket;
    }

    public static OutPacket changeSkillRecordResult(Skill skill) {
        List<Skill> skills = new ArrayList<>();
        skills.add(skill);
        return changeSkillRecordResult(skills, true, false, false, false);
    }

    public static OutPacket changeSkillRecordResult(List<Skill> skills, boolean exclRequestSent, boolean showResult
            , boolean removeLinkSkill, boolean sn) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHANGE_SKILL_RESULT.getValue());
        outPacket.writeBool(exclRequestSent);
        outPacket.writeBool(showResult);
        outPacket.writeBool(removeLinkSkill);
        outPacket.writeShort(skills.size());
        for (Skill skill : skills) {
            outPacket.writeInt(skill.getSkillId());
            outPacket.writeInt(skill.getCurrentLevel());
            outPacket.writeInt(skill.getMasterLevel());
            outPacket.writeLong(MAX_TIME);
        }
        outPacket.writeBool(sn);
        return outPacket;
    }

    public static OutPacket skillCoolTimes(Map<Integer, Integer> cooltimes) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SKILL_COOLTIME.getValue());
        outPacket.writeInt(cooltimes.size());
        cooltimes.forEach((id, cooltime) -> {
            outPacket.writeInt(id);
            outPacket.writeInt(cooltime);
        });
        return outPacket;
    }

    public static OutPacket skillCoolDown(int skillId) {
        HashMap<Integer, Integer> skills = new HashMap<>();
        skills.put(skillId, 0);
        return skillCoolTimes(skills);
    }

    public static OutPacket removeBuff(TemporaryStatManager tsm, boolean demount) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.REMOVE_BUFF.getValue());
        outPacket.writeInt(0);
        outPacket.write(1);
        outPacket.write(1);
        for (int i : tsm.getRemovedMask()) {
            outPacket.writeInt(i);
        }
        tsm.getRemovedStats().forEach((characterTemporaryStat, options) -> {
            outPacket.writeInt(0);
        });
        if (demount) {
            outPacket.writeBool(true);
        }
        return outPacket;
    }

    public static OutPacket giveBuff(TemporaryStatManager tsm) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.GIVE_BUFF.getValue());
        outPacket.writeZeroBytes(8);
        tsm.encodeForLocal(outPacket);
        return outPacket;
    }


    public static OutPacket effect(Effect effect) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.EFFECT.getValue());
        effect.encode(outPacket);
        return outPacket;
    }

    public static OutPacket incMoneyMessage(int amount) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        outPacket.write(MessageType.INC_MONEY_MESSAGE.getVal());
        outPacket.writeInt(amount);
        outPacket.writeInt(amount > 0 ? 1 : -1);
        return outPacket;
    }

    public static OutPacket message(MessageType mt, int i, String string, byte type) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        outPacket.write(mt.getVal());
        switch (mt) {
            case CASH_ITEM_EXPIRE_MESSAGE:
            case INC_POP_MESSAGE:
            case INC_GP_MESSAGE:
            case GIVE_BUFF_MESSAGE:
                outPacket.writeInt(i);
                break;
            case INC_COMMITMENT_MESSAGE:
                outPacket.writeInt(i);
                outPacket.write(i < 0 ? 1 : i == 0 ? 2 : 0); // gained = 0, lost = 1, cap = 2
                break;
            case SYSTEM_MESSAGE:
                outPacket.writeMapleAsciiString(string);
                break;
            case QUEST_RECORD_EX_MESSAGE:
            case WORLD_SHARE_RECORD_MESSAGE:
            case COLLECTION_RECORD_MESSAGE:
                outPacket.writeInt(i);
                outPacket.writeMapleAsciiString(string);
                break;
            case INC_HARDCORE_EXP_MESSAGE:
                outPacket.writeInt(i); //You have gained x EXP
                outPacket.writeInt(i); //Field Bonus Exp
                break;
            case BARRIER_EFFECT_IGNORE_MESSAGE:
                outPacket.write(type); //protection/shield scroll pop-up Message
                break;
        }
        return outPacket;
    }

    public static OutPacket stylishKillMessage(long exp, int count) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        outPacket.write(MessageType.STYLISH_KILL_MESSAGE.getVal());
        outPacket.write(0);
        outPacket.writeLong(exp);
        outPacket.writeInt(0); //unk
        outPacket.writeInt(count);
        outPacket.writeInt(1); //unk
        return outPacket;
    }

    public static OutPacket comboKillMessage(int objId, int combo) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        outPacket.write(MessageType.STYLISH_KILL_MESSAGE.getVal());
        outPacket.write(1);
        outPacket.writeInt(combo);
        outPacket.writeInt(objId);
        outPacket.writeInt(0); //unk
        outPacket.writeInt(1); //unk
        return outPacket;
    }

    public static OutPacket inventoryRefresh(boolean excl) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.INVENTORY_OPERATION.getValue());
        outPacket.writeBool(excl);
        outPacket.writeShort(0);
        return outPacket;
    }

    public static OutPacket scrollUpgradeDisplay(boolean feverTime, List<ScrollUpgradeInfo> scrolls) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.EQUIP_ENCHANT.getValue());
        outPacket.write(EquipmentEnchantType.ScrollUpgradeDisplay.getVal());
        outPacket.writeBool(feverTime);
        outPacket.write(scrolls.size());
        scrolls.forEach(scrollUpgradeInfo -> scrollUpgradeInfo.encode(outPacket));
        return outPacket;
    }

    public static OutPacket showScrollUpgradeResult(boolean feverTime, int result, String desc, Equip prevEquip, Equip equip) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.EQUIP_ENCHANT.getValue());
        outPacket.write(EquipmentEnchantType.ShowScrollUpgradeResult.getVal());
        outPacket.writeBool(feverTime);
        outPacket.writeInt(result);
        outPacket.writeMapleAsciiString(desc);
        PacketHelper.addItemInfo(outPacket, prevEquip);
        PacketHelper.addItemInfo(outPacket, equip);
        return outPacket;
    }

    public static OutPacket initSkillMacro() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.INIT_SKILL_MACRO.getValue());
        outPacket.write(0); //size
        return outPacket;
    }

    public static OutPacket characterPotentialSet(CharacterPotential cp) {
        return characterPotentialSet(true, true, cp.getKey(), cp.getSkillID(), cp.getSlv(), cp.getGrade(), true);
    }

    public static OutPacket characterPotentialSet(boolean exclRequest, boolean changed, short pos, int skillID,
                                                  short skillLevel, short grade, boolean updatePassive) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHARACTER_POTENTIAL_SET.getValue());
        outPacket.writeBool(exclRequest);
        outPacket.writeBool(changed);
        if (changed) {
            outPacket.writeShort(pos);
            outPacket.writeInt(skillID);
            outPacket.writeShort(skillLevel);
            outPacket.writeShort(grade);
            outPacket.writeBool(updatePassive);
        }
        return outPacket;
    }

    public static OutPacket noticeMsg(String msg) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.NOTICE_MSG.getValue());
        outPacket.writeMapleAsciiString(msg);
        outPacket.write(1);
        return outPacket;
    }

    public static OutPacket setDamageSkin(MapleCharacter chr) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SET_DAMAGE_SKIN.getValue());
        outPacket.writeInt(chr.getId());
        outPacket.writeInt(chr.getDamageSkin().getDamageSkinID());
        outPacket.writeInt(0); //unk
        return outPacket;

    }

    public static OutPacket damageSkinSaveResult(DamageSkinType req, DamageSkinType res, MapleCharacter chr) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.DAMAGE_SKIN_SAVE_RESULT.getValue());
        outPacket.write(req.getVal());
        if (req.getVal() <= 2) {
            outPacket.write(res.getVal());
            if (res == DamageSkinType.DamageSkinSave_Success) {
                chr.encodeDamageSkins(outPacket);
            }
        } else if (req == DamageSkinType.DamageSkinSaveReq_SendInfo) {
            chr.encodeDamageSkins(outPacket);
        }
        return outPacket;
    }

    public static OutPacket updateHonerPoint(int honerPoint) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHARACTER_HONOR_POINT.getValue());
        outPacket.writeInt(honerPoint);
        return outPacket;
    }

    public static OutPacket showItemUpgradeEffect(int charId, boolean success, boolean enchantDlg, int uItemId, int eItemId, boolean boom) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.SHOW_ITEM_UPGRADE_EFFECT.getValue());
        outPacket.writeInt(charId);
        outPacket.write(boom ? 2 : success ? 1 : 0);
        outPacket.writeBool(enchantDlg);
        outPacket.writeInt(uItemId);
        outPacket.writeInt(eItemId);
        outPacket.write(0);
        return outPacket;
    }

    public static OutPacket keymapInit(MapleCharacter character) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.KEYMAP_INIT.getValue());
        character.getKeyMap().encode(outPacket);
        return outPacket;
    }

    public static OutPacket quickslotInit(MapleCharacter player) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.QUICKSLOT_INIT.getValue());
        boolean edited = player.getQuickslots() != null && player.getQuickslots().size() == 32;
        outPacket.writeBool(edited);
        if (player.getQuickslots() != null) {
            for (Integer key : player.getQuickslots()) {
                outPacket.writeInt(key);
            }
        }
        return outPacket;
    }

    public static OutPacket openWorldMap() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.OPEN_WORLDMAP.getValue());
        outPacket.writeInt(0);
        return outPacket;
    }

    public static OutPacket encodeOpcodes(MapleClient client) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.OPCODE_TABLE.getValue());
        outPacket.writeInt(4); //block size
        List<Integer> used = new ArrayList<>();
        StringBuilder sOpcodes = new StringBuilder();
        for (int i = RecvOpcode.BEGIN.getValue(); i < RecvOpcode.END.getValue(); i++) {
            int opcode = Randomizer.rand(RecvOpcode.BEGIN.getValue(), 9999);
            while (used.contains(opcode)) {
                opcode = Randomizer.rand(RecvOpcode.BEGIN.getValue(), 9999);
            }
            String sOpcode = String.format("%04d", opcode);
            if (!used.contains(opcode)) {
                client.mEncryptedOpcode.put(opcode, i);
                used.add(opcode);
                sOpcodes.append(sOpcode);
            }
        }
        used.clear();

        TripleDESCipher tripleDESCipher = new TripleDESCipher(DESKEY);
        try {
            byte[] buffer = new byte[Short.MAX_VALUE + 1];
            byte[] encrypt = tripleDESCipher.Encrypt(sOpcodes.toString().getBytes());
            System.arraycopy(encrypt, 0, buffer, 0, encrypt.length);
            for (int i = encrypt.length; i < buffer.length; i++) {
                buffer[i] = 0;
            }
            outPacket.writeInt(buffer.length);
            outPacket.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            client.close();
        }
        return outPacket;
    }

    public static OutPacket updateEventNameTag() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CANCEL_TITLE_EFFECT.getValue());
        for (int i = 0; i < 5; i++) {
            outPacket.writeShort(0);
            outPacket.write(-1);
        }
        return outPacket;
    }
}

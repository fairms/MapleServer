package im.cave.ms.network.packet;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.MapleStat;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.InventoryOperation;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.items.ScrollUpgradeInfo;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.enums.DamageSkinType;
import im.cave.ms.enums.EquipmentEnchantType;
import im.cave.ms.enums.InventoryOperationType;
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

import static im.cave.ms.constants.ServerConstants.DESKEY;
import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.enums.InventoryType.EQUIPPED;

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
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.UPDATE_STATS.getValue());
        out.write(enableActions ? 1 : 0);
        out.write(0); //unk
        long mask = 0;
        for (MapleStat stat : stats.keySet()) {
            mask |= stat.getValue();
        }
        out.writeLong(mask);
        Comparator<MapleStat> comparator = Comparator.comparingLong(MapleStat::getValue);
        TreeMap<MapleStat, Long> sortedStats = new TreeMap<>(comparator);
        sortedStats.putAll(stats);
        for (Map.Entry<MapleStat, Long> entry : sortedStats.entrySet()) {
            MapleStat stat = entry.getKey();
            long value = entry.getValue();
            switch (stat) {
                case SKIN:
                    out.write((byte) value);
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
                    out.writeInt((int) value);
                    break;
                case STR:
                case DEX:
                case INT:
                case LUK:
                case AVAILABLEAP:
                case FATIGUE:
                    out.writeShort((int) value);
                    break;
                case AVAILABLESP:
                    PacketHelper.addCharSP(out, chr);
                    break;
                case EXP:
                case MESO:
                    out.writeLong(value);
                    break;
                case TODAYS_TRAITS:
                    out.writeZeroBytes(21); //限制
                    break;
            }
        }
        out.write(chr != null ? chr.getCharLook().getHairColorBase() : -1);
        out.write(chr != null ? chr.getCharLook().getHairColorMixed() : 0);
        out.write(chr != null ? chr.getCharLook().getHairColorProb() : 0);
        out.write(0);
        out.write(0);

        return out;

    }

    public static OutPacket inventoryOperation(boolean exclRequestSent,
                                               InventoryOperationType type,
                                               short oldPos, short newPos,
                                               int bagPos, Item item) {
        InventoryOperation inventoryOperation = new InventoryOperation(type);
        inventoryOperation.setItem(item);
        inventoryOperation.setBagPos(bagPos);
        inventoryOperation.setNewPos(newPos);
        inventoryOperation.setOldPos(oldPos);
        List<InventoryOperation> operations = Collections.singletonList(inventoryOperation);
        return inventoryOperation(exclRequestSent, operations);
    }

    public static OutPacket move(MapleCharacter player, MovementInfo movementInfo) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.REMOTE_MOVE.getValue());
        out.writeInt(player.getId());
        movementInfo.encode(out);
        return out;
    }

    public static OutPacket setInGameDirectionMode(boolean enable) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SET_STAND_ALONE_MODE.getValue());
        out.writeBool(enable);
        return out;
    }

    public static OutPacket disableUI(boolean disable) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
        out.writeBool(disable);
        out.writeBool(disable);
        if (disable) {
            out.writeBool(false);
            out.writeBool(false);
        }
        return out;
    }

    public static OutPacket updateVoucher(MapleCharacter chr) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.UPDATE_VOUCHER.getValue());
        out.writeInt(chr.getId());
        out.writeInt(chr.getAccount().getVoucher());
        return out;
    }

    public static OutPacket sitResult(int charId, short id) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SIT_RESULT.getValue());
        out.writeInt(charId);
        if (id != -1) {
            out.write(1);
            out.writeShort(id);
        } else {
            out.write(0);
        }
        return out;
    }

    public static OutPacket userSit() {
        OutPacket out = new OutPacket();
        out.writeInt(SendOpcode.USER_SIT.getValue());
        out.writeInt(0);
        return out;
    }

    public static OutPacket sendRebirthConfirm(boolean onDeadRevive, boolean onDeadProtectForBuff, boolean onDeadProtectBuffMaplePoint,
                                               boolean onDeadProtectExpMaplePoint, boolean anniversary, int reviveType, int protectType) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.OPEN_DEAD_UI.getValue());
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
        out.writeInt(reviveMask);
        out.writeBool(anniversary);
        out.writeInt(reviveType);
        if (onDeadProtectForBuff || onDeadProtectExpMaplePoint) {
            out.writeInt(protectType);
        }
        return out;
    }

    public static OutPacket changeSkillRecordResult(Skill skill) {
        List<Skill> skills = new ArrayList<>();
        skills.add(skill);
        return changeSkillRecordResult(skills, true, false, false, false);
    }

    public static OutPacket changeSkillRecordResult(List<Skill> skills, boolean exclRequestSent, boolean showResult
            , boolean removeLinkSkill, boolean sn) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CHANGE_SKILL_RESULT.getValue());
        out.writeBool(exclRequestSent);
        out.writeBool(showResult);
        out.writeBool(removeLinkSkill);
        out.writeShort(skills.size());
        for (Skill skill : skills) {
            out.writeInt(skill.getSkillId());
            out.writeInt(skill.getCurrentLevel());
            out.writeInt(skill.getMasterLevel());
            out.writeLong(MAX_TIME);
        }
        out.writeBool(sn);
        return out;
    }

    public static OutPacket skillCoolTimeSet(Map<Integer, Integer> cooltimes) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SKILL_COOLTIME_SET.getValue());
        out.writeInt(cooltimes.size());
        cooltimes.forEach((id, cooltime) -> {
            out.writeInt(id);
            out.writeInt(cooltime);
        });
        return out;
    }

    public static OutPacket skillCoolDown(int skillId) {
        HashMap<Integer, Integer> skills = new HashMap<>();
        skills.put(skillId, 0);
        return skillCoolTimeSet(skills);
    }

    public static OutPacket removeBuff(TemporaryStatManager tsm, boolean demount) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.REMOVE_BUFF.getValue());
        out.writeInt(0);
        out.write(1);
        out.write(1);
        for (int i : tsm.getRemovedMask()) {
            out.writeInt(i);
        }
        tsm.getRemovedStats().forEach((characterTemporaryStat, options) -> out.writeInt(0));
        if (demount) {
            out.writeBool(true);
        }
        return out;
    }

    public static OutPacket giveBuff(TemporaryStatManager tsm) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.GIVE_BUFF.getValue());
        out.writeZeroBytes(8);
        tsm.encodeForLocal(out);
        return out;
    }

    public static OutPacket effect(Effect effect) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.EFFECT.getValue());
        effect.encode(out);
        return out;
    }

    public static OutPacket incMoneyMessage(int amount) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        out.write(MessageType.INC_MONEY_MESSAGE.getVal());
        out.writeInt(amount);
        out.writeInt(-1);
        out.writeInt(amount > 0 ? 0 : -1);
        return out;
    }

    public static OutPacket message(MessageType mt, int i, String string, byte type) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        out.write(mt.getVal());
        switch (mt) {
            case CASH_ITEM_EXPIRE_MESSAGE:
            case INC_POP_MESSAGE:
            case INC_GP_MESSAGE:
            case GIVE_BUFF_MESSAGE:
                out.writeInt(i);
                break;
            case INC_COMMITMENT_MESSAGE:
                out.writeInt(i);
                out.write(i < 0 ? 1 : i == 0 ? 2 : 0); // gained = 0, lost = 1, cap = 2
                break;
            case SYSTEM_MESSAGE:
                out.writeMapleAsciiString(string);
                break;
            case QUEST_RECORD_EX_MESSAGE:
            case WORLD_SHARE_RECORD_MESSAGE:
            case COLLECTION_RECORD_MESSAGE:
                out.writeInt(i);
                out.writeMapleAsciiString(string);
                break;
            case INC_HARDCORE_EXP_MESSAGE:
                out.writeInt(i); //You have gained x EXP
                out.writeInt(i); //Field Bonus Exp
                break;
            case BARRIER_EFFECT_IGNORE_MESSAGE:
                out.write(type); //protection/shield scroll pop-up Message
                break;
        }
        return out;
    }

    public static OutPacket stylishKillMessage(long exp, int count) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        out.write(MessageType.STYLISH_KILL_MESSAGE.getVal());
        out.write(0);
        out.writeLong(exp);
        out.writeInt(0); //unk
        out.writeInt(count);
        out.writeInt(1); //unk
        return out;
    }

    public static OutPacket comboKillMessage(int objId, int combo) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
        out.write(MessageType.STYLISH_KILL_MESSAGE.getVal());
        out.write(1);
        out.writeInt(combo);
        out.writeInt(objId);
        out.writeInt(0); //unk
        out.writeInt(1); //unk
        return out;
    }

    public static OutPacket inventoryRefresh(boolean excl) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.INVENTORY_OPERATION.getValue());
        out.writeBool(excl);
        out.writeShort(0);
        return out;
    }

    public static OutPacket scrollUpgradeDisplay(boolean feverTime, List<ScrollUpgradeInfo> scrolls) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.EQUIP_ENCHANT.getValue());
        out.write(EquipmentEnchantType.ScrollUpgradeDisplay.getVal());
        out.writeBool(feverTime);
        out.write(scrolls.size());
        scrolls.forEach(scrollUpgradeInfo -> scrollUpgradeInfo.encode(out));
        return out;
    }

    public static OutPacket showScrollUpgradeResult(boolean feverTime, int result, String desc, Equip prevEquip, Equip equip) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.EQUIP_ENCHANT.getValue());
        out.write(EquipmentEnchantType.ShowScrollUpgradeResult.getVal());
        out.writeBool(feverTime);
        out.writeInt(result);
        out.writeMapleAsciiString(desc);
        PacketHelper.addItemInfo(out, prevEquip);
        PacketHelper.addItemInfo(out, equip);
        return out;
    }

    public static OutPacket macroSysDataInit(MapleCharacter chr) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MACRO_SYS_DATA_INIT.getValue());
        out.write(0); //size
        return out;
    }

    public static OutPacket characterPotentialSet(CharacterPotential cp) {
        return characterPotentialSet(true, true, cp.getKey(), cp.getSkillID(), cp.getSlv(), cp.getGrade(), true);
    }

    public static OutPacket characterPotentialSet(boolean exclRequest, boolean changed, short pos, int skillID,
                                                  short skillLevel, short grade, boolean updatePassive) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CHARACTER_POTENTIAL_SET.getValue());
        out.writeBool(exclRequest);
        out.writeBool(changed);
        if (changed) {
            out.writeShort(pos);
            out.writeInt(skillID);
            out.writeShort(skillLevel);
            out.writeShort(grade);
            out.writeBool(updatePassive);
        }
        return out;
    }

    public static OutPacket noticeMsg(String msg) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.NOTICE_MSG.getValue());
        out.writeMapleAsciiString(msg);
        out.write(1);
        return out;
    }


    public static OutPacket damageSkinSaveResult(DamageSkinType req, DamageSkinType res, MapleCharacter chr) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.DAMAGE_SKIN_SAVE_RESULT.getValue());
        out.write(req.getVal());
        if (req.getVal() <= 2) {
            out.write(res.getVal());
            if (res == DamageSkinType.DamageSkinSave_Success) {
                chr.encodeDamageSkins(out);
            }
        } else if (req == DamageSkinType.DamageSkinSaveReq_SendInfo) {
            chr.encodeDamageSkins(out);
        }
        return out;
    }

    public static OutPacket updateHonerPoint(int honerPoint) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CHARACTER_HONOR_POINT.getValue());
        out.writeInt(honerPoint);
        return out;
    }


    public static OutPacket keymapInit(MapleCharacter character) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.KEYMAP_INIT.getValue());
        character.getKeyMap().encode(out);
        return out;
    }

    public static OutPacket quickslotInit(MapleCharacter player) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.QUICKSLOT_INIT.getValue());
        boolean edited = player.getQuickslots() != null && player.getQuickslots().size() == 32;
        out.writeBool(edited);
        if (player.getQuickslots() != null) {
            for (Integer key : player.getQuickslots()) {
                out.writeInt(key);
            }
        }
        return out;
    }

    public static OutPacket openWorldMap() {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.OPEN_WORLDMAP.getValue());
        out.writeInt(0);
        return out;
    }

    public static OutPacket initOpCodeEncryption(MapleClient client) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.INIT_OPCODE_ENCRYPTION.getValue());
        out.writeInt(4); //block size
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
            out.writeInt(buffer.length);
            out.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            client.close();
        }
        return out;
    }

    public static OutPacket updateEventNameTag() {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CANCEL_TITLE_EFFECT.getValue());
        for (int i = 0; i < 5; i++) {
            out.writeShort(0);
            out.write(-1);
        }
        return out;
    }

    public static OutPacket addFameResponse(MapleCharacter other, int mode, int newFame) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.FAME_RESPONSE.getValue());
        out.write(0);
        out.writeMapleAsciiString(other.getName());
        out.write(mode);
        out.writeInt(newFame);
        return out;
    }

    public static OutPacket receiveFame(int mode, String charName) {

        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.FAME_RESPONSE.getValue());
        out.write(5);
        out.writeMapleAsciiString(charName);
        out.write(mode);
        return out;
    }

    public static OutPacket inventoryOperation(boolean exclRequestSent, List<InventoryOperation> operations) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.INVENTORY_OPERATION.getValue());
        out.writeBool(exclRequestSent);
        out.writeShort(operations.size());
        byte equipMove = 0;
        boolean addMovementInfo = false;
        for (InventoryOperation operation : operations) {
            Item item = operation.getItem();
            short newPos = operation.getNewPos();
            short oldPos = operation.getOldPos();
            int bagPos = operation.getBagPos();
            InventoryOperationType type = operation.getType();
            InventoryType invType = item.getInvType();
            if ((oldPos > 0 && newPos < 0 && invType == EQUIPPED) ||
                    (invType == EQUIPPED && oldPos < 0)) {
                invType = InventoryType.EQUIP;
            }
            out.write(type.getVal());
            out.write(invType.getVal());
            out.writeShort(oldPos);
            switch (type) {
                case ADD:
                    item.encode(out);
                    addMovementInfo = true;
                    break;
                case UPDATE_QUANTITY:
                    out.writeShort(item.getQuantity());
                    break;
                case MOVE:
                    out.writeShort(newPos);
                    if (invType == InventoryType.EQUIP && (oldPos < 0 || newPos < 0)) {
                        addMovementInfo = true;
                        if (oldPos > 0) {
                            equipMove += 2;
                        } else {
                            equipMove += 1;
                        }
                    }
                    break;
                case REMOVE:
                    if (invType == InventoryType.EQUIP && (oldPos < 0 || newPos < 0)) {
                        addMovementInfo = true;
                    }
                    break;
                case ITEM_EXP:
                    out.writeLong(((Equip) item).getExp());
                    break;
                case UPDATE_BAG_POS:
                    out.writeInt(bagPos);
                    break;
                case UPDATE_BAG_QUANTITY:
                    out.writeShort(newPos);
                    break;
                case UNK_1:
                case UNK_3:
                    break;
                case UNK_2:
                    out.writeShort(bagPos);
                    break;
                case UPDATE_ITEM_INFO:
                    item.encode(out);
                    break;
            }
        }
        if (addMovementInfo) {
            out.write(equipMove);
        }
        return out;
    }

    public static OutPacket gatherItemResult(byte val) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.GATHER_ITEM_RESULT.getValue());
        out.write(1);
        out.write(val);
        return out;
    }

    public static OutPacket sortItemResult(byte val) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SORT_ITEM_RESULT.getValue());
        out.write(1);
        out.write(val);
        return out;
    }


    public static OutPacket finalAttack(MapleCharacter chr, int weapon, int skillId, int finalSkillId) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.FINAL_ATTACK.getValue());
        out.writeInt(chr.getTick());
        out.writeInt(finalSkillId > 0);
        out.writeInt(skillId);
        out.writeInt(finalSkillId);
        out.writeInt(weapon);
        return out;
    }

    public static OutPacket invExpandResult(int i, int point, boolean cash) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.SLOT_EXPAND_RESULT.getValue());
        out.writeInt(i);
        out.writeInt(0);
        out.writeInt(point);
        out.write(2);
        out.writeShort(cash ? 1 : 0);
        return out;
    }

    public static OutPacket characterModified(MapleCharacter player) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CHARACTER_MODIFIED.getValue());
        out.write(1);
//        PacketHelper.addCharInfo(out, player, CharMask.Character);
        return out;
    }

    public static OutPacket openLimitBreakUI(MapleCharacter player, boolean success, Item item, long incALB, Equip equip) {
        OutPacket out = new OutPacket(SendOpcode.LIMIT_BREAK_UI);
        out.writeInt(player.getId());
        out.writeBool(success);
        out.writeInt(item.getItemId());
        out.writeInt(item.getPos());
        out.writeInt(equip.getPos());
        out.writeInt(1000);
        out.writeLong(equip.getLimitBreak());
        out.writeLong(incALB);
        PacketHelper.addItemInfo(out, equip);
        return out;
    }

    public static OutPacket remainingMapTransferCoupon(MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.REMAINING_MAP_TRANSFER_COUPON);
        out.writeInt(1000);
        out.writeInt(1000);
        return out;
    }
}

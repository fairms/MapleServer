package im.cave.ms.connection.packet;

import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.Macro;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.Stat;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.InventoryOperation;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.items.ScrollUpgradeInfo;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.Portal;
import im.cave.ms.client.field.movement.MovementInfo;
import im.cave.ms.connection.crypto.TripleDESCipher;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.netty.Packet;
import im.cave.ms.connection.packet.opcode.RecvOpcode;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.connection.packet.result.FameResult;
import im.cave.ms.connection.packet.result.InGameDirectionEvent;
import im.cave.ms.enums.*;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Randomizer;

import java.util.*;

import static im.cave.ms.constants.ServerConstants.DES_KEY;
import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.enums.InventoryType.EQUIPPED;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet.opcode
 * @date 11/29 22:25
 */
public class UserPacket {
    public static final Map<Stat, Long> EMPTY_STATUS = Collections.emptyMap();

    public static OutPacket enableActions() {
        return statChanged(EMPTY_STATUS, true, null);
    }

    public static OutPacket statChanged(Map<Stat, Long> stats, MapleCharacter chr) {
        return statChanged(stats, false, chr);
    }

    public static OutPacket statChanged(Map<Stat, Long> stats, boolean enableActions, MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.STAT_CHANGED);
        out.write(enableActions ? 1 : 0);
        out.write(0); //unk
        long mask = 0;
        for (Stat stat : stats.keySet()) {
            mask |= stat.getValue();
        }
        out.writeLong(mask);
        Comparator<Stat> comparator = Comparator.comparingLong(Stat::getValue);
        TreeMap<Stat, Long> sortedStats = new TreeMap<>(comparator);
        sortedStats.putAll(stats);
        for (Map.Entry<Stat, Long> entry : sortedStats.entrySet()) {
            Stat stat = entry.getKey();
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
                    chr.encodeRemainingSp(out);
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
        OutPacket out = new OutPacket(SendOpcode.REMOTE_MOVE);

        out.writeInt(player.getId());
        movementInfo.encode(out);

        return out;
    }

    public static OutPacket setStandAloneMode(boolean enable) {
        OutPacket out = new OutPacket(SendOpcode.SET_STAND_ALONE_MODE);

        out.writeBool(enable);

        return out;
    }

    public static OutPacket setInGameDirectionMode(boolean lockUI, boolean blackFrame, boolean forceMouseOver, boolean showUI) {
        OutPacket out = new OutPacket(SendOpcode.SET_IN_GAME_DIRECTION_MODE);

        out.writeBool(lockUI);
        out.writeBool(blackFrame);
        if (lockUI) {
            out.writeBool(forceMouseOver);
            out.writeBool(showUI);
        }

        return out;
    }

    public static OutPacket updateMaplePoint(MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.UPDATE_MAPLE_POINT);

        out.writeInt(chr.getId());
        out.writeInt(chr.getAccount().getPoint());

        return out;
    }

    /*
        角色乘坐地图固定椅子或者起身离开移动椅子时
     */
    public static OutPacket sitResult(int charId, short id) {
        OutPacket out = new OutPacket(SendOpcode.SIT_RESULT);

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
        OutPacket out = new OutPacket(SendOpcode.USER_SIT);

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
        OutPacket out = new OutPacket(SendOpcode.CHANGE_SKILL_RECORD_RESULT);

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

    public static OutPacket setSkillCoolTime(int skillId, int cdMS) {
        Map<Integer, Integer> cds = new HashMap<>();
        cds.put(skillId, cdMS);
        return setSkillCoolTime(cds);
    }

    public static OutPacket setSkillCoolTime(MapleCharacter chr) {
        Map<Integer, Long> skillCooltimes = chr.getSkillCooltimes();

        if (skillCooltimes == null || skillCooltimes.size() == 0) {
            return null;
        }

        long now = System.currentTimeMillis();
        HashMap<Integer, Integer> cds = new HashMap<>();

        skillCooltimes.forEach((skillId, time) ->
                cds.put(skillId, Math.min((int) (time - now), 0))
        );

        return setSkillCoolTime(cds);
    }

    public static OutPacket setSkillCoolTime(Map<Integer, Integer> cooltimes) {
        OutPacket out = new OutPacket(SendOpcode.SKILL_COOLTIME_SET);

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
        return setSkillCoolTime(skills);
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
        OutPacket out = new OutPacket(SendOpcode.EFFECT);
        effect.encode(out);
        return out;
    }

    public static OutPacket teleport(int type, Position position, Portal portal) {
        OutPacket out = new OutPacket(SendOpcode.TELEPORT);
        out.writeBool(false);// excl request
        out.write(type);
        /*
        TODO: import the enum
        enum USER_TELEPORT_CALLING_TYPE
        {
          TELEPORT_CALLING_TYPE_DEFAULT = 0x0,
          TELEPORT_CALLING_TYPE_GUILD = 0x1,
          TELEPORT_CALLING_TYPE_FLAMEWIZARD_FLAREBLINK = 0x2,
          TELEPORT_CALLING_TYPE_BYSCRIPT = 0x3,
        };
         */
        switch (type) {
            case 0x00:
                out.writeInt(portal.getId());
                out.write(0);
                break;
            case 0xCD:
                out.writeInt(1);//charId
                out.writePosition(position);
                break;

        }

        return out;
    }

    public static OutPacket incMoneyMessage(int amount) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MESSAGE.getValue());
        out.write(MessageType.INC_MONEY_MESSAGE.getVal());
        out.writeInt(amount);
        out.writeInt(-1);
        out.writeInt(amount > 0 ? 0 : -1);
        return out;
    }

    public static OutPacket message(MessageType mt, int i, String string, byte type) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.MESSAGE.getValue());
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
        out.writeShort(SendOpcode.MESSAGE.getValue());
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
        out.writeShort(SendOpcode.MESSAGE.getValue());
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
        OutPacket out = new OutPacket(SendOpcode.EQUIPMENT_ENCHANT);

        out.write(EquipmentEnchantType.ScrollUpgradeDisplay.getVal());
        out.writeBool(feverTime);
        out.write(scrolls.size());
        scrolls.forEach(scrollUpgradeInfo -> scrollUpgradeInfo.encode(out));

        return out;
    }

    public static OutPacket showScrollUpgradeResult(boolean feverTime, int result, String desc, Equip prevEquip, Equip equip) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.EQUIPMENT_ENCHANT.getValue());
        out.write(EquipmentEnchantType.ShowScrollUpgradeResult.getVal());
        out.writeBool(feverTime);
        out.writeInt(result);
        out.writeMapleAsciiString(desc);
        prevEquip.encode(out);
        equip.encode(out);
        return out;
    }

    public static OutPacket macroSysDataInit(MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.MACRO_SYS_DATA_INIT);

        out.write(chr.getMacros().size());
        for (Macro macro : chr.getMacros()) {
            macro.encode(out);
        }

        return out;
    }

    public static OutPacket characterPotentialSet(CharacterPotential cp) {
        return characterPotentialSet(true, true, cp.getKey(), cp.getSkillID(), cp.getSlv(), cp.getGrade(), true);
    }

    public static OutPacket characterPotentialSet(CharacterPotential cp, boolean updatePassive) {
        return characterPotentialSet(true, true, cp.getKey(), cp.getSkillID(), cp.getSlv(), cp.getGrade(), updatePassive);
    }

    public static OutPacket characterPotentialSet(boolean exclRequest, boolean changed, short pos, int skillID,
                                                  short skillLevel, short grade, boolean updatePassive) {
        OutPacket out = new OutPacket(SendOpcode.CHARACTER_POTENTIAL_SET);
        out.writeBool(exclRequest);
        out.writeBool(changed);
        if (changed) {
            out.writeShort(pos);
            out.writeInt(skillID);
            out.writeShort(skillLevel);
            out.writeShort(grade);
            out.writeBool(updatePassive); //全部重置的话第三条就是true
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


    public static OutPacket resurrectionCountdown(int time1, int time2, boolean opt) {
        OutPacket out = new OutPacket(SendOpcode.RESURRECTION_COUNTDOWN);

        out.writeInt(time1);  //20s
        out.writeInt(time2);  //20s
        out.writeBool(opt);   //false

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
        boolean edited = player.getQuickSlots() != null && player.getQuickSlots().size() == 32;
        out.writeBool(edited);
        if (player.getQuickSlots() != null) {
            for (Integer key : player.getQuickSlots()) {
                out.writeInt(key);
            }
        }
        return out;
    }

    public static OutPacket progress(int progress) {
        OutPacket out = new OutPacket(SendOpcode.PROGRESS);

        out.writeInt(progress);

        return out;
    }

    public static OutPacket openWorldMap() {
        OutPacket out = new OutPacket(SendOpcode.OPEN_WORLDMAP);

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

        TripleDESCipher tripleDESCipher = new TripleDESCipher(DES_KEY);
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

    public static OutPacket fameResponse(FameResult fameResult) {
        OutPacket out = new OutPacket(SendOpcode.FAME_RESPONSE);

        out.write(fameResult.getAction().getVal());
        switch (fameResult.getAction()) {
            case Add:
                out.writeMapleAsciiString(fameResult.getStr());
                out.write(fameResult.getArg1());
                out.writeInt(fameResult.getArg2());
                break;
            case Receive:
                out.writeMapleAsciiString(fameResult.getStr());
                out.write(fameResult.getArg1());
                break;
            case AlreadyAddInThisMonth:
                break;
        }

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

                invType = item.isCash() ? InventoryType.CASH_EQUIP : InventoryType.EQUIP;
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
                    if ((invType == InventoryType.EQUIP || invType == InventoryType.CASH_EQUIP)
                            && (oldPos < 0 || newPos < 0)) {
                        addMovementInfo = true;
                        if (oldPos > 0) {
                            equipMove += 2;
                        } else {
                            equipMove += 1;
                        }
                    }
                    break;
                case REMOVE:
                    if ((invType == InventoryType.EQUIP || invType == InventoryType.CASH_EQUIP)
                            && (oldPos < 0 || newPos < 0)) {
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

    public static OutPacket inventoryGrow(InventoryType type, byte slots) {
        OutPacket out = new OutPacket();
        out.write(type.getVal());
        out.write(slots);
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
        out.writeInt(0);
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
        out.writeLong(1); //mask
        out.write(0);
        player.encode(out, CharMask.Character);
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
        equip.encode(out);
        return out;
    }

    public static OutPacket hyperUpgradeDisplay(Equip equip, boolean downgradable, long stars, int successChance, int destroyChance,
                                                boolean chanceTime) {

        OutPacket out = new OutPacket(SendOpcode.EQUIPMENT_ENCHANT);

        out.write(EquipmentEnchantType.HyperUpgradeDisplay.getVal());
        out.writeBool(downgradable);
        out.writeLong(stars);
        out.writeZeroBytes(18);
        out.writeInt(successChance);
        out.writeInt(destroyChance);
        out.writeBool(chanceTime);
        out.writeLong(0);

        TreeMap<EnchantStat, Integer> vals = equip.getHyperUpgradeStats();
        int mask = 0;
        for (EnchantStat es : vals.keySet()) {
            mask |= es.getVal();
        }
        out.writeInt(mask);
        vals.forEach((es, val) -> out.writeInt(val));

        return out;
    }

    public static OutPacket showUnknownEnchantFailResult(byte type) {
        OutPacket outPacket = new OutPacket(SendOpcode.EQUIPMENT_ENCHANT);

        outPacket.write(EquipmentEnchantType.ShowUnknownFailResult.getVal());
        outPacket.write(type);

        return outPacket;
    }

    public static OutPacket miniGameDisplay() {
        OutPacket outPacket = new OutPacket(SendOpcode.EQUIPMENT_ENCHANT);

        outPacket.write(EquipmentEnchantType.MiniGameDisplay.getVal());
        outPacket.write(0);
        outPacket.writeInt(DateUtil.getTime());

        return outPacket;
    }

    public static OutPacket showUpgradeResult(Equip oldEquip, Equip equip, boolean succeed, boolean boom, boolean canDegrade) {
        OutPacket out = new OutPacket(SendOpcode.EQUIPMENT_ENCHANT);

        out.write(EquipmentEnchantType.ShowHyperUpgradeResult.getVal());
        out.writeInt(boom ? 2 : succeed ? 1 : canDegrade ? 0 : 3);
        out.write(0);
        oldEquip.encode(out);
        equip.encode(out);

        return out;
    }

    public static OutPacket additionalCubeResult(int charId, boolean upgrade, Item item, Equip equip) {
        OutPacket out = new OutPacket(SendOpcode.ADDITIONAL_CUBE_RESULT);

        out.writeInt(charId);
        out.writeBool(upgrade);
        out.writeInt(item.getItemId());
        out.writeInt(equip.getPos());
        out.writeInt(item.getQuantity());
        equip.encode(out);

        return out;
    }

    public static OutPacket memorialCubeResult(Item item, Equip equip) {
        OutPacket out = new OutPacket(SendOpcode.MEMORIAL_CUBE_RESULT);

        out.writeLong(equip.getId());
        out.writeBool(true); //equip!=null
        equip.encode(out);
        out.writeInt(item.getItemId());
        out.writeInt(equip.getPos());
        out.writeInt(item.getQuantity());
        out.writeInt(item.getPos());

        return out;
    }

    public static OutPacket blackCubeResult(Item item, Equip equip) {
        OutPacket out = new OutPacket(SendOpcode.BLACK_CUBE_RESULT);

        out.writeLong(equip.getId());
        out.writeBool(true);
        equip.encode(out);
        out.writeInt(item.getItemId());
        out.writeInt(equip.getPos());
        out.writeInt(item.getQuantity());
        out.writeInt(item.getPos());

        return out;
    }

    public static Packet hexagonalCubeModifiedResult() {
        OutPacket out = new OutPacket(SendOpcode.HEXAGONAL_CUBE_RESULT);

        out.writeShort(1);
        out.writeInt(0);

        return out;
    }

    public static Packet reflectionCubeResult(Item item, Equip equip, MapleCharacter chr) {
        OutPacket out = new OutPacket(SendOpcode.REFLECTION_CUBE_RESULT);

        out.writeInt(chr.getId());
        out.write(0);
        out.writeInt(item.getItemId());
        out.writeInt(equip.getPos());
        equip.encode(out);

        return out;
    }

    public static OutPacket hexagonalCubeResult(int level, List<Integer> options) {
        OutPacket out = new OutPacket(SendOpcode.HEXAGONAL_CUBE_RESULT);

        out.writeShort(0);
        out.writeInt(0);

        out.writeInt(level);
        out.writeInt(options.size());
        for (Integer option : options) {
            out.writeInt(option);
        }

        return out;
    }

    public static OutPacket uniqueCubeResult(int line) {
        OutPacket out = new OutPacket(SendOpcode.UNIQUE_CUBE_RESULT);

        out.writeShort(0);
        out.writeInt(0);
        out.writeInt(line);

        return out;
    }

    public static OutPacket uniqueCubeModifiedResult(int line) {
        OutPacket out = new OutPacket(SendOpcode.UNIQUE_CUBE_RESULT);

        out.writeShort(1);
        out.writeInt(0);
        out.writeInt(line);

        return out;
    }


    public static OutPacket memorialCubeModified() {
        OutPacket out = new OutPacket(SendOpcode.MEMORIAL_CUBE_MODIFIED);

        out.writeBool(false);

        return out;
    }

    public static OutPacket miracleCirculatorResult(Set<CharacterPotential> potentials, Item item) {
        OutPacket out = new OutPacket(SendOpcode.MIRACLE_CIRCULATOR_RESULT);

        out.writeInt(potentials.size());
        for (CharacterPotential potential : potentials) {
            out.writeInt(potential.getSkillID());
            out.write(potential.getSlv());
            out.write(potential.getKey());
            out.write(potential.getGrade());
        }
        out.writeInt(item.getItemId());

        out.writeInt(0);
        out.writeInt(0);
        out.writeInt(0);
        out.writeInt(0);
        return out;
    }

    public static Packet userB2Body(short type, int bodyIdCounter) {
        OutPacket out = new OutPacket(SendOpcode.USER_B2_BODY);

        out.writeInt(type);
        out.writeInt(bodyIdCounter);

        return out;
    }


    public static OutPacket lifeCount(int count) {
        OutPacket out = new OutPacket(SendOpcode.LIFE_COUNT);

        out.writeInt(count);

        return out;
    }


    public static OutPacket goldHammerItemUpgradeResult(byte returnResult, int msg) {
        OutPacket out = new OutPacket(SendOpcode.GOLD_HAMMER_RESULT);

        out.write(0);
        out.write(returnResult);
        out.writeInt(msg);
        out.writeInt(0);
        out.writeInt(0);

        return out;
    }


    public static OutPacket modComboResponse(int combo) {
        OutPacket out = new OutPacket(SendOpcode.MOD_COMBO_RESPONSE);

        out.writeInt(combo);

        return out;
    }


    public static OutPacket incJudgementStack(byte amount) {
        OutPacket out = new OutPacket(SendOpcode.INC_JUDGEMENT_STACK_RESPONSE);

        out.write(0);
        out.write(amount);

        return out;
    }


    public static OutPacket updateVMatrix(MapleCharacter chr, boolean update, MatrixUpdateType updateType, int pos) {
        OutPacket out = new OutPacket(SendOpcode.UPDATE_MATRIX);
        chr.getMatrixInventory().encode(out);
        out.writeInt(update);
        if (update) {
            out.writeInt(updateType.getVal());
            out.writeInt(pos);
        }
        return out;
    }

    public static OutPacket nodeCraftResult(int coreID, int quantity, int skillID1, int skillID2, int skillID3) {
        OutPacket out = new OutPacket(SendOpcode.NODE_CRAFT_RESULT);

        out.writeInt(coreID);
        out.writeInt(1);
        out.writeInt(skillID1);
        out.writeInt(skillID2);
        out.writeInt(skillID3);
        out.writeInt(quantity); //size
        return out;
    }

    public static OutPacket nodeEnhanceResult(int recordID, int exp, int slv1, int slv2) {
        OutPacket out = new OutPacket(SendOpcode.NODE_ENHANCE_RESULT);

        out.writeInt(recordID);
        out.writeInt(exp);
        out.writeInt(slv1);
        out.writeInt(slv2);

        return out;
    }

    public static OutPacket nodeShardResult(int shard) {
        OutPacket out = new OutPacket(SendOpcode.NODE_SHARD_RESULT);

        out.writeInt(shard);

        return out;
    }

    public static OutPacket erdaSpectrumCounter(int erda, int arg1, int arg2) {
        OutPacket out = new OutPacket(SendOpcode.ERDA_SPECTRUM);

        out.writeInt(erda);
        out.writeInt(arg1);
        out.writeInt(arg2);

        return out;
    }


    public static OutPacket inGameDirectionEvent(InGameDirectionEvent igdr) {
        OutPacket out = new OutPacket(SendOpcode.IN_GAME_DIRECTION_EVENT);

        igdr.encode(out);

        return out;
    }
}

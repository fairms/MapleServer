package im.cave.ms.net.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.items.ScrollUpgradeInfo;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.enums.EquipmentEnchantType;
import im.cave.ms.enums.InventoryOperation;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.MessageType;
import im.cave.ms.net.netty.OutPacket;
import im.cave.ms.net.packet.opcode.SendOpcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static im.cave.ms.enums.InventoryType.EQUIPPED;
import static im.cave.ms.net.packet.PacketHelper.MAX_TIME;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet.opcode
 * @date 11/29 22:25
 */
public class PlayerPacket {


    public static OutPacket inventoryOperation(boolean exclRequestSent, boolean notRemoveAddInfo, InventoryOperation type, short oldPos, short newPos,
                                               int bagPos, Item item) {
        OutPacket outPacket = new OutPacket();
        InventoryType invType = item.getInvType();
        if ((oldPos > 0 && newPos < 0 && invType == EQUIPPED) || (invType == EQUIPPED && oldPos < 0)) {
            invType = InventoryType.EQUIP;
        }
        outPacket.writeShort(SendOpcode.INVENTORY_OPERATION.getValue());
        outPacket.writeBool(exclRequestSent);
        outPacket.write(1); //size
        outPacket.writeBool(notRemoveAddInfo);

        boolean addMovementInfo = false;
        outPacket.write(type.getVal());
        outPacket.write(invType.getVal());
        outPacket.writeShort(oldPos);
        switch (type) {
            case ADD:
                PacketHelper.addItemInfo(outPacket, item);
                break;
            case UPDATE_QUANTITY:
                outPacket.writeShort(item.getQuantity());
                break;
            case MOVE:
                outPacket.writeShort(newPos);
                if (invType == InventoryType.EQUIP && (oldPos < 0 || newPos < 0)) {
                    addMovementInfo = true;
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
                break;
            case UNK_2:
                outPacket.writeShort(bagPos); // ?
                break;
            case UPDATE_ITEM_INFO:
//                item.encode(outPacket);
                break;
            case UNK_3:
                break;
        }
        if (addMovementInfo) {
            outPacket.writeBool(true);
        }
        return outPacket;
    }

    public static OutPacket move(MapleCharacter player, MovementInfo movementInfo) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.MOVE_PLAYER.getValue());
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

    //角色信息面板
    public static OutPacket charInfo(MapleCharacter chr) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.CHAR_INFO.getValue());
        outPacket.writeInt(chr.getId());
        outPacket.writeInt(chr.getLevel());
        outPacket.writeShort(chr.getJobId());
        outPacket.writeShort(0);//sub job
        outPacket.write(0x0A); //pvp grade
        outPacket.writeInt(chr.getFame());
        outPacket.writeBool(false); //marriage
        //todo marriage = true
        outPacket.write(0); //making skill size
        outPacket.writeMapleAsciiString("-"); //party name
        outPacket.writeMapleAsciiString(""); // 联盟
        outPacket.write(-1); //unk
        outPacket.write(0);  //unk
        outPacket.write(0); //pet size
        //todo pet info
        outPacket.write(0);
        outPacket.writeInt(0); //装备的的勋章
        outPacket.writeShort(0); //收藏数目
        //todo 收藏任务id+完成时间
        outPacket.writeBool(true);//hasDamageSkins always true

        outPacket.writeInt(0);
        outPacket.writeInt(0);
        outPacket.writeBool(false);
        outPacket.writeInt(0);
        outPacket.writeMapleAsciiString("");

        outPacket.writeInt(-1);
        outPacket.writeInt(0);
        outPacket.writeBool(true); //notSave
        outPacket.writeInt(0);
        outPacket.writeMapleAsciiString("");

        outPacket.writeInt(-1);
        outPacket.writeInt(0);
        outPacket.writeBool(true);
        outPacket.writeInt(0);
        outPacket.writeMapleAsciiString("");


        outPacket.writeShort(1); //damage skins slots
        outPacket.writeShort(0); //damage skins count
        //倾向
        outPacket.write(1);
        outPacket.write(2);
        outPacket.write(3);
        outPacket.write(4);
        outPacket.write(5);
        outPacket.write(6);
        //
        outPacket.write(0);
        outPacket.writeZeroBytes(8);

        outPacket.writeInt(0);//椅子数
        outPacket.writeInt(0);//勋章数

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
        outPacket.writeBool(true);
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
        //unk
        outPacket.write(1);
        outPacket.write(1);
        outPacket.write(1);
        outPacket.writeInt(0);
        outPacket.write(0);  //sometimes
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

    // todo 纯复制
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


    public static OutPacket inventoryRefresh() {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.INVENTORY_OPERATION.getValue());
        outPacket.writeBool(true);
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

    public static OutPacket updateQuestEx(int questId) {
        OutPacket outPacket = new OutPacket();
        outPacket.writeShort(SendOpcode.UPDATE_QUEST_EX.getValue());
        outPacket.writeInt(questId);
        return outPacket;
    }
}

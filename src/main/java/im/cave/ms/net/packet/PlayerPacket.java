package im.cave.ms.net.packet;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.temp.CharacterTemporaryStat;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.movement.MovementInfo;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.enums.InventoryOperation;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.net.packet.opcode.SendOpcode;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static im.cave.ms.enums.InventoryType.EQUIPPED;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.packet.opcode
 * @date 11/29 22:25
 */
public class PlayerPacket {

    public static MaplePacketLittleEndianWriter inventoryOperation(boolean exclRequestSent, boolean notRemoveAddInfo, InventoryOperation type, short oldPos, short newPos,
                                                                   int bagPos, Item item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        InventoryType invType = item.getInvType();
        if ((oldPos > 0 && newPos < 0 && invType == EQUIPPED) || (invType == EQUIPPED && oldPos < 0)) {
            invType = InventoryType.EQUIP;
        }
        mplew.writeShort(SendOpcode.INVENTORY_OPERATION.getValue());
        mplew.writeBool(exclRequestSent);
        mplew.write(1); //size
        mplew.writeBool(notRemoveAddInfo);

        boolean addMovementInfo = false;
        mplew.write(type.getVal());
        mplew.write(invType.getVal());
        mplew.writeShort(oldPos);
        switch (type) {
            case ADD:
                PacketHelper.addItemInfo(mplew, item);
                break;
            case UPDATE_QUANTITY:
                mplew.writeShort(item.getQuantity());
                break;
            case MOVE:
                mplew.writeShort(newPos);
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
                mplew.writeLong(((Equip) item).getExp());
                break;
            case UPDATE_BAG_POS:
                mplew.writeInt(bagPos);
                break;
            case UPDATE_BAG_QUANTITY:
                mplew.writeShort(newPos);
                break;
            case UNK_1:
                break;
            case UNK_2:
                mplew.writeShort(bagPos); // ?
                break;
            case UPDATE_ITEM_INFO:
//                item.encode(outPacket);
                break;
            case UNK_3:
                break;
        }
        if (addMovementInfo) {
            mplew.writeBool(true);
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter move(MapleCharacter player, MovementInfo movementInfo) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.MOVE_PLAYER.getValue());
        mplew.writeInt(player.getId());
        movementInfo.encode(mplew);
        return mplew;
    }


    public static MaplePacketLittleEndianWriter lockUI(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.LOCK_UI.getValue());
        mplew.writeBool(enable);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter disableUI(boolean disable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.DISABLE_UI.getValue());
        mplew.writeBool(disable);
        mplew.writeBool(disable);
        if (disable) {
            mplew.writeBool(false);
            mplew.writeBool(false);
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter updateVoucher(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(chr.getId());
        mplew.write(chr.getAccount().getVoucher());
        return mplew;
    }

    //角色信息面板
    public static MaplePacketLittleEndianWriter charInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.CHAR_INFO.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getLevel());
        mplew.writeShort(chr.getJobId());
        mplew.writeShort(0);//sub job
        mplew.write(0x0A); //pvp grade
        mplew.writeInt(chr.getFame());
        mplew.writeBool(false); //marriage
        //todo marriage = true
        mplew.write(0); //making skill size
        mplew.writeMapleAsciiString("-"); //party name
        mplew.writeMapleAsciiString(""); // 联盟
        mplew.write(-1); //unk
        mplew.write(0);  //unk
        mplew.write(0); //pet size
        //todo pet info
        mplew.write(0);
        mplew.writeInt(0); //装备的的勋章
        mplew.writeShort(0); //收藏数目
        //todo 收藏任务id+完成时间
        mplew.writeBool(true);//hasDamageSkins always true

        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeBool(false);
        mplew.writeInt(0);
        mplew.writeMapleAsciiString("");

        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.writeBool(true); //notSave
        mplew.writeInt(0);
        mplew.writeMapleAsciiString("");

        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.writeBool(true);
        mplew.writeInt(0);
        mplew.writeMapleAsciiString("");


        mplew.writeShort(1); //damage skins slots
        mplew.writeShort(0); //damage skins count
        //倾向
        mplew.write(1);
        mplew.write(2);
        mplew.write(3);
        mplew.write(4);
        mplew.write(5);
        mplew.write(6);
        //
        mplew.write(0);
        mplew.writeZeroBytes(8);

        mplew.writeInt(0);//椅子数
        mplew.writeInt(0);//勋章数

        return mplew;
    }


    public static MaplePacketLittleEndianWriter cancelChair(int charId, short id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.CANCEL_CHAIR.getValue());
        mplew.writeInt(charId);
        if (id != -1) {
            mplew.write(1);
            mplew.writeShort(id);
        } else {
            mplew.write(0);
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter hiddenEffectEquips(MapleCharacter player) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.HIDDEN_EFFECT_EQUIP.getValue());
        mplew.writeInt(player.getId());
        List<Item> items = player.getEquippedInventory().getItems();
        List<Item> equips = items.stream().filter(item -> !((Equip) item).isShowEffect()).collect(Collectors.toList());
        mplew.writeInt(equips.size());
        for (Item equip : equips) {
            mplew.writeInt(equip.getPos());
        }
        mplew.writeBool(true);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter sendRebirthConfirm(boolean onDeadRevive, boolean onDeadProtectForBuff, boolean onDeadProtectBuffMaplePoint,
                                                                   boolean onDeadProtectExpMaplePoint, boolean anniversary, int reviveType, int protectType) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.DEATH_CONFIRM.getValue());
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
        mplew.writeInt(reviveMask);
        mplew.writeBool(anniversary);
        mplew.writeInt(reviveType);
        if (onDeadProtectForBuff || onDeadProtectExpMaplePoint) {
            mplew.writeInt(protectType);
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter changeSkillRecordResult(Skill skill) {
        List<Skill> skills = new ArrayList<>();
        skills.add(skill);
        return changeSkillRecordResult(skills, true, false, false, false);
    }

    public static MaplePacketLittleEndianWriter changeSkillRecordResult(List<Skill> skills, boolean exclRequestSent, boolean showResult
            , boolean removeLinkSkill, boolean sn) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.CHANGE_SKILL_RESULT.getValue());
        mplew.writeBool(exclRequestSent);
        mplew.writeBool(showResult);
        mplew.writeBool(removeLinkSkill);
        mplew.writeShort(skills.size());
        for (Skill skill : skills) {
            mplew.writeInt(skill.getSkillId());
            mplew.writeInt(skill.getCurrentLevel());
            mplew.writeInt(skill.getMasterLevel());
            mplew.writeLong(DateUtil.getFileTime(-1));
        }
        mplew.writeBool(sn);
        return mplew;
    }

    public static MaplePacketLittleEndianWriter skillCoolTimes(Map<Integer, Integer> cooltimes) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.SKILL_COOLTIME.getValue());
        mplew.writeInt(cooltimes.size());
        cooltimes.forEach((id, cooltime) -> {
            mplew.writeInt(id);
            mplew.writeInt(cooltime);
        });
        return mplew;
    }

    public static MaplePacketLittleEndianWriter skillCoolDown(int skillId) {
        HashMap<Integer, Integer> skills = new HashMap<>();
        skills.put(skillId, 0);
        return skillCoolTimes(skills);
    }


    public static MaplePacketLittleEndianWriter removeBuff(TemporaryStatManager tsm, boolean demount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.REMOVE_BUFF.getValue());
        mplew.writeInt(0);
        mplew.write(1);
        mplew.write(1);
        for (int i : tsm.getRemovedMask()) {
            mplew.writeInt(i);
        }
        tsm.getRemovedStats().forEach((characterTemporaryStat, options) -> {
            mplew.writeInt(0);
        });
        if (demount) {
            mplew.writeBool(true);
        }
        return mplew;
    }

    public static MaplePacketLittleEndianWriter giveBuff(TemporaryStatManager tsm) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.GIVE_BUFF.getValue());
        mplew.writeZeroBytes(8);
        tsm.encodeForLocal(mplew);
        //unk
        mplew.write(1);
        mplew.write(1);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.write(0);  //sometimes
        return mplew;
    }
}

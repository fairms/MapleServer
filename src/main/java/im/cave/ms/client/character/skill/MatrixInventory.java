package im.cave.ms.client.character.skill;


import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.constants.MatrixConstants;
import im.cave.ms.enums.MatrixStateType;
import im.cave.ms.enums.MatrixUpdateType;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.data.VCoreData;
import im.cave.ms.provider.info.VCore;
import im.cave.ms.tools.Util;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@Entity
@Table(name = "matrix_inventory")
public class MatrixInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "matrixId")
    private List<MatrixSkill> skills;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "matrixId")
    private List<MatrixSlot> slots;

    public MatrixInventory() {
        this.skills = new CopyOnWriteArrayList<>();
        this.slots = new CopyOnWriteArrayList<>();
    }


    public static MatrixInventory getDefault() {
        MatrixInventory ret = new MatrixInventory();
        for (int i = 0; i < 19; i++) {
            MatrixSlot slot = new MatrixSlot();
            slot.setSlotId(i);
            slot.setEquippedSkill(-1);
            ret.slots.add(slot);
        }
        return ret;
    }

    public void addSkill(MatrixSkill skill) {
        if (skills.size() < MatrixConstants.SLOT_MAX) {
            List<MatrixSkill> temp = new ArrayList<>(skills);
            skills.clear();
            skills.add(skill);
            skills.addAll(temp);
            temp.clear();
        }
    }

    public MatrixSlot getMatrixSlotBySlotId(int slotId) {
        return Util.findWithPred(getSlots(), slot -> slot.getSlotId() == slotId);
    }


    public MatrixSkill getMatrixSkillById(long id) {
        return Util.findWithPred(getSkills(), skill -> skill.getId() == id);
    }

    //有点傻瓜
    public static void reloadSkills(MapleCharacter chr) {
        List<Skill> changed = new ArrayList<>();
        Skill skill;
        for (MatrixSkill skillRecord : chr.getMatrixInventory().skills) {
            int[] toRemove = {skillRecord.getSkill1(), skillRecord.getSkill2(), skillRecord.getSkill3()};
            for (int skillID : toRemove) {
                if (skillID != 0) {
                    skill = SkillData.getSkill(skillID);
                    if (skill == null) {
                        continue;
                    }
                    skill.setCurrentLevel(0);
                    chr.removeSkill(skillID);
                    changed.add(skill);
                }
            }
        }
        chr.write(UserPacket.changeSkillRecordResult(changed, true, false, false, false));
        changed.clear();

        for (MatrixSkill skillRecord : chr.getMatrixInventory().skills) {
            if (skillRecord.getState() == MatrixStateType.ACTIVE) {
                if (skillRecord.getSlot() == 0) {
                    continue;
                }
                int[] toAdd = {skillRecord.getSkill1(), skillRecord.getSkill2(), skillRecord.getSkill3()};
                for (int skillID : toAdd) {
                    if (skillID != 0) {
                        int slv = Math.min(skillRecord.getLevel(), skillRecord.getMasterLevel());// + chr.getSkillLevel(skillRecord.getSkillLevel());
                        skill = SkillData.getSkill(skillID);
                        if (skill == null) {
                            continue;
                        }
                        skill.setCurrentLevel(slv);
                        skill.setMasterLevel(skillRecord.getMasterLevel());
                        chr.addSkill(skill);
                        changed.add(skill);
                    }
                }
            }
        }
        chr.write(UserPacket.changeSkillRecordResult(changed, true, false, false, false));
        changed.clear();
    }


    public void encode(OutPacket out) {
        out.writeInt(getSkills().size());
        for (MatrixSkill skill : getSkills()) {
            skill.encode(out);
        }
        out.writeInt(getSlots().size());
        for (MatrixSlot slot : getSlots()) {
            slot.encode(out);
        }
    }


    public MatrixSkill getSkill(int slotID) {
        return skills.get(slotID);
    }


    public void enhance(MapleCharacter chr, MatrixSkill toEnhance, List<MatrixSkill> enhanceSkills) {
        int incExp = 0;
        for (MatrixSkill skill : enhanceSkills) {
            if (VCoreData.isSkillNode(skill.getCoreId())) {
                incExp += VCoreData.getEnforceOption(VCoreData.SKILL).get(skill.getLevel()).getEnforceExp();
            } else if (VCoreData.isBoostNode(skill.getCoreId())) {
                incExp += VCoreData.getEnforceOption(VCoreData.BOOST).get(skill.getLevel()).getEnforceExp();
            }
            skills.remove(skill);
        }

        Map<Integer, VCore.EnforceOption> enforce = VCoreData.getEnforceOption(VCoreData.getCore(toEnhance.getCoreId()).getType());
        int incExpCopy = incExp;
        int curSLV = toEnhance.getLevel();
        if (enforce == null) {
            return;
        }
        while (incExp > 0) {
            int nextExp = enforce.get(toEnhance.getLevel()).getNextExp() - toEnhance.getExperience();
            if (incExp > nextExp) {
                toEnhance.setLevel(Math.min(toEnhance.getLevel() + 1, 25));
                toEnhance.setExperience(0);
                incExp -= nextExp;
            } else {
                toEnhance.setExperience(toEnhance.getExperience() + incExp);
                incExp = 0;
            }
        }
        chr.write(UserPacket.updateVMatrix(chr, true, MatrixUpdateType.ENHANCE, toEnhance.getSlot()));
        chr.write(UserPacket.nodeEnhanceResult(skills.indexOf(toEnhance), incExpCopy, curSLV, toEnhance.getLevel()));
    }

    public int activateSkill(int slotID, int toSlot) {
        MatrixSkill skill = skills.get(slotID);
        if (skill != null && skill.getSlot() == -1) {
            if (toSlot != -1) {
                MatrixSlot slot = slots.get(toSlot);
                if (slot != null && slot.getEquippedSkill() == -1) {
                    slot.setEquippedSkill(slotID);
                    skill.setState(MatrixStateType.ACTIVE);
                    skill.setSlot(slot.getSlotId());
                    return slot.getSlotId();
                }
            } else {
                for (MatrixSlot slot : slots) {
                    if (slot.getEquippedSkill() == -1) {
                        slot.setEquippedSkill(slotID);
                        skill.setState(MatrixStateType.ACTIVE);
                        skill.setSlot(slot.getSlotId());
                        return slot.getSlotId();
                    }
                }
            }
        }
        return 0;
    }


    public void moveSkill(int skillSlot, int replaceSlot, int from, int to) {
        MatrixSkill skill = skills.get(skillSlot);
        if (skill != null) {
            MatrixSkill replaceSkill = null;
            if (replaceSlot != -1) {
                replaceSkill = skills.get(replaceSlot);
            }
            move(skill, replaceSkill, from, to, skillSlot, replaceSlot);
        }
    }

    private void move(MatrixSkill skill, MatrixSkill toReplace, int from, int to, int slot, int replaceSlot) {
        if (skill != null && skill.isActive() && skill.getSlot() == from) {
            MatrixSlot toSlot = slots.get(to);
            if (toSlot != null) {
                toSlot.setEquippedSkill(slot);
                skill.setSlot(toSlot.getSlotId());
            }
            MatrixSlot fromSlot = slots.get(from);
            if (fromSlot != null) {
                if (toReplace != null && toReplace.isActive() && toReplace.getSlot() == to) {
                    fromSlot.setEquippedSkill(replaceSlot);
                    toReplace.setSlot(fromSlot.getSlotId());
                } else {
                    fromSlot.setEquippedSkill(-1);
                }
            }
        }
    }

    public int deactivateSkill(int slotID) {
        MatrixSkill skill = skills.get(slotID);
        if (skill != null && skill.getSlot() != -1) {
            MatrixSlot slot = slots.get(skill.getSlot());
            if (slot != null) {
                slot.setEquippedSkill(-1);
                skill.setState(MatrixStateType.INACTIVE);
                skill.setSlot(0);
                return slotID;
            }
        }
        return 0;
    }

    public void disassembleMultiple(MapleCharacter chr, List<MatrixSkill> toDisassemble) {
        int incShard = 0;
        for (MatrixSkill skill : toDisassemble) {
            if (skill != null && !skill.isActive()) {
                if (removeSkill(skills.indexOf(skill))) {
                    incShard += MatrixConstants.getIncShard(skill.getCoreId(), skill.getLevel());
                }
            }
        }
        chr.incShards(incShard);
        chr.write(UserPacket.updateVMatrix(chr, true, MatrixUpdateType.DISASSEMBLE_MULTIPLE, 0));
        chr.write(UserPacket.nodeShardResult(incShard));
    }


    public void disassemble(MapleCharacter chr, int slot) {
        MatrixSkill skill = skills.get(slot);
        if (skill != null && !skill.isActive()) {
            if (removeSkill(slot)) {
                int shard = MatrixConstants.getIncShard(skill.getCoreId(), skill.getLevel());
                chr.incShards(shard);
                chr.write(UserPacket.updateVMatrix(chr, true, MatrixUpdateType.DISASSEMBLE_SINGLE, slot));
                chr.write(UserPacket.nodeShardResult(shard));
            }
        }
    }


    public boolean removeSkill(int slot) {
        if (skills.remove(slot) != null) {
            List<MatrixSkill> temp = new ArrayList<>(skills);
            skills.clear();
            skills.addAll(temp);
            temp.clear();
            return true;
        }
        return false;
    }

    public void resetSlotsEnhanceLevel(MapleCharacter chr) {
        for (MatrixSlot slot : getSlots()) {
            slot.setEnhanceLevel(0);
        }
        reloadSkills(chr);
        chr.write(UserPacket.updateVMatrix(chr, false, MatrixUpdateType.RESET_SLOT_ENHANCEMENT, 0));
    }
}

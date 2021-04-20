package im.cave.ms.constants;


import im.cave.ms.provider.data.VCoreData;

public class MatrixConstants {
    // Etc.wz/VMatrixOption.img
    public static final int SLOT_MAX = 500;
    public static final int EQUIP_SLOT_MIN = 4;
    public static final int EQUIP_SLOT_MAX = 15;
    public static final int SPECIAL_SLOT_MAX = 1;
    public static final int EXTEND_LEVEL = 5;
    public static final int EXTEND_AF = 0;
    public static final int GRADE_MAX = 25;
    public static final int TOTAL_GRADE_MAX = 50;
    public static final int CRAFT_SKILL_CORE_COST = 140;
    public static final int CRAFT_ENCHANT_CORE_COST = 70;
    public static final int CRAFT_SPECIAL_CORE_COST = 250;
    public static final int CRAFT_GEMSTONE_COST = 35;
    public static final int MATRIX_POINT_RESET_MESO = 1000000;
    public static final int EQUIP_SLOT_ENHANCE_MAX = 5;
    // slotExpansionMeso can be helpful

    public static int getIncShard(int coreID, int skillLevel) {
        int shard = 0;
        if (VCoreData.isSkillNode(coreID)) {
            shard = VCoreData.getEnforceOption(VCoreData.SKILL).get(skillLevel).getExtract();
        } else if (VCoreData.isBoostNode(coreID)) {
            shard = VCoreData.getEnforceOption(VCoreData.BOOST).get(skillLevel).getExtract();
        } else if (VCoreData.isSpecialNode(coreID)) {
            shard = VCoreData.getEnforceOption(VCoreData.SPECIAL).get(skillLevel).getExtract();
        } else if (VCoreData.isExpNode(coreID)) {
            shard = VCoreData.getEnforceOption(VCoreData.EXP).get(skillLevel).getExtract();
        }
        return shard;
    }

    public static int getSlotsByLevel(int level) {
        int slotByLevel = level / 5 - 40;
        return Math.min(slotByLevel, EQUIP_SLOT_MAX);
    }
}

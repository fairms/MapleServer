package im.cave.ms.enums;

public enum DamageSkinType {
    DamageSkinSaveReq_Reg(0),
    DamageSkinSaveReq_Remove(1),
    DamageSkinSaveReq_Active(2),
    DamageSkinSaveReq_SendInfo(3),
    DamageSkinSave_Success(4),
    DamageSkinSave_Fail_Unknown(5),
    DamageSkinSave_Fail_SlotCount(6),
    DamageSkinSave_Fail_AlreadyExist(7),
    DamageSkinSave_Fail_NotSave(8),
    DamageSkinSave_Fail_ServerValueBlock(9),
    DamageSkinSave_Fail_AlreadyActive(10),
    ;

    private int val;

    DamageSkinType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}

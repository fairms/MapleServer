package im.cave.ms.enums;


public enum AssistType { //Summon Assist Type
    None(0),
    Attack(1),
    Heal(2),
    AttackEx(3),
    AttackEx2(4),
    Summon(5),
    AttackManual(6),
    AttackCounter(7),
    CreateArea(8),
    Bodyguard(9),
    Jaguar(10),
    ;
    private final byte val;

    AssistType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }
}

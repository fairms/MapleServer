package im.cave.ms.enums;


/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.enums
 * @date 11/19 21:51
 */
public enum InventoryType {
    EQUIPPED(-1),
    EQUIP(1),
    CONSUME(2),
    ETC(4),
    INSTALL(3),
    CASH(5);

    //    @EnumValue
    private final byte val;

    InventoryType(int val) {
        this((byte) val);
    }

    InventoryType(byte val) {
        this.val = val;
    }


    public static InventoryType getTypeById(byte id) {
        for (InventoryType inventoryType : InventoryType.values()) {
            if (inventoryType.getVal() == id) {
                return inventoryType;
            }
        }
        return null;
    }

    public static InventoryType getInvTypeByString(String subMap) {
        subMap = subMap.toLowerCase();
        InventoryType ret = null;
        switch (subMap) {
            case "cash":
            case "pet":
                ret = CASH;
                break;
            case "consume":
            case "special":
            case "use":
                ret = CONSUME;
                break;
            case "etc":
                ret = ETC;
                break;
            case "install":
            case "setup":
                ret = INSTALL;
                break;
            case "eqp":
            case "equip":
                ret = EQUIP;
                break;
        }
        return ret;

    }

    public byte getVal() {
        return val;
    }

    public boolean isStackable() {
        return this != EQUIP && this != EQUIPPED && this != CASH;
    }
}

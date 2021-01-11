package im.cave.ms.enums;

/**
 * Created on 4/20/2018.
 */
public enum PetSkill {
    ITEM_PICKUP(0x1), //捡起道具
    EXPANDED_AUTO_MOVE(0x2),
    AUTO_MOVE(0x4), //范围自动捡取
    IGNORE_ITEM(0x8), //排除道具
    EXPIRED_PICKUP(0x10), //捡起无所有权
    AUTO_HP(0x20),
    AUTO_MP(0x40),
    RECALL(0x80), //宠物召回
    AUTO_SPEAKING(0x100), //自言自语
    AUTO_BUFF(0x200),
    PET_SHOP(0x400), //不知道
    AUTO_FEED(0x800),
    FATTEN_UP(0x1000), //巨大化

    ;

    private final int val;

    public int getVal() {
        return val;
    }

    PetSkill(int val) {
        this.val = val;
    }
}

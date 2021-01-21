package im.cave.ms.provider.info;

import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.ScrollStat;
import im.cave.ms.enums.SpecStat;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.items
 * @date 11/22 1:35
 */
@Data
public class ItemInfo {
    private int itemId;
    private InventoryType invType;
    private boolean cash;
    private int price;
    private int slotMax = 200;
    private boolean tradeBlock;
    private boolean notSale;
    private String path = "";
    private boolean noCursed;
    private Map<ScrollStat, Integer> scrollStats = new HashMap<>();
    private Map<SpecStat, Integer> specStats = new HashMap<>();
    private int bagType;
    private int charmEXP;
    private boolean quest;
    private int reqQuestOnProgress;
    private int senseEXP;
    private final Set<Integer> questIDs = new HashSet<>();
    private int mobID;
    private int npcID;
    private int linkedID;
    private boolean monsterBook;
    private boolean notConsume;
    private String script = "";
    private int scriptNPC;
    private int life;
    private int masterLv;
    private int reqSkillLv;
    private Set<Integer> skills = new HashSet<>();
    private int moveTo;
    private final Set<ItemRewardInfo> itemRewardInfos = new HashSet<>();
    private int skillId;
    private int grade;
    private int android;
    private int familiarID;
    private double unitPrice;
    private int reqLevel;
    private int incTameness;
    private int incRepleteness;
    private int incCharmExp;
    private boolean choice;
    private int gender;
    private List<Integer> limitedPets = new ArrayList<>();

    public void putScrollStat(ScrollStat scrollStat, int val) {
        getScrollStats().put(scrollStat, val);
    }

    public void addQuest(int questId) {
        questIDs.add(questId);
    }

    public void putSpecStat(SpecStat ss, int i) {
        getSpecStats().put(ss, i);
    }

    public void addLimitedPet(int itemId) {
        limitedPets.add(itemId);
    }

    public void addItemReward(ItemRewardInfo rewardInfo) {
        itemRewardInfos.add(rewardInfo);
    }
}

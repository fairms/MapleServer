package im.cave.ms.provider.info;

import im.cave.ms.tools.Tuple;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.info
 * @date 2/7 15:15
 */
@Getter
@Setter
public class MakingSkillRecipe {
    private int recipeID;
    private List<TargetElem> target = new ArrayList<>();// rewards
    private int weatherItemID;
    private int incSkillProficiency;//
    private int incSkillProficiencyOnFailure;
    private int incFatigability;
    private int incSkillMasterProficiency;
    private int incSkillMasterProficiencyOnFailure;
    private boolean needOpenItem;
    private int reqSkillID;
    private int recommendedSkillLevel;
    private int reqSkillProficiency;
    private int reqMeso;
    private String reqMapObjectTag = "";
    private List<Tuple<Integer, Integer>> ingredient = new ArrayList<>();//
    private int addedCoolProb;
    private int coolTimeSec;
    private int addedSecForMaxGauge;
    private int expiredPeriod;
    private boolean premiumItem;

    public void addTarget(TargetElem tar) {
        target.add(tar);
    }

    public void addIngredient(int itemID, int count) {
        this.ingredient.add(new Tuple<>(itemID, count));
    }


    public static class TargetElem {
        private int itemID;
        private int count;
        private int probWeight;

        public int getItemID() {
            return itemID;
        }

        public void setItemID(int itemID) {
            this.itemID = itemID;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getProbWeight() {
            return probWeight;
        }

        public void setProbWeight(int probWeight) {
            this.probWeight = probWeight;
        }
    }
}

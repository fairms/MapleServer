package im.cave.ms.provider.info;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VCore {
    private byte type;
    private short maxLevel;
    private int coreID;
    private int expireAfter;
    private String name = "";
    private String desc = "";
    private CoreOption option = new CoreOption();
    private List<String> jobs = new ArrayList<>();
    private List<Integer> connectSkills = new ArrayList<>();



    public boolean isJobSkill(int job) {
        if (jobs.contains("warrior") || jobs.contains("magician") || jobs.contains("archer")
                || jobs.contains("rogue") || jobs.contains("pirate") || jobs.contains("none") || jobs.contains("all")) {
            return false;
        }
        for (String sJob : jobs) {
            if (Integer.parseInt(sJob) == job) {
                return true;
            }
        }
        return false;
    }

    public void addConnectedSkill(int skill) {
        this.connectSkills.add(skill);
    }

    public void addJob(String job) {
        this.jobs.add(job);
    }



    public static class EnforceOption {
        private int enforceExp;
        private int nextExp;
        private int extract;


        public int getEnforceExp() {
            return enforceExp;
        }

        public void setEnforceExp(int enforceExp) {
            this.enforceExp = enforceExp;
        }

        public int getNextExp() {
            return nextExp;
        }

        public void setNextExp(int nextExp) {
            this.nextExp = nextExp;
        }

        public int getExtract() {
            return extract;
        }

        public void setExtract(int extract) {
            this.extract = extract;
        }
    }


    public static class CoreOption {
        private short slv;
        private int skillID;
        private int cooltime;
        private int validTime;
        private int count;
        private int healPercent;
        private int reducePercent;
        private String effectType = "";
        private String condType = "";
        private double prob;

        public short getSLV() {
            return slv;
        }

        public void setSLV(short slv) {
            this.slv = slv;
        }

        public int getSkillID() {
            return skillID;
        }

        public void setSkillID(int skillID) {
            this.skillID = skillID;
        }

        public int getCooltime() {
            return cooltime;
        }

        public void setCooltime(int cooltime) {
            this.cooltime = cooltime;
        }

        public int getValidTime() {
            return validTime;
        }

        public void setValidTime(int validTime) {
            this.validTime = validTime;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getHealPercent() {
            return healPercent;
        }

        public void setHealPercent(int healPercent) {
            this.healPercent = healPercent;
        }

        public int getReducePercent() {
            return reducePercent;
        }

        public void setReducePercent(int reducePercent) {
            this.reducePercent = reducePercent;
        }

        public String getEffectType() {
            return effectType;
        }

        public void setEffectType(String effectType) {
            this.effectType = effectType;
        }

        public String getCondType() {
            return condType;
        }

        public void setCondType(String condType) {
            this.condType = condType;
        }

        public double getProb() {
            return prob;
        }

        public void setProb(double prob) {
            this.prob = prob;
        }
    }

}

package im.cave.ms.provider.info;

import im.cave.ms.tools.Pair;
import org.objectweb.asm.Handle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.info
 * @date 2/24 16:30
 */
public class SkillOption {
    private int id;
    private int skillId;
    private int reqLevel;
    private List<Pair<Integer, Integer>> tempOptions = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getReqLevel() {
        return reqLevel;
    }

    public void setReqLevel(int reqLevel) {
        this.reqLevel = reqLevel;
    }

    public List<Pair<Integer, Integer>> getTempOptions() {
        return tempOptions;
    }

    public void setTempOptions(List<Pair<Integer, Integer>> tempOptions) {
        this.tempOptions = tempOptions;
    }

    public void addTempOption(Pair<Integer, Integer> tempOption) {
        tempOptions.add(tempOption);
    }
}

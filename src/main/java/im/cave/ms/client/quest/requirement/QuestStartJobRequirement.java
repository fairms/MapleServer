package im.cave.ms.client.quest.requirement;

import im.cave.ms.client.character.MapleCharacter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on 3/2/2018.
 */
public class QuestStartJobRequirement implements QuestStartRequirement {
    private final Set<Short> jobReq;

    public QuestStartJobRequirement() {
        this.jobReq = new HashSet<>();
    }

    public Set<Short> getJobReq() {
        return jobReq;
    }

    public void addJobReq(short job) {
        getJobReq().add(job);
    }

    @Override
    public boolean hasRequirements(MapleCharacter chr) {
        return getJobReq().contains(chr.getJobId());
    }
}

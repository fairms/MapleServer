package im.cave.ms.client.multiplayer.party;

import im.cave.ms.client.field.MapleMap;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.enums.PartyQuestType;
import im.cave.ms.scripting.ScriptException;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.Util;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 问题来了 如何在有人切换地图的时候判断组队任务是否结束
 *
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client.multiplayer.party
 * @date 2/23 16:16
 */
@Getter
@Setter
public class PartyQuest {
    private static final AtomicLong ids = new AtomicLong(DateUtil.getDate() * 10000L);
    private long id;
    private int world;
    private int channel;
    private PartyQuestType type;
    private int progress;
    private Party party;
    private boolean[] stages; //用来表示关卡是否通过
    private List<MapleMap> maps = new ArrayList<>();

    private String param1;
    private int param2;
    private boolean param3;

    public PartyQuest(PartyQuestType type, Party party) {
        this.id = ids.getAndIncrement();
        this.world = party.getWorld().getId();
        this.channel = party.getPartyLeader().getChannel();
        this.type = type;
        this.stages = new boolean[10];
        this.party = party;
    }

    public void broadcast(OutPacket out) {
        for (PartyMember member : party.getOnlineMembers()) {
            if (member.getPartyQuest() == this) {
                member.getChr().announce(out);
            }
        }
    }

    public void addMap(MapleMap map) {
        maps.add(map);
    }

    public List<PartyMember> getCharInProgress() {
        return party.getOnlineMembers().stream().filter(pm -> pm.getPartyQuest() != null).collect(Collectors.toList());
    }

    public void dispose() {
        for (MapleMap map : maps) {
            party.removeMap(map);
            map = null;
        }
        for (PartyMember member : party.getMembers()) {
            member.setPartyQuest(null);
        }
        party.getWorld().removePQ(this);
    }

    public boolean pass(int stage) throws ScriptException {
        if (stage <= 0 || stage >= stages.length) {
            throw new ScriptException("Index Out stage应大于0小于10");
        }
        stages[stage - 1] = true;
        return true;
    }

    public boolean hasPassed(int stage) throws ScriptException {
        if (stage >= stages.length || stage <= 0) {
            throw new ScriptException("Index Out stage应大于0小于10");
        }
        return stages[stage - 1];
    }

    public MapleMap getMap(int mapId) {
        return Util.findWithPred(maps, map -> map.getId() == mapId);
    }
}

package im.cave.ms.scripting;


import im.cave.ms.client.MapleClient;
import im.cave.ms.client.Record;
import im.cave.ms.client.RecordManager;
import im.cave.ms.client.Clock;
import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.FieldEffect;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.Portal;
import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.client.multiplayer.guilds.Guild;
import im.cave.ms.client.multiplayer.party.Party;
import im.cave.ms.client.multiplayer.party.PartyMember;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.connection.packet.result.GuildResult;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.ClockType;
import im.cave.ms.enums.JobType;
import im.cave.ms.enums.PartyQuestType;
import im.cave.ms.enums.RecordType;
import im.cave.ms.scripting.npc.NpcScriptManager;
import im.cave.ms.tools.Pair;

import java.util.Calendar;
import java.util.Set;

public class AbstractPlayerInteraction {

    public MapleClient c;

    public AbstractPlayerInteraction(MapleClient c) {
        this.c = c;
    }

    public MapleClient getClient() {
        return c;
    }

    public MapleCharacter getChar() {
        return c.getPlayer();
    }

    public int getJob() {
        return getChar().getJob();
    }

    public int getLevel() {
        return getChar().getLevel();
    }

    public MapleMap getMap() {
        return getChar().getMap();
    }

    public int getHourOfDay() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public void serverMsg(String content) {
        c.announce(WorldPacket.chatMessage(content, ChatType.Tip));
    }

    public void dropMessage(Integer content) {
        c.announce(WorldPacket.chatMessage(String.valueOf(content), ChatType.Tip));
    }

    public void dropMessage(String content) {
        getChar().dropMessage(content);
    }

    public boolean forceCompleteQuest(int questId) {
        getChar().getQuestManager().completeQuest(questId);
        return true;
    }

    public boolean forceStartQuest(int questId) {
        getChar().getQuestManager().addQuest(questId);
        return true;
    }

    public void openUnityPortal() {
        c.announce(WorldPacket.unityPortal());
    }

    public void warp(MapleMap map) {
        getChar().changeMap(map, 0);
    }

    public void warp(int mapId) {
        getChar().changeMap(mapId);
    }

    public void warp(int mapId, String portalName) {
        MapleCharacter player = getChar();
        Portal portal = player.getMap().getPortal(portalName);
        player.changeMap(mapId, portal != null ? portal.getId() : 0);
    }

    public void warp(int mapId, int portal) {
        getChar().changeMap(mapId, (byte) portal);
    }

    public int getMapId() {
        return getChar().getMapId();
    }


    public void updateRecord(RecordType type, int key, int value) {
        RecordManager recordManager = getChar().getRecordManager();
        Record record = recordManager.getRecord(type, key);
        if (record == null) {
            record = Record.builder()
                    .type(type)
                    .key(key)
                    .value(value)
                    .lastReset(System.currentTimeMillis())
                    .lastUpdated(System.currentTimeMillis())
                    .build();
        } else {
            record.setValue(value);
        }
        recordManager.addRecord(record);
    }

    public void updateRecord(String typeStr, int key, int value) throws ScriptException {
        RecordType type = RecordType.getByName(typeStr);
        if (type == null) {
            throw new ScriptException("记录类型名称错误.");
        }
        updateRecord(type, key, value);
    }

    public int getRecordValue(String typeStr, int key) throws ScriptException {
        RecordType type = RecordType.getByName(typeStr);
        if (type == null) {
            throw new ScriptException("记录类型名称错误.");
        }
        return getRecordValue(type, key);
    }

    public int getRecordValue(RecordType type, int key) {
        RecordManager recordManager = getChar().getRecordManager();
        Record record = recordManager.getRecord(type, key);
        return record != null ? record.getValue() : 0;
    }


    public void openUI() {
        MapleCharacter player = getChar();
        c.announce(WorldPacket.openUI(player.getCombo()));
        player.setCombo(player.getCombo() + 1);
    }

    public Set<JobType> getAdvancedJobs(int jobId) {
        return JobType.getAdvancedJobs(jobId);
    }

    public int getJobReqLev(int jobId) {
        return JobConstants.getJobReqLev(jobId);
    }

    public boolean changeJob(int jobId) {
        return getChar().changeJob(jobId);
    }

    public int findSPNearNpc(int mapId, int npcId) {
        MapleMap map = getChar().getChannel().getMap(mapId);
        Npc npc = map.getNpcById(npcId);
        Portal portal = null;
        if (npc != null) {
            portal = map.getSpawnPortalNearby(npc.getPosition());
        }
        return portal != null ? portal.getId() : 0;
    }

    public void runNPCScript(String script, int npcID) {
        NpcScriptManager.getInstance().start(c, npcID, script);
    }

    public void deductMesos(long mesos) {
        getChar().deductMoney(mesos);
        getChar().write(UserPacket.incMoneyMessage((int) -mesos));
    }

    public void incrementMaxGuildMembers(int amount) {
        MapleCharacter chr = getChar();
        Guild guild = chr.getGuild();
        guild.incMaxMembers(amount);
        guild.broadcast(WorldPacket.guildResult(GuildResult.incMaxMemberNum(guild)));
    }


    /**
     * @param minMembers 要求最低人数
     * @param maxMembers 要求最大人数
     * @param minLevel   最低等级
     * @param maxLevel   最高等级
     * @param sameMap    是否需要在同一地图
     * @return 返回结果
     */
    public Pair<Boolean, String> partyRequireCheck(int minMembers, int maxMembers, int minLevel, int maxLevel, boolean sameMap) {
        MapleCharacter chr = getChar();
        Party party = chr.getParty();
        MapleMap map = chr.getMap();
        boolean success = true;
        String msg = null;
        if (party == null) {
            success = false;
            msg = String.format("你需要一个%d~%d人的组队,并且等级在%d~%d范围,那么请让你的队长和我对话吧!", minMembers, maxMembers, minLevel, maxLevel);
            return new Pair<>(false, msg);
        }
        if (party.getPartyLeaderId() != chr.getId()) {
            success = false;
            msg = "请让你的队长和我对话.";
        }
        if (success && party.getMembers().size() < minMembers || party.getMembers().size() > maxMembers) {
            success = false;
            msg = String.format("你需要一个%d~%d人的组队,请检查队伍人数.", minMembers, maxMembers);
        }
        if (success) {
            for (PartyMember member : party.getMembers()) {
                if (member.getLevel() < minLevel) {
                    success = false;
                    msg = String.format("%s,等级不足.", member.getCharName());
                } else if (member.getLevel() > maxLevel) {
                    success = false;
                    msg = String.format("%s,等级超过限制.", member.getCharName());
                }
            }
        }
        if (success && party.getOnlineMembers().size() != party.getMembers().size()) {
            success = false;
            msg = "你有队友不在身边,请集合后在和我对话.";
        }
        if (success && sameMap) {
            for (MapleCharacter mc : party.getOnlineChar()) {
                if (!mc.getMap().equals(map)) {
                    success = false;
                    msg = "你有队友不在身边,请集合后在和我对话.";
                }
            }
        }
        return new Pair<>(success, msg);
    }

    public void beginClock(int seconds) {
        Clock.startTimer(getChar(), ClockType.SecondsClock, seconds);
    }

    public void fieldEffect(FieldEffect effect) {
        getChar().getMap().broadcastMessage(WorldPacket.fieldEffect(effect));
    }

    public boolean hasInProgress(PartyQuestType type) {
        Party party = getChar().getParty();
        int channel = party.getPartyLeader().getChannel();
        return party.getWorld().hasInProgress(channel, type);
    }

    public void disablePortal(String portalName) {
        getChar().getMap().disablePortal(portalName);
    }
}
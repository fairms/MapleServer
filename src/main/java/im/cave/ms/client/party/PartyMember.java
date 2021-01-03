package im.cave.ms.client.party;

import im.cave.ms.client.character.MapleCharacter;


public class PartyMember {

    private MapleCharacter chr;
    private int partyBossCharId;
    private int charId;
    private String name;
    private short job;
    private short subJob;
    private int level;
    private int channel;
    private int mapId;
    private TownPortal townPortal;

    public PartyMember(MapleCharacter chr) {
        this.chr = chr;
        updateInfoByChar(chr);
    }

    public int getCharId() {
        return charId;
    }

    public void charId(int charId) {
        this.charId = charId;
    }

    public String getCharName() {
        return name;
    }

    public short getJob() {
        return job;
    }

    public void setJob(short job) {
        this.job = job;
    }

    public short getSubSob() {
        return subJob;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isOnline() {
        return chr != null && chr.isOnline();
    }

    public MapleCharacter getChr() {
        return chr;
    }

    public void setChr(MapleCharacter chr) {
        this.chr = chr;
    }

    public int getPartyBossCharId() {
        return partyBossCharId;
    }

    public void setPartyBossCharId(int partyBossCharId) {
        this.partyBossCharId = partyBossCharId;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PartyMember && ((PartyMember) obj).getChr().equals(getChr());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubJob(short subJob) {
        this.subJob = subJob;
    }

    public void updateInfoByChar(MapleCharacter chr) {
        if (chr != null) {
            setChr(chr);
            charId(chr.getId());
            setName(chr.getName());
            setJob(chr.getJob().getJobId());
            setSubJob((short) 0);
            setLevel(chr.getLevel());
            setChannel(chr.getClient().getChannel());
            setMapId(chr.getMapId());
        } else {
            setMapId(0);
        }
    }

    public TownPortal getTownPortal() {
        return townPortal;
    }
}

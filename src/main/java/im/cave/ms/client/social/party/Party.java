package im.cave.ms.client.social.party;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.WorldPacket;
import im.cave.ms.network.server.world.World;
import im.cave.ms.provider.data.MapData;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class Party {
    private final PartyMember[] partyMembers = new PartyMember[6];
    private final Map<Integer, MapleMap> maps = new HashMap<>();
    private int id;
    private boolean appliable;
    private String name;
    private int partyLeaderId;
    private World world;
    private MapleCharacter applyingChar;

    public static Party createNewParty(boolean appliable, String name, World world) {
        Party party = new Party();
        party.setAppliable(appliable);
        party.setName(name);
        party.setWorld(world);
        world.addParty(party);
        return party;
    }

    public boolean isAppliable() {
        return appliable;
    }

    public void setAppliable(boolean appliable) {
        this.appliable = appliable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PartyMember[] getPartyMembers() {
        return partyMembers;
    }

    public boolean isFull() {
        return Arrays.stream(getPartyMembers()).noneMatch(Objects::isNull);
    }

    public boolean isEmpty() {
        return Arrays.stream(getPartyMembers()).allMatch(Objects::isNull);
    }

    public void encode(OutPacket out) {
        for (PartyMember pm : partyMembers) {
            out.writeInt(pm != null ? pm.getCharId() : 0);
        }
        for (PartyMember pm : partyMembers) {
            out.writeAsciiString(pm != null ? pm.getCharName() : "", 13);
        }
        for (PartyMember pm : partyMembers) {
            out.writeInt(pm != null ? pm.getJob() : 0);
        }
        for (PartyMember pm : partyMembers) {
            out.writeInt(pm != null ? pm.getSubSob() : 0);
        }
        for (PartyMember pm : partyMembers) {
            out.writeInt(pm != null ? pm.getLevel() : 0);
        }
        for (PartyMember pm : partyMembers) {
            out.writeInt(pm != null ? pm.getChannel() - 1 : -1);
        }
        for (PartyMember pm : partyMembers) {
            out.writeInt(pm != null && pm.isOnline() ? 1 : 0);
        }
        for (PartyMember pm : partyMembers) {
            out.writeInt(0);
        }
        out.writeInt(getPartyLeaderId());
        for (PartyMember pm : partyMembers) {
            out.writeInt(pm != null ? pm.getMapId() : 0);
        }
        for (PartyMember pm : partyMembers) {
            if (pm != null && pm.getTownPortal() != null) {
                pm.getTownPortal().encode(out);
            } else {
                new TownPortal().encode(out);
            }
            // TownPortal == 16 bytes
            out.write(0);
            out.write(0);
            out.write(0);
            out.write(0);
        }
        out.writeBool(isAppliable() && !isFull());
        out.write(0);
        out.writeMapleAsciiString(getName());
        //todo unk
        out.writeZeroBytes(7);
        out.writeInt(2);
        out.writeZeroBytes(20);
        out.write(2);
        out.writeZeroBytes(20);
    }

    public int getPartyLeaderId() {
        return partyLeaderId;
    }

    public void setPartyLeaderID(int partyLeaderId) {
        this.partyLeaderId = partyLeaderId;
    }

    public void addPartyMember(MapleCharacter chr) {
        if (isFull()) {
            return;
        }
        PartyMember pm = new PartyMember(chr);
        if (isEmpty()) {
            setPartyLeaderID(chr.getId());
        }
        PartyMember[] partyMembers = getPartyMembers();
        boolean added = false;
        for (int i = 0; i < partyMembers.length; i++) {
            if (partyMembers[i] == null) {
                partyMembers[i] = pm;
                chr.setParty(this);
                chr.setPartyId(getId());
                added = true;
                break;
            }
        }
        if (added && chr.getId()
                != partyLeaderId) {
            broadcast(WorldPacket.partyResult(PartyResult.joinParty(this, chr.getName())));
        }
        updateHp();
    }

    private void updateHp() {
        List<MapleCharacter> onlineChar = getOnlineChar();
        for (MapleCharacter chr : onlineChar) {
            Set<MapleCharacter> membersInSameField = getPartyMembersInSameField(chr);
            for (MapleCharacter other : membersInSameField) {
                other.announce(WorldPacket.updatePartyHpInfo(chr));
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PartyMember getPartyLeader() {
        return Arrays.stream(getPartyMembers()).filter(p -> p != null && p.getCharId() == getPartyLeaderId()).findFirst().orElse(null);
    }

    public boolean hasCharAsLeader(MapleCharacter chr) {
        return getPartyLeaderId() == chr.getId();
    }

    public void disband() {
        broadcast(WorldPacket.partyResult(PartyResult.withdrawParty(this, getPartyLeader(), false, false)));
        for (MapleCharacter chr : getOnlineChar()) {
            chr.setParty(null);
        }
        Arrays.fill(getPartyMembers(), null);
        getWorld().removeParty(this);
        setWorld(null);
    }

    public List<MapleCharacter> getOnlineChar() {
        return getOnlineMembers().stream().filter(pm -> pm.getChr() != null).map(PartyMember::getChr).collect(Collectors.toList());
    }

    public List<PartyMember> getOnlineMembers() {
        return Arrays.stream(getPartyMembers()).filter(pm -> pm != null && pm.isOnline()).collect(Collectors.toList());
    }

    public List<PartyMember> getMembers() {
        return Arrays.stream(getPartyMembers()).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void updateFull() {
        broadcast(WorldPacket.partyResult(PartyResult.loadParty(this)));
    }

    public PartyMember getPartyMemberByID(int charId) {
        return Arrays.stream(getPartyMembers()).filter(p -> p != null && p.getCharId() == charId).findFirst().orElse(null);
    }

    public void broadcast(OutPacket out) {
        for (PartyMember pm : getOnlineMembers()) {
            pm.getChr().announce(out);
        }
    }

    public void broadcast(OutPacket out, MapleCharacter exceptMapleCharacter) {
        for (PartyMember pm : getOnlineMembers()) {
            if (!pm.getChr().equals(exceptMapleCharacter)) {
                pm.getChr().announce(out);
            }
        }
    }

    public void removePartyMember(PartyMember partyMember) {
        for (int i = 0; i < getPartyMembers().length; i++) {
            PartyMember pm = getPartyMembers()[i];
            if (pm != null && pm.equals(partyMember)) {
                pm.getChr().setParty(null);
                pm.getChr().setPartyId(0);
                getPartyMembers()[i] = null;
                break;
            }
        }
    }

    public void expel(int expelID) {
        PartyMember leaver = getPartyMemberByID(expelID);
        broadcast(WorldPacket.partyResult(PartyResult.withdrawParty(this, leaver, true, true)));
        removePartyMember(leaver);
        updateFull();
    }

    public int getAvgLevel() {
        Collection<PartyMember> partyMembers = getMembers();
        return partyMembers.stream()
                .mapToInt(pm -> pm.getChr().getLevel())
                .sum() / partyMembers.size();
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public MapleCharacter getapplyingChar() {
        return applyingChar;
    }

    public void setApplyingChar(MapleCharacter applyingChar) {
        this.applyingChar = applyingChar;
    }

    public Map<Integer, MapleMap> getMaps() {
        return maps;
    }

    public void addMap(MapleMap field) {
        getMaps().put(field.getId(), field);
    }

    /**
     * Clears the current Fields. Will return any MapleCharacteracter that is currently on any of the Fields to the Field's return field.
     *
     * @param warpToId the field id that all MapleCharacters should be warped to
     */
    public void clearFieldInstances(int warpToId) {
        Set<MapleCharacter> chars = new HashSet<>();
        for (MapleMap f : getMaps().values()) {
            chars.addAll(f.getCharacters());
        }
        for (MapleCharacter chr : chars) {
            int returnMap = warpToId == 0 ? chr.getMap().getForcedReturn() : warpToId;
            if (returnMap != GameConstants.NO_MAP_ID) {
                chr.changeMap(warpToId);
            }
        }
        getMaps().clear();
    }

    /**
     * Returns the Field corresponding to the provided fieldID. If there is none, creates one.
     *
     * @param mapId The Field's id.
     * @return The Field corresponding to the given id.
     */
    public MapleMap getOrCreateFieldById(int mapId) {
        if (getMaps().containsKey(mapId)) {
            return getMaps().get(mapId);
        } else {
            MapleMap field = MapData.loadMapDataFromWz(mapId, world.getId(), getPartyLeader().getChannel());
            addMap(field);
            return field;
        }
    }

    public boolean isPartyMember(MapleCharacter chr) {
        return getPartyMemberByID(chr.getId()) != null;
    }

    public void updatePartyMemberInfoByChr(MapleCharacter chr) {
        if (!isPartyMember(chr)) {
            return;
        }
        getPartyMemberByID(chr.getId()).updateInfoByChar(chr);
        updateFull();
    }

    /**
     * Returns the average party member's level, according to the given MapleCharacter's field.
     *
     * @param chr the chr to get the map to
     * @return the average level of the party in the MapleCharacter's field
     */
    public int getAvgPartyLevel(MapleCharacter chr) {
        MapleMap map = chr.getMap();
        return (int) getOnlineMembers().stream().filter(om -> om.getChr().getMap() == map)
                .mapToInt(PartyMember::getLevel).average().orElse(chr.getLevel());
    }

    /**
     * Gets a list of party members in the same Field instance as the given MapleCharacter, excluding the given MapleCharacter.
     *
     * @param chr the given MapleCharacter
     * @return a set of MapleCharacteracters that are in the same field as the given MapleCharacter
     */
    public Set<MapleCharacter> getPartyMembersInSameField(MapleCharacter chr) {
        return getOnlineMembers().stream()
                .filter(pm -> pm.getChr() != null && pm.getChr() != chr && pm.getChr().getMap() == chr.getMap())
                .map(PartyMember::getChr)
                .collect(Collectors.toSet());
    }

    /**
     * Checks if this Party has a member with the given MapleCharacteracter id.
     *
     * @param charId the charId to look for
     * @return if the corresponding MapleCharacter is in the party
     */
    public boolean hasPartyMember(int charId) {
        return getPartyMemberByID(charId) != null;
    }

    public TownPortal getTownPortal() {
        PartyMember pm = Arrays.stream(getPartyMembers()).filter(Objects::nonNull)
                .filter(p -> p.getTownPortal() != null)
                .findFirst().orElse(null);
        return pm != null ? pm.getTownPortal() : new TownPortal();
    }

}

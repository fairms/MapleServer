package im.cave.ms.client.character;

import im.cave.ms.client.Account;
import im.cave.ms.client.Job.MapleJob;
import im.cave.ms.client.Job.adventurer.Beginner;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.Portal;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.items.ItemInfo;
import im.cave.ms.client.items.SpecStat;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.client.skill.SkillInfo;
import im.cave.ms.client.skill.SkillStat;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.BaseStat;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.EquipAttribute;
import im.cave.ms.enums.EquipSpecialAttribute;
import im.cave.ms.enums.InventoryOperation;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.MapleTraitType;
import im.cave.ms.net.db.DataBaseManager;
import im.cave.ms.net.packet.ChannelPacket;
import im.cave.ms.net.packet.MaplePacketCreator;
import im.cave.ms.net.packet.PlayerPacket;
import im.cave.ms.net.server.Server;
import im.cave.ms.net.server.channel.MapleChannel;
import im.cave.ms.net.server.world.World;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Util;
import im.cave.ms.tools.data.output.MaplePacketLittleEndianWriter;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static im.cave.ms.enums.InventoryOperation.REMOVE;
import static im.cave.ms.enums.InventoryOperation.UPDATE_QUANTITY;
import static im.cave.ms.enums.InventoryType.CASH;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client
 * @date 11/19 17:28
 */
@Getter
@Setter
@Entity
@Table(name = "`character`")
public class MapleCharacter implements Serializable {
    @Transient
    private static final Logger log = LoggerFactory.getLogger(MapleCharacter.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int world;
    @Transient
    private int channel;
    @Column(name = "accId")
    private int accId;
    @Transient
    private MapleClient client;
    @Transient
    private Account account;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "charstats")
    private CharStats stats;
    private int face, hair, decorate = 0;
    private byte gender = 0;
    private int remainingAp = 0;
    @Enumerated(EnumType.ORDINAL)
    private JobConstants.JobEnum job = JobConstants.JobEnum.BEGINNER;
    @Transient
    private MapleMap map;
    @Column(name = "map")
    private int mapId;
    private int party;
    private String remainingSp;
    private byte buddyCapacity, skin, hairColorBase = -1, hairColorMixed, hairColorProb, gm = 0, spawnPoint = 0;
    private boolean isDeleted;
    private Long deleteTime = 0L;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "equippedInventory")
    private Inventory equippedInventory = new Inventory(InventoryType.EQUIPPED, (byte) 32);
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipInventory")
    private Inventory equipInventory = new Inventory(InventoryType.EQUIP, (byte) 32);
    @JoinColumn(name = "consumeInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory consumeInventory = new Inventory(InventoryType.CONSUME, (byte) 32);
    @JoinColumn(name = "installInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory installInventory = new Inventory(InventoryType.INSTALL, (byte) 32);
    @JoinColumn(name = "etcInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory etcInventory = new Inventory(InventoryType.ETC, (byte) 32);
    @JoinColumn(name = "cashInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory cashInventory = new Inventory(CASH, (byte) 60);
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MapleQuickslotBinding quickSlot;
    @JoinColumn(name = "charId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Skill> skills;
    @Transient
    private MapleKeyBinding keyBinding;
    @Transient
    private MapleJob jobHandler;
    @Transient
    private transient boolean isChangingChannel = false;
    private transient List<Integer> visitedMaps = new ArrayList<>();
    private transient String blessOfFairyOrigin;
    private transient String blessOfEmpressOrigin;
    @Transient
    private final Map<Integer, String> entered = new HashMap<>();
    private transient boolean isConversation = false;
    @Transient
    private Position position;
    @Transient
    private Map<BaseStat, Long> baseStats = new HashMap<>();
    @Transient
    private Map<Integer, Integer> hyperPsdSkillsCooltimeR = new HashMap<>();
    private transient int tick;
    private transient short foothold;
    private transient byte moveAction;
    private transient int chairId;


    public MapleCharacter() {
        keyBinding = new MapleKeyBinding();
    }

    public static MapleCharacter getCharByName(String name) {
        return (MapleCharacter) DataBaseManager.getObjFromDB(MapleCharacter.class, "name", name);
    }

    public static int nameCheck(String name) {
        MapleCharacter character = MapleCharacter.getCharByName(name);
        if (character != null) {
            return 1;
        }
        return Pattern.compile("[a-zA-Z0-9\\u4e00-\\u9fa5]{2,12}").matcher(name).matches() ? 0 : 2;
    }

    public static MapleCharacter getDefault(JobConstants.JobEnum job) {
        MapleCharacter character = new MapleCharacter();
        character.setJob(job);
        character.setBuddyCapacity((byte) 20);
        character.setStats(new CharStats());
        character.setRemainingSp("");
        return character;
    }


    public boolean hasDecorate() {
        return decorate > 0;
    }

    public Inventory getInventory(InventoryType type) {
        switch (type) {
            case EQUIPPED:
                return equippedInventory;
            case EQUIP:
                return equipInventory;
            case CONSUME:
                return consumeInventory;
            case INSTALL:
                return installInventory;
            case ETC:
                return etcInventory;
            case CASH:
                return cashInventory;
            default:
                return null;
        }
    }

    public int getJobId() {
        return job.getJobId();
    }

    public int[] getRemainingSps() {
        String remainingSp = getRemainingSp();
        String[] sps = remainingSp.split(","); //0,1,2,3
        int size = 0;
        for (String sp : sps) {
            if (!Util.isNumber(sp)) {
                continue;
            }
            size++;
        }
        int[] remainingSps = new int[size];
        for (int i = 0; i < sps.length; i++) {
            if (!Util.isNumber(sps[i])) {
                continue;
            }
            int sp = Integer.parseInt(sps[i]);
            remainingSps[i] = sp;
        }
        return remainingSps;
    }


    public int getRemainingSpsSize() {
        int[] remainingSps = getRemainingSps();
        int i = 0;
        for (int sp : remainingSps) {
            if (sp > 0) {
                i++;
            }
        }
        return i;
    }


    public void logout() {
        List<World> worlds = Server.getInstance().getWorlds();
        for (World world : worlds) {
            for (MapleChannel channel : world.getChannels()) {
                channel.removePlayer(this);
            }
        }
        saveToDB();
    }

    public void logout(int channel) {
        MapleChannel prevChannel = Server.getInstance().getWorldById(world).getChannel(channel);
        prevChannel.removePlayer(this);
        saveToDB();
    }

    public void putItem(Item item) {
        putItem(item, false);
    }

    public void putItem(Item item, boolean hasCorrectPos) {
        if (item == null) {
            return;
        }
        Inventory inventory = getInventory(item.getInvType());
        ItemInfo ii = ItemData.getItemById(item.getItemId());
        int quantity = item.getQuantity();
        if (inventory != null) {
            Item existingItem = inventory.getItemByItemIDAndStackable(item.getItemId());
            boolean rec = false;
            if (existingItem != null && existingItem.getInvType().isStackable() && existingItem.getQuantity() < ii.getSlotMax()) {
                if (quantity + existingItem.getQuantity() > ii.getSlotMax()) {
                    quantity = ii.getSlotMax() - existingItem.getQuantity();
                    item.setQuantity(item.getQuantity() - quantity);
                    rec = true;
                }
                existingItem.addQuantity(quantity);
                announce(PlayerPacket.inventoryOperation(true, false, InventoryOperation.UPDATE_QUANTITY, (short) existingItem.getPos(), (short) -1, 0, existingItem));
                if (rec) {
                    putItem(item);
                }
            } else {
                if (!hasCorrectPos) {
                    item.setPos(inventory.getNextFreeSlot());
                }
                Item itemCopy = null;
                if (item.getInvType().isStackable() && ii != null && item.getQuantity() > ii.getSlotMax()) {
                    itemCopy = item.deepCopy();
                    quantity = quantity - ii.getSlotMax();
                    itemCopy.setQuantity(quantity);
                    item.setQuantity(ii.getSlotMax());
                    rec = true;
                }
                inventory.addItem(item);
                announce(PlayerPacket.inventoryOperation(true, false, InventoryOperation.ADD, (short) item.getPos(), (short) -1, 0, item));
                if (rec) {
                    putItem(itemCopy);
                }
            }
        }
    }

    public long getExp() {
        return stats.getExp();
    }

    public int getFatigue() {
        return stats.getFatigue();
    }

    public int getLevel() {
        return stats.getLevel();
    }

    public int getWeaponPoint() {
        return stats.getWeaponPoint();
    }

    public void saveToDB() {
        DataBaseManager.saveToDB(this);
    }

    public int getFame() {
        return stats.getFame();
    }

    public int getStr() {
        return stats.getStr();
    }

    public int getDex() {
        return stats.getDex();
    }

    public int getInt_() {
        return stats.getInt_();
    }

    public int getLuk() {
        return stats.getLuk();
    }

    public int getHp() {
        return stats.getHp();
    }

    public int getMaxHP() {
        return stats.getMaxHP();
    }

    public int getMaxMP() {
        return stats.getMaxMP();
    }

    public int getMp() {
        return stats.getMp();
    }

    public int getTraitTotalExp(MapleTraitType type) {
        switch (type) {
            case will:
                return stats.getWillExp();
            case charm:
                return stats.getCharmExp();
            case craft:
                return stats.getCraftExp();
            case sense:
                return stats.getSenseExp();
            case insight:
                return stats.getInsightExp();
            case charisma:
                return stats.getCharismaExp();
            default:
                return 0;
        }
    }

    public int getVisitedMapCount() {
        return visitedMaps.size();
    }

    public long getMeso() {
        return stats.getMeso();
    }

    public MapleMap getMap() {
        MapleChannel channel = Server.getInstance().getWorldById(world).getChannel(this.channel);
        return channel.getMap(mapId);
    }

    public boolean hasItemCount(int itemID, int requiredCount) {
        ItemInfo item = ItemData.getItemById(itemID);
        Inventory inventory = getInventory(item.getInvType());
        return inventory.getItems().stream()
                .filter(i -> i.getItemId() == itemID)
                .mapToInt(Item::getQuantity)
                .sum() >= requiredCount;
    }

    public boolean hasEntered(String mapScriptPath, int mapId) {
        String s = entered.get(mapId);
        return mapScriptPath.equals(s);
    }

    public void enteredScript(String mapScriptPath, int mapId) {
        entered.put(mapId, mapScriptPath);
    }

    public void changeMap(MapleMap map, Portal portal) {
        // npcs clear?
        if (getMap() != null) {
            getMap().removePlayer(this);
        }
        setJob(this.job);
        setMap(map);
        announce(ChannelPacket.getWarpToMap(this, map, portal == null ? 0 : portal.getId()));
        map.addPlayer(this);
    }

    public void announce(MaplePacketLittleEndianWriter mplew) {
        client.announce(mplew);
    }

    public boolean equip(Item item) {
        Equip equip = (Equip) item;
        if (equip.hasSpecialAttribute(EquipSpecialAttribute.Vestige)) {
            return false;
        }
        if (equip.isEquipTradeBlock()) {
            equip.setTradeBlock(true);
            equip.setEquipTradeBlock(false);
            equip.addAttribute(EquipAttribute.Untradable);
        }
        getEquipInventory().removeItem(item);
        getEquippedInventory().addItem(item);
        return true;
    }

    public void unequip(Item item) {
        getEquippedInventory().removeItem(item);
        getEquipInventory().addItem(item);
    }


    @Override
    public String toString() {
        return "Char{" +
                "(" + super.toString() +
                ")id=" + id +
                ", accId=" + accId +
                ", name=" + getName() +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MapleCharacter && ((MapleCharacter) other).getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void chatMessage(ChatType type, String content) {
        announce(ChannelPacket.chatMessage(content, type));
    }


    public void addStat(MapleStat stat, int amount) {
        setStat(stat, getStat(stat) + amount);
    }

    public int getStat(MapleStat stat) {
        switch (stat) {
            case STR:
                return stats.getStr();
            case DEX:
                return stats.getDex();
            case INT:
                return stats.getInt_();
            case LUK:
                return stats.getLuk();
            case HP:
                return stats.getHp();
            case MAXHP:
                return stats.getMaxHP();
            case MP:
                return stats.getMp();
            case MAXMP:
                return stats.getMaxMP();
            case AVAILABLEAP:
                return getRemainingAp();
            case LEVEL:
                return stats.getLevel();
            case SKIN:
                return getSkin();
            case FACE:
                return getFace();
            case HAIR:
                return getHair();
            case FAME:
                return stats.getFame();
            case CHARISMA:
                return stats.getCharismaExp();
            case CHARM:
                return stats.getCharmExp();
            case CRAFT:
                return stats.getCraftExp();
            case INSIGHT:
                return stats.getInsightExp();
            case SENSE:
                return stats.getSenseExp();
            case WILL:
                return stats.getWillExp();
            case FATIGUE:
                return stats.getFatigue();
        }
        return -1;
    }

    public void setStat(MapleStat stat, int value) {
        switch (stat) {
            case STR:
                stats.setStr(value);
                break;
            case DEX:
                stats.setDex(value);
                break;
            case INT:
                stats.setInt_(value);
                break;
            case LUK:
                stats.setLuk(value);
                break;
            case HP:
                stats.setHp(value);
                break;
            case MAXHP:
                stats.setMaxHP(value);
                break;
            case MP:
                stats.setMp(value);
                break;
            case MAXMP:
                stats.setMaxMP(value);
                break;
            case AVAILABLEAP:
                setRemainingAp(value);
                break;
            case LEVEL:
                stats.setLevel(value);
                break;
            case SKIN:
                setSkin((byte) value);
                break;
            case FACE:
                setFace(value);
                break;
            case HAIR:
                setHair(value);
                break;
            case FAME:
                stats.setFame(value);
                break;
            case CHARISMA:
                stats.setCharismaExp(value);
                break;
            case CHARM:
                stats.setCharmExp(value);
                break;
            case CRAFT:
                stats.setCraftExp(value);
                break;
            case INSIGHT:
                stats.setInsightExp(value);
                break;
            case SENSE:
                stats.setSenseExp(value);
                break;
            case WILL:
                stats.setWillExp(value);
                break;
            case FATIGUE:
                stats.setFatigue(value);
                break;
        }
    }

    public void addDrop(Drop drop) {
        if (drop.isMeso()) {
            addMeso(drop.getMoney());
            announce(ChannelPacket.dropPickupMessage(drop.getMoney(), (short) 0, (short) 0));
        } else {
            Item item = drop.getItem();
            int itemId = item.getItemId();
            boolean isConsume = false;
            boolean isRunOnPickUp = false;
            if (!ItemConstants.isEquip(itemId)) {
                ItemInfo ii = ItemData.getItemById(itemId);
                isConsume = ii.getSpecStats().getOrDefault(SpecStat.consumeOnPickup, 0) != 0;
                isRunOnPickUp = ii.getSpecStats().getOrDefault(SpecStat.runOnPickup, 0) != 0;
            }
            if (isConsume) {
                announce(MaplePacketCreator.enableActions());
            } else if (isRunOnPickUp) {

            } else if (getInventory(item.getInvType()).canPickUp(item)) {
                if (item instanceof Equip) {
                    Equip equip = (Equip) item;
                    if (equip.hasAttribute(EquipAttribute.UntradableAfterTransaction)) {
                        equip.removeAttribute(EquipAttribute.UntradableAfterTransaction);
                        equip.addAttribute(EquipAttribute.Untradable);
                    }
                }
                putItem(item, false);
            }
        }
    }


    private void addMeso(int amount) {
        long meso = getMeso();
        long newMeso = meso + amount;
        if (newMeso >= 0) {
            newMeso = Math.min(GameConstants.MAX_MONEY, newMeso);
            Map<MapleStat, Long> stats = new HashMap<>();
            setMeso(newMeso);
            stats.put(MapleStat.MESO, newMeso);
            announce(MaplePacketCreator.updatePlayerStats(stats, this));
        }
    }

    private void setMeso(long newMeso) {
        stats.setMeso(newMeso);
    }

    public void addExp(long amount, ExpIncreaseInfo expIncreaseInfo) {
        if (amount <= 0) {
            return;
        }
        int level = getLevel();
        long curExp = getExp();
        if (level >= GameConstants.maxLevel) {
            return;
        }
        long newExp = curExp + amount;
        Map<MapleStat, Long> stats = new HashMap<>();
        while (newExp >= GameConstants.charExp[level]) {
            newExp -= GameConstants.charExp[level];
            addStat(MapleStat.LEVEL, 1);
            stats.put(MapleStat.LEVEL, (long) getStat(MapleStat.LEVEL));
            level++;
            getJobHandler().handleLevelUp();
//            getMap().broadcastMessage(ChannelPacket.effect(getId()));
            heal(getMaxHP());
            healMP(getMaxMP());
        }
        setExp(newExp);
        stats.put(MapleStat.EXP, newExp);
        if (expIncreaseInfo != null) {
            int expFromR = 0;
            expIncreaseInfo.setIndieBonusExp(expFromR);
            announce(ChannelPacket.incExpMessage(expIncreaseInfo));
        }
        announce(MaplePacketCreator.updatePlayerStats(stats, this));
    }

    private void setExp(long newExp) {
        stats.setExp(newExp);
    }

    public void consumeItem(Item item) {
        Inventory inventory = getInventory(item.getInvType());
        if (item.getQuantity() <= 1 && !ItemConstants.isThrowingItem(item.getItemId())) {
            item.setQuantity(0);
            inventory.removeItem(item);
            short pos = (short) item.getPos();
            announce(PlayerPacket.inventoryOperation(true, false, REMOVE, pos, (short) 0, 0, item));
        } else {
            item.setQuantity(item.getQuantity() - 1);
            announce(PlayerPacket.inventoryOperation(true, false, UPDATE_QUANTITY, (short) item.getPos(), (short) -1, 0, item));
        }
    }

    public void heal(int amount) {
        heal(amount, false);
    }

    public void heal(int amount, boolean mp) {
        int curHP = getHp();
        int maxHP = getMaxHP();
        int newHP = Math.min(curHP + amount, maxHP);
        Map<MapleStat, Long> stats = new HashMap<>();
        setStat(MapleStat.HP, newHP);
        stats.put(MapleStat.HP, (long) newHP);
        if (mp) {
            int curMP = getMp();
            int maxMP = getMaxMP();
            int newMP = Math.min(curMP + amount, maxMP);
            setStat(MapleStat.MP, newMP);
            stats.put(MapleStat.MP, (long) newMP);
        }
        announce(MaplePacketCreator.updatePlayerStats(stats, this));
    }

    public void healMP(int amount) {
        int curMP = getMp();
        int maxMP = getMaxMP();
        int newMP = Math.min(curMP + amount, maxMP);
        Map<MapleStat, Long> stats = new HashMap<>();
        setStat(MapleStat.MP, newMP);
        stats.put(MapleStat.MP, (long) newMP);
        announce(MaplePacketCreator.updatePlayerStats(stats, this));
    }

    public void addSpToJobByCurrentLevel(int num) {
        int[] remainingSps = getRemainingSps();
        byte jobLevel = (byte) JobConstants.getJobLevelByCharLevel(getJob().getJobId(), getLevel());
        if (jobLevel == 0) {
            return;
        }
        remainingSps[jobLevel - 1] = num;
        StringBuilder sb = new StringBuilder();
        for (int sp : remainingSps) {
            sb.append(sp);
            sb.append(",");
        }
        String r = sb.substring(0, sb.length() - 1);
        setRemainingSp(r);
    }


    public void setJob(JobConstants.JobEnum job) {
        if (job == null) {
            return;
        }
        this.job = job;
        setJobHandler(new Beginner(this));
    }

    public void changeMap(int mapId) {
        MapleChannel channel = Server.getInstance().getWorldById(world).getChannel(this.channel);
        MapleMap map = channel.getMap(mapId);
        if (map != null) {
            changeMap(map, map.getPortal("sp"));
        }
    }

    public Skill getSkill(int skillId) {
        return getSkill(skillId, false);
    }

    public Skill getSkill(int id, boolean createIfNull) {
        for (Skill s : getSkills()) {
            if (s.getSkillId() == id) {
                return s;
            }
        }
        return createIfNull ? createAndReturnSkill(id) : null;
    }

    private Skill createAndReturnSkill(int id) {
        Skill skill = SkillData.getSkill(id);
        addSkill(skill);
        return skill;
    }

    public void addSkill(Skill skill) {
        addSkill(skill, false);
    }


    public void addSkill(Skill skill, boolean addRegardlessOfLevel) {
        if (!addRegardlessOfLevel && skill.getCurrentLevel() == 0) {
            removeSkill(skill.getSkillId());
            return;
        }
        skill.setCharId(getId());
        boolean isPassive = SkillConstants.isPassiveSkill(skill.getSkillId());
        boolean isChanged;
        if (getSkills().stream().noneMatch(s -> s.getSkillId() == skill.getSkillId())) {
            getSkills().add(skill);
            isChanged = true;
        } else {
            Skill oldSkill = getSkill(skill.getSkillId());
            isChanged = oldSkill.getCurrentLevel() != skill.getCurrentLevel();
            if (isPassive && isChanged) {
                removeFromBaseStatCache(oldSkill);
            }
            oldSkill.setCurrentLevel(skill.getCurrentLevel());
            oldSkill.setMasterLevel(skill.getMasterLevel());
        }
        // Change cache accordingly
        if (isPassive && isChanged) {
            addToBaseStatCache(skill);
        }
    }

    private void addToBaseStatCache(Skill skill) {
        SkillInfo si = SkillData.getSkillInfo(skill.getSkillId());
        if (SkillConstants.isPassiveSkill(skill.getSkillId())) {
            Map<BaseStat, Integer> stats = si.getBaseStatValues(this, skill.getCurrentLevel(), skill.getSkillId());
            stats.forEach(this::addBaseStat);
        }
        if (si.isPsd() && si.getSkillStatInfo().containsKey(SkillStat.coolTimeR)) {
            for (int psdSkill : si.getPsdSkills()) {
                getHyperPsdSkillsCooltimeR().put(psdSkill, si.getValue(SkillStat.coolTimeR, 1));
            }
        }

    }

    public Map<Integer, Integer> getHyperPsdSkillsCooltimeR() {
        return hyperPsdSkillsCooltimeR;
    }


    public void removeFromBaseStatCache(Skill skill) {
        SkillInfo si = SkillData.getSkillInfo(skill.getSkillId());
        Map<BaseStat, Integer> stats = si.getBaseStatValues(this, skill.getCurrentLevel(), skill.getSkillId());
        stats.forEach(this::removeBaseStat);
    }

    public void removeBaseStat(BaseStat bs, int amount) {
        addBaseStat(bs, -amount);
    }

    public void addBaseStat(BaseStat bs, int amount) {
        getBaseStats().put(bs, getBaseStats().getOrDefault(bs, 0L) + amount);
    }

    public void removeSkill(int skillID) {
        Skill skill = Util.findWithPred(getSkills(), s -> s.getSkillId() == skillID);
        if (skill != null) {
            if (SkillConstants.isPassiveSkill(skillID)) {
                removeFromBaseStatCache(skill);
            }
            getSkills().remove(skill);

        }
    }

    public void setRemainingSp(String remainingSp) {
        this.remainingSp = remainingSp;
    }

    public void setRemainingSp(int[] remainingSps) {
        StringBuilder sb = new StringBuilder();
        for (int sp : remainingSps) {
            sb.append(sp);
            sb.append(",");
        }
        setRemainingSp(sb.substring(0, sb.length() - 1));
    }
}


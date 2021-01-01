package im.cave.ms.client.character;

import im.cave.ms.client.Account;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.character.potential.CharacterPotentialMan;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.Familiar;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.Portal;
import im.cave.ms.client.field.obj.Android;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.client.field.obj.npc.shop.NpcShop;
import im.cave.ms.client.field.obj.npc.shop.NpcShopItem;
import im.cave.ms.client.items.Equip;
import im.cave.ms.client.items.Inventory;
import im.cave.ms.client.items.Item;
import im.cave.ms.client.items.ItemInfo;
import im.cave.ms.client.job.JobManager;
import im.cave.ms.client.job.MapleJob;
import im.cave.ms.client.miniroom.MiniRoom;
import im.cave.ms.client.party.Party;
import im.cave.ms.client.quest.Quest;
import im.cave.ms.client.quest.QuestManager;
import im.cave.ms.client.skill.Skill;
import im.cave.ms.client.skill.SkillInfo;
import im.cave.ms.client.skill.SkillStat;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.BaseStat;
import im.cave.ms.enums.BodyPart;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.EquipAttribute;
import im.cave.ms.enums.EquipSpecialAttribute;
import im.cave.ms.enums.InventoryOperationType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.MapleTraitType;
import im.cave.ms.enums.MessageType;
import im.cave.ms.enums.SpecStat;
import im.cave.ms.network.db.DataBaseManager;
import im.cave.ms.network.db.InlinedIntArrayConverter;
import im.cave.ms.network.netty.OutPacket;
import im.cave.ms.network.packet.UserPacket;
import im.cave.ms.network.packet.UserRemote;
import im.cave.ms.network.packet.WorldPacket;
import im.cave.ms.network.server.Server;
import im.cave.ms.network.server.channel.MapleChannel;
import im.cave.ms.network.server.world.World;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.scripting.item.ItemScriptManager;
import im.cave.ms.tools.DateUtil;
import im.cave.ms.tools.Pair;
import im.cave.ms.tools.Position;
import im.cave.ms.tools.Rect;
import im.cave.ms.tools.Util;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static im.cave.ms.constants.GameConstants.NO_MAP_ID;
import static im.cave.ms.constants.QuestConstants.QUEST_DAMAGE_SKIN;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_MOB_KILL_COUNT;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_SKILL_STATE;
import static im.cave.ms.enums.ChatType.SystemNotice;
import static im.cave.ms.enums.InventoryOperationType.REMOVE;
import static im.cave.ms.enums.InventoryOperationType.UPDATE_QUANTITY;
import static im.cave.ms.enums.InventoryType.CASH;
import static im.cave.ms.enums.InventoryType.EQUIPPED;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.client
 * @date 11/19 17:28
 */
@Getter
@Setter
@Entity
@Table(name = "`characters`")
public class MapleCharacter implements Serializable {
    @Transient
    private static final Logger log = LoggerFactory.getLogger(MapleCharacter.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private byte world;
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
    private JobConstants.JobEnum job;
    @Transient
    private MapleMap map;
    @Column(name = "map")
    private int mapId;
    @Column(name = "party")
    private int partyId;
    @Transient
    private Party party;
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Integer> remainingSp;
    private byte buddyCapacity, skin, hairColorBase = -1, hairColorMixed, hairColorProb, spawnPoint = 0;
    private boolean gm;
    private boolean isDeleted;
    private Long deleteTime = 0L;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "equippedInventory")
    private Inventory equippedInventory = new Inventory(EQUIPPED, (byte) 32);
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
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Integer> quickslots;
    @JoinColumn(name = "charId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Skill> skills;
    @ElementCollection
    @CollectionTable(name = "skillcooldown", joinColumns = @JoinColumn(name = "charID"))
    @MapKeyColumn(name = "skillId")
    @Column(name = "nextUsableTime")
    private Map<Integer, Long> skillCooltimes;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "keymap")
    private MapleKeyMap keyMap;
    @JoinColumn(name = "questmanager")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private QuestManager questManager;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "charId")
    private Set<DamageSkinSaveData> damageSkins = new HashSet<>();
    private int damageSkinSlotSize = 1;
    @Transient
    private DamageSkinSaveData damageSkin;
    @Transient
    private DamageSkinSaveData premiumDamageSkin = new DamageSkinSaveData();
    @JoinColumn(name = "charID")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CharacterPotential> potentials;
    @Transient
    private CharacterPotentialMan potentialMan;
    @Transient
    private Map<Integer, Map<String, String>> questEx = new HashMap<>();
    @ElementCollection
    @CollectionTable(name = "quest_ex", joinColumns = @JoinColumn(name = "charId"))
    @MapKeyColumn(name = "questId")
    @Column(name = "qrValue")
    private Map<Integer, String> questsExStorage;
    @JoinColumn(name = "charId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Familiar> familiars = new HashSet<>();
    @Transient
    private Map<Integer, Pair<Long, ScheduledFuture>> cooltimes;
    @Transient
    private MapleJob jobHandler;
    @Transient
    private boolean isChangingChannel = false;
    private transient List<Integer> visitedMaps = new ArrayList<>();
    private transient String blessOfFairyOrigin;
    private transient String blessOfEmpressOrigin;
    @Transient
    private final Map<Integer, String> entered = new HashMap<>();
    @Transient
    private boolean isConversation = false;
    @Transient
    private MiniRoom miniRoom;
    @Transient
    private Position position;
    @Transient
    private Map<BaseStat, Long> baseStats = new HashMap<>();
    @Transient
    private Map<Integer, Integer> hyperPsdSkillsCooltimeR = new HashMap<>();
    @Transient
    private int tick;
    @Transient
    private short foothold;
    @Transient
    private byte moveAction;
    @Transient
    private int chairId;
    @Transient
    private TemporaryStatManager temporaryStatManager;
    @Transient
    private int combatOrders; //战斗命令
    @Transient
    private Set<MapleMapObj> visibleMapObjs = new HashSet<>();
    @Transient
    private Set<MapleCharacter> visibleChar = new HashSet<>();
    @Transient
    private long lastKill;
    @Transient
    private int combo;
    @Transient
    private NpcShop shop;
    @Transient
    private boolean online;
    @Transient
    private List<NpcShopItem> repurchaseItems = new ArrayList<>();
    @Transient
    private Npc npc;
    @Transient
    private List<Integer> cashCart = new ArrayList<>(12);
    @Transient
    private Android android;


    public MapleCharacter() {
        skills = new HashSet<>();
        temporaryStatManager = new TemporaryStatManager(this);
        skillCooltimes = new HashMap<>();
        questManager = new QuestManager(this);
        potentialMan = new CharacterPotentialMan(this);
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
        character.setRemainingSp(new ArrayList<>(6));
        character.setKeyMap(new MapleKeyMap());
        return character;
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

    public short getJobId() {
        return job.getJobId();
    }

    public int getRemainingSpsSize() {
        List<Integer> remainingSp = getRemainingSp();
        int i = 0;
        for (int sp : remainingSp) {
            if (sp > 0) {
                i++;
            }
        }
        return i;
    }

    public void logout() {
        this.setOnline(false);
        getMap().removePlayer(this);
        if (getMap().getForcedReturn() != NO_MAP_ID) {
            this.mapId = getMap().getForcedReturn();
        }
        getClient().getMapleChannel().removePlayer(this);
        if (!isChangingChannel) {
            getAccount().logout();
            getClient().setPlayer(null);
        } else {
            getAccount().saveToDb();
        }
    }

    public void addItemToInv(Item item) {
        addItemToInv(item, false);
    }

    public void addItemToInv(Item item, boolean hasCorrectPos) {
        if (item == null) {
            return;
        }
        Inventory inventory = getInventory(item.getInvType());
        ItemInfo ii = ItemData.getItemInfoById(item.getItemId());
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
                announce(UserPacket.inventoryOperation(true, UPDATE_QUANTITY, (short) existingItem.getPos(), (short) -1, 0, existingItem));
                if (rec) {
                    addItemToInv(item);
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

                if (ItemConstants.isFamiliar(item.getItemId()) && item.getFamiliar() == null) {

                }
                inventory.addItem(item);
                announce(UserPacket.inventoryOperation(true, InventoryOperationType.ADD, (short) item.getPos(), (short) -1, 0, item));
                if (rec) {
                    addItemToInv(itemCopy);
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

    @Transactional
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
        World curWorld = Server.getInstance().getWorldById(world);
        MapleChannel curChannel = curWorld.getChannel(channel);
        return curChannel.getMap(mapId);
    }

    public boolean hasItemCount(int itemID, int requiredCount) {
        ItemInfo item = ItemData.getItemInfoById(itemID);
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

    public void changeMap(MapleMap map, byte portal) {
        changeMap(map, portal, false);
    }

    public void changeMap(MapleMap map, byte portal, boolean load) {
        getVisibleMapObjs().clear();
        getVisibleChar().clear();
        if (getMap() != null) {
            getMap().removePlayer(this);
            getMap().broadcastMessage(WorldPacket.userLeaveMap(getId()));
        }
        setMap(map);
        Portal targetPortal = map.getPortal(portal) == null ? map.getDefaultPortal() : map.getPortal(portal);
        setPosition(new Position(targetPortal.getX(), targetPortal.getY()));
        if (load) {
            announce(WorldPacket.getWarpToMap(this, true));
        } else {
            announce(WorldPacket.getWarpToMap(this, map, portal));
        }
        map.addPlayer(this);
        map.sendMapObjectPackets(this);
        map.broadcastMessage(UserRemote.hiddenEffectEquips(this));
        map.broadcastMessage(UserRemote.soulEffect(this));
        announce(WorldPacket.quickMove(map.isTown() && (map.getId() % 1000000) == 0));
    }

    public void announce(OutPacket outPacket) {
        client.announce(outPacket);
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
        if (ItemConstants.isAndroid(item.getItemId()) || ItemConstants.isMechanicalHeart(item.getItemId())) {
            if (getEquippedEquip(BodyPart.Android) != null && getEquippedEquip(BodyPart.MechanicalHeart) != null) {
                Equip androidEquip = getEquippedEquip(BodyPart.Android);
                Android android = androidEquip.getAndroid();
                if (android == null) {
                    android = ItemData.createAndroidFromItem(androidEquip);
                }
                setAndroid(android);
            }
        }
        return true;
    }


    public Equip getEquippedEquip(BodyPart bodyPart) {
        return (Equip) getEquippedInventory().getItem((short) bodyPart.getVal());
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
        announce(WorldPacket.chatMessage(content, type));
    }

    public void chatMessage(String msg) {
        chatMessage(SystemNotice, msg);
    }


    public void addStat(MapleStat stat, int amount) {
        setStat(stat, (int) (getStat(stat) + amount));
    }

    public long getStat(MapleStat stat) {
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
            case EXP:
                return stats.getExp();
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
        if (drop.isMoney()) {
            addMeso(drop.getMoney());
            getQuestManager().handleMoneyGain(drop.getMoney());
            announce(WorldPacket.dropPickupMessage(drop.getMoney(), (short) 0, (short) 0));
            announce(UserPacket.inventoryRefresh(true));
        } else {
            Item item = drop.getItem();
            int itemId = item.getItemId();
            boolean isConsume = false;
            boolean isRunOnPickUp = false;
            if (!ItemConstants.isEquip(itemId)) {
                ItemInfo ii = ItemData.getItemInfoById(itemId);
                isConsume = ii.getSpecStats().getOrDefault(SpecStat.consumeOnPickup, 0) != 0;
                isRunOnPickUp = ii.getSpecStats().getOrDefault(SpecStat.runOnPickup, 0) != 0;
            }
            if (isConsume) {
                announce(UserPacket.enableActions());
            } else if (isRunOnPickUp) {
                String script = String.valueOf(itemId);
                int npcID = 0;
                ItemInfo itemInfo = ItemData.getItemInfoById(itemId);
                if (itemInfo.getScript() != null && !"".equals(itemInfo.getScript())) {
                    script = itemInfo.getScript();
                    npcID = itemInfo.getNpcID();
                }
                ItemScriptManager.getInstance().startScript(itemId, script, npcID, client);
                announce(WorldPacket.dropPickupMessage(item, false, (short) item.getQuantity()));
            } else if (getInventory(item.getInvType()).canPickUp(item)) {
                if (item instanceof Equip) {
                    Equip equip = (Equip) item;
                    if (equip.hasAttribute(EquipAttribute.UntradableAfterTransaction)) {
                        equip.removeAttribute(EquipAttribute.UntradableAfterTransaction);
                        equip.addAttribute(EquipAttribute.Untradable);
                    }
                }
                addItemToInv(item, false);
                announce(WorldPacket.dropPickupMessage(item, true, (short) item.getQuantity()));
            }
        }
    }


    public void addMeso(long amount) {
        long meso = getMeso();
        long newMeso = meso + amount;
        if (newMeso >= 0) {
            newMeso = Math.min(GameConstants.MAX_MONEY, newMeso);
            Map<MapleStat, Long> stats = new HashMap<>();
            setMeso(newMeso);
            stats.put(MapleStat.MESO, newMeso);
            announce(UserPacket.updatePlayerStats(stats, true, this));
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
            stats.put(MapleStat.LEVEL, getStat(MapleStat.LEVEL));
            level++;
            getJobHandler().handleLevelUp();
            getMap().broadcastMessage(UserRemote.effect(getId(), Effect.levelUpEffect()));
            heal(getMaxHP());
            healMP(getMaxMP());
        }
        setExp(newExp);
        stats.put(MapleStat.EXP, newExp);
        if (expIncreaseInfo != null) {
            int expFromR = 0;
            expIncreaseInfo.setIndieBonusExp(expFromR);
            announce(WorldPacket.incExpMessage(expIncreaseInfo));
        }
        announce(UserPacket.updatePlayerStats(stats, this));
    }

    private void setExp(long newExp) {
        stats.setExp(newExp);
    }

    public void consumeItem(int itemId, int quantity) {
        Item checkItem = ItemData.getItemCopy(itemId, false);
        Item item = getInventory(checkItem.getInvType()).getItemByItemID(itemId);
        if (item != null) {
            int consumed = quantity > item.getQuantity() ? 0 : item.getQuantity() - quantity;
            item.setQuantity(consumed + 1); // +1 because 1 gets consumed by consumeItem(item)
            consumeItem(item);
        }
    }

    public void consumeItem(Item item) {
        Inventory inventory = getInventory(item.getInvType());
        if (item.getQuantity() <= 1 && !ItemConstants.isThrowingItem(item.getItemId())) {
            item.setQuantity(0);
            inventory.removeItem(item);
            short pos = (short) item.getPos();
            announce(UserPacket.inventoryOperation(true, REMOVE, pos, (short) 0, 0, item));
        } else {
            item.setQuantity(item.getQuantity() - 1);
            announce(UserPacket.inventoryOperation(true, UPDATE_QUANTITY, (short) item.getPos(), (short) -1, 0, item));
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
        announce(UserPacket.updatePlayerStats(stats, this));
    }

    public void healMP(int amount) {
        int curMP = getMp();
        int maxMP = getMaxMP();
        int newMP = Math.min(curMP + amount, maxMP);
        Map<MapleStat, Long> stats = new HashMap<>();
        setStat(MapleStat.MP, newMP);
        stats.put(MapleStat.MP, (long) newMP);
        announce(UserPacket.updatePlayerStats(stats, this));
    }

    public void addSpToJobByCurrentLevel(int amount) {
        byte jobLevel = (byte) JobConstants.getJobLevelByCharLevel(getJob().getJobId(), getLevel());
        addSp(amount, jobLevel);
    }

    public void addSp(int amount, int jobLevel) {
        List<Integer> remainingSp = getRemainingSp();
        if (jobLevel == 0) {  //新手没有技能点
            return;
        }
        remainingSp.set(jobLevel, remainingSp.get(jobLevel - 1) + amount);
    }


    public void setJob(JobConstants.JobEnum job) {
        if (job == null) {
            job = JobConstants.JobEnum.BEGINNER;
        }
        this.job = job;
    }

    public void changeMap(int mapId, boolean load) {
        MapleChannel channel = Server.getInstance().getWorldById(world).getChannel(this.channel);
        MapleMap map = channel.getMap(mapId);
        if (map != null) {
            changeMap(map, getSpawnPoint(), load);
        } else {
            announce(UserPacket.enableActions());
        }
    }

    public void changeMap(int mapId) {
        changeMap(mapId, false);
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

    public void addToBaseStatCache(Skill skill) {
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

    public void enterCashShop() {
        setChangingChannel(true);
        logout();
        MapleMap map = getMap();
        map.removePlayer(this);
        map.broadcastMessage(WorldPacket.userLeaveMap(getId()));
        Server.getInstance().addClientInTransfer((byte) channel, getId(), getClient());
        announce(WorldPacket.getChannelChange(Server.getInstance().getCashShop(getWorld()).getPort()));
    }

    public void changeChannel(byte channel) {
        changeChannelAndWarp(channel, getMapId());
    }

    private void changeChannelAndWarp(byte channel, int mapId) {
        setChangingChannel(true);
        logout();
        MapleMap map = getMap();
        if (mapId == getMapId()) {
            Portal spawnPortalNearby = map.getSpawnPortalNearby(getPosition());
            setSpawnPoint(spawnPortalNearby.getId());
        } else {
            setMapId(mapId);
            setSpawnPoint((byte) 0); //todo
        }
        map.removePlayer(this);
        map.broadcastMessage(WorldPacket.userLeaveMap(getId()));
        this.map = null;
        Server.getInstance().addClientInTransfer(channel, getId(), getClient());
        int port = Server.getInstance().getChannel(world, channel).getPort();
        announce(WorldPacket.getChannelChange(port));
    }

    public boolean applyMpCon(int skillId, int skillLevel) {
        int mp = getMp();
        SkillInfo skillInfo = SkillData.getSkillInfo(skillId);
        if (skillInfo == null) {
            return true;
        }
        int mpCon = skillInfo.getValue(SkillStat.mpCon, skillLevel);
        if (mp >= mpCon) {
            addStatAndSendPacket(MapleStat.MP, -mpCon);
            return true;
        }
        return false;
    }

    public void addStatAndSendPacket(MapleStat stat, int amount) {
        addStat(stat, amount);
        HashMap<MapleStat, Long> stats = new HashMap<>();
        stats.put(stat, getStat(stat));
        announce(UserPacket.updatePlayerStats(stats, true, this));
    }

    public void dropMessage(String message) {
        announce(WorldPacket.serverNotice(message));
    }


    public boolean isSkillInCd(int skillId) {
        boolean t = System.currentTimeMillis() > getSkillCooltimes().getOrDefault(skillId, 0L);
        if (t) {
            getSkillCooltimes().remove(skillId);
        }
        return t;
    }

    private double getTotalStatAsDouble(BaseStat baseStat) {
        // TODO cache this completely
        double stat = 0;
        // Stat allocated by sp
        stat += baseStat.toStat() == null ? 0 : getStat(baseStat.toStat());
        // Stat gained by passives
        stat += getBaseStats().getOrDefault(baseStat, 0L);
        // Stat gained by buffs
        int ctsStat = getTemporaryStatManager().getBaseStats().getOrDefault(baseStat, 0);
        stat += ctsStat;
        // Stat gained by equips
//        for (Item item : getEquippedInventory().getItems()) {
//            Equip equip = (Equip) item;
//            stat += equip.getBaseStat(baseStat);
//        }
        // Stat gained by the stat's corresponding rate value
        if (baseStat.getRateVar() != null) {
            stat += stat * (getTotalStat(baseStat.getRateVar()) / 100D);
        }
        // Stat gained by the stat's corresponding "per level" value
        if (baseStat.getLevelVar() != null) {
            stat += getTotalStatAsDouble(baseStat.getLevelVar()) * getLevel();
        }
        // --- Everything below this doesn't get affected by the rate var
        // Character potential
        //潜能
//        for (CharacterPotential cp : getPotentials()) {
//            Skill skill = cp.getSkill();
//            SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
//            Map<BaseStat, Integer> stats = si.getBaseStatValues(this, skill.getCurrentLevel(), skill.getSkillId());
//            stat += stats.getOrDefault(baseStat, 0);
//        }

        return stat;
    }

    public int getTotalStat(BaseStat stat) {
        return (int) getTotalStatAsDouble(stat);
    }


    public void initBaseStats() {
        getBaseStats().clear();
        Map<BaseStat, Long> stats = getBaseStats();
        stats.put(BaseStat.cr, 5L);
        stats.put(BaseStat.cd, 0L);
        stats.put(BaseStat.pdd, 9L);
        stats.put(BaseStat.mdd, 9L);
        stats.put(BaseStat.acc, 11L);
        stats.put(BaseStat.eva, 8L);
        stats.put(BaseStat.buffTimeR, 100L);
        getSkills().stream().filter(skill -> SkillConstants.isPassiveSkill_NoPsdSkillsCheck(skill.getSkillId())).
                forEach(this::addToBaseStatCache);
    }

    public void changeJob(int jobId) {
        JobConstants.JobEnum job = JobConstants.JobEnum.getJobById((short) jobId);
        if (job == null) {
            return;
        }
        setJobHandler(JobManager.getJobById(getJobId(), this));
        setJob(job);
        HashMap<MapleStat, Long> stats = new HashMap<>();
        stats.put(MapleStat.JOB, (long) getJobId());
        announce(UserPacket.updatePlayerStats(stats, this));
    }

    public boolean hasSkill(int skill) {
        return getSkills().stream().anyMatch(s -> s.getSkillId() == skill) && getSkill(skill, false).getCurrentLevel() > 0;
    }

    public void addSkillCooltime(int skillId, int cooltime) {
        getSkillCooltimes().put(skillId, System.currentTimeMillis() + cooltime);
    }

    public boolean isMarried() {
        return false;
    }

    public QuestManager getQuestManager() {
        if (questManager == null) {
            questManager = new QuestManager(this);
        }
        if (questManager.getChr() == null) {
            questManager.setChr(this);
        }
        return questManager;
    }


    public boolean hasAnyQuestsInProgress(Set<Integer> quests) {
        return true;
    }


    public void changeSkillState(int skillId) {
        String skill = String.valueOf(skillId);
        Map<String, String> value = new HashMap<>();
        Map<String, String> options = getQuestEx().get(QUEST_EX_SKILL_STATE);
        if (options != null && options.containsKey(skill)) {
            String option = options.get(skill);
            if (option.equals("0")) {
                value.put(skill, "1");
            } else {
                value.put(skill, "0");
            }
        } else {
            value.put(skill, "1");
        }
        addQuestEx(QUEST_EX_SKILL_STATE, value);
        announce(UserPacket.message(MessageType.QUEST_RECORD_EX_MESSAGE, QUEST_EX_SKILL_STATE, questsExStorage.get(QUEST_EX_SKILL_STATE), (byte) 0));
    }

    public void buildQuestEx() {
        getQuestsExStorage().forEach((qrKey, qrValue) ->
                {
                    HashMap<String, String> value = new HashMap<>();
                    for (String option : qrValue.split(";")) {
                        String[] pair = option.split("=");
                        value.put(pair[0], pair[1]);
                    }
                    addQuestEx(qrKey, value);
                }
        );
    }

    public void buildQuestExStorage() {
        getQuestEx().forEach((questId, options) -> {
            StringBuilder value = new StringBuilder();
            for (String key : options.keySet()) {
                value.append(key).append("=").append(options.get(key)).append(";");
            }
            questsExStorage.put(questId, value.substring(0, value.length() - 1));
        });
    }


    public void addQuestEx(int questId, Map<String, String> value) {
        Map<String, String> options = questEx.getOrDefault(questId, null);
        if (options == null) {
            questEx.put(questId, value);
        } else {
            questEx.get(questId).putAll(value);
        }
        buildQuestExStorage();
    }

    public void addVisibleMapObj(MapleMapObj obj) {
        getVisibleMapObjs().add(obj);
    }

    public void removeVisibleMapObj(MapleMapObj object) {
        getVisibleMapObjs().remove(object);
    }


    public Rect getVisibleRect() {
        int x = getPosition().getX();
        int y = getPosition().getY();
        return new Rect(x - GameConstants.MAX_VIEW_X, y - GameConstants.MAX_VIEW_Y, x + GameConstants.MAX_VIEW_X, y + GameConstants.MAX_VIEW_Y);
    }

    public void addDailyMobKillCount() {
        Map<String, String> options = getQuestEx().get(QUEST_EX_MOB_KILL_COUNT);
        if (options == null) {
            options = new HashMap<>();
            options.put("date", String.valueOf(DateUtil.getDate()));
            options.put("count", "1");
            addQuestEx(QUEST_EX_MOB_KILL_COUNT, options);
        } else if (options.get("date").equals(String.valueOf(DateUtil.getDate()))) {
            options.put("count", String.valueOf(Integer.parseInt(options.get("count")) + 1));
            buildQuestExStorage();
        } else {
            options.put("date", String.valueOf(DateUtil.getDate()));
            options.put("count", "1");
            buildQuestExStorage();
        }
        announce(UserPacket.message(MessageType.QUEST_RECORD_EX_MESSAGE, QUEST_EX_MOB_KILL_COUNT, questsExStorage.get(QUEST_EX_MOB_KILL_COUNT), (byte) 0));
    }

    public void comboKill(int objectId) {
        combo++;
        announce(UserPacket.comboKillMessage(objectId, combo));
    }


    public int getHonerPoint() {
        return stats.getHonerPoint();
    }

    public void addHonerPoint(int amount) {
        stats.setHonerPoint(stats.getHonerPoint() + amount);
        if (stats.getHonerPoint() < 0) {
            stats.setHonerPoint(0);
        }
        announce(UserPacket.updateHonerPoint(stats.getHonerPoint()));
    }

    public int getTotalChuc() {
        return getInventory(EQUIPPED).getItems().stream().mapToInt(i -> ((Equip) i).getChuc()).sum();
    }


    public DamageSkinSaveData getDamageSkinByItemID(int itemId) {
        return getDamageSkins().stream().filter(d -> d.getItemID() == itemId).findAny().orElse(null);
    }

    public void addDamageSkin(DamageSkinSaveData damageSkinSaveData) {
        if (getDamageSkinByItemID(damageSkinSaveData.getItemID()) == null) {
            getDamageSkins().add(damageSkinSaveData);
        }
    }


    public DamageSkinSaveData getDamageSkinBySkinId(int damageSkinId) {
        DamageSkinSaveData defaultSkin = new DamageSkinSaveData(0, 0, false, "");
        return getDamageSkins().stream().filter(d -> d.getDamageSkinID() == damageSkinId).findAny().orElse(defaultSkin);
    }

    public DamageSkinSaveData getDamageSkin() {
        if (damageSkin == null) {
            Quest quest = questManager.getQuestById(QUEST_DAMAGE_SKIN);
            DamageSkinSaveData defaultSkin = new DamageSkinSaveData(0, 0, false, "");
            if (quest == null) {
                damageSkin = defaultSkin;
            } else {
                int damageSkinId = Integer.parseInt(quest.getQRValue());
                damageSkin = getDamageSkins().stream().filter(d -> d.getDamageSkinID() == damageSkinId).findAny().orElse(defaultSkin);
            }
        }
        return damageSkin;
    }

    public void setDamageSkin(DamageSkinSaveData damageSkin) {
        this.damageSkin = damageSkin;
    }

    public void setDamageSkin(int itemID) {
        setDamageSkin(new DamageSkinSaveData(ItemConstants.getDamageSkinIDByItemID(itemID), itemID, false,
                ""));
    }

    public List<DamageSkinSaveData> getSavedDamageSkins() {
        return getDamageSkins().stream().filter(d -> !d.isNotSave()).collect(Collectors.toList());
    }

    public void encodeDamageSkins(OutPacket outPacket) {
        outPacket.writeBool(true);
        getDamageSkin().encode(outPacket);
        getPremiumDamageSkin().encode(outPacket);
        getPremiumDamageSkin().encode(outPacket);
        outPacket.writeShort(getDamageSkinSlotSize());
        outPacket.writeShort(getSavedDamageSkins().size());
        for (DamageSkinSaveData damageSkinSaveData : getSavedDamageSkins()) {
            damageSkinSaveData.encode(outPacket);
        }
    }

    public Rect getRectAround(Rect rect) {
        int x = getPosition().getX();
        int y = getPosition().getY();
        return new Rect(x + rect.getLeft(), y + rect.getTop(), x + rect.getRight(), y + rect.getBottom());
    }

    public boolean canHold(List<Item> items) {
        return canHold(items, deepCopyForInvCheck());
    }

    private boolean canHold(List<Item> items, MapleCharacter deepCopiedChar) {
        // explicitly use a Char param to avoid accidentally adding items
        if (items.size() == 0) {
            return true;
        }
        Item item = items.get(0);
        if (canHold(item.getItemId())) {
            Inventory inv = deepCopiedChar.getInventory(item.getInvType());
            inv.addItem(item);
            items.remove(item);
            return deepCopiedChar.canHold(items, deepCopiedChar);
        } else {
            return false;
        }
    }

    public boolean canHold(int id) {
        boolean canHold;
        if (ItemConstants.isEquip(id)) {  //Equip
            canHold = getEquipInventory().getSlots() > getEquipInventory().getItems().size();
        } else {    //Item
            ItemInfo ii = ItemData.getItemInfoById(id);
            Inventory inv = getInventory(ii.getInvType());
            Item curItem = inv.getItemByItemID(id);
            canHold = (curItem != null && curItem.getQuantity() + 1 < ii.getSlotMax()) || inv.getSlots() > inv.getItems().size();
        }
        return canHold;
    }

    private MapleCharacter deepCopyForInvCheck() {
        MapleCharacter chr = new MapleCharacter();
        chr.setEquippedInventory(getEquippedInventory().deepCopy());
        chr.setEquipInventory(getEquipInventory().deepCopy());
        chr.setConsumeInventory(getConsumeInventory().deepCopy());
        chr.setEtcInventory(getEtcInventory().deepCopy());
        chr.setInstallInventory(getInstallInventory().deepCopy());
        chr.setCashInventory(getCashInventory().deepCopy());
        return chr;
    }

    public World getMapleWorld() {
        return Server.getInstance().getWorldById(world);
    }

    public int getSpentHyperStatSp() {
        int sp = 0;
        for (int skillId = 80000400; skillId <= 80000418; skillId++) {
            Skill skill = getSkill(skillId);
            if (skill != null) {
                sp += SkillConstants.getTotalNeededSpForHyperStatSkill(skill.getCurrentLevel());
            }
        }
        return sp;
    }

    public void deductMoney(long amount) {
        addMeso(-amount);
    }

    public int getSpentHyperPassiveSkillSp() {
        int i = 0;
        for (Skill skill : getSkills()) {
            SkillInfo si = SkillData.getSkillInfo(skill.getSkillId());
            if (si.getHyper() == 1) {
                i++;
            }
        }
        return i;
    }

    public int getSpentHyperActiveSkillSp() {
        int i = 0;
        for (Skill skill : getSkills()) {
            SkillInfo si = SkillData.getSkillInfo(skill.getSkillId());
            if (si.getHyper() == 2) {
                i++;
            }
        }
        return i;
    }


    public void addRepurchaseItem(NpcShopItem item) {
        getRepurchaseItems().add(item);
    }

    public Npc getNpc() {
        return npc;
    }

    public void setNpc(Npc npc) {
        this.npc = npc;
    }

    public void enableAction() {
        announce(UserPacket.enableActions());
    }
}

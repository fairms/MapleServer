package im.cave.ms.client.character;

import im.cave.ms.client.Account;
import im.cave.ms.client.Clock;
import im.cave.ms.client.MapleClient;
import im.cave.ms.client.HotTimeReward;
import im.cave.ms.client.Record;
import im.cave.ms.client.RecordManager;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.Inventory;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.items.PetItem;
import im.cave.ms.client.character.items.PotionPot;
import im.cave.ms.client.character.items.WishedItem;
import im.cave.ms.client.character.job.JobManager;
import im.cave.ms.client.character.job.MapleJob;
import im.cave.ms.client.character.job.adventurer.Beginner;
import im.cave.ms.client.character.job.adventurer.Warrior;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.character.potential.CharacterPotentialMan;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.character.temp.TemporaryStatManager;
import im.cave.ms.client.field.Effect;
import im.cave.ms.client.field.MapleMap;
import im.cave.ms.client.field.Portal;
import im.cave.ms.client.field.obj.Android;
import im.cave.ms.client.field.obj.Drop;
import im.cave.ms.client.field.obj.Familiar;
import im.cave.ms.client.field.obj.MapleMapObj;
import im.cave.ms.client.field.obj.Pet;
import im.cave.ms.client.field.obj.Summon;
import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.client.field.obj.npc.shop.NpcShop;
import im.cave.ms.client.field.obj.npc.shop.NpcShopItem;
import im.cave.ms.client.multiplayer.Express;
import im.cave.ms.client.multiplayer.MapleNotes;
import im.cave.ms.client.multiplayer.friend.Friend;
import im.cave.ms.client.multiplayer.guilds.Guild;
import im.cave.ms.client.multiplayer.miniroom.MiniRoom;
import im.cave.ms.client.multiplayer.miniroom.TradeRoom;
import im.cave.ms.client.multiplayer.party.Party;
import im.cave.ms.client.multiplayer.party.PartyMember;
import im.cave.ms.client.multiplayer.party.PartyQuest;
import im.cave.ms.client.multiplayer.party.PartyResult;
import im.cave.ms.client.quest.Quest;
import im.cave.ms.client.quest.QuestManager;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.connection.db.InlinedIntArrayConverter;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.netty.Packet;
import im.cave.ms.connection.packet.CashShopPacket;
import im.cave.ms.connection.packet.PetPacket;
import im.cave.ms.connection.packet.UserPacket;
import im.cave.ms.connection.packet.UserRemote;
import im.cave.ms.connection.packet.WorldPacket;
import im.cave.ms.connection.server.Server;
import im.cave.ms.connection.server.channel.MapleChannel;
import im.cave.ms.connection.server.world.World;
import im.cave.ms.constants.GameConstants;
import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.JobConstants;
import im.cave.ms.constants.SkillConstants;
import im.cave.ms.enums.BaseStat;
import im.cave.ms.enums.BodyPart;
import im.cave.ms.enums.CashShopCurrencyType;
import im.cave.ms.enums.CharMask;
import im.cave.ms.enums.ChatType;
import im.cave.ms.enums.EquipAttribute;
import im.cave.ms.enums.EquipSpecialAttribute;
import im.cave.ms.enums.InventoryOperationType;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.enums.JobType;
import im.cave.ms.enums.MapTransferType;
import im.cave.ms.enums.MessageType;
import im.cave.ms.enums.SkillStat;
import im.cave.ms.enums.SpecStat;
import im.cave.ms.provider.data.ItemData;
import im.cave.ms.provider.data.SkillData;
import im.cave.ms.provider.info.ItemInfo;
import im.cave.ms.provider.info.SkillInfo;
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
import java.time.LocalDate;
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

import static im.cave.ms.client.character.temp.CharacterTemporaryStat.SoulMP;
import static im.cave.ms.connection.packet.UserPacket.enableActions;
import static im.cave.ms.constants.GameConstants.DEFAULT_BUDDY_CAPACITY;
import static im.cave.ms.constants.GameConstants.DEFAULT_CASH_INVENTORY_SLOTS;
import static im.cave.ms.constants.GameConstants.DEFAULT_CONSUME_INVENTORY_SLOTS;
import static im.cave.ms.constants.GameConstants.DEFAULT_DAMAGE_SLOTS;
import static im.cave.ms.constants.GameConstants.DEFAULT_EQUIP_INVENTORY_SLOTS;
import static im.cave.ms.constants.GameConstants.DEFAULT_ETC_INVENTORY_SLOTS;
import static im.cave.ms.constants.GameConstants.DEFAULT_INSTALL_INVENTORY_SLOTS;
import static im.cave.ms.constants.GameConstants.INVENTORY_MAX_SLOTS;
import static im.cave.ms.constants.GameConstants.NO_MAP_ID;
import static im.cave.ms.constants.QuestConstants.QUEST_DAMAGE_SKIN;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_MAP_TRANSFER_COUPON_FREE_USED;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_MOB_KILL_COUNT;
import static im.cave.ms.constants.QuestConstants.QUEST_EX_SKILL_STATE;
import static im.cave.ms.constants.ServerConstants.MAX_TIME;
import static im.cave.ms.constants.ServerConstants.ZERO_TIME;
import static im.cave.ms.enums.ChatType.SystemNotice;
import static im.cave.ms.enums.InventoryOperationType.REMOVE;
import static im.cave.ms.enums.InventoryOperationType.UPDATE_QUANTITY;
import static im.cave.ms.enums.InventoryType.CASH;
import static im.cave.ms.enums.InventoryType.CASH_EQUIP;
import static im.cave.ms.enums.InventoryType.CONSUME;
import static im.cave.ms.enums.InventoryType.EQUIP;
import static im.cave.ms.enums.InventoryType.EQUIPPED;
import static im.cave.ms.enums.InventoryType.ETC;
import static im.cave.ms.enums.InventoryType.INSTALL;

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
    ///////////////////////////////////////////////////////
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private byte world;
    private int accId;
    private byte buddyCapacity = DEFAULT_BUDDY_CAPACITY;
    private byte spawnPoint = 0;
    private boolean gm;
    private long deleteTime;
    private int damageSkinSlotSize = DEFAULT_DAMAGE_SLOTS;
    private long lastLogout;
    private boolean isDeleted;
    private long extendedPendant;
    private long createdTime;
    //好友
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "charId")
    private Set<Friend> friends;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "charstats")
    private CharStats stats;
    //角色外观 一个或两个(神之子)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chr", orphanRemoval = true)
    private Set<CharLook> charLook = new HashSet<>();
    @Column(name = "map")
    private int mapId;
    @Column(name = "party")
    private int partyId;
    /*
        背包 0 1 2 3 4 5 6
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "equippedInventory")
    private Inventory equippedInventory;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipInventory")
    private Inventory equipInventory;
    @JoinColumn(name = "consumeInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory consumeInventory;
    @JoinColumn(name = "installInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory installInventory;
    @JoinColumn(name = "etcInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory etcInventory;
    @JoinColumn(name = "cashInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory cashInventory;
    @JoinColumn(name = "cashEquipInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory cashEquipInventory;
    //快捷键映射
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Integer> quickslots;
    @JoinColumn(name = "charId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Skill> skills;
    //技能冷却时间 Map<技能ID,下次可用时间>
    @ElementCollection
    @CollectionTable(name = "skill_cooldown", joinColumns = @JoinColumn(name = "charId"))
    @MapKeyColumn(name = "skillId")
    @Column(name = "nextUsableTime")
    private Map<Integer, Long> skillCooltimes;
    //键盘映射
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "keymap")
    private MapleKeyMap keyMap;
    //任务
    @JoinColumn(name = "questmanager")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private QuestManager questManager;
    //伤害皮肤
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "charId")
    private Set<DamageSkinSaveData> damageSkins;
    //内在潜能
    @JoinColumn(name = "charId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CharacterPotential> potentials;
    //Quest Ex
    @ElementCollection
    @CollectionTable(name = "quest_ex", joinColumns = @JoinColumn(name = "charId"))
    @MapKeyColumn(name = "questId")
    @Column(name = "qrValue")
    private Map<Integer, String> questsExStorage;
    //怪怪图鉴
    @JoinColumn(name = "charId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Familiar> familiars;
    @JoinColumn(name = "charId")
    //技能宏
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Macro> macros;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "charId")
    //商城购物车
    private List<WishedItem> wishedItems;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "charId")
    //游戏记录
    private Set<Record> records;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //冒险岛信息
    @JoinColumn(name = "fromId")
    private List<MapleNotes> Outbox;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "toId")
    private List<MapleNotes> InBox;
    //冒险岛快递
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "toId")
    private List<Express> expresses;
    //未领取的在线奖励
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "charId")
    private List<HotTimeReward> hotTimeRewards;
    @JoinColumn(name = "guild")
    @OneToOne(cascade = CascadeType.ALL)
    private Guild guild;
    /////////////////////////////////////////////////////////
    @Transient
    private MapleMap map;
    @Transient
    private int channel;
    @Transient
    private MapleClient client;
    @Transient
    private Account account;
    @Transient
    private Party party;
    @Transient
    private DamageSkinSaveData damageSkin;
    @Transient
    private DamageSkinSaveData premiumDamageSkin;
    @Transient
    private CharacterPotentialMan potentialMan;
    @Transient
    private Map<Integer, Map<String, String>> questEx;
    //冷却时间计时器
    @Transient
    private Map<Integer, Pair<Long, ScheduledFuture>> cooltimes;
    //其他计时器
    @Transient
    private Map<Integer, ScheduledFuture> schedules;
    @Transient
    private MapleJob jobHandler;
    @Transient
    private boolean isChangingChannel = false;
    @Transient
    private List<Integer> visitedMaps; //访问过的地图
    @Transient
    private String blessOfFairyOrigin;
    @Transient
    private String blessOfEmpressOrigin;
    @Transient
    private List<Pet> pets = new ArrayList<>(GameConstants.MAX_PET_AMOUNT); //召唤的宠物
    @Transient
    private final Map<Integer, String> entered = new HashMap<>(); //触发过的地图脚本
    @Transient
    private MiniRoom miniRoom;
    @Transient
    private Position position;
    @Transient
    private Map<BaseStat, Long> baseStats = new HashMap<>();
    @Transient
    private Map<Integer, Integer> hyperPsdSkillsCooltimeR;
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
    private Set<MapleMapObj> visibleMapObjs; //已经显示的地图对象
    @Transient
    private Set<MapleCharacter> visibleChars; //已经显示的角色
    @Transient
    private long lastKill;
    @Transient
    private int combo;
    @Transient
    private NpcShop shop;
    @Transient
    private boolean online;
    @Transient
    private List<NpcShopItem> repurchaseItems; //回购商品
    @Transient
    private Npc npc; //当前对话的NPC对象
    @Transient
    private boolean isConversation = false; //是否处于对话状态
    @Transient
    private Android android;  //当前召唤的机器人
    @Transient
    private Clock clock;  //当前角色的定时器
    @Transient
    private PotionPot potionPot; //药剂罐
    @Transient
    private RecordManager recordManager;
    @Transient
    private Familiar familiar; //当前召唤的怪怪
    @Transient
    private int activeNickItemId; //激活的称号ID
    @Transient
    private boolean battleRecordOn; //是否开启战斗分析
    @Transient
    private DamageCalc damageCalc;
    @Transient
    private Pair<Integer, Integer> prepareSkill; //按压技能 skillId,skillLevel
    @Transient
    private String portableChairMsg;

    public MapleCharacter() {
        temporaryStatManager = new TemporaryStatManager(this);
        questManager = new QuestManager(this);
        potentialMan = new CharacterPotentialMan(this);
        recordManager = new RecordManager(this);
        premiumDamageSkin = new DamageSkinSaveData();
        visibleMapObjs = new HashSet<>();
        visibleChars = new HashSet<>();
        repurchaseItems = new ArrayList<>();
        questEx = new HashMap<>();
        visitedMaps = new ArrayList<>();
        hyperPsdSkillsCooltimeR = new HashMap<>();
    }

    public static MapleCharacter getCharByName(String name) {
        return (MapleCharacter) DataBaseManager.getObjFromDB(MapleCharacter.class, "name", name);
    }

    public void cleanTemp() {
        records.removeIf(record -> record.getType().isTransition());
    }

    /*
        ret 0 角色名可用 1 角色已存在 2 角色名不可用
     */
    public static int nameValidate(String name) {
        int ret = Pattern.compile("[a-zA-Z0-9\\u4e00-\\u9fa5]{2,12}").matcher(name).matches() ? 0 : 2;
        if (ret == 0) {
            MapleCharacter character = MapleCharacter.getCharByName(name);
            if (character != null) {
                ret = 1;
            }
        }
        return ret;
    }


    public static MapleCharacter getDefault(int jobId) {
        MapleCharacter character = new MapleCharacter();
        character.setEquippedInventory(new Inventory(EQUIPPED, INVENTORY_MAX_SLOTS));
        character.setEquipInventory(new Inventory(EQUIP, DEFAULT_EQUIP_INVENTORY_SLOTS));
        character.setConsumeInventory(new Inventory(CONSUME, DEFAULT_CONSUME_INVENTORY_SLOTS));
        character.setInstallInventory(new Inventory(INSTALL, DEFAULT_INSTALL_INVENTORY_SLOTS));
        character.setEtcInventory(new Inventory(ETC, DEFAULT_ETC_INVENTORY_SLOTS));
        character.setCashInventory(new Inventory(CASH, DEFAULT_CASH_INVENTORY_SLOTS));
        character.setCashEquipInventory(new Inventory(CASH_EQUIP, INVENTORY_MAX_SLOTS));
        character.setStats(CharStats.getDefaultStats(jobId));
        character.setKeyMap(new MapleKeyMap());
        character.addCharLook(new CharLook());
//        character.setCharLook(Collections.singleton(new CharLook()));
        if (!character.setJob(jobId)) {
            return null;
        }
        return character;
    }

    private void addCharLook(CharLook cl) {
        charLook.add(cl);
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
            case CASH_EQUIP:
                return cashEquipInventory;
            default:
                return null;
        }
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

    //stats
    public List<Integer> getRemainingSp() {
        return stats.getRemainingSp();
    }

    public int getRemainingAp() {
        return stats.getRemainingAp();
    }

    public void setRemainingAp(int value) {
        stats.setRemainingAp(value);
    }

    public short getJob() {
        return stats.getJob();
    }

    public int getSubJob() {
        return stats.getSubJob();
    }

    public void setSubJob(short subJob) {
        stats.setSubJob(subJob);
    }

    //char look
    public int getHair() {
        return getCharLook().getHair();
    }

    public void setHair(int hairId) {
        getCharLook().setHair(hairId);
    }

    public int getFace() {
        return getCharLook().getFace();
    }

    public void setFace(int faceId) {
        getCharLook().setFace(faceId);
    }

    public byte getSkin() {
        return getCharLook().getSkin();
    }

    public void setSkin(byte skinId) {
        getCharLook().setSkin(skinId);
    }

    public byte getGender() {
        return getCharLook().getGender();
    }

    public void setGender(byte gender) {
        getCharLook().setGender(gender);
    }

    public void logout() {
        log.info("{} 断开连接 世界-{},频道-{}", getName(), getWorld(), getChannel());
        setLastLogout(DateUtil.getFileTime(System.currentTimeMillis()));
        if (getMap().getForcedReturn() != NO_MAP_ID) {
            this.mapId = getMap().getForcedReturn();
            this.spawnPoint = 0;
        }
        Portal spawnPortalNearby = map.getSpawnPortalNearby(getPosition());
        setSpawnPoint(spawnPortalNearby.getId());
        getMap().removeChar(this);
        if (getMiniRoom() != null) {
            if (getMiniRoom() instanceof TradeRoom) {
                MapleCharacter other = ((TradeRoom) getMiniRoom()).getOther();
                ((TradeRoom) getMiniRoom()).cancelTrade();
                other.chatMessage("Your trade partner disconnected.");
            }
            //todo
        }
        buildQuestExStorage();
        setOnline(false);
        getAccount().setOnlineChar(null);
        if (!isChangingChannel()) {
            getAccount().logout();
            getClient().getMapleChannel().removePlayer(this);
        } else {
            getClient().setPlayer(null);
            getAccount().save();
        }
    }

    public void save() {
        cleanTemp();
        buildQuestExStorage();
        getAccount().save();
    }

    public void addItemToInv(Item item) {
        addItemToInv(item, false);
    }

    public void addItemsToInv(List<Item> items) {
        for (Item item : items) {
            addItemToInv(item, false);
        }
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
                    item.setFamiliar(Familiar.generate(item.getItemId()));
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

    public int getVisitedMapCount() {
        return visitedMaps.size();
    }

    public long getMeso() {
        return stats.getMeso();
    }

    public MapleMap getMap() {
        if (map != null) {
            return map;
        }
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


    public void announce(Packet out) {
        client.announce(out);
    }

    public void write(Packet out) {
        client.write(out);
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
        if (!equip.hasAttribute(EquipAttribute.NoNonCombatStatGain) && equip.getCharmEXP() != 0) {
            addStatAndSendPacket(Stat.CHARM, equip.getCharmEXP());
            equip.addAttribute(EquipAttribute.NoNonCombatStatGain);
        }
        if (item.isCash()) {
            getCashEquipInventory().removeItem(item);
        } else {
            getEquipInventory().removeItem(item);
        }
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
        if (item.getItemId() == ItemConstants.ARES_BLESSING_RING) {
            Summon summon = Summon.getSummonBy(this, Beginner.ARES_BLESSING, (byte) 1);
            getMap().spawnSummon(summon);
        }
        return true;
    }


    public Equip getEquippedEquip(BodyPart bodyPart) {
        return (Equip) getEquippedInventory().getItem((short) bodyPart.getVal());
    }

    public Equip getEquippedCashEquip(BodyPart bodyPart) {
        return (Equip) getEquippedInventory().getItem((short) ((short) bodyPart.getVal() + 100));
    }

    public void unequip(Item item) {
        getEquippedInventory().removeItem(item);
        if (item.isCash()) {
            getCashEquipInventory().addItem(item);
        } else {
            getEquipInventory().addItem(item);
        }
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
        return other instanceof MapleCharacter && ((MapleCharacter) other).getId() == (getId());
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


    public void addStat(Stat stat, int amount) {
        setStat(stat, (int) (getStat(stat) + amount));
    }

    public long getStat(Stat stat) {
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

    public void setStat(Stat stat, int value) {
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

    public boolean addDrop(Drop drop) {
        if (drop.isMoney()) {
            addMeso(drop.getMoney());
            getQuestManager().handleMoneyGain(drop.getMoney());
            announce(WorldPacket.dropPickupMessage(drop.getMoney(), (short) 0, (short) 0));
            announce(UserPacket.inventoryRefresh(true));
            return true;
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
                announce(enableActions());
                return true;
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
                return true;
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
                return true;
            } else {
                enableAction();
                return false;
            }
        }
    }


    public void addMeso(long amount) {
        long meso = getMeso();
        long newMeso = meso + amount;
        if (newMeso >= 0) {
            newMeso = Math.min(GameConstants.MAX_MONEY, newMeso);
            Map<Stat, Long> stats = new HashMap<>();
            setMeso(newMeso);
            stats.put(Stat.MESO, newMeso);
            announce(UserPacket.statChanged(stats, false, this));
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
        Map<Stat, Long> stats = new HashMap<>();
        while (newExp >= GameConstants.charExp[level]) {
            newExp -= GameConstants.charExp[level];
            addStat(Stat.LEVEL, 1);
            stats.put(Stat.LEVEL, getStat(Stat.LEVEL));
            level++;
            getJobHandler().handleLevelUp();
            getMap().broadcastMessage(UserRemote.effect(getId(), Effect.levelUpEffect()));
            heal(getMaxHP());
            healMP(getMaxMP());
        }
        setExp(newExp);
        stats.put(Stat.EXP, newExp);
        if (expIncreaseInfo != null) {
            int expFromR = 0;
            expIncreaseInfo.setIndieBonusExp(expFromR);
            announce(WorldPacket.incExpMessage(expIncreaseInfo));
        }
        announce(UserPacket.statChanged(stats, this));
    }

    private void setExp(long newExp) {
        stats.setExp(newExp);
    }

    public boolean haveItem(int itemId, int quantity) {
        ItemInfo ii = ItemData.getItemInfoById(itemId);
        Item item = getInventory(ii.getInvType()).getItemByItemID(ii.getItemId());

        if (item != null) {
            return getInventory(ii.getInvType()).getItemQuantity(ii.getItemId()) >= quantity;
        }
        return false;
    }

    public int getItemQuantity(int itemId) {
        ItemInfo ii = ItemData.getItemInfoById(itemId);
        return getInventory(ii.getInvType()).getItemQuantity(ii.getItemId());
    }

    public void consumeItem(int itemId, int quantity, boolean excl) {
        Item checkItem = ItemData.getItemCopy(itemId, false);
        Item item = getInventory(checkItem.getInvType()).getItemByItemID(itemId);
        if (item != null) {

            int consumed = quantity > item.getQuantity() ? 0 : item.getQuantity() - quantity;
            int remain = quantity - item.getQuantity();
            item.setQuantity(consumed + 1); // +1 because 1 gets consumed by consumeItem(item)
            consumeItem(item, excl);
            if (remain > 0) {
                consumeItem(itemId, remain);
            }
        }
    }


    public void consumeItem(int itemId, int quantity) {
        consumeItem(itemId, quantity, true);
    }

    public void consumeItem(Item item) {
        consumeItem(item, true);
    }

    public void consumeItem(Item item, boolean excl) {
        Inventory inventory = getInventory(item.getInvType());
        if (item.getQuantity() <= 1 && !ItemConstants.isThrowingItem(item.getItemId())) {
            item.setQuantity(0);
            inventory.removeItem(item);
            short pos = (short) item.getPos();
            announce(UserPacket.inventoryOperation(excl, REMOVE, pos, (short) 0, 0, item));
        } else {
            item.setQuantity(item.getQuantity() - 1);
            announce(UserPacket.inventoryOperation(excl, UPDATE_QUANTITY, (short) item.getPos(), (short) -1, 0, item));
        }
    }

    public void heal(int amount) {
        heal(amount, false);
    }

    public void heal(int amount, boolean mp) {
        int curHP = getHp();
        int maxHP = getMaxHP();
        int newHP = Math.min(curHP + amount, maxHP);
        Map<Stat, Long> stats = new HashMap<>();
        setStat(Stat.HP, newHP);
        stats.put(Stat.HP, (long) newHP);
        if (mp) {
            int curMP = getMp();
            int maxMP = getMaxMP();
            int newMP = Math.min(curMP + amount, maxMP);
            setStat(Stat.MP, newMP);
            stats.put(Stat.MP, (long) newMP);
        }
        if (getParty() != null) {
            updatePartyHpBar();
        }
        announce(UserPacket.statChanged(stats, this));
    }

    public void healMP(int amount) {
        int curMP = getMp();
        int maxMP = getMaxMP();
        int newMP = Math.min(curMP + amount, maxMP);
        Map<Stat, Long> stats = new HashMap<>();
        setStat(Stat.MP, newMP);
        stats.put(Stat.MP, (long) newMP);
        announce(UserPacket.statChanged(stats, this));
    }

    public void addSpToJobByCurrentLevel(int amount) {
        byte jobLevel = (byte) JobConstants.getJobLevelByCharLevel(getJob(), getLevel());
        addSp(amount, jobLevel - 1);
    }

    public void addSp(int amount, int jobLevel) {
        getStats().addRemainingSp(amount, jobLevel);
    }

    public boolean setJob(int jobId) {
        JobType job = JobType.getJobById((short) jobId);
        if (job == null) {
            return false;
        }
        getStats().setJob((short) jobId);
        return true;
    }

    /*
        地图切换 开始
     */

    public void changeMap(int mapId, boolean load) {
        MapleChannel channel = Server.getInstance().getWorldById(world).getChannel(this.channel);
        MapleMap map = channel.getMap(mapId);
        if (map == null && load) {
            map = channel.getMap(mapId);
        }
        if (map == null) {
            announce(WorldPacket.mapTransferResult(MapTransferType.TargetNotExist, (byte) 0, null));
            enableActions();
            return;
        } else if (map == getMap() && !load) {
            announce(WorldPacket.mapTransferResult(MapTransferType.AlreadyInMap, (byte) 0, null));
            enableAction();
            return;
        }
        if (load) {
            initSoulMP();
        }
        changeMap(map, map.getSpawnPortal().getId(), load);
    }

    public void changeMap(int mapId) {
        changeMap(mapId, false);
    }

    public void changeMap(MapleMap map, int portal) {
        changeMap(map, (byte) portal, false);
    }

    //传送门的走这里
    public void changeMap(int mapId, byte portal) {
        MapleChannel channel = Server.getInstance().getWorldById(world).getChannel(this.channel);
        MapleMap map = channel.getMap(mapId);
        if (map != null) {
            if (map == getMap()) {
                announce(WorldPacket.mapTransferResult(MapTransferType.AlreadyInMap, (byte) 0, null));
                enableAction();
                return;
            }
            changeMap(map, portal, false);
        }
    }

    //切换地图
    private void changeMap(MapleMap map, byte portal, boolean load) {
        announce(UserPacket.effect(Effect.playPortalSE()));
        if (party != null && party.getPartyQuest() != null) { //处理组队地图
            int pMap = map.getId();
            MapleMap temp = Util.findWithPred(party.getPartyQuest().getMaps(), m -> m.getId() == pMap);
            if (temp != null)
                map = temp;
        }
        getVisibleMapObjs().clear();
        getVisibleChars().clear();
        if (getMap() != null) {
            getMap().removeChar(this);
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
        addVisitedMap(map);
        initPets();
        map.sendMapObjectPackets(this);
        map.broadcastMessage(UserRemote.hiddenEffectEquips(this));
        map.broadcastMessage(UserRemote.setSoulEffect(this));
        announce(WorldPacket.quickMove(map.isTown() && (map.getId() % 1000000) == 0));
        if (getParty() != null) {
            announce(WorldPacket.partyResult(PartyResult.loadParty(getParty())));
            PartyMember partyMember = getParty().getPartyMemberByID(getId());
            PartyQuest partyQuest = partyMember.getPartyQuest();
            if (partyQuest != null && !partyQuest.getMaps().contains(map)) {
                partyMember.setPartyQuest(null);
                getClock().stopClock();
            }
        }
        if (getClock() != null) {
            getClock().showClock();
        }
    }
    /*
        地图切换 结束
     */

    private void initPets() {
        for (PetItem petItem : getCashInventory().getItems().stream().filter(i -> i instanceof PetItem && ((PetItem) i).getActiveState() > 0)
                .map(i -> ((PetItem) i)).collect(Collectors.toList())) {
            Pet p = getPets().stream().filter(pet -> pet.getPetItem().equals(petItem)).findAny().orElse(null);
            boolean load = false;
            if (p == null) {
                // only create a new pet if the active state is > 0 (active), but isn't added to our own list yet
                p = petItem.createPet(this);
                addPet(p);
                load = true;
            }
            getMap().broadcastMessage(PetPacket.petActivateChange(p, true, (byte) 0));
            if (load && petItem.getExceptionList() != null) {
                announce(PetPacket.initPetExceptionList(p));
            }
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

    public void addSkill(int skillId, int currentLevel, int masterLevel) {
        Skill skill = SkillData.getSkill(skillId);
        if (skill == null) {
            log.error("No such skill found.");
            return;
        }
        skill.setCurrentLevel(currentLevel);
        skill.setMasterLevel(masterLevel);
        List<Skill> list = new ArrayList<>();
        list.add(skill);
        addSkill(skill);
        announce(UserPacket.changeSkillRecordResult(list, true, false, false, false));
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
        logout();
        setChangingChannel(true);
        MapleMap map = getMap();
        map.removeChar(this);
        Server.getInstance().addClientInTransfer((byte) channel, getId(), getClient());
        announce(WorldPacket.getChannelChange(true, getMapleWorld().getCashShop().getPort()));
    }

    public void enterAuction() {
        logout();
        setChangingChannel(true);
        MapleMap map = getMap();
        map.removeChar(this);
        Server.getInstance().addClientInTransfer((byte) channel, getId(), getClient());
        announce(WorldPacket.getChannelChange(true, getMapleWorld().getAuction().getPort()));
    }


    public void changeChannel(byte channel) {
        changeChannelAndWarp(channel, getMapId());
    }

    private void changeChannelAndWarp(byte channel, int mapId) {
        logout();
        setChangingChannel(true);
        MapleMap map = getMap();
        if (mapId != getMapId()) {
            setMapId(mapId);
            setSpawnPoint((byte) 0);
        }
        map.removeChar(this);
        this.map = null;
        Server.getInstance().addClientInTransfer(channel, getId(), getClient());
        int port = Server.getInstance().getChannel(world, channel).getPort();
        announce(WorldPacket.getChannelChange(true, port));
    }

    public boolean applyMpCon(int skillId, int skillLevel) {
        int mp = getMp();
        SkillInfo skillInfo = SkillData.getSkillInfo(skillId);
        if (skillInfo == null) {
            return true;
        }
        int mpCon = skillInfo.getValue(SkillStat.mpCon, skillLevel);
        if (mp >= mpCon) {
            addStatAndSendPacket(Stat.MP, -mpCon);
            return true;
        }
        return false;
    }

    public void addStatAndSendPacket(Stat stat, int amount) {
        addStat(stat, amount);
        HashMap<Stat, Long> stats = new HashMap<>();
        stats.put(stat, getStat(stat));
        announce(UserPacket.statChanged(stats, true, this));
    }

    public void dropMessage(String message) {
        announce(WorldPacket.scriptProgressMessage(message));
    }

    public boolean isSkillInCd(int skillId) {
        return System.currentTimeMillis() < getSkillCooltimes().getOrDefault(skillId, 0L);
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
//         --- Everything below this doesn't get affected by the rate var
//         Character potential
//        潜能
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

    public boolean changeJob(int jobId) {
        JobType job = JobType.getJobById((short) jobId);
        if (job == null) {
            return false;
        }
        if (!setJob(jobId)) {
            return false;
        }
        setJobHandler(JobManager.getJobById(getJob(), this));
        HashMap<Stat, Long> stats = new HashMap<>();
        stats.put(Stat.JOB, (long) getJob());
        announce(UserPacket.statChanged(stats, this));
        return true;
    }

    public boolean hasSkill(int skill) {
        return getSkills().stream().anyMatch(s -> s.getSkillId() == skill) && getSkill(skill, false).getCurrentLevel() > 0;
    }


    public void setSkillCooltime(int skillId, int slv) {
        SkillInfo si = SkillData.getSkillInfo(skillId);
        if (si != null) {
            int cdInSec = si.getValue(SkillStat.cooltime, slv);
            int cdInMillis = cdInSec > 0 ? cdInSec * 1000 : si.getValue(SkillStat.cooltimeMS, slv);
            if (cdInMillis > 0) {
                addSkillCoolTime(skillId, System.currentTimeMillis() + cdInMillis);
                write(UserPacket.setSkillCoolTime(skillId, cdInMillis));
            }
        }
    }

    public void addSkillCoolTime(int skillId, long cooltime) {
        getSkillCooltimes().put(skillId, cooltime);
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
        return quests.size() == 0 || quests.stream().anyMatch(this::hasQuestInProgress);
    }

    public boolean hasQuestInProgress(int questId) {
        return questId == 0 || getQuestManager().hasQuestInProgress(questId);
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
        addQuestExAndSendPacket(QUEST_EX_SKILL_STATE, value);
        if (skillId == Warrior.HERO_COMBO_ATTACK) {
            ((Warrior) getJobHandler()).getComboCount().decrementAndGet();
            ((Warrior) getJobHandler()).incCombo();
        }
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
    }

    public void removeQuestEx(int questId) {
        questEx.remove(questId);
        announce(UserPacket.message(MessageType.QUEST_RECORD_EX_MESSAGE, questId, "", (byte) 0));
    }

    public void addQuestExAndSendPacket(int questId, Map<String, String> value) {
        addQuestEx(questId, value);
        announce(UserPacket.message(MessageType.QUEST_RECORD_EX_MESSAGE, questId, getQuestsExStorage().get(questId), (byte) 0));
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

    public Rect getRectAround(Rect rect) {
        int x = getPosition().getX();
        int y = getPosition().getY();
        return new Rect(x + rect.getLeft(), y + rect.getTop(), x + rect.getRight(), y + rect.getBottom());
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

    /*
        伤害皮肤
     */
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
            DamageSkinSaveData defaultSkin = new DamageSkinSaveData(0, 2438159, false, "基本伤害皮肤。\\r\\n\\r\\n\\r\\n\\r\\n\\r\\n");
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

    public void encodeDamageSkins(OutPacket out) {
        out.writeBool(true);
        getDamageSkin().encode(out);
        getPremiumDamageSkin().encode(out);
        getPremiumDamageSkin().encode(out);
        out.writeShort(getDamageSkinSlotSize());
        out.writeShort(getSavedDamageSkins().size());
        for (DamageSkinSaveData damageSkinSaveData : getSavedDamageSkins()) {
            damageSkinSaveData.encode(out);
        }
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
        if (ItemConstants.isCashEquip(id)) {
            canHold = getCashEquipInventory().getSlots() > getCashEquipInventory().getItems().size();
        } else if (ItemConstants.isEquip(id)) {  //Equip
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

    public MapleChannel getMapleChannel() {
        return client.getMapleChannel();
    }

    public void deductMoney(long amount) {
        addMeso(-amount);
    }

    public void deductMoney(long amount, boolean cashShop) {
        if (!cashShop) {
            addMeso(-amount);
        } else {
            long meso = getMeso();
            long newMeso = meso + amount;
            if (newMeso >= 0) {
                newMeso = Math.min(GameConstants.MAX_MONEY, newMeso);
                Map<Stat, Long> stats = new HashMap<>();
                setMeso(newMeso);
                stats.put(Stat.MESO, newMeso);
                announce(CashShopPacket.updatePlayerStats(stats, this));
            }
        }
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


    //回购商品
    public void addRepurchaseItem(NpcShopItem item) {
        getRepurchaseItems().add(item);
    }

    // 宠物
    public void addPet(Pet pet) {
        pets.add(pet);
    }

    public int getFirstPetIdx() {
        int chosenIdx = -1;
        for (int i = 0; i < GameConstants.MAX_PET_AMOUNT; i++) {
            Pet p = getPetByIdx(i);
            if (p == null) {
                chosenIdx = i;
                break;
            }
        }
        return chosenIdx;
    }

    public Pet getPetByIdx(int idx) {
        return getPets().stream()
                .filter(p -> p.getIdx() == idx)
                .findAny()
                .orElse(null);
    }

    /*
        好友
     */
    public Set<Friend> getAllFriends() {
        Set<Friend> res = new HashSet<>(getFriends());
        res.addAll(getAccount().getFriends());
        return res;
    }

    public Friend getFriendByCharId(int charId) {
        return getFriends().stream().filter(f -> f.getFriendId() == charId).findAny().orElse(null);
    }

    public void removeFriend(Friend friend) {
        if (friend != null) {
            getFriends().remove(friend);
        }
    }

    public void removeFriendByID(int charId) {
        removeFriend(getFriendByCharId(charId));
    }

    public void addFriend(Friend friend) {
        if (getFriendByCharId(friend.getFriendId()) == null) {
            getFriends().add(friend);
        }
    }

    public List<Item> getMedals() {
        List<Item> items = getEquippedInventory().getItems();
        return items.stream().filter(item -> ItemConstants.isMedal(item.getItemId())).collect(Collectors.toList());
    }

    public List<Item> getChairs() {
        List<Item> items = getInstallInventory().getItems();
        return items.stream().filter(item -> ItemConstants.isChair(item.getItemId())).collect(Collectors.toList());
    }

    public CharLook getCharLook() {
        return Util.findWithPred(charLook, avatar -> !avatar.isZero(), CharLook.defaultLook(getJob()));
    }

    public CharLook getBetaCharLook() {
        return Util.findWithPred(charLook, CharLook::isZero);
    }

    public void enableAction() {
        announce(enableActions());
    }


    public int getPetEquip(int idx, int i) {
        Equip equip = null;
        switch (idx) {
            case 0:
                if (i == 0) {
                    equip = getEquippedCashEquip(BodyPart.PetWear1);
                } else {
                    equip = getEquippedCashEquip(BodyPart.PetCollar1);
                }
                break;
            case 1:
                if (i == 0) {
                    equip = getEquippedCashEquip(BodyPart.PetWear2);
                } else {
                    equip = getEquippedCashEquip(BodyPart.PetCollar2);
                }
                break;
            case 2:
                if (i == 0) {
                    equip = getEquippedCashEquip(BodyPart.PetWear3);
                } else {
                    equip = getEquippedCashEquip(BodyPart.PetCollar3);
                }
                break;
        }
        return equip != null ? equip.getItemId() : 0;
    }

    public void addCurrency(CashShopCurrencyType currencyType, int amount) {
        switch (currencyType) {
            case Cash:
                getAccount().addCash(amount);
                break;
            case MaplePoint:
                getAccount().addPoint(amount);
                break;
            case Point:
                getAccount().addMaplePoint(amount);
                break;
            case Meso:
                addMeso(amount);
                break;
        }
    }

    public void initMapTransferCoupon() {
        if (!getQuestEx().containsKey(QUEST_EX_MAP_TRANSFER_COUPON_FREE_USED)) {
            Map<String, String> options = new HashMap<>();
            options.put("count", "0");
            options.put("date", DateUtil.getFormatDate(DateUtil.getNextMonday()));
            addQuestEx(QUEST_EX_MAP_TRANSFER_COUPON_FREE_USED, options);
        } else {
            Map<String, String> options = getQuestEx().get(QUEST_EX_MAP_TRANSFER_COUPON_FREE_USED);
            String dateString = options.get("date");
            LocalDate date = DateUtil.getDate(dateString);
            if (!LocalDate.now().isBefore(date)) {
                options.put("count", "0");
                options.put("date", DateUtil.getFormatDate(DateUtil.getNextMonday()));
                addQuestEx(QUEST_EX_MAP_TRANSFER_COUPON_FREE_USED, options);
            }
        }
    }

    public void encodeRemainingSp(OutPacket out) {
        List<Integer> remainingSp = getRemainingSp();
        if (JobConstants.isExtendSpJob(getJob())) {
            out.write(getRemainingSpsSize());
            for (int i = 0; i < remainingSp.size(); i++) {
                if (remainingSp.get(i) > 0) {
                    out.write(i + 1);
                    out.writeInt(remainingSp.get(i));
                }
            }
        } else {
            out.writeShort(remainingSp.get(0));
        }
    }

    public void encode(OutPacket out, CharMask mask) {
        out.writeLong(mask.get());
        out.write(getCombatOrders());
        out.writeInt(-1);
        out.writeInt(-1);
        out.writeInt(-1);
        out.writeZeroBytes(6);
        if (mask.isInMask(CharMask.Character)) {
            out.writeInt(getId());
            out.writeInt(getId());
            out.writeInt(getWorld());
            out.writeAsciiString(getName(), 13);
            CharLook charLook = getCharLook();
            out.write(charLook.getGender());
            out.write(charLook.getSkin());
            out.writeInt(charLook.getFace());
            out.writeInt(charLook.getHair());
            out.write(charLook.getHairColorBase());
            out.write(charLook.getHairColorMixed());
            out.write(charLook.getHairColorProb());
            getStats().encode(out);
            out.writeLong(0); //
            out.writeLong(DateUtil.getFileTime(System.currentTimeMillis()));
            out.writeInt(getMapId());
            out.write(getSpawnPoint());
            out.writeShort(stats.getSubJob());
            if (JobConstants.isXenon(getJob()) || JobConstants.isDemon(getJob())) {
                out.writeInt(charLook.getMark());
            }
            out.write(0);
            out.writeLong(getCreatedTime());
            out.writeShort(stats.getFatigue());
            out.writeInt(stats.getFatigueUpdated() == 0 ? DateUtil.getTime() : stats.getFatigueUpdated());

            out.writeInt(stats.getCharismaExp());
            out.writeInt(stats.getInsightExp());
            out.writeInt(stats.getWillExp());
            out.writeInt(stats.getCraftExp());
            out.writeInt(stats.getSenseExp());
            out.writeInt(stats.getCharmExp());
            stats.getNonCombatStatDayLimit().encode(out);

            out.writeInt(0); //pvp exp
            out.write(10); //pvp grade
            out.writeInt(0); // pvp maplePoint
            out.write(5); // unk
            out.write(5); // pvp mode type
            out.writeInt(0); //event maplePoint

            out.writeReversedLong(getLastLogout());
            out.writeLong(MAX_TIME);
            out.writeLong(ZERO_TIME);
            out.writeZeroBytes(14);
            out.writeInt(-1);
            out.writeInt(0); //bBurning
            out.write(getBuddyCapacity()); //friend
            boolean hasBlessingOfFairy = getBlessOfFairyOrigin() != null;
            out.writeBool(hasBlessingOfFairy);
            if (hasBlessingOfFairy) {
                out.writeMapleAsciiString(getBlessOfFairyOrigin());
            }
            boolean hasBlessOfEmpress = getBlessOfEmpressOrigin() != null;
            out.writeBool(hasBlessOfEmpress);
            if (hasBlessingOfFairy) {
                out.writeMapleAsciiString(getBlessOfEmpressOrigin());
            }
            out.writeBool(false); //ultimate explorer
        }
        out.writeInt(0);
        out.write(-1);
        out.writeInt(0);
        out.write(-1);

        Account account = getAccount();
        Map<Integer, String> sharedQuestExStorage = account.getSharedQuestExStorage();
        out.writeShort(sharedQuestExStorage.size());
        sharedQuestExStorage.forEach((qrKey, qrValue) -> {
            out.writeInt(qrKey);
            out.writeMapleAsciiString(qrValue);
        });

        out.write(0);
        out.writeInt(0);
    }

    public void initPotionPot() {
        Item pt = getCashInventory().getItemByItemID(ItemConstants.POTION_POT);
        if (pt != null) {
            setPotionPot((PotionPot) pt);
        }
    }

    public void addFamiliar(Familiar familiar) {
        familiars.add(familiar);

    }

    public void removePet(Pet pet) {
        getPets().remove(pet);
    }

    public Familiar getFamiliar() {
        return familiar;
    }

    public Familiar getFamiliar(int id) {
        return Util.findWithPred(getFamiliars(), fam -> fam.getItemId() == id);
    }

    public Express getNewExpress() {
        return Util.findWithPred(getExpresses(), express -> express.getStatus() == 2);
    }

    public int getActiveNickItemId() {
        return activeNickItemId;
    }

    public boolean isBattleRecordOn() {
        return battleRecordOn;
    }

    public void setBattleRecordOn(boolean battleRecordOn) {
        this.battleRecordOn = battleRecordOn;
    }

    public Map<BaseStat, Integer> getTotalBasicStats() {
        Map<BaseStat, Integer> stats = new HashMap<>();
        for (BaseStat bs : BaseStat.values()) {
            stats.put(bs, getTotalStat(bs));
        }
        return stats;
    }

    public DamageCalc getDamageCalc() {
        return damageCalc;
    }

    public void setDamageCalc(DamageCalc damageCalc) {
        this.damageCalc = damageCalc;
    }


    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public void setPrepareSkill(int skill, int slv) {
        this.prepareSkill = new Pair<>(skill, slv);
    }

    public Pair<Integer, Integer> getPrepareSkill() {
        return prepareSkill;
    }

    public void initSoulMP() {
        Equip weapon = getEquippedEquip(BodyPart.Weapon);
        TemporaryStatManager tsm = getTemporaryStatManager();
        if (weapon != null && weapon.getSoulSocketId() != 0 && !tsm.hasStat(SoulMP)) {
            Option o = new Option();
            o.rOption = ItemConstants.getSoulSkillFromSoulID(weapon.getSoulOptionId());
            o.tOption = Integer.MAX_VALUE;
            o.xOption = ItemConstants.MAX_SOUL_CAPACITY;
            tsm.putCharacterStatValue(SoulMP, o);
            tsm.sendSetStatPacket();
        }
    }

    public void updatePartyHpBar() {
        for (MapleCharacter member : getParty().getOnlineChar()) {
            if (member.getMap().equals(getMap())) {
                member.announce(WorldPacket.receiveHP(this));
            }
        }
    }

    public void teleport(String portalName) {
        Portal portal = getMap().getPortal(portalName);
        announce(UserPacket.teleport(position, portal));
    }

    public void addVisitedMap(MapleMap map) {
        visitedMaps.add(map.getId());
    }
}

package im.cave.ms.provider.data;

import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.client.field.obj.npc.shop.NpcShop;
import im.cave.ms.client.field.obj.npc.shop.NpcShopItem;
import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.connection.server.Server;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataFileEntry;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import im.cave.ms.tools.StringUtil;
import im.cave.ms.tools.Util;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.data
 * @date 11/22 0:07
 */
public class NpcData {
    private static final Logger log = LoggerFactory.getLogger("NpcData");
    private static final MapleDataProvider npcData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Npc.wz"));
    private static final Set<Npc> npcs = new HashSet<>();
    private static final Map<Integer, NpcShop> shops = new HashMap<>();
    private static final List<Integer> illustrations = new ArrayList<>();
    private static final Map<Integer, Integer> npcShops = new HashMap<>();

    public static void loadNpcShops() {
        Session session = DataBaseManager.getSession();
        Query query = session.createNativeQuery("SELECT npcId,shopId FROM npc_shop");
        List rows = query.getResultList();
        for (Object row : rows) {
            Object[] cells = (Object[]) row;
            npcShops.put(((Integer) cells[0]), ((Integer) cells[1]));
        }
    }

    public static Npc getNpc(int npcId) {
        Npc npc = Util.findWithPred(npcs, n -> n.getTemplateId() == npcId);
        if (npc == null) {
            npc = getNpcDataFromWz(npcId);
        }
        return npc;
    }

    public static Npc getNpcDataFromWz(int npcId) {
        String npcStringId = StringUtil.getLeftPaddedStr(String.valueOf(npcId), '0', 7);
        String path = String.format("%s.img", npcStringId);
        MapleData data = npcData.getData(path);
        Npc npc = new Npc(npcId);
        if (data == null) {
            return null;
        }
        loadNpcData(data, npc);
        return npc;
    }

    public static NpcShop getShopById(int npcId) {
        if (!npcShops.containsKey(npcId)) {
            return null;
        }
        Integer shopId = npcShops.get(npcId);
        NpcShop shop = shops.get(shopId);
        if (shop == null) {
            shop = getNpcShopFromDB(npcId, shopId);
        }
        return shop;
    }

    private static NpcShop getNpcShopFromDB(int npcId, int shopId) {
        NpcShop ns = new NpcShop();
        ns.setNpcTemplateId(npcId);
        ns.setShopId(shopId);
        List<NpcShopItem> items = (List<NpcShopItem>) DataBaseManager.getObjListFromDB(NpcShopItem.class, "shopId", shopId);
        //check null itemId
        items.removeIf(item -> {
            boolean invalid = ItemData.getItemInfoById(item.getItemId()) == null;
            if (invalid) {
                log.error("商店ID:{} 商品{}不存在 錯誤物品ID:", shopId, item.getItemId());
            }
            return invalid;
        });
        items.sort(Comparator.comparingInt(NpcShopItem::getItemId));
        ns.setItems(items);
        addShop(shopId, ns);
        return ns;
    }

    private static void addShop(int id, NpcShop ns) {
        shops.put(id, ns);
    }

    public static void refreshShop() {
        shops.clear();
    }

    public static void loadNpcDataFromWz() {
        for (MapleDataFileEntry file : npcData.getRoot().getFiles()) {
            MapleData data = NpcData.npcData.getData(file.getName());
            if (data == null) {
                return;
            }
            String name = data.getName();
            String npcId = name.substring(0, name.indexOf('.'));
            Npc npc = new Npc(Integer.parseInt(npcId));
            loadNpcData(data, npc);
            npcs.add(npc);
            if (npc.isIllustration()) {
                illustrations.add(npc.getTemplateId());
            }
        }
        Server.getInstance().setOnline(true);
    }

    private static void loadNpcData(MapleData data, Npc npc) {
        npc.setMove(data.getChildByPath("move") != null);
        MapleData info = data.getChildByPath("info");
        if (info != null) {
            MapleData scriptData = info.getChildByPath("script");
            if (scriptData != null) {
                for (MapleData script : scriptData.getChildren()) {
                    String ids = script.getName();
                    if (!StringUtil.isNumber(ids)) {
                        continue;
                    }
                    int scriptId = Integer.parseInt(ids);
                    MapleData scriptInfo = script.getChildByPath("script");
                    if (scriptInfo != null) {
                        String scriptName = MapleDataTool.getString(scriptInfo);
                        npc.getScripts().put(scriptId, scriptName);
                    }
                }
            }
            for (MapleData attr : info.getChildren()) {
                String name = attr.getName();
                switch (name) {
                    case "shop":
                        npc.setShop(MapleDataTool.getInt(attr, 0) != 0);
                        break;
                    case "trunkGet":
                        npc.setTrunkGet(MapleDataTool.getInt(attr));
                        break;
                    case "trunkPut":
                        npc.setTrunkPut(MapleDataTool.getInt(attr));
                        break;
                    case "dcLeft":
                        npc.getDCRange().setLeft(MapleDataTool.getInt(attr));
                        break;
                    case "dcRight":
                        npc.getDCRange().setRight(MapleDataTool.getInt(attr));
                        break;
                    case "dcTop":
                        npc.getDCRange().setTop(MapleDataTool.getInt(attr));
                        break;
                    case "dcBottom":
                        npc.getDCRange().setBottom(MapleDataTool.getInt(attr));
                        break;
                    case "illustration2":
                        npc.setIllustration(true);
                        break;
                }
            }
        }
        npcs.add(npc);
    }

    public static Integer getIllustration() {
        return Util.getRandomFromCollection(illustrations);
    }

}

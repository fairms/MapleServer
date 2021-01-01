package im.cave.ms.provider.data;

import im.cave.ms.client.field.obj.npc.Npc;
import im.cave.ms.client.field.obj.npc.shop.NpcShop;
import im.cave.ms.client.field.obj.npc.shop.NpcShopItem;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.network.db.DataBaseManager;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import im.cave.ms.tools.StringUtil;
import im.cave.ms.tools.Util;

import java.io.File;
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
    private static final MapleDataProvider npcData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/Npc.wz"));
    private static final Set<Npc> npcs = new HashSet<>();
    private static final Map<Integer, NpcShop> shops = new HashMap<>();

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
                }
            }
        }
        npcs.add(npc);
        return npc;
    }

    public static NpcShop getShopById(int npcId) {
        NpcShop shop = shops.get(npcId);
        if (shop == null) {
            shop = loadNpcShopFromDB(npcId);
        }
        return shop;
    }

    private static NpcShop loadNpcShopFromDB(int id) {
        NpcShop ns = new NpcShop();
        ns.setNpcTemplateId(id);
        ns.setShopId(id);
        List<NpcShopItem> items = (List<NpcShopItem>) DataBaseManager.getObjListFromDB(NpcShopItem.class, "shopId", id);
        items.sort(Comparator.comparingInt(NpcShopItem::getItemId));
        ns.setItems(items);
        addShop(id, ns);
        return ns;
    }

    private static void addShop(int id, NpcShop ns) {
        shops.put(id, ns);
    }

    public static void refreshShop() {
        shops.clear();
    }
}

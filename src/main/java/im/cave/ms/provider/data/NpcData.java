package im.cave.ms.provider.data;

import im.cave.ms.client.field.obj.Npc;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.net.packet.PacketHelper;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataDirectoryEntry;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import im.cave.ms.tools.StringUtil;

import java.io.File;
import java.util.HashSet;
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


    public static Npc getNpcDataFromWz(int npcId) {
        String path = String.format("%d.img", npcId);
        MapleData data = npcData.getData(path);
        Npc npc = new Npc(npcId);
        if (data == null) {
            return npc;
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

    public static void getShopById(int npcId) {

    }
}

package im.cave.ms.provider.data;

import im.cave.ms.constants.ServerConstants;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.data
 * @date 11/22 0:06
 */
public class StringData {
    private static final MapleDataProvider stringData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/String.wz"));

    private static final Map<Integer, String> equipNames = new HashMap<>();

    public static void init() {
        loadEquipNames();
    }


    public static void loadEquipNames() {
        MapleData eqpNameData = stringData.getData("Eqp.img");
        for (MapleData eqpClass : eqpNameData.getChildByPath("Eqp").getChildren()) {
            for (MapleData eqp : eqpClass) {
                String id = eqp.getName();
                String name = MapleDataTool.getString("name", eqp, "Unknown");
                equipNames.put(Integer.valueOf(id), name);
            }
        }
    }

    public static String getEquipName(int itemId) {
        String name = equipNames.get(itemId);
        if (name == null) {
            return "Unknown";
        }
        return name;
    }
}

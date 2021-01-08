package im.cave.ms.provider.data;

import im.cave.ms.constants.ItemConstants;
import im.cave.ms.constants.ServerConstants;
import im.cave.ms.provider.wz.MapleData;
import im.cave.ms.provider.wz.MapleDataProvider;
import im.cave.ms.provider.wz.MapleDataProviderFactory;
import im.cave.ms.provider.wz.MapleDataTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(StringData.class);
    private static final MapleDataProvider stringData = MapleDataProviderFactory.getDataProvider(new File(ServerConstants.WZ_DIR + "/String.wz"));

    private static final Map<Integer, String> equipNames = new HashMap<>();
    private static final Map<Integer, String> consumeNames = new HashMap<>();
    private static final Map<Integer, String> etcNames = new HashMap<>();
    private static final Map<Integer, String> insNames = new HashMap<>();
    private static final Map<Integer, String> cashItemNames = new HashMap<>();
    private static final Map<Integer, String> mobNames = new HashMap<>();
    private static final Map<Integer, String> petNames = new HashMap<>();
    private static final Map<Integer, String> familiarSkills = new HashMap<>();

    public static void init() {
        loadEquipNames();
        loadConsumeItemNames();
        loadFamiliarSkills();
        loadMobNames();
        loadPetNames();
    }


    public static void loadPetNames() {
        MapleData root = stringData.getData("Pet.img");
        for (MapleData data : root.getChildren()) {
            String id = data.getName();
            String name = MapleDataTool.getString("name", data, "unknown");
            petNames.put(Integer.valueOf(id), name);
        }
    }

    public static void loadMobNames() {
        MapleData root = stringData.getData("Mob.img");
        for (MapleData data : root.getChildren()) {
            String id = data.getName();
            String name = MapleDataTool.getString("name", data, "unknown");
            mobNames.put(Integer.valueOf(id), name);
        }
    }

    public static void loadEquipNames() {
        MapleData eqpNameData = stringData.getData("Eqp.img");
        for (MapleData eqpClass : eqpNameData.getChildByPath("Eqp").getChildren()) {
            for (MapleData eqp : eqpClass) {
                String id = eqp.getName();
                String name = MapleDataTool.getString("name", eqp, "unknown");
                equipNames.put(Integer.valueOf(id), name);
            }
        }
    }

    public static void loadConsumeItemNames() {
        MapleData root = stringData.getData("Consume.img");
        for (MapleData data : root.getChildren()) {
            String id = data.getName();
            String name = MapleDataTool.getString("name", data, "unknown");
            consumeNames.put(Integer.valueOf(id), name);
        }
    }

    public static void loadFamiliarSkills() {
        MapleData root = stringData.getData("FamiliarSkill.img");
        for (MapleData data : root.getChildByPath("skill").getChildren()) {
            String id = data.getName();
            String name = MapleDataTool.getString("name", data, "unknown");
            familiarSkills.put(Integer.valueOf(id), name);
        }

    }

    public static String getEquipName(int itemId) {
        return equipNames.getOrDefault(itemId, "unknown");
    }

    public static String getConsumeItemName(int itemId) {
        return consumeNames.getOrDefault(itemId, "unknown");
    }

    public static String getPetNameById(int itemId) {
        return petNames.getOrDefault(itemId, "unknown");
    }

    public static Map<Integer, String> getEquipNames() {
        return equipNames;
    }

    public static Map<Integer, String> getConsumeNames() {
        return consumeNames;
    }

    public static Map<Integer, String> getEtcNames() {
        return etcNames;
    }

    public static Map<Integer, String> getInsNames() {
        return insNames;
    }

    public static Map<Integer, String> getCashItemNames() {
        return cashItemNames;
    }

    public static Map<Integer, String> getMobNames() {
        return mobNames;
    }

    public static Map<Integer, String> getPetNames() {
        return petNames;
    }

    public static Map<Integer, String> getFamiliarSkills() {
        return familiarSkills;
    }

    public static String getItemName(int itemId) {
        if (ItemConstants.isEquip(itemId)) {
            return getEquipName(itemId);
        } else {
            String name;
            name = getConsumeNames().get(itemId);
            if (name == null) {
                name = getCashItemNames().get(itemId);
                if (name == null) {
                    name = getInsNames().getOrDefault(itemId, "unknown");
                }
            }
            return name;
        }
    }
}

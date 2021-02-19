package im.cave.ms.connection.server.service.task;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.client.character.items.Inventory;
import im.cave.ms.connection.server.service.EventManager;
import im.cave.ms.enums.InventoryType;
import im.cave.ms.tools.DateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 对在线角色的过期道具进行回收
 * 为在线角色的过期道具将于角色登录后进行回收
 *
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.connection.server.service
 * @date 2/18 15:54
 */
public class ExpiredItemRecycle {
    private static final Map<MapleCharacter, ScheduledFuture<?>> tasks = new HashMap<>();

    public static void remove(MapleCharacter chr) {
        if (tasks.containsKey(chr)) {
            tasks.get(chr).cancel(true);
            tasks.remove(chr);
        }
    }

    public static void add(MapleCharacter chr) {
        ScheduledFuture<?> scheduledFuture = EventManager.addFixedRateEvent(() -> run(chr), 0, 5, TimeUnit.MINUTES);
        tasks.put(chr, scheduledFuture);
    }

    public static void run(MapleCharacter chr) {
        long currentFileTime = DateUtil.getFileTime(System.currentTimeMillis());
        for (InventoryType value : InventoryType.values()) {
            Inventory inventory = chr.getInventory(value);
            if (inventory == null) {
                continue;
            }
            inventory.getItems().removeIf(item -> item.getExpireTime() < currentFileTime);
        }
    }
}

package im.cave.ms.provider.data;

import im.cave.ms.connection.db.DataBaseManager;
import im.cave.ms.provider.info.DropInfo;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.provider.data
 * @date 12/10 15:00
 */
public class DropData {
    private static final Logger log = LoggerFactory.getLogger(DropData.class);

    private static final HashMap<Integer, Set<DropInfo>> drops = new HashMap<>();

    public static Set<DropInfo> getDrops(int mobId) {
        Set<DropInfo> mobDrops = drops.getOrDefault(mobId, null);
        if (mobDrops == null) {
            mobDrops = getDropsFromDb(mobId);
            if (mobId != -1) {
                mobDrops.addAll(getDropsFromDb(-1));
            }
        }
        drops.put(mobId, mobDrops);
        return mobDrops;
    }

    private static Set<DropInfo> getDropsFromDb(int mobId) {
        List l;
        try (Session session = DataBaseManager.getSession()) {
            Transaction transaction = session.beginTransaction();
            javax.persistence.Query query = session.createQuery("FROM DropInfo WHERE mobId = :mobId");
            query.setParameter("mobId", mobId);
            l = ((org.hibernate.query.Query) query).list();
            transaction.commit();
            session.close();
        }
        return l == null ? new HashSet<>() : new HashSet<>(l);
    }

    public static void addDrop(int mobId, DropInfo dropInfo) {
        if (!drops.containsKey(mobId)) {
            drops.put(mobId, new HashSet<>());
        }
        drops.get(mobId).add(dropInfo);
    }

    public static void clear() {
        drops.clear();
    }
}

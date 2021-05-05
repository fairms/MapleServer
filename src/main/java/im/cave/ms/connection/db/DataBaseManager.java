package im.cave.ms.connection.db;

import im.cave.ms.client.Account;
import im.cave.ms.client.HotTimeReward;
import im.cave.ms.client.Record;
import im.cave.ms.client.character.*;
import im.cave.ms.client.character.items.CashShopItem;
import im.cave.ms.client.character.items.Equip;
import im.cave.ms.client.character.items.ExceptionItem;
import im.cave.ms.client.character.items.FlameStats;
import im.cave.ms.client.character.items.Inventory;
import im.cave.ms.client.character.items.Item;
import im.cave.ms.client.character.items.PetItem;
import im.cave.ms.client.character.items.WishedItem;
import im.cave.ms.client.character.potential.CharacterPotential;
import im.cave.ms.client.character.skill.MatrixInventory;
import im.cave.ms.client.character.skill.MatrixSkill;
import im.cave.ms.client.character.skill.MatrixSlot;
import im.cave.ms.client.character.skill.Skill;
import im.cave.ms.client.field.obj.Android;
import im.cave.ms.client.multiplayer.guilds.GuildGrade;
import im.cave.ms.client.multiplayer.guilds.GuildSkill;
import im.cave.ms.provider.info.DropInfo;
import im.cave.ms.client.field.obj.Familiar;
import im.cave.ms.client.field.obj.npc.shop.NpcShopItem;
import im.cave.ms.client.multiplayer.Express;
import im.cave.ms.client.multiplayer.MapleNotes;
import im.cave.ms.client.multiplayer.friend.Friend;
import im.cave.ms.client.multiplayer.guilds.Guild;
import im.cave.ms.client.multiplayer.guilds.GuildMember;
import im.cave.ms.client.multiplayer.guilds.GuildRequestor;
import im.cave.ms.client.quest.Quest;
import im.cave.ms.client.quest.QuestManager;
import im.cave.ms.client.quest.progress.QuestProgressItemRequirement;
import im.cave.ms.client.quest.progress.QuestProgressLevelRequirement;
import im.cave.ms.client.quest.progress.QuestProgressMobRequirement;
import im.cave.ms.client.quest.progress.QuestProgressMoneyRequirement;
import im.cave.ms.client.quest.progress.QuestProgressRequirement;
import im.cave.ms.client.storage.Locker;
import im.cave.ms.client.storage.Storage;
import im.cave.ms.client.storage.Trunk;
import im.cave.ms.provider.info.CashItemInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.net.db
 * @date 11/23 10:30
 */
public class DataBaseManager {
    private static final Logger log = LoggerFactory.getLogger(DataBaseManager.class);
    private static SessionFactory sessionFactory;
    private static List<Session> sessions;

    public static void init() {
        Configuration configuration = new Configuration().configure();
        configuration.setProperty("autoReconnect", "true");
        Class<?>[] dbClasses = new Class[]{
                Android.class,
                PetItem.class,
                FlameStats.class,
                NonCombatStatDayLimit.class,
                Item.class,
                Equip.class,
                Inventory.class,
                Skill.class,
                KeyBind.class,
                MapleCharacter.class,
                MapleKeyMap.class,
                CharLook.class,
                CharStats.class,
                Account.class,
                QuestManager.class,
                Quest.class,
                QuestProgressRequirement.class,
                QuestProgressLevelRequirement.class,
                QuestProgressItemRequirement.class,
                QuestProgressMobRequirement.class,
                QuestProgressMoneyRequirement.class,
                Guild.class,
                GuildMember.class,
                GuildRequestor.class,
                GuildGrade.class,
                GuildSkill.class,
                Friend.class,
                Macro.class,
                DamageSkinSaveData.class,
                Trunk.class,
                PetItem.class,
                CharacterPotential.class,
                Familiar.class,
                CashItemInfo.class,
                CashShopItem.class,
                DropInfo.class,
                NpcShopItem.class,
                Record.class,
                Locker.class,
                Trunk.class,
                Storage.class,
                MapleNotes.class,
                WishedItem.class,
                ExceptionItem.class,
                HotTimeReward.class,
                Express.class,
                LinkSkill.class,
                MatrixInventory.class,
                MatrixSkill.class,
                MatrixSlot.class

        };
        for (var clazz : dbClasses) {
            configuration.addAnnotatedClass(clazz);
        }
        sessionFactory = configuration.buildSessionFactory();
        sessions = new ArrayList<>();
    }


    public static Session getSession() {
        Session session = sessionFactory.openSession();
        sessions.add(session);
        return session;
    }

    public static void cleanUpSessions() {
        sessions.removeAll(sessions.stream().filter(s -> !s.isOpen()).collect(Collectors.toList()));
    }

    public static void saveToDB(Object obj) {
        log.debug(String.format("%s: Trying to save obj %s.", LocalDateTime.now(), obj));
        synchronized (obj) {
            try (Session session = getSession()) {
                Transaction t = session.beginTransaction();
                session.saveOrUpdate((obj));
                t.commit();
            }
        }
        cleanUpSessions();
    }

    public static void deleteFromDB(Object obj) {
        log.debug(String.format("%s: Trying to delete obj %s.", LocalDateTime.now(), obj));
        synchronized (obj) {
            try (Session session = getSession()) {
                Transaction t = session.beginTransaction();
                session.delete(obj);
                t.commit();
            }
        }
        cleanUpSessions();
    }

    public static Object getObjFromDB(Class clazz, int id) {
        log.debug(String.format("%s: Trying to get obj %s with id %d.", LocalDateTime.now(), clazz, id));
        Object o;
        try (Session session = getSession()) {
            Transaction t = session.beginTransaction();
            o = session.get(clazz, id);
            t.commit();
        }
        return o;
    }

    public static Object getObjFromDB(Class clazz, String name) {
        return getObjFromDB(clazz, "name", name);
    }

    public static Object getObjFromDB(Class clazz, String columnName, Object value) {
        log.debug(String.format("%s: Trying to get obj %s with value %s.", LocalDateTime.now(), clazz, value));
        Object o = null;
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems
            javax.persistence.Query query = session.createQuery(String.format("FROM %s WHERE %s = :val", clazz.getName(), columnName));
//            System.out.println(((Query) query).getQueryString());
            query.setParameter("val", value);
            List l = ((org.hibernate.query.Query) query).list();
            if (l != null && l.size() > 0) {
                o = l.get(0);
            }
            transaction.commit();
            session.close();
        }
        return o;
    }

    public static Object getObjListFromDB(Class clazz) {
        List list;
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems
            javax.persistence.Query query = session.createQuery(String.format("FROM %s", clazz.getName()));
            list = ((org.hibernate.query.Query) query).list();
            transaction.commit();
            session.close();
        }
        return list;
    }

    public static Object getObjListFromDB(Class clazz, String columnName, Object value) {
        log.debug(String.format("%s: Trying to get obj %s with value %s.", LocalDateTime.now(), clazz, value));
        List list;
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems
            javax.persistence.Query query = session.createQuery(String.format("FROM %s WHERE %s = :val", clazz.getName(), columnName));
//            System.out.println(((Query) query).getQueryString());
            query.setParameter("val", value);
            list = ((org.hibernate.query.Query) query).list();
            transaction.commit();
            session.close();
        }
        return list;
    }

    public static <T> Object getObjListFromDB(Class T, String cloumn1, Object value1, String cloumn2, Object value2) {
        log.debug(String.format("%s: Trying to get obj %s with value %s , %s.", LocalDateTime.now(), T, value1, value2));
        List<T> list;
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems
            javax.persistence.Query query = session.createQuery(String.format("FROM %s WHERE %s = :val1 AND %s = :val2", T.getName(), cloumn1, cloumn2));
//            System.out.println(((Query) query).getQueryString());
            query.setParameter("val1", value1);
            query.setParameter("val2", value2);
            list = ((org.hibernate.query.Query) query).list();
            transaction.commit();
            session.close();
        }
        return list;
    }


}


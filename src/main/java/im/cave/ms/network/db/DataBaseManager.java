package im.cave.ms.network.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
@Component
public class DataBaseManager {
    private static final Logger log = LoggerFactory.getLogger(DataBaseManager.class);
    @Resource
    private SessionFactory sf;
    private static SessionFactory sessionFactory;

    @PostConstruct
    public void init() {
        sessions = new ArrayList<>();
        sessionFactory = sf;
    }

    private static List<Session> sessions;


    public static Session getSession() {
        Session session = sessionFactory.openSession();
        sessions.add(session);
        return session;
    }

    public static void cleanUpSessions() {
        sessions.removeAll(sessions.stream().filter(s -> !s.isOpen()).collect(Collectors.toList()));
    }

    public static void saveToDB(Object obj) {
        log.info(String.format("%s: Trying to save obj %s.", LocalDateTime.now(), obj));
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
        log.info(String.format("%s: Trying to delete obj %s.", LocalDateTime.now(), obj));
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
        log.info(String.format("%s: Trying to get obj %s with id %d.", LocalDateTime.now(), clazz, id));
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
        log.info(String.format("%s: Trying to get obj %s with value %s.", LocalDateTime.now(), clazz, value));
        Object o = null;
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems
            javax.persistence.Query query = session.createQuery(String.format("FROM %s WHERE %s = :val", clazz.getName(), columnName));
            System.out.println(((Query) query).getQueryString());
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
        log.info(String.format("%s: Trying to get obj %s with value %s.", LocalDateTime.now(), clazz, value));
        List list;
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems
            javax.persistence.Query query = session.createQuery(String.format("FROM %s WHERE %s = :val", clazz.getName(), columnName));
            System.out.println(((Query) query).getQueryString());
            query.setParameter("val", value);
            list = ((org.hibernate.query.Query) query).list();
            transaction.commit();
            session.close();
        }
        return list;
    }

    public static <T> Object getObjListFromDB(Class T, String cloumn1, Object value1, String cloumn2, Object value2) {
        log.info(String.format("%s: Trying to get obj %s with value %s , %s.", LocalDateTime.now(), T, value1, value2));
        List<T> list;
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems
            javax.persistence.Query query = session.createQuery(String.format("FROM %s WHERE %s = :val1 AND %s = :val2", T.getName(), cloumn1, cloumn2));
            System.out.println(((Query) query).getQueryString());
            query.setParameter("val1", value1);
            query.setParameter("val2", value2);
            list = ((org.hibernate.query.Query) query).list();
            transaction.commit();
            session.close();
        }
        return list;
    }


}


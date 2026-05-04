package dbService.executor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import java.util.Properties;
import java.util.function.Function;

public class HibernateExecutor {
    private static SessionFactory sessionFactory;

    public static synchronized SessionFactory getSessionFactory(Properties props) {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(dbService.dataSets.UsersDataSet.class);

            configuration.setProperty("hibernate.connection.url", props.getProperty("db.url"));
            configuration.setProperty("hibernate.connection.username", props.getProperty("db.user"));
            configuration.setProperty("hibernate.connection.password", props.getProperty("db.password"));


            configuration.setProperty("hibernate.dialect", props.getProperty("hibernate.dialect"));
            configuration.setProperty("hibernate.connection.driver_class", props.getProperty("hibernate.connection.driver_class"));
            configuration.setProperty("hibernate.show_sql", props.getProperty("hibernate.show_sql", "true"));
            configuration.setProperty("hibernate.format_sql", props.getProperty("hibernate.format_sql", "true"));
            configuration.setProperty("hibernate.hbm2ddl.auto", props.getProperty("hibernate.hbm2ddl.auto", "validate"));

            sessionFactory = configuration.buildSessionFactory();
        }
        return sessionFactory;
    }

    public static <T> T executeInTransaction(Function<Session, T> action) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            T result = action.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("БД транзакция выдала ошибку", e);
        } finally {
            session.close();
        }
    }

    public static <T> T executeInSession(Function<Session, T> action) {
        Session session = sessionFactory.openSession();
        try {
            return action.apply(session);
        } finally {
            session.close();
        }
    }
}

package dbService.dao;

import dbService.dataSets.UsersDataSet;
import org.hibernate.Session;

public class UsersDAO {

    public UsersDataSet findByLogin(Session session, String login) {
        String hql = "FROM UsersDataSet u WHERE u.login = :login";
        return session.createQuery(hql, UsersDataSet.class)
                .setParameter("login", login)
                .uniqueResult();
    }

    public void save(Session session, UsersDataSet user) {
        session.persist(user);
    }
}
package dbService;

import dbService.dao.UsersDAO;
import dbService.dataSets.UsersDataSet;
import dbService.executor.HibernateExecutor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernateService {
    private static HibernateService instance;
    private static final String PROPERTIES_FILE = "db.properties";

    private HibernateService() {
        try {
            Properties props = loadProperties();
            HibernateExecutor.getSessionFactory(props);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить db.properties", e);
        }
    }

    public static synchronized HibernateService getInstance() {
        if (instance == null) {
            instance = new HibernateService();
        }
        return instance;
    }

    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) throw new RuntimeException("Файл db.properties не найден в classpath");
            props.load(input);
        }
        return props;
    }

    public void addUser(String login, String password, String email) {
        UsersDAO dao = new UsersDAO();
        HibernateExecutor.executeInTransaction(session -> {
            dao.save(session, new UsersDataSet(login, password, email));
            return null;
        });
    }

    public UsersDataSet getUserByLogin(String login) {
        UsersDAO dao = new UsersDAO();
        return HibernateExecutor.executeInSession(session ->
                dao.findByLogin(session, login)
        );
    }
}
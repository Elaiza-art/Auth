package dbService;

import dbService.dao.UsersDAO;
import dbService.dataSets.UsersDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBService {

    private static DBService instance;
    private Connection connection;

    private DBService() {
        this.connection = getMySQLConnection();
    }

    public static synchronized DBService getInstance() {
        if (instance == null) {
            instance = new DBService();
        }
        return instance;
    }

    private Properties loadDbProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Не удалось найти db.properties");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения db.properties", e);
        }
        return props;
    }

    private Connection getMySQLConnection() {
        try {
            Properties props = loadDbProperties();
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Загрузка драйвера
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Подключено к БД: " + url);
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Не удалось подключиться к БД", e);
        }
    }

    public void addUser(String login, String password, String email) throws DBException {
        try {
            connection.setAutoCommit(false);
            UsersDAO dao = new UsersDAO(connection);
            dao.insertUser(login, password, email);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {}
            throw new DBException("Ошибка при регистрации пользователя", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {}
        }
    }

    public UsersDataSet getUserByLogin(String login) throws DBException {
        try {
            return new UsersDAO(connection).findByLogin(login);
        } catch (SQLException e) {
            throw new DBException("Ошибка при поиске пользователя", e);
        }
    }
}

package dbService.dao;

import dbService.dataSets.UsersDataSet;
import dbService.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;

public class UsersDAO {
    private final Executor executor;

    public UsersDAO(Connection connection) {
        this.executor = new Executor(connection);
    }

    public UsersDataSet findByLogin(String login) throws SQLException {
        String sql = "SELECT login, password, email FROM users WHERE login = ?";
        return executor.execQuery(sql, stmt -> stmt.setString(1, login), rs -> {
            if (rs.next()) {
                return new UsersDataSet(
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
            return null;
        });
    }

    public void insertUser(String login, String password, String email) throws SQLException {
        String sql = "INSERT INTO users (login, password, email) VALUES (?, ?, ?)";
        executor.execUpdate(sql, stmt -> {
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setString(3, email);
        });
    }
}

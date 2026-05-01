package dbService.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Executor {
    private final Connection connection;

    public Executor(Connection connection) {
        this.connection = connection;
    }

    public void execUpdate(String sql, PreparedStatementSetter setter) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (setter != null) setter.setParameters(stmt);
            stmt.executeUpdate();
        }
    }

    public <T> T execQuery(String sql, PreparedStatementSetter setter, ResultHandler<T> handler) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (setter != null) setter.setParameters(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                return handler.handle(rs);
            }
        }
    }

    // Вспомогательный ф.и для установки параметров
    @FunctionalInterface
    public interface PreparedStatementSetter {
        void setParameters(PreparedStatement stmt) throws SQLException;
    }
}

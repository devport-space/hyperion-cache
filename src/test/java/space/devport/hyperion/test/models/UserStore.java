package space.devport.hyperion.test.models;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.store.PersistentStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStore extends PersistentStore<User> {

    private final Connection connection;

    public UserStore(RedisConnector connector) {
        super(connector);

        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("toor");
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/hyperion");
        HikariDataSource ds = new HikariDataSource(config);

        try {
            this.connection = ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getRedisCollectionName() {
        return "users";
    }

    @Override
    public User createEntry(String identifier) {
        return new User(this.connector, getKey().identifier(identifier));
    }

    @Override
    public void save(User entry) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("INSERT INTO `players` (name, money) VALUES (?, ?) ON DUPLICATE KEY UPDATE money = ?;");
            statement.setString(1, entry.getIdentifier());
            statement.setDouble(2, entry.money().get());
            statement.setDouble(3, entry.money().get());
            statement.execute();
            System.out.printf("Saved entry %s into persistent DB.\n", entry.getIdentifier());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load(User entry) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(String.format("SELECT name, money from `players` where `players`.`name`='%s';", entry.getIdentifier()));
            ResultSet set = statement.executeQuery();
            if (!set.next()) {
                System.err.printf("Failed to load player %s from db.\n", entry.getIdentifier());
                return;
            }
            entry.money().set(set.getDouble("money"));
            System.out.printf("Loaded player %s from db.\n", entry.getIdentifier());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

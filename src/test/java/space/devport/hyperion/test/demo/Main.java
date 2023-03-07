package space.devport.hyperion.test.demo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.field.CacheField;
import space.devport.hyperion.factory.EntryFactory;
import space.devport.hyperion.persistence.PersistenceProvider;
import space.devport.hyperion.test.demo.models.DemoPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, InterruptedException {
        System.out.println("Starting demo app.");

        HyperionCache cache = new HyperionCache();

        cache.addFactory(DemoPlayer.class, new EntryFactory<>() {
            @Override
            public DemoPlayer createEntry(RedisConnector connector, String identifier) {
                return new DemoPlayer(connector, identifier);
            }

            @Override
            public String parseIdentifier(String str) {
                return str;
            }
        });

        // init db
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("toor");
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/hyperion");
        HikariDataSource ds = new HikariDataSource(config);

        Connection conn = ds.getConnection();

        cache.addPersistenceProvider(DemoPlayer.class, new PersistenceProvider<>() {
            @Override
            public void load(DemoPlayer entry) {
                try {
                    PreparedStatement statement = conn.prepareStatement(String.format("SELECT name, money from `players` where `players`.`name`='%s';", entry.getIdentifier()));
                    ResultSet set = statement.executeQuery();
                    if (!set.next()) {
                        System.err.printf("Failed to load player %s from db.\n", entry.getIdentifier());
                        return;
                    }
                    entry.money().set(set.getLong("money"));
                    System.out.printf("Loaded player %s from db.\n", entry.getIdentifier());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void save(DemoPlayer entry) {
                try {
                    PreparedStatement statement = conn.prepareStatement("INSERT INTO `players` (name, money) VALUES (?, ?) ON DUPLICATE KEY UPDATE money = ?;");
                    statement.setString(1, entry.getStringIdentifier());
                    statement.setLong(2, entry.money().get());
                    statement.setLong(3, entry.money().get());
                    statement.execute();
                    System.out.printf("Saved entry %s into persistent DB.\n", entry.getIdentifier());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void saveField(DemoPlayer entry, CacheField<?> field) {
                try {
                    PreparedStatement statement = conn.prepareStatement(String.format("INSERT INTO `players` (name, %s) VALUES (?, ?) ON DUPLICATE UPDATE %s = ?;", field.fieldName(), field.fieldName()));
                    statement.setString(1, entry.getStringIdentifier());
                    statement.setObject(2, field.get());
                    statement.setObject(3, field.get());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // obtain a handle
        DemoPlayer player = cache.createHandle(DemoPlayer.class, "Wertik1206");

        // todo: own notifications with field and value? field at least
        //  value could change until the update
        cache.startKeyspaceNotifications();

        Thread.sleep(1000L);

        player.money().increment(200L);
        System.out.println(player.money().get());
    }
}

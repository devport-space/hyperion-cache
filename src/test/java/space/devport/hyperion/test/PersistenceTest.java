package space.devport.hyperion.test;

public class PersistenceTest {

    /*@Test
    public void demo() throws SQLException, InterruptedException {
        HyperionCache cache = new HyperionCache();

        cache.addFactory(User.class, new EntryFactory<>() {
            @Override
            public User createEntry(RedisConnector connector, String identifier) {
                return new User(connector, identifier);
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

        cache.addPersistenceProvider(User.class, new PersistenceProvider<>() {
            @Override
            public void load(User entry) {
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
            public void save(User entry) {
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
        });

        // obtain a handle
        User player = cache.createHandle(User.class, "Wertik1206");
        player.money().set(100L);

        cache.saveUpdatedEntries();
        Thread.sleep(1000L);

        cache.saveUpdatedEntries();
        // updated entries should get removed from the internal list
        // hence nothing saves here
        Assert.assertEquals(0, cache.saveUpdatedEntries());

        player.money().set(666L);
        cache.loadPersistentEntry(player);

        Assert.assertEquals(100L, (long) player.money().get());
    }*/
}

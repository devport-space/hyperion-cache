package space.devport.hyperion.test;

import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.resps.Tuple;
import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Key;
import space.devport.hyperion.entry.field.DoubleField;
import space.devport.hyperion.leaderboard.Leaderboard;
import space.devport.hyperion.test.models.User;
import space.devport.hyperion.test.models.UserStore;

import java.util.List;

public class RedisCacheTest {

    private final RedisConnector connector = RedisConnector.create();

    /*
     * The API should be usable even when parts of abstraction are omitted.
     * */

    @Test
    public void onlyEntry() {
        User user = new User(this.connector, Key.onDefaultBase("users", "Wertik1206"));

        // Set the money
        user.money().set(100D);

        // expect it to be set properly
        double money = this.connector.withConnection((jedis) -> {
            return Double.valueOf(jedis.hget(String.format("%s.%s:%s", HyperionCache.ENTRY_KEY_BASE, "users", "Wertik1206"), "money"));
        });

        Assert.assertEquals(100D, money, 0.01);
    }

    @Test
    public void onlyField() {
        DoubleField money = new DoubleField(
                this.connector,
                Key.onDefaultBase("users", "Wertik1206"),
                "money"
        );

        // Set the money
        money.set(100D);

        // expect it to be set properly
        double expMoney = this.connector.withConnection((jedis) -> {
            return Double.valueOf(jedis.hget(String.format("%s.%s:%s", HyperionCache.ENTRY_KEY_BASE, "users", "Wertik1206"), "money"));
        });

        Assert.assertEquals(100D, expMoney, 0.01);
    }

    @Test
    public void getset() {
        // create a store that provides entries
        UserStore userStore = new UserStore(connector);

        // obtain an entry that's tied to a specific object
        User user = userStore.entry("Wertik1206");

        // set value
        user.money().set(100D);

        // get a value from it
        double money = user.money().get();

        Assert.assertEquals(100D, money, 0.01);
    }

    @Test
    public void leaderboard() {
        UserStore store = new UserStore(connector);

        Leaderboard<User> leaderboard = store.leaderboard("users-money", User::money);

        leaderboard.add("Wertik1206");

        String first = leaderboard.at(0);

        Assert.assertEquals("Wertik1206", first);
    }

    @Test
    public void leaderboardMultipleEntries() {
        UserStore store = new UserStore(connector);

        Leaderboard<User> leaderboard = store.leaderboard("users-money-multiple", User::money);

        store.entry("Wertik1206").money().set(100D);
        store.entry("MoonSoD").money().set(200D);

        leaderboard.add("Wertik1206", "MoonSoD");

        String first = leaderboard.at(0);

        Assert.assertEquals("MoonSoD", first);
    }

    @Test
    public void leaderboardEntryAt() {
        UserStore store = new UserStore(connector);

        Leaderboard<User> leaderboard = store.leaderboard("users-money-multiple", User::money);

        store.entry("Wertik1206").money().set(100D);
        store.entry("MoonSoD").money().set(200D);

        leaderboard.add("Wertik1206", "MoonSoD");

        User first = leaderboard.entryAt(0);

        Assert.assertEquals("MoonSoD", first.getIdentifier());
        Assert.assertEquals(200D, first.money().get(), 0.01);
    }

    @Test
    public void leaderboardEntryRange() {
        UserStore store = new UserStore(connector);

        Leaderboard<User> leaderboard = store.leaderboard("users-money-multiple", User::money);

        store.entry("Wertik1206").money().set(100D);
        store.entry("MoonSoD").money().set(200D);

        leaderboard.add("Wertik1206", "MoonSoD");

        List<User> top = leaderboard.entryRange(0, 1);

        Assert.assertEquals("MoonSoD", top.get(0).getIdentifier());
        Assert.assertEquals(200D, top.get(0).money().get(), 0.01);

        Assert.assertEquals("Wertik1206", top.get(1).getIdentifier());
        Assert.assertEquals(100D, top.get(1).money().get(), 0.01);
    }

    @Test
    public void leaderboardRange() {
        UserStore store = new UserStore(connector);

        Leaderboard<User> leaderboard = store.leaderboard("users-money-multiple", User::money);

        store.entry("Wertik1206").money().set(100D);
        store.entry("MoonSoD").money().set(200D);

        leaderboard.add("Wertik1206", "MoonSoD");

        List<Tuple> top = leaderboard.range(0, 1);

        Assert.assertEquals("MoonSoD", top.get(0).getElement());
        Assert.assertEquals(200D, top.get(0).getScore(), 0.01);

        Assert.assertEquals("Wertik1206", top.get(1).getElement());
        Assert.assertEquals(100D, top.get(1).getScore(), 0.01);
    }
}

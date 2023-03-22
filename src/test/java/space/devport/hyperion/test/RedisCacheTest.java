package space.devport.hyperion.test;

import org.junit.Assert;
import org.junit.Test;
import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.entry.Store;
import space.devport.hyperion.leaderboard.Leaderboard;
import space.devport.hyperion.test.models.User;
import space.devport.hyperion.test.models.UserStore;

public class RedisCacheTest {

    private final HyperionCache cache = new HyperionCache();

    @Test
    public void getset() {

        // if the UserStore implements the Persistent interface
        // it can be passed here
        //cache.saveUpdatedEntries(userStore);

        // same goes here
        //cache.savePersistentEntry(userStore, identifier);

        // and here
        //cache.loadPersistentEntry(userStore, identifier);

        // create a store that provides entries and models
        UserStore userStore = new UserStore(cache.getRedisConnector());

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
        UserStore store = new UserStore(cache.getRedisConnector());

        Leaderboard<User, Store<User>> leaderboard = store.leaderboard("users-money", User::money);

        leaderboard.load("Wertik1206");

        String first = leaderboard.at(0);

        Assert.assertEquals("Wertik1206", first);
    }
}

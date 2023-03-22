package space.devport.hyperion.test;

import org.junit.Assert;
import org.junit.Test;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.test.models.User;
import space.devport.hyperion.test.models.UserStore;

public class PersistenceTest {

    private final RedisConnector connector = RedisConnector.create();

    @Test
    public void basic() {
        UserStore store = new UserStore(connector);

        User user = store.entry("Wertik1206");

        user.money().set(100D);

        // save the user
        store.save(user);

        // Wait a bit for the write to db to happen
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // set to random amount
        user.money().set(666D);

        // load the user
        // this should overwrite the random value with 100D
        store.load(user);

        Assert.assertEquals(100D, user.money().get(), 0.01);
    }

    @Test
    public void saveUpdates() {
        UserStore store = new UserStore(connector);

        User user = store.entry("Wertik1206");

        // cause an update
        user.money().set(100D);

        // this should save the entry into persistent db
        store.saveUpdatedEntries();

        // Wait a bit for the write to db to happen
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // set to random amount
        user.money().set(666D);

        // load the user
        // this should overwrite the random value with 100D
        store.load(user);

        Assert.assertEquals(100D, user.money().get(), 0.01);
    }
}

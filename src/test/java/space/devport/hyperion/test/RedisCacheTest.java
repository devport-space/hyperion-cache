package space.devport.hyperion.test;

import org.junit.Assert;
import org.junit.Test;
import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.test.models.DemoPlayer;

public class RedisCacheTest {

    private final HyperionCache cache = new HyperionCache();

    @Test
    public void setAndGet() {
        DemoPlayer player = cache.createHandle(DemoPlayer.class, "Wertik1206");

        player.money().set(200L);

        Assert.assertEquals(200L, (long) player.money().get());
    }

    @Test
    public void increment() {
        DemoPlayer player = cache.createHandle(DemoPlayer.class, "Wertik1206");

        player.money().set(200L);

        player.money().increment(100L);

        Assert.assertEquals(300L, (long) player.money().get());
    }

    @Test
    public void decrement() {
        DemoPlayer player = cache.createHandle(DemoPlayer.class, "Wertik1206");

        player.money().set(200L);

        player.money().decrement(100L);

        Assert.assertEquals(100L, (long) player.money().get());
    }
}

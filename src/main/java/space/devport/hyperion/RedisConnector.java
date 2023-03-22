package space.devport.hyperion;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Holds a redis connection pool.
 */
public class RedisConnector {

    private final JedisPool jedisPool = new JedisPool();

    public RedisConnector() {
        //
    }

    public static RedisConnector create() {
        return new RedisConnector();
    }

    public Jedis getConnection() {
        return this.jedisPool.getResource();
    }

    public <T> T withConnection(Function<Jedis, T> function) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            return function.apply(jedis);
        }
    }

    public void withConnection(Consumer<Jedis> consumer) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            consumer.accept(jedis);
        }
    }
}

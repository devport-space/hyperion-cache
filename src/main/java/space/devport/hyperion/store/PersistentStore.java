package space.devport.hyperion.store;

import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Entry;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PersistentStore<E extends Entry> extends Store<E> {

    public PersistentStore(RedisConnector connector) {
        super(connector);
    }

    // Save an entry into the redis cache
    public abstract void save(E entry);

    // Load an entry into the redis cache
    public abstract void load(E entry);

    @Override
    public abstract String getRedisCollectionName();

    @Override
    public abstract E createEntry(String identifier);

    // todo: add an option to toggle this off
    public int saveUpdatedEntries() {
        AtomicInteger c = new AtomicInteger(0);

        // query updates that happened
        this.connector.withConnection((jedis) -> {
            Set<String> updatedIdentifiers = jedis.smembers(String.format(HyperionCache.UPDATES_KEY_FORMAT, getRedisCollectionName()));

            // save each
            // todo: allow bulk?
            for (String identifier : updatedIdentifiers) {

                E entry = entry(identifier);
                save(entry);

                c.incrementAndGet();

                // todo: do in single redis cmd
                jedis.srem(String.format(HyperionCache.UPDATES_KEY_FORMAT, entry.getKey()), identifier);
            }
        });
        return c.get();
    }
}

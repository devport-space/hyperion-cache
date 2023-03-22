package space.devport.hyperion;

import java.util.HashMap;
import java.util.Map;

/**
 * Main API instance, handles everything.
 */
public class HyperionCache {

    public static final String UPDATES_KEY_FORMAT = "hyperion.updates:%s";

    public static final String ENTRY_KEY_FORMAT = "hyperion.collections.%s:%s";

    public static final String LEADERBOARD_KEY_FORMAT = "hyperion.leaderboards:%s";

    private final RedisConnector redisConnector = RedisConnector.create();

    // TODO: Replace with own notifications through pub/sub to include exact field updates.
    //  We need that to properly communicate with the other side.
    /*private final Map<String, Class<? extends Store<?>>> keyspaces = new HashMap<>();*/

    // all collection keys accessed
    private final Map<String, Class<?>> collections = new HashMap<>();

    public HyperionCache() {
        //
    }

    // todo: move this to store
    /*public <K, T extends Store<K>> int saveUpdatedEntries() {
        // go through collections

        AtomicInteger c = new AtomicInteger(0);

        for (Map.Entry<String, Class<?>> entry : this.collections.entrySet()) {
            // query updates that happened
            this.redisConnector.withConnection((jedis) -> {
                Set<String> updatedIdentifiers = jedis.smembers(String.format(HyperionCache.UPDATES_KEY_FORMAT, entry.getKey()));

                for (String identifier : updatedIdentifiers) {
                    // get the handle
                    Class<T> clazz = (Class<T>) entry.getValue();

                    EntryFactory<K, T> factory = getFactory(clazz);

                    K id = factory.parseIdentifier(identifier);

                    Store<?> handle = createHandle(clazz, id);

                    savePersistentEntry(handle);
                    c.incrementAndGet();

                    jedis.srem(String.format(HyperionCache.UPDATES_KEY_FORMAT, entry.getKey()), identifier);
                }
            });
        }
        return c.get();
    }*/

    // todo: move to store
    /*public <K, T extends Store<K>> void savePersistentEntry(T entry) {
        // get the persistence provider for this class
        // todo: check assignable
        PersistenceProvider<K, T> provider = (PersistenceProvider<K, T>) this.persistenceProviders.get(entry.getClass());

        provider.save(entry);
    }*/

    // todo: move to store
    // load an entry from persistent db to redis cache
    /*public <K, T extends Store<K>> void loadPersistentEntry(T entry) {
        // get the persistence provider for this class
        // todo: check assignable
        PersistenceProvider<K, T> provider = (PersistenceProvider<K, T>) this.persistenceProviders.get(entry.getClass());

        provider.load(entry);
    }*/

    /*public <K, T extends Store<K>> void startKeyspaceNotifications() {
        new Thread(() -> {
            this.redisConnector.withConnection((jedis) -> {
                jedis.psubscribe(new JedisPubSub() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onPMessage(String pattern, String channel, String message) {
                        System.out.println(channel);

                        int idx = channel.indexOf(':');
                        String key = channel.substring(idx + 1);

                        System.out.println(key);

                        String[] arr = key.split(":");
                        String collectionKey = arr[0];
                        String identifier = arr[1];

                        System.out.println(collectionKey);

                        Class<T> entryClazz = (Class<T>) HyperionCache.this.keyspaces.get(collectionKey);

                        if (entryClazz == null) {
                            System.out.println("no clazz for keyspace");
                            return;
                        }

                        EntryFactory<K, T> factory = getFactory(entryClazz);

                        K id = factory.parseIdentifier(identifier);

                        // get the field? reflection...
                        T entry = createHandle(entryClazz, id);

                        savePersistentEntry(entry);
                    }
                }, "__keyspace@0__:*");
            });
        }).start();
    }*/

    public RedisConnector getRedisConnector() {
        return redisConnector;
    }
}

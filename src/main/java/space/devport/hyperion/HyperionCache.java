package space.devport.hyperion;

import redis.clients.jedis.JedisPubSub;
import space.devport.hyperion.entry.CacheHandle;
import space.devport.hyperion.entry.field.CacheField;
import space.devport.hyperion.factory.EntryFactory;
import space.devport.hyperion.persistence.PersistenceProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class HyperionCache {

    private final RedisConnector redisConnector = RedisConnector.create();

    private final Map<Class<?>, PersistenceProvider<?, ?>> persistenceProviders = new HashMap<>();

    private final Map<Class<?>, EntryFactory<?, ?>> factories = new HashMap<>();

    private final Map<String, Class<? extends CacheHandle<?>>> keyspaces = new HashMap<>();

    public HyperionCache() {
        //
    }

    public <K, T extends CacheHandle<K>> T createHandle(Class<T> handleClazz, K identifier) {
        EntryFactory<K, T> factory = this.getFactory(handleClazz);

        T handle;

        if (factory == null) {
            // attempt to find a constructor with connector & identifier
            // todo: create a default factory
            Constructor<T> constructor;
            try {
                constructor = handleClazz.getDeclaredConstructor(RedisConnector.class, String.class);
                handle = constructor.newInstance(this.redisConnector, String.valueOf(identifier));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException("No factory registered and default constructor not present for cache handle.");
            }
        } else {
            handle = factory.createEntry(this.redisConnector, identifier);
        }

        this.keyspaces.put(handle.getRedisCollectionKey(), handleClazz);
        return handle;
    }

    @SuppressWarnings("unchecked")
    public <K, T extends CacheHandle<K>> void savePersistentEntry(T entry) {
        // get the persistence provider for this class
        // todo: check assignable
        PersistenceProvider<K, T> provider = (PersistenceProvider<K, T>) this.persistenceProviders.get(entry.getClass());

        provider.save(entry);
    }

    // load an entry from persistent db
    @SuppressWarnings("unchecked")
    public <K, T extends CacheHandle<K>> void loadPersistentEntry(T entry) {
        // get the persistence provider for this class
        // todo: check assignable
        PersistenceProvider<K, T> provider = (PersistenceProvider<K, T>) this.persistenceProviders.get(entry.getClass());

        provider.load(entry);
    }

    public <K, T extends CacheHandle<K>> void addPersistenceProvider(Class<T> entryClazz, PersistenceProvider<K, T> provider) {
        this.persistenceProviders.put(entryClazz, provider);
    }

    public <K, T extends CacheHandle<K>> void addFactory(Class<T> handleClazz, EntryFactory<K, T> factory) {
        this.factories.put(handleClazz, factory);
    }

    @SuppressWarnings("unchecked")
    public <K, T extends CacheHandle<K>> EntryFactory<K, T> getFactory(Class<T> entryClazz) {
        // todo check
        return (EntryFactory<K, T>) this.factories.get(entryClazz);
    }

    @SuppressWarnings("unchecked")
    private <K, C extends CacheHandle<K>, T> CacheField<T> getField(C handle, String fieldName) {
        // get all the fields
        Field[] fields = handle.getClass().getFields();

        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                try {
                    return (CacheField<T>) field.get(handle);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public <K, T extends CacheHandle<K>> void startKeyspaceNotifications() {
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
    }
}

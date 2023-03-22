package space.devport.hyperion.persistence;

import space.devport.hyperion.entry.CacheHandle;

public interface PersistenceProvider<K, T extends CacheHandle<K>> {

    // load from db to redis through cache entry
    void load(T entry);

    // save to db from cache entry
    void save(T entry);
}

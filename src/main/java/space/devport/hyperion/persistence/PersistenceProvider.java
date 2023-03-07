package space.devport.hyperion.persistence;

import space.devport.hyperion.entry.CacheHandle;
import space.devport.hyperion.entry.field.CacheField;

public interface PersistenceProvider<K, T extends CacheHandle<K>> {

    // load from db to redis through cache entry
    void load(T entry);

    // save to db from cache entry
    void save(T entry);

    void saveField(T entry, CacheField<?> field);
}

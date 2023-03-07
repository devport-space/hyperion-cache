package space.devport.hyperion.factory;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.CacheHandle;

public interface EntryFactory<K, T extends CacheHandle<K>> {

    T createEntry(RedisConnector connector, K identifier);

    @SuppressWarnings("unchecked")
    default K parseIdentifier(String str) {
        return (K) str;
    }
}

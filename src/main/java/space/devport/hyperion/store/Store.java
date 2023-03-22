package space.devport.hyperion.store;

import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Entry;
import space.devport.hyperion.entry.Key;
import space.devport.hyperion.entry.field.DoubleField;
import space.devport.hyperion.leaderboard.Leaderboard;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public abstract class Store<E extends Entry> {

    protected final RedisConnector connector;

    // todo: caffeine with a ttl?
    private final Map<String, E> entries = new HashMap<>();

    private final Key key = new Key(HyperionCache.ENTRY_KEY_BASE, getRedisCollectionName(), null);

    public Store(RedisConnector connector) {
        this.connector = connector;
    }

    public abstract String getRedisCollectionName();

    public abstract E createEntry(String identifier);

    public E entry(String identifier) {
        E e = this.entries.get(identifier);

        if (e == null) {
            e = this.createEntry(identifier);
            this.entries.put(identifier, e);
            return e;
        }

        return e;
    }

    public Leaderboard<E> leaderboard(String name, Function<E, DoubleField> valueLoader) {
        return new Leaderboard<>(this.connector, name, this, valueLoader);
    }

    public Key getKey() {
        return this.key;
    }
}

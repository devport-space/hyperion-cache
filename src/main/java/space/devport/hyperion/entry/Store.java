package space.devport.hyperion.entry;

import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.entry.field.DoubleField;
import space.devport.hyperion.leaderboard.Leaderboard;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public abstract class Store<E extends Entry> {

    protected final HyperionCache cache;

    // todo: caffeine?
    private final Map<String, E> entries = new HashMap<>();

    public Store(HyperionCache cache) {
        this.cache = cache;
    }

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

    public Leaderboard<E, Store<E>> leaderboard(String name, Function<E, DoubleField> valueLoader) {
        return new Leaderboard<>(this.cache.getRedisConnector(), name, this, valueLoader);
    }
}

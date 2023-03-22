package space.devport.hyperion.entry;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.field.DoubleField;
import space.devport.hyperion.leaderboard.Leaderboard;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// a base store to work with redis
// this can be extended by a "Persistent" interface that provides a way to store entries
// in a persistent manner, like a SQL db

// unlike previous "handles", stores are not tied to a specific entry in the redis cache
// hence no identifier.
public abstract class Store<E extends Entry> {

    protected final RedisConnector connector;

    // todo: caffeine?
    private final Map<String, E> entries = new HashMap<>();

    public Store(RedisConnector connector) {
        this.connector = connector;
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
        return new Leaderboard<>(this.connector, name, this, valueLoader);
    }
}

package space.devport.hyperion.leaderboard;

import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Entry;
import space.devport.hyperion.entry.Store;
import space.devport.hyperion.entry.field.DoubleField;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Leaderboard<E extends Entry, S extends Store<E>> {

    private final RedisConnector connector;

    private final S store;

    private final Function<E, DoubleField> valueLoader;

    private final String name;

    // todo: create an initializer in store

    public Leaderboard(RedisConnector connector, String name, S store, Function<E, DoubleField> valueLoader) {
        this.connector = connector;
        this.valueLoader = valueLoader;
        this.store = store;
        this.name = name;
    }

    public void load(String identifier) {
        E entry = this.store.entry(identifier);

        // fetch the value
        DoubleField field = this.valueLoader.apply(entry);

        this.connector.withConnection((jedis) -> {
            jedis.zadd(getKey(), field.get(), identifier);
        });
    }

    // a way to load in a bunch of data

    public void load(Set<String> identifiers) {

    }

    // a way to retrieve a position of a player by identifier
    public long positionOf(String identifier) {
        return this.connector.withConnection((jedis) -> (long) jedis.zrank(getKey(), identifier));
    }

    public String at(int position) {
        List<String> zrange = this.connector.withConnection((jedis) -> {
            return jedis.zrange(getKey(), position, position);
        });

        return zrange.isEmpty() ? null : zrange.get(0);
    }

    // retrieve an entry
    public E get(int position) {
        String identifier = this.at(position);
        return store.entry(identifier);
    }

    private String getKey() {
        return String.format(HyperionCache.LEADERBOARD_KEY_FORMAT, this.name);
    }
}

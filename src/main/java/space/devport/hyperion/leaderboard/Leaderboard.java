package space.devport.hyperion.leaderboard;

import redis.clients.jedis.resps.Tuple;
import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Entry;
import space.devport.hyperion.entry.field.DoubleField;
import space.devport.hyperion.store.Store;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Leaderboard<E extends Entry> {

    private final RedisConnector connector;

    private final Store<E> store;

    private final Function<E, DoubleField> valueLoader;

    private final String name;

    private final boolean descend;

    // todo: create an initializer in store

    public Leaderboard(RedisConnector connector, String name, Store<E> store, Function<E, DoubleField> valueLoader, boolean descend) {
        this.connector = connector;
        this.valueLoader = valueLoader;
        this.store = store;
        this.name = name;
        this.descend = descend;
    }

    public void add(String identifier) {
        E entry = this.store.entry(identifier);

        // fetch the value
        DoubleField field = this.valueLoader.apply(entry);

        this.connector.withConnection((jedis) -> {
            jedis.zadd(getKey(), field.get(), identifier);
        });
    }

    // a way to add in a bunch of data

    public void add(Collection<String> identifiers) {
        for (String id : identifiers) {
            add(id);
        }
    }

    public void add(String... identifiers) {
        for (String id : identifiers) {
            add(id);
        }
    }

    // a way to retrieve a position of a player by identifier
    public long positionOf(String identifier) {
        return this.connector.withConnection((jedis) -> (long) jedis.zrank(getKey(), identifier));
    }

    public long positionOf(E entry) {
        return positionOf(entry.getIdentifier());
    }

    public List<Tuple> range(int start, int offset) {
        return this.connector.withConnection((jedis) -> {
            if (this.descend) {
                return jedis.zrevrangeWithScores(getKey(), start, start + offset);
            } else {
                return jedis.zrangeWithScores(getKey(), start, start + offset);
            }
        });
    }

    // both inclusive
    public List<E> entryRange(int start, int offset) {
        List<String> zrange = this.connector.withConnection((jedis) -> {
            if (this.descend) {
                return jedis.zrevrange(getKey(), start, start + offset);
            } else {
                return jedis.zrange(getKey(), start, start + offset);
            }
        });

        return zrange.stream().map(store::createEntry).collect(Collectors.toList());
    }

    public String at(int position) {
        List<String> zrange = this.connector.withConnection((jedis) -> {
            if (this.descend) {
                return jedis.zrevrange(getKey(), position, position);
            } else {
                return jedis.zrange(getKey(), position, position);
            }
        });

        return zrange.isEmpty() ? null : zrange.get(0);
    }

    // retrieve an entry
    public E entryAt(int position) {
        String identifier = this.at(position);
        return store.entry(identifier);
    }

    private String getKey() {
        return String.format(HyperionCache.LEADERBOARD_KEY_FORMAT, this.name);
    }
}

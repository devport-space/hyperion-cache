package space.devport.hyperion.entry.field;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Entry;

import java.util.List;

public abstract class Field<T> {

    protected final Entry entry;
    protected final RedisConnector connector;
    protected final String fieldName;

    public Field(RedisConnector connector, Entry entry, String fieldName) {
        this.connector = connector;
        this.fieldName = fieldName;
        this.entry = entry;
    }

    public abstract T deserialize(String value);

    public abstract String serialize(T value);

    public abstract T getDefaultValue();

    protected String getRawProperty() {
        return this.connector.withConnection((jedis) -> {
            return jedis.hget(this.entry.getKey(), this.fieldName);
        });
    }

    protected void getRawProperty(Transaction transaction) {
        transaction.hget(this.entry.getKey(), this.fieldName);
    }

    protected void setRawProperty(String value) {
        this.connector.withConnection((jedis) -> {
            jedis.hset(this.entry.getKey(), this.fieldName, value);

            // add entry for update registry so the CacheHandle object gets saved into DB later.
            jedis.sadd(String.format(HyperionCache.UPDATES_KEY_FORMAT, this.entry.getRedisCollectionName()), this.entry.getIdentifier());
        });
    }

    protected void setRawProperty(Transaction transaction, String value) {
        transaction.hset(this.entry.getKey(), this.fieldName, value);
        // add entry for update registry so the CacheHandle object gets saved into DB later.
        transaction.sadd(String.format(HyperionCache.UPDATES_KEY_FORMAT, this.entry.getRedisCollectionName()), this.entry.getIdentifier());
    }

    // get the value deserialized
    public T get() {
        return this.deserialize(getRawProperty());
    }

    public void get(Transaction t) {
        this.getRawProperty(t);
    }

    // serialize and set the property
    public void set(T value) {
        this.setRawProperty(this.serialize(value));
    }

    public void set(Transaction t, T value) {
        this.setRawProperty(t, this.serialize(value));
    }

    public interface CommandExecutor<T> {
        boolean exec(Transaction transaction, T currentValue);
    }

    public boolean isCached() {
        return this.connector.withConnection((jedis) -> {
            return jedis.hexists(this.entry.getKey(), this.fieldName);
        });
    }

    public T watched(CommandExecutor<T> executor) {
        Jedis jedis = this.connector.getConnection();

        jedis.watch(this.entry.getKey());

        Transaction transaction = jedis.multi();

        T currentValue = this.get();

        // don't exec
        if (!executor.exec(transaction, currentValue)) {
            return currentValue;
        }

        List<Object> results = transaction.exec();

        // deserialize the last result
        return this.deserialize((String) results.get(results.size() - 1));
    }

    public String getFieldName() {
        return fieldName;
    }
}

package space.devport.hyperion.entry.field;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import space.devport.hyperion.RedisConnector;

import java.util.List;

public abstract class CacheField<T> {

    protected final RedisConnector connector;

    private final String key;
    protected final String fieldName;

    public CacheField(RedisConnector connector, String key, String fieldName) {
        this.connector = connector;
        this.key = key;
        this.fieldName = fieldName;
    }

    protected String getRawProperty() {
        return this.connector.withConnection((jedis) -> {
            return jedis.hget(this.getKey(), this.fieldName);
        });
    }

    protected void getRawProperty(Transaction transaction) {
        transaction.hget(this.key, this.fieldName);
    }

    protected void setRawProperty(String value) {
        this.connector.withConnection((jedis) -> {
            jedis.hset(this.key, this.fieldName, value);
        });
    }

    protected void setRawProperty(Transaction transaction, String value) {
        transaction.hset(this.key, this.fieldName, value);
    }

    public T get() {
        return this.deserialize(getRawProperty());
    }

    public void get(Transaction t) {
        this.getRawProperty(t);
    }

    public void set(T value) {
        this.setRawProperty(this.serialize(value));
    }

    public void set(Transaction t, T value) {
        this.setRawProperty(t, this.serialize(value));
    }

    public interface CommandExecutor<T> {
        boolean exec(Transaction transaction, T currentValue);
    }

    public abstract T deserialize(String value);

    public abstract String serialize(T value);

    public abstract T getDefaultValue();

    public boolean isCached() {
        return this.connector.withConnection((jedis) -> {
            return jedis.hexists(this.key, this.fieldName);
        });
    }

    public T watched(CommandExecutor<T> executor) {
        Jedis jedis = this.connector.getConnection();

        jedis.watch(this.key);

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

    public String getKey() {
        return key;
    }

    public String fieldName() {
        return fieldName;
    }
}

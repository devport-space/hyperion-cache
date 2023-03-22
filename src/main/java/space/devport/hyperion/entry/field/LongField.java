package space.devport.hyperion.entry.field;

import redis.clients.jedis.Transaction;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.CacheHandle;

public class LongField extends CacheField<Long> implements NumericField<Long> {

    public LongField(RedisConnector connector, CacheHandle<?> handle, String fieldName) {
        super(connector, handle, fieldName);
    }

    @Override
    public Long deserialize(String value) {
        return Long.parseLong(value);
    }

    @Override
    public Long increment(Long value) {
        return super.connector.withConnection((jedis) -> {
            return jedis.hincrBy(this.handle.getKey(), this.fieldName, value);
        });
    }

    @Override
    public Long decrement(Long value) {
        return super.connector.withConnection((jedis) -> {
            return jedis.hincrBy(this.handle.getKey(), this.fieldName, -value);
        });
    }

    @Override
    public void increment(Transaction transaction, Long value) {
        transaction.hincrBy(this.handle.getKey(), this.fieldName, value);
    }

    @Override
    public void decrement(Transaction transaction, Long value) {
        transaction.hincrBy(this.handle.getKey(), this.fieldName, -value);
    }

    @Override
    public String serialize(Long value) {
        return String.valueOf(value);
    }

    @Override
    public Long getDefaultValue() {
        return 0L;
    }
}

package space.devport.hyperion.entry.field;

import redis.clients.jedis.Transaction;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Entry;

public class LongField extends Field<Long> implements NumericField<Long> {

    public LongField(RedisConnector connector, Entry entry, String fieldName) {
        super(connector, entry, fieldName);
    }

    @Override
    public Long deserialize(String value) {
        return Long.parseLong(value);
    }

    @Override
    public Long increment(Long value) {
        return super.connector.withConnection((jedis) -> {
            return jedis.hincrBy(this.entry.getKey(), this.fieldName, value);
        });
    }

    @Override
    public Long decrement(Long value) {
        return super.connector.withConnection((jedis) -> {
            return jedis.hincrBy(this.entry.getKey(), this.fieldName, -value);
        });
    }

    @Override
    public void increment(Transaction transaction, Long value) {
        transaction.hincrBy(this.entry.getKey(), this.fieldName, value);
    }

    @Override
    public void decrement(Transaction transaction, Long value) {
        transaction.hincrBy(this.entry.getKey(), this.fieldName, -value);
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

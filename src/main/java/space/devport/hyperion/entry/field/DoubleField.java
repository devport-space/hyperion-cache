package space.devport.hyperion.entry.field;

import redis.clients.jedis.Transaction;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Key;

public class DoubleField extends Field<Double> implements NumericField<Double> {

    public DoubleField(RedisConnector connector, Key key, String fieldName) {
        super(connector, key, fieldName);
    }

    @Override
    public Double deserialize(String value) {
        return Double.valueOf(value);
    }

    @Override
    public String serialize(Double value) {
        return String.valueOf(value);
    }

    @Override
    public Double getDefaultValue() {
        return 0D;
    }

    @Override
    public Double increment(Double value) {
        return super.connector.withConnection((jedis) -> {
            return jedis.hincrByFloat(getKey().compose(), this.fieldName, value);
        });
    }

    @Override
    public Double decrement(Double value) {
        return super.connector.withConnection((jedis) -> {
            return jedis.hincrByFloat(getKey().compose(), this.fieldName, -value);
        });
    }

    @Override
    public void increment(Transaction transaction, Double value) {
        transaction.hincrByFloat(getKey().compose(), this.fieldName, value);
    }

    @Override
    public void decrement(Transaction transaction, Double value) {
        transaction.hincrByFloat(getKey().compose(), this.fieldName, -value);
    }
}

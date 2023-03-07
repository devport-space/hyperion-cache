package space.devport.hyperion.entry.field;

import redis.clients.jedis.Transaction;

public interface NumericField<T> {
    T increment(T value);

    T decrement(T value);

    void increment(Transaction transaction, T value);

    void decrement(Transaction transaction, T value);
}

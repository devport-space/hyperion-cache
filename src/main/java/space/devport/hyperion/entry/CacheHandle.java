package space.devport.hyperion.entry;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.field.LongField;
import space.devport.hyperion.entry.field.StringField;

public abstract class CacheHandle<K> {

    protected final RedisConnector connector;

    protected final K identifier;

    public CacheHandle(RedisConnector connector, K identifier) {
        this.connector = connector;
        this.identifier = identifier;
    }

    protected LongField longField(String fieldName) {
        return new LongField(this.connector, this.getKey(), fieldName);
    }

    protected StringField stringField(String fieldName) {
        return new StringField(this.connector, this.getKey(), fieldName);
    }

    public String getComposedIdentifier() {
        return String.format("%s:%s", this.getKey(), this.getStringIdentifier());
    }

    public abstract String getRedisCollectionKey();

    public String getKey() {
        return String.format("%s:%s", this.getRedisCollectionKey(), this.identifier);
    }

    public K getIdentifier() {
        return identifier;
    }

    public String getStringIdentifier() {
        return String.valueOf(this.getIdentifier());
    }
}

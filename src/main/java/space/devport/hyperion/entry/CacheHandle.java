package space.devport.hyperion.entry;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.field.LongField;
import space.devport.hyperion.entry.field.StringField;

public abstract class CacheHandle<K> {

    protected final RedisConnector connector;

    // identifier of the object this handle is pointing to
    protected final K identifier;

    public CacheHandle(RedisConnector connector, K identifier) {
        this.connector = connector;
        this.identifier = identifier;
    }

    /*
     * Return the collection key.
     * */
    public abstract String getRedisCollectionKey();

    // create a long field
    protected LongField longField(String fieldName) {
        return new LongField(this.connector, this, fieldName);
    }

    // create a string field
    protected StringField stringField(String fieldName) {
        return new StringField(this.connector, this, fieldName);
    }

    // get the key of the object in redis this handle is pointing to
    public String getKey() {
        return String.format("%s:%s", this.getRedisCollectionKey(), this.getStringIdentifier());
    }

    public K getIdentifier() {
        return identifier;
    }

    public String getStringIdentifier() {
        return String.valueOf(this.getIdentifier());
    }
}

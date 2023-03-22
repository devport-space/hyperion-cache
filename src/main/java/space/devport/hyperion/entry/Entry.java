package space.devport.hyperion.entry;

import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.field.DoubleField;
import space.devport.hyperion.entry.field.LongField;
import space.devport.hyperion.entry.field.StringField;

public abstract class Entry {

    private final RedisConnector connector;

    private final String identifier;

    public Entry(RedisConnector connector, String identifier) {
        this.connector = connector;
        this.identifier = identifier;
    }

    public abstract String getRedisCollectionName();

    public String getIdentifier() {
        return identifier;
    }

    // -- field constr helper methods

    public LongField longField(String fieldName) {
        return new LongField(this.connector, this, fieldName);
    }

    public DoubleField doubleField(String fieldName) {
        return new DoubleField(this.connector, this, fieldName);
    }

    public StringField stringField(String fieldName) {
        return new StringField(this.connector, this, fieldName);
    }

    // get the whole key of this entry
    public String getKey() {
        return String.format(HyperionCache.ENTRY_KEY_FORMAT, getRedisCollectionName(), getIdentifier());
    }
}

package space.devport.hyperion.entry;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.field.DoubleField;
import space.devport.hyperion.entry.field.LongField;
import space.devport.hyperion.entry.field.StringField;

public abstract class Entry {

    private final RedisConnector connector;

    private final String identifier;

    private final Key key;

    public Entry(RedisConnector connector, Key key) {
        this.connector = connector;
        this.identifier = key.identifier();
        this.key = key;
    }

    public String getIdentifier() {
        return identifier;
    }

    // -- field constr helper methods

    public LongField longField(String fieldName) {
        return new LongField(this.connector, getKey(), fieldName);
    }

    public DoubleField doubleField(String fieldName) {
        return new DoubleField(this.connector, getKey(), fieldName);
    }

    public StringField stringField(String fieldName) {
        return new StringField(this.connector, getKey(), fieldName);
    }

    // get the whole key of this entry
    public Key getKey() {
        return this.key;
    }
}

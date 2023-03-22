package space.devport.hyperion.entry.field;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Entry;

public class StringField extends Field<String> {

    public StringField(RedisConnector connector, Entry entry, String fieldName) {
        super(connector, entry, fieldName);
    }

    @Override
    public String deserialize(String value) {
        return value;
    }

    @Override
    public String serialize(String value) {
        return value;
    }

    @Override
    public String getDefaultValue() {
        return null;
    }
}

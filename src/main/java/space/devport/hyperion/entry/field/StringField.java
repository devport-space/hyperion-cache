package space.devport.hyperion.entry.field;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Key;

public class StringField extends Field<String> {

    public StringField(RedisConnector connector, Key key, String fieldName) {
        super(connector, key, fieldName);
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

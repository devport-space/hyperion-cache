package space.devport.hyperion.entry.field;

import space.devport.hyperion.RedisConnector;

public class StringField extends CacheField<String> {

    public StringField(RedisConnector connector, String key, String fieldName) {
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

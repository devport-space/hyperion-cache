package space.devport.hyperion.entry.field;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.CacheHandle;

public class StringField extends CacheField<String> {

    public StringField(RedisConnector connector, CacheHandle<?> handle, String fieldName) {
        super(connector, handle, fieldName);
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

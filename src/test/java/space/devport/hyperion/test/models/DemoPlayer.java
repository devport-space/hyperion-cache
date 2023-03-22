package space.devport.hyperion.test.models;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.CacheHandle;
import space.devport.hyperion.entry.field.LongField;
import space.devport.hyperion.entry.field.StringField;

// Cache handle with a string key
public class DemoPlayer extends CacheHandle<String> {

    private final LongField money = longField("money");

    private final StringField displayName = stringField("displayName");

    public DemoPlayer(RedisConnector connector, String identifier) {
        super(connector, identifier);
    }

    @Override
    public String getRedisCollectionKey() {
        return "players";
    }

    public LongField money() {
        return money;
    }

    public StringField displayName() {
        return displayName;
    }
}

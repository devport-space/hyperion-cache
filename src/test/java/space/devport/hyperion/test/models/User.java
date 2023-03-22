package space.devport.hyperion.test.models;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Entry;
import space.devport.hyperion.entry.field.DoubleField;

public class User extends Entry {

    private final DoubleField money = doubleField("money");

    public User(RedisConnector connector, String identifier) {
        super(connector, identifier);
    }

    @Override
    public String getRedisCollectionName() {
        return "users";
    }

    public DoubleField money() {
        return money;
    }
}

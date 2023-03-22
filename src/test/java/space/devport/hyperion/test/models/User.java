package space.devport.hyperion.test.models;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Entry;
import space.devport.hyperion.entry.Key;
import space.devport.hyperion.entry.field.DoubleField;

public class User extends Entry {

    private final DoubleField money = doubleField("money");

    public User(RedisConnector connector, Key key) {
        super(connector, key);
    }

    public DoubleField money() {
        return money;
    }
}

package space.devport.hyperion.test.models;

import space.devport.hyperion.RedisConnector;
import space.devport.hyperion.entry.Store;

public class UserStore extends Store<User> {
    public UserStore(RedisConnector connector) {
        super(connector);
    }

    @Override
    public User createEntry(String identifier) {
        return new User(this.connector, identifier);
    }
}

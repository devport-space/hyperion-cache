package space.devport.hyperion.test.models;

import space.devport.hyperion.HyperionCache;
import space.devport.hyperion.entry.Store;

public class UserStore extends Store<User> {
    public UserStore(HyperionCache cache) {
        super(cache);
    }

    @Override
    public User createEntry(String identifier) {
        return new User(this.cache.getRedisConnector(), identifier);
    }
}

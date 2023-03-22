package space.devport.hyperion.entry;

import space.devport.hyperion.HyperionCache;

public record Key(String base, String collection, String identifier) {

    public static Key onDefaultBase(String collection, String identifier) {
        return new Key(HyperionCache.ENTRY_KEY_BASE, collection, identifier);
    }

    public Key base(String base) {
        return new Key(base, collection, identifier);
    }

    public Key collection(String collection) {
        return new Key(base, collection, identifier);
    }

    public Key identifier(String identifier) {
        return new Key(base, collection, identifier);
    }

    public String compose() {
        StringBuilder key = new StringBuilder(base == null ? "" : base);
        if (collection != null) {
            if (!key.isEmpty())
                key.append(".");
            key.append(collection);
        }
        if (identifier != null) {
            if (!key.isEmpty())
                key.append(":");
            key.append(identifier);
        }
        return key.toString();
    }
}

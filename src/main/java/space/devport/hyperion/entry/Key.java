package space.devport.hyperion.entry;

public record Key(String base, String collection, String identifier) {

    public Key base(String base) {
        return new Key(base, null, null);
    }

    public Key collection(String collection) {
        return new Key(base, collection, null);
    }

    public Key identifier(String identifier) {
        return new Key(base, collection, identifier);
    }

    public String compose() {
        StringBuilder key = new StringBuilder(base == null ? "" : base);
        if (collection != null) {
            key.append(".").append(collection);
        }
        if (identifier != null) {
            key.append(":").append(identifier);
        }
        return key.toString();
    }
}

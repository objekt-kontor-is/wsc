package de.objektkontor.wsc.container;

public class ResourceId<T extends Resource> {

    private final Class<T> type;
    private final String id;

    @SuppressWarnings("unchecked")
    public ResourceId(Class<?> type, String id) {
        this.type = (Class<T>) type;
        this.id = id;
    }

    public Class<T> type() {
        return type;
    }

    public String id() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        result = prime * result + type.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourceId<?> other = (ResourceId<?>) obj;
        if (!id.equals(other.id))
            return false;
        if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return id + "[" + type.getSimpleName() + "]";
    }
}

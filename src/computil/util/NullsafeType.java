package computil.util;

import computil.tree.TypeTree;

import java.util.List;

public record NullsafeType(TypeTree type) implements TypeTree {
    public String name() {
        return type.name();
    }
    public boolean isNullable() {
        return false;
    }
    public List<TypeTree> getGenerics() {
        return type.getGenerics();
    }
    public Location getLocation() {
        return type.getLocation();
    }
}

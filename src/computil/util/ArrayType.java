package computil.util;

import computil.tree.TypeTree;

import java.util.List;

public record ArrayType(TypeTree generic) implements TypeTree {
    public String name() {
        return "arr";
    }
    public boolean isNullable() {
        return false;
    }
    public List<TypeTree> getGenerics() {
        return List.of(generic);
    }
    public Location getLocation() {
        return generic.getLocation();
    }
}

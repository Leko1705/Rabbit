package computil.util;

import computil.tree.TypeTree;

import java.util.List;

public class PrimitiveType implements TypeTree {

    public static final TypeTree NULL = new PrimitiveType("void", false);
    public static final TypeTree INTEGER = new PrimitiveType("int");
    public static final TypeTree FLOAT = new PrimitiveType("float");
    public static final TypeTree BOOL = new PrimitiveType("bool");
    public static final TypeTree STRING = new PrimitiveType("str");

    private final String name;
    private final boolean isNullable;

    private PrimitiveType(String name){
        this.name = name;
        isNullable = false;
    }
    private PrimitiveType(String name, boolean isNullable){
        this.name = name;
        this.isNullable = isNullable;
    }

    @Override
    public String name() {
        return name;
    }

    public boolean isNullable() {
        return isNullable;
    }
    public List<TypeTree> getGenerics() {
        return List.of();
    }

    @Override
    public Location getLocation() {
        return null;
    }
}

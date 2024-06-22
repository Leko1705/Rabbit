package computil.diags;

import computil.tree.TypeTree;
import computil.util.Location;

import java.util.Iterator;

public final class Errors {

    private Errors() {
    }

    public static Error missingDigitOnRadixSpecs(Location location){
        return new Error("missing digit while using radix on integer", location);
    }

    public static Error invalidDigitOnRadixSpecs(Location location){
        return new Error("invalid digit while using radix on integer", location);
    }

    public static Error invalidFraction(Location location){
        return new Error("invalid fraction", location);
    }

    public static Error missingSymbol(Location location, String s) {
        return new Error("missing symbol '" + s + "'", location);
    }

    public static Error invalidEscapeCharacter(Location location) {
        return new Error("invalid escape character", location);
    }

    public static Error unexpectedToken(Location location, char c) {
        return new Error("unexpected token '" + c + "'", location);
    }

    public static Error typeMismatch(TypeTree required, TypeTree got, Location location){
        String msg = """
                type mismatch:
                    required: {1}
                         got: {2}
                """
                .replace("{1}", getTypeString(required))
                .replace("{2}", getTypeString(got));
        return new Error(msg, location);
    }

    public static Error invalidReturnType(TypeTree required, TypeTree got, Location location){
        String msg = """
                invalid return type:
                    required: {1}
                         got: {2}
                """
                .replace("{1}", getTypeString(required))
                .replace("{2}", getTypeString(got));
        return new Error(msg, location);
    }

    public static String getTypeString(TypeTree typeTree){
        StringBuilder sb = new StringBuilder(typeTree.name());
        if (!typeTree.getGenerics().isEmpty()){
            Iterator<TypeTree> generics = typeTree.getGenerics().iterator();
            sb.append("<").append(getTypeString(generics.next()));
            while (generics.hasNext())
                sb.append(", ").append(getTypeString(generics.next()));
            sb.append(">");
        }
        if (typeTree.isNullable()) sb.append("?");
        return sb.toString();
    }

    public static Error arrayExpectedButGot(TypeTree containerType, Location location) {
        String msg = """
                type mismatch:
                required: arr<_>
                     got: {2}
                """.replace("{2}", getTypeString(containerType));
        return new Error(msg, location);
    }

}

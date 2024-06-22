package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface TypeTree extends Tree {

    String name();

    boolean isNullable();

    List<TypeTree> getGenerics();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitTypeTree(this, p);
    }
}

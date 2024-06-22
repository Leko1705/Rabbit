package computil.tree;

import computil.util.TreeVisitor;

public interface FieldTree extends Tree {

    String getName();

    TypeTree getType();

    boolean isMutable();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitFieldTree(this, p);
    }
}

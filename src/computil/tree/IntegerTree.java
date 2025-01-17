package computil.tree;

import computil.util.TreeVisitor;

public interface IntegerTree extends LiteralTree<Integer> {

    Integer get();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitIntegerTree(this, p);
    }
}

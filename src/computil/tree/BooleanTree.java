package computil.tree;

import computil.util.TreeVisitor;

public interface BooleanTree extends LiteralTree<Boolean> {

    Boolean get();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitBooleanTree(this, p);
    }
}

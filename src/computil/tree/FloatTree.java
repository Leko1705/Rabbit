package computil.tree;

import computil.util.TreeVisitor;

public interface FloatTree extends LiteralTree<Float> {

    Float get();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitFloatTree(this, p);
    }
}

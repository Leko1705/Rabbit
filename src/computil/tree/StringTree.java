package computil.tree;

import computil.util.TreeVisitor;

public interface StringTree extends LiteralTree<String> {

    String get();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitStringTree(this, p);
    }
}

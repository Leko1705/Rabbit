package computil.tree;

import computil.util.TreeVisitor;

public interface NullTree extends LiteralTree<Void> {

    default Void get(){ return null; }

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitNullTree(this, p);
    }
}

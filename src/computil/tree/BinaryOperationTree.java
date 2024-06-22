package computil.tree;

import computil.util.TreeVisitor;

public interface BinaryOperationTree extends BinaryExpressionTree {

    Operation getOperation();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitBinaryOperationTree(this, p);
    }
}

package computil.tree;

import computil.util.TreeVisitor;

public interface NotTree extends UnaryExpressionTree {

    ExpressionTree getOperand();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitNotTree(this, p);
    }
}

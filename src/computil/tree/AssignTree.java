package computil.tree;

import computil.util.TreeVisitor;

public interface AssignTree extends BinaryExpressionTree {

    ExpressionTree getLeftOperand();

    ExpressionTree getRightOperand();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitAssignTree(this, p);
    }
}

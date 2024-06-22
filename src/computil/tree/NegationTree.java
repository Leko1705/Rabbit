package computil.tree;

import computil.util.TreeVisitor;

public interface NegationTree extends UnaryExpressionTree {


    ExpressionTree getOperand();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitNegationTree(this, p);
    }
}

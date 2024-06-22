package computil.tree;

import computil.util.TreeVisitor;

public interface ContainerAccessTree extends UnaryExpressionTree, AssignableTree {

    ExpressionTree getOperand();

    ExpressionTree getKey();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitContainerAccessTree(this, p);
    }
}

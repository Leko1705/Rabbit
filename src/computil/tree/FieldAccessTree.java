package computil.tree;

import computil.util.TreeVisitor;

public interface FieldAccessTree extends UnaryExpressionTree {

    ExpressionTree getOperand();

    String getFieldName();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitFieldAccessTree(this, p);
    }
}

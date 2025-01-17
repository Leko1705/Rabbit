package computil.tree;

import computil.util.TreeVisitor;

public interface ReturnTree extends StatementTree {

    ExpressionTree getExpression();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitReturnTree(this, p);
    }
}

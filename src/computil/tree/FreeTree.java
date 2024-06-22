package computil.tree;

import computil.util.TreeVisitor;

public interface FreeTree extends StatementTree {

    ExpressionTree getExpression();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitFreeTree(this, p);
    }
}

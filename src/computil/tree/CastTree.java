package computil.tree;

import computil.util.TreeVisitor;

public interface CastTree extends UnaryExpressionTree {


    TypeTree getCastType();

    ExpressionTree getOperand();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitCastTree(this, p);
    }
}

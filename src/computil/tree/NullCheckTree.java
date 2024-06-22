package computil.tree;

import computil.util.TreeVisitor;

public interface NullCheckTree extends UnaryExpressionTree {

    ExpressionTree getOperand();

    default <P, R> R accept(TreeVisitor<P, R> visitor, P p){
        return visitor.visitNullCheckTree(this, p);
    }
}

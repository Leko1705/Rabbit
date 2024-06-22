package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface CallMethodTree extends UnaryExpressionTree {

    ExpressionTree getOperand();

    String getMethodName();

    List<ExpressionTree> getArguments();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitCallMethodTree(this, p);
    }
}

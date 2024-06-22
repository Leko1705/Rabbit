package computil.tree;

import computil.util.TreeVisitor;

public interface VariableTree extends ExpressionTree, AssignableTree {

    String getName();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitVariableTree(this, p);
    }
}

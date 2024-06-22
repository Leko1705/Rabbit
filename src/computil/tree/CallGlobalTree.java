package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface CallGlobalTree extends CallTree {

    String getName();

    List<ExpressionTree> getArguments();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitCallGlobalTree(this, p);
    }
}

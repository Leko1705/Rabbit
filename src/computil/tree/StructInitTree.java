package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface StructInitTree extends ExpressionTree {

    TypeTree getType();

    List<ExpressionTree> getArguments();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitStructInitTree(this, p);
    }
}

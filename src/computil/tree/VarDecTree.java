package computil.tree;

import computil.util.TreeVisitor;

public interface VarDecTree extends StatementTree {

    boolean isMutable();

    String getName();

    TypeTree getType();

    ExpressionTree getInitializer();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitVarDecTree(this, p);
    }
}

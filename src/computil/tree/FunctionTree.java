package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface FunctionTree extends CallableTree {

    String getName();

    List<ParameterTree> getParameters();

    TypeTree getReturnType();

    BlockTree getBody();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitFunctionTree(this, p);
    }
}

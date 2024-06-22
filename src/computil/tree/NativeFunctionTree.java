package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface NativeFunctionTree extends CallableTree {

    String getName();

    TypeTree getReturnType();

    List<ParameterTree> getParameters();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitNativeFunctionTree(this, p);
    }
}

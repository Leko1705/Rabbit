package computil.tree;

import computil.util.TreeVisitor;

public interface ParameterTree extends Tree {

    boolean isMutable();

    String getName();

    TypeTree getType();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p){
        return visitor.visitParameterTree(this, p);
    }
}

package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface ImplementationTree extends DefinitionTree {


    String getName();

    String getForTypeName();

    String getObjectName();

    List<CallableTree> getImplementations();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitImplementationTree(this, p);
    }
}

package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface StructTree extends DefinitionTree {

    String getName();

    List<FieldTree> getFields();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitStructTree(this, p);
    }
}

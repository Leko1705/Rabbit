package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface TemplateTree extends DefinitionTree {

    String getName();

    List<TemplateMethodTree> getMethods();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitTemplateTree(this, p);
    }
}

package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface TemplateMethodTree extends Tree {

    String getName();

    TypeTree getReturnType();

    List<ParameterTree> getParameters();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitTemplateMethodTree(this, p);
    }
}

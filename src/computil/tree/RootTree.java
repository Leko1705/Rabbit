package computil.tree;

import computil.util.TreeVisitor;

import java.util.List;

public interface RootTree extends Tree {

    List<StructTree> getStructs();

    List<CallableTree> getCallables();

    List<TemplateTree> getTemplates();

    List<ImplementationTree> getImpls();

    @Override
    default <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
        return visitor.visitRootTree(this, p);
    }
}

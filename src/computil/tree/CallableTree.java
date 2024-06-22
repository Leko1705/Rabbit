package computil.tree;

import computil.util.InheritOnly;

import java.util.List;

@InheritOnly
public interface CallableTree extends DefinitionTree {

    String getName();

    List<ParameterTree> getParameters();

    TypeTree getReturnType();

}

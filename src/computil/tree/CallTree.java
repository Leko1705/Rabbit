package computil.tree;

import computil.util.InheritOnly;

import java.util.List;

@InheritOnly
public interface CallTree extends ExpressionTree {

    List<ExpressionTree> getArguments();
}

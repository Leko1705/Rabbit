package computil.tree;

import computil.util.InheritOnly;

@InheritOnly
public interface LiteralTree<T> extends ExpressionTree {

    T get();
}

package computil.tree;

import computil.util.InheritOnly;

@InheritOnly
public interface UnaryExpressionTree extends ExpressionTree {

    ExpressionTree getOperand();

}

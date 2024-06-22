package computil.tree;

import computil.util.InheritOnly;

@InheritOnly
public interface BinaryExpressionTree extends ExpressionTree {

    ExpressionTree getLeftOperand();

    ExpressionTree getRightOperand();

}

package computil.parse;

import computil.tree.ExpressionTree;
import computil.tree.RootTree;
import computil.tree.StatementTree;

public interface Parser {

    RootTree parseProgram();

    StatementTree parseStatement();

    ExpressionTree parseExpression();

}

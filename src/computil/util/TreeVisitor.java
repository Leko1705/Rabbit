package computil.util;

import computil.tree.*;

public interface TreeVisitor<P, R> {

    R visitRootTree(RootTree rootTree, P p);

    R visitStructTree(StructTree structTree, P p);

    R visitFieldTree(FieldTree fieldTree, P p);

    R visitTemplateTree(TemplateTree templateTree, P p);

    R visitTemplateMethodTree(TemplateMethodTree methodTree, P p);

    R visitImplementationTree(ImplementationTree implTree, P p);



    R visitFunctionTree(FunctionTree functionTree, P p);

    R visitNativeFunctionTree(NativeFunctionTree functionTree, P p);

    R visitParameterTree(ParameterTree parameterTree, P p);

    R visitReturnTree(ReturnTree returnTree, P p);



    R visitBlockTree(BlockTree blockTree, P p);

    R visitVarDecTree(VarDecTree varDecTree, P p);

    R visitExpressionStatementTree(ExpressionStatementTree statementTree, P p);

    R visitIfElseTree(IfElseTree ifElseTree, P p);

    R visitWhileDoTree(WhileDoTree whileDoTree, P p);

    R visitDoWhileTree(DoWhileTree doWhileTree, P p);

    R visitFreeTree(FreeTree freeTree, P p);



    R visitNullTree(NullTree nullTree, P p);

    R visitIntegerTree(IntegerTree integerTree, P p);

    R visitFloatTree(FloatTree floatTree, P p);

    R visitBooleanTree(BooleanTree booleanTree, P p);

    R visitStringTree(StringTree stringTree, P p);

    R visitVariableTree(VariableTree variableTree, P p);

    R visitArrayTree(ArrayTree arrayTree, P p);

    R visitAssignTree(AssignTree assignTree, P p);

    R visitContainerAccessTree(ContainerAccessTree accessTree, P p);

    R visitCallGlobalTree(CallGlobalTree callGlobalTree, P p);

    R visitCallMethodTree(CallMethodTree callMethodTree, P p);

    R visitFieldAccessTree(FieldAccessTree accessTree, P p);

    R visitNotTree(NotTree notTree, P p);

    R visitNegationTree(NegationTree negationTree, P p);

    R visitCastTree(CastTree castTree, P p);

    R visitNullCheckTree(NullCheckTree nullCheckTree, P p);

    R visitBinaryOperationTree(BinaryOperationTree operationTree, P p);

    R visitStructInitTree(StructInitTree structInitTree, P p);



    R visitTypeTree(TypeTree typeTree, P p);

}

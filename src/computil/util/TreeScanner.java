package computil.util;

import computil.tree.*;

import java.util.List;

public abstract class TreeScanner<P, R> implements TreeVisitor<P, R> {

    private final R defaultValue;

    public TreeScanner(R defaultValue) {
        this.defaultValue = defaultValue;
    }

    public TreeScanner() {
        defaultValue = null;
    }

    public R selectResult(R r1, R r2){
        return r1;
    }

    public R scan(Tree tree, P p){
        return (tree != null) ? tree.accept(this, p) : null;
    }

    public R scan(List<? extends Tree> trees, P p){
        R r = null;
        for (Tree tree : trees)
            r = scanSelective(tree, p, r);
        return r;
    }

    public R scanSelective(Tree tree, P p, R r){
        return selectResult(scan(tree, p), r);
    }

    public R scanSelective(List<? extends Tree> trees, P p, R r){
        for (Tree tree : trees)
            r = scanSelective(tree, p, r);
        return r;
    }


    @Override
    public R visitRootTree(RootTree rootTree, P p) {
        R r = scan(rootTree.getStructs(), p);
        r = scanSelective(rootTree.getTemplates(), p, r);
        r = scanSelective(rootTree.getImpls(), p, r);
        r = scanSelective(rootTree.getCallables(), p, r);
        return r;
    }

    @Override
    public R visitStructTree(StructTree structTree, P p) {
        return scan(structTree.getFields(), p);
    }

    @Override
    public R visitFieldTree(FieldTree fieldTree, P p) {
        return scan(fieldTree.getType(), p);
    }

    @Override
    public R visitTemplateTree(TemplateTree templateTree, P p) {
        return scan(templateTree.getMethods(), p);
    }

    @Override
    public R visitTemplateMethodTree(TemplateMethodTree methodTree, P p) {
        R r = scan(methodTree.getReturnType(), p);
        r = scanSelective(methodTree.getParameters(), p, r);
        return r;
    }

    @Override
    public R visitImplementationTree(ImplementationTree implTree, P p) {
        return scan(implTree.getImplementations(), p);
    }

    @Override
    public R visitFunctionTree(FunctionTree functionTree, P p) {
        R r = scan(functionTree.getReturnType(), p);
        r = scanSelective(functionTree.getParameters(), p, r);
        r = scanSelective(functionTree.getBody(), p, r);
        return r;
    }

    @Override
    public R visitNativeFunctionTree(NativeFunctionTree functionTree, P p) {
        R r = scan(functionTree.getReturnType(), p);
        r = scanSelective(functionTree.getParameters(), p, r);
        return r;
    }

    @Override
    public R visitParameterTree(ParameterTree parameterTree, P p) {
        return scan(parameterTree.getType(), p);
    }

    @Override
    public R visitReturnTree(ReturnTree returnTree, P p) {
        return scan(returnTree.getExpression(), p);
    }

    @Override
    public R visitBlockTree(BlockTree blockTree, P p) {
        return scan(blockTree.getStatements(), p);
    }

    @Override
    public R visitVarDecTree(VarDecTree varDecTree, P p) {
        R r = scan(varDecTree.getType(), p);
        r = scanSelective(varDecTree.getInitializer(), p, r);
        return r;
    }

    @Override
    public R visitExpressionStatementTree(ExpressionStatementTree statementTree, P p) {
        return scan(statementTree.getExpression(), p);
    }

    @Override
    public R visitIfElseTree(IfElseTree ifElseTree, P p) {
        R r = scan(ifElseTree.getCondition(), p);
        r = scanSelective(ifElseTree.getIfBody(), p, r);
        r = scanSelective(ifElseTree.getElseBody(), p, r);
        return r;
    }

    @Override
    public R visitWhileDoTree(WhileDoTree whileDoTree, P p) {
        R r = scan(whileDoTree.getCondition(), p);
        r = scanSelective(whileDoTree.getBody(), p, r);
        return r;
    }

    @Override
    public R visitDoWhileTree(DoWhileTree doWhileTree, P p) {
        R r = scan(doWhileTree.getBody(), p);
        r = scanSelective(doWhileTree.getCondition(), p, r);
        return r;
    }

    @Override
    public R visitFreeTree(FreeTree freeTree, P p) {
        return scan(freeTree.getExpression(), p);
    }



    @Override
    public R visitNullTree(NullTree nullTree, P p) {
        return defaultValue;
    }

    @Override
    public R visitIntegerTree(IntegerTree integerTree, P p) {
        return defaultValue;
    }

    @Override
    public R visitFloatTree(FloatTree floatTree, P p) {
        return defaultValue;
    }

    @Override
    public R visitBooleanTree(BooleanTree booleanTree, P p) {
        return defaultValue;
    }

    @Override
    public R visitStringTree(StringTree stringTree, P p) {
        return defaultValue;
    }

    @Override
    public R visitVariableTree(VariableTree variableTree, P p) {
        return defaultValue;
    }

    @Override
    public R visitArrayTree(ArrayTree arrayTree, P p) {
        return scan(arrayTree.getContent(), p);
    }

    @Override
    public R visitAssignTree(AssignTree assignTree, P p) {
        R r = scan(assignTree.getLeftOperand(), p);
        r = scanSelective(assignTree.getRightOperand(), p, r);
        return r;
    }

    @Override
    public R visitContainerAccessTree(ContainerAccessTree accessTree, P p) {
        R r = scan(accessTree.getOperand(), p);
        r = scanSelective(accessTree.getKey(), p, r);
        return r;
    }

    @Override
    public R visitCallGlobalTree(CallGlobalTree callGlobalTree, P p) {
        return scan(callGlobalTree.getArguments(), p);
    }

    @Override
    public R visitCallMethodTree(CallMethodTree callMethodTree, P p) {
        return scan(callMethodTree.getArguments(), p);
    }

    @Override
    public R visitFieldAccessTree(FieldAccessTree accessTree, P p) {
        return scan(accessTree.getOperand(), p);
    }

    @Override
    public R visitNotTree(NotTree notTree, P p) {
        return scan(notTree.getOperand(), p);
    }

    @Override
    public R visitNegationTree(NegationTree negationTree, P p) {
        return scan(negationTree.getOperand(), p);
    }

    @Override
    public R visitCastTree(CastTree castTree, P p) {
        R r = scan(castTree.getCastType(), p);
        r = scanSelective(castTree.getOperand(), p, r);
        return r;
    }

    @Override
    public R visitNullCheckTree(NullCheckTree nullCheckTree, P p) {
        return scan(nullCheckTree.getOperand(), p);
    }

    @Override
    public R visitBinaryOperationTree(BinaryOperationTree operationTree, P p) {
        R r = scan(operationTree.getLeftOperand(), p);
        r = scanSelective(operationTree.getRightOperand(), p, r);
        return r;
    }

    @Override
    public R visitStructInitTree(StructInitTree structInitTree, P p) {
        R r = scan(structInitTree.getType(), p);
        r = scanSelective(structInitTree.getArguments(), p, r);
        return r;
    }

    @Override
    public R visitTypeTree(TypeTree typeTree, P p) {
        return scan(typeTree.getGenerics(), p);
    }
}

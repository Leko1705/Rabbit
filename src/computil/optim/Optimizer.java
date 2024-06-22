package computil.optim;

import computil.tree.*;
import computil.util.TreeScanner;

import java.util.List;

public abstract class Optimizer<R> extends TreeScanner<Tree, R> {

    private boolean optimizationPerformed = false;

    protected void setOptimizationPerformed(boolean performed){
        this.optimizationPerformed = performed;
    }

    public boolean isOptimizationPerformed() {
        return optimizationPerformed;
    }

    protected void replace(Tree owner, Tree oldTree, Tree newTree){
        optimizationPerformed |= owner.replace(oldTree, newTree);
    }

    protected void remove(Tree owner, Tree toRemove){
        optimizationPerformed |= owner.remove(toRemove);
    }


    @SuppressWarnings("all")
    protected R scanIntrinsic(List<? extends Tree> parents, Tree parent, R r){
        for (int i = 0; i < parents.size(); i++)
            r = scanSelective(parents.get(i), parent, r);
        return r;
    }

    protected R scanIntrinsic(List<? extends Tree> parents, Tree parent){
        return scanIntrinsic(parents, parent, null);
    }


    @Override
    public R visitRootTree(RootTree rootTree, Tree parent) {
        return super.visitRootTree(rootTree, rootTree);
    }

    @Override
    public R visitFunctionTree(FunctionTree functionTree, Tree parent) {
        R r = scan(functionTree.getReturnType(), functionTree);
        r = scanIntrinsic(functionTree.getParameters(), functionTree, r);
        return scanSelective(functionTree.getBody(), functionTree, r);
    }

    @Override
    public R visitNativeFunctionTree(NativeFunctionTree functionTree, Tree parent) {
        R r = scan(functionTree.getReturnType(), functionTree);
        r = scanIntrinsic(functionTree.getParameters(), functionTree, r);
        return r;
    }

    @Override
    public R visitStructTree(StructTree structTree, Tree parent) {
        return scanIntrinsic(structTree.getFields(), structTree);
    }

    @Override
    public R visitFieldTree(FieldTree fieldTree, Tree parent) {
        return super.visitFieldTree(fieldTree, fieldTree);
    }

    @Override
    public R visitTemplateTree(TemplateTree templateTree, Tree parent) {
        return scanIntrinsic(templateTree.getMethods(), templateTree);
    }

    @Override
    public R visitTemplateMethodTree(TemplateMethodTree methodTree, Tree parent) {
        R r = scan(methodTree.getReturnType(), methodTree);
        return scanIntrinsic(methodTree.getParameters(), methodTree, r);
    }

    @Override
    public R visitImplementationTree(ImplementationTree implTree, Tree parent) {
        return scanIntrinsic(implTree.getImplementations(), implTree);
    }

    @Override
    public R visitParameterTree(ParameterTree parameterTree, Tree parent) {
        return super.visitParameterTree(parameterTree, parameterTree);
    }

    @Override
    public R visitBlockTree(BlockTree blockTree, Tree parent) {
        return scanIntrinsic(blockTree.getStatements(), blockTree);
    }

    @Override
    public R visitWhileDoTree(WhileDoTree whileDoTree, Tree parent) {
        return super.visitWhileDoTree(whileDoTree, whileDoTree);
    }

    @Override
    public R visitDoWhileTree(DoWhileTree doWhileTree, Tree parent) {
        return super.visitDoWhileTree(doWhileTree, doWhileTree);
    }

    @Override
    public R visitIfElseTree(IfElseTree ifElseTree, Tree parent) {
        return super.visitIfElseTree(ifElseTree, ifElseTree);
    }

    @Override
    public R visitReturnTree(ReturnTree returnTree, Tree parent) {
        return super.visitReturnTree(returnTree, returnTree);
    }

    @Override
    public R visitExpressionStatementTree(ExpressionStatementTree statementTree, Tree parent) {
        return super.visitExpressionStatementTree(statementTree, statementTree);
    }

    @Override
    public R visitVarDecTree(VarDecTree varDecTree, Tree parent) {
        return super.visitVarDecTree(varDecTree, varDecTree);
    }

    @Override
    public R visitFreeTree(FreeTree freeTree, Tree parent) {
        return super.visitFreeTree(freeTree, freeTree);
    }

    @Override
    public R visitNullTree(NullTree nullTree, Tree parent) {
        return super.visitNullTree(nullTree, nullTree);
    }

    @Override
    public R visitIntegerTree(IntegerTree integerTree, Tree parent) {
        return super.visitIntegerTree(integerTree, integerTree);
    }

    @Override
    public R visitFloatTree(FloatTree floatTree, Tree parent) {
        return super.visitFloatTree(floatTree, floatTree);
    }

    @Override
    public R visitBooleanTree(BooleanTree booleanTree, Tree parent) {
        return super.visitBooleanTree(booleanTree, booleanTree);
    }

    @Override
    public R visitStringTree(StringTree stringTree, Tree parent) {
        return super.visitStringTree(stringTree, stringTree);
    }

    @Override
    public R visitVariableTree(VariableTree variableTree, Tree parent) {
        return super.visitVariableTree(variableTree, variableTree);
    }

    @Override
    public R visitArrayTree(ArrayTree arrayTree, Tree parent) {
        return scanIntrinsic(arrayTree.getContent(), arrayTree);
    }

    @Override
    public R visitCastTree(CastTree castTree, Tree parent) {
        return super.visitCastTree(castTree, castTree);
    }

    @Override
    public R visitNullCheckTree(NullCheckTree nullCheckTree, Tree parent) {
        return super.visitNullCheckTree(nullCheckTree, nullCheckTree);
    }

    @Override
    public R visitContainerAccessTree(ContainerAccessTree accessTree, Tree parent) {
        return super.visitContainerAccessTree(accessTree, accessTree);
    }

    @Override
    public R visitFieldAccessTree(FieldAccessTree accessTree, Tree parent) {
        return super.visitFieldAccessTree(accessTree, accessTree);
    }

    @Override
    public R visitAssignTree(AssignTree assignTree, Tree parent) {
        return super.visitAssignTree(assignTree, assignTree);
    }

    @Override
    public R visitStructInitTree(StructInitTree structInitTree, Tree parent) {
        R r = scan(structInitTree.getType(), structInitTree);
        return scanIntrinsic(structInitTree.getArguments(), structInitTree, r);
    }

    @Override
    public R visitCallGlobalTree(CallGlobalTree callGlobalTree, Tree parent) {
        return scanIntrinsic(callGlobalTree.getArguments(), callGlobalTree);
    }

    @Override
    public R visitCallMethodTree(CallMethodTree callMethodTree, Tree parent) {
        R r = scan(callMethodTree.getOperand(), callMethodTree);
        r = scanIntrinsic(callMethodTree.getArguments(), callMethodTree, r);
        return r;
    }

    @Override
    public R visitNotTree(NotTree notTree, Tree parent) {
        return super.visitNotTree(notTree, notTree);
    }

    @Override
    public R visitNegationTree(NegationTree negationTree, Tree parent) {
        return super.visitNegationTree(negationTree, negationTree);
    }

    @Override
    public R visitBinaryOperationTree(BinaryOperationTree operationTree, Tree parent) {
        return super.visitBinaryOperationTree(operationTree, operationTree);
    }

    @Override
    public R visitTypeTree(TypeTree typeTree, Tree parent) {
        return scanIntrinsic(typeTree.getGenerics(), typeTree);
    }

}

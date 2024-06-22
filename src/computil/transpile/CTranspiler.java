package computil.transpile;

import computil.check.TypeChecker;
import computil.tree.*;
import computil.util.TreeScanner;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CTranspiler extends TreeScanner<Void, Void> {

    private final OutputStream out;

    private boolean inGlobalScope = true;

    private final TypeChecker typeResolver = new TypeChecker();


    public CTranspiler(OutputStream out) {
        this.out = out;
    }

    private void write(String s){
        try {
            out.write(s.getBytes(StandardCharsets.UTF_8));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Void visitRootTree(RootTree rootTree, Void unused) {
        typeResolver.visitRootTree(rootTree, null);

        write("#include \"stdio.h\"\n");
        write("#define true 1\n#define false 0\n");
        write("typedef int bool;\n");

        genAbstractTypeDefs(rootTree.getStructs());
        genAbstractFunctionDefs(rootTree.getCallables());
        scan(rootTree.getStructs(), null);
        scan(rootTree.getCallables(), null);
        inGlobalScope = false;
        return null;
    }

    private void genAbstractTypeDefs(Collection<StructTree> structTrees){
        for (StructTree structTree : structTrees)
            write("typedef __" + structTree.getName() + " " + structTree.getName() + ";\n");
    }

    private void genAbstractFunctionDefs(Collection<CallableTree> callableTrees){
        for (CallableTree callable : callableTrees){
            if (inGlobalScope && callable.getName().equals("main"))
                write("int");
            else
                scan(callable.getReturnType(), null);
            write(" " + callable.getName() + "(");
            scan(callable.getParameters(), null);
            write(");\n");
        }
    }

    @Override
    public Void visitParameterTree(ParameterTree parameterTree, Void unused) {
        scan(parameterTree.getType(), null);
        write(" " + parameterTree.getName());
        return null;
    }

    @Override
    public Void visitTypeTree(TypeTree typeTree, Void unused) {
        if (typeTree.name().equals("str")) {
            write("char*");
        }
        else {
            write(typeTree.name());
        }
        if (!isPrimitive(typeTree))
            write("*");
        return null;
    }

    private boolean isPrimitive(TypeTree type){
        String name = type.name();
        return Set.of("int", "float", "bool", "str", "void").contains(name);
    }

    @Override
    public Void visitStructTree(StructTree structTree, Void unused) {
        write("typedef struct __" + structTree.getName() + " { ");
        scan(structTree.getFields(), null);
        write("} " + structTree.getName() + ";\n");
        return null;
    }

    @Override
    public Void visitFieldTree(FieldTree fieldTree, Void unused) {
        scan(fieldTree.getType(), null);
        write(" " + fieldTree.getName() + "; ");
        return null;
    }

    @Override
    public Void visitFunctionTree(FunctionTree functionTree, Void unused) {
        boolean isMain = inGlobalScope && functionTree.getName().equals("main");

        if (isMain)
            write("int");
        else
            scan(functionTree.getReturnType(), null);

        write(" " + functionTree.getName() + "(");
        scan(functionTree.getParameters(), null);
        write("){\n");
        scan(functionTree.getBody(), null);
        if (isMain)
            write("return 0;\n");
        write("}\n");
        return null;
    }

    @Override
    public Void visitNativeFunctionTree(NativeFunctionTree functionTree, Void unused) {
        // nothing happens, since native function is a built-in function
        return null;
    }

    @Override
    public Void visitVarDecTree(VarDecTree varDecTree, Void unused) {
        TypeTree type = varDecTree.getType();
        if (type != null) {
            scan(type, null);
        }
        else {
            scan(varDecTree.getInitializer().accept(typeResolver, null), null);
        }
        write(" " + varDecTree.getName());

        if (varDecTree.getInitializer() == null) {
            write(" = NULL;\n");
        } else {
            write(" = ");
            scan(varDecTree.getInitializer(), null);
            write(";\n");
        }
        return null;
    }

    @Override
    public Void visitFreeTree(FreeTree freeTree, Void unused) {
        write("free(");
        scan(freeTree.getExpression(), null);
        write(");\n");
        return null;
    }

    @Override
    public Void visitVariableTree(VariableTree variableTree, Void unused) {
        write(variableTree.getName());
        return null;
    }

    @Override
    public Void visitNullTree(NullTree nullTree, Void unused) {
        write("NULL");
        return null;
    }

    @Override
    public Void visitIntegerTree(IntegerTree integerTree, Void unused) {
        write(Integer.toString(integerTree.get()));
        return null;
    }

    @Override
    public Void visitFloatTree(FloatTree floatTree, Void unused) {
        write(Double.toString(floatTree.get()));
        return null;
    }

    @Override
    public Void visitBooleanTree(BooleanTree booleanTree, Void unused) {
        write(Boolean.toString(booleanTree.get()));
        return null;
    }

    @Override
    public Void visitStringTree(StringTree stringTree, Void unused) {
        write("\"" + stringTree.get() + "\"");
        return null;
    }

    @Override
    public Void visitAssignTree(AssignTree assignTree, Void unused) {
        write("(");
        scan(assignTree.getLeftOperand(), null);
        write(" = ");
        scan(assignTree.getRightOperand(), null);
        write(")");
        return null;
    }

    @Override
    public Void visitStructInitTree(StructInitTree structInitTree, Void unused) {
        write("malloc(sizeof(" + structInitTree.getType().name() + "))");
        return null;
    }

    @Override
    public Void visitCallGlobalTree(CallGlobalTree callGlobalTree, Void unused) {
        write(callGlobalTree.getName());
        writeArguments(callGlobalTree.getArguments());
        return null;
    }

    @Override
    public Void visitCallMethodTree(CallMethodTree callMethodTree, Void unused) {
        scan(callMethodTree.getOperand(), null);
        write("->" + callMethodTree.getMethodName());
        writeArguments(callMethodTree.getArguments());
        return null;
    }

    private void writeArguments(List<ExpressionTree> arguments){
        write("(");
        Iterator<ExpressionTree> args = arguments.iterator();
        if (args.hasNext()){
            ExpressionTree arg = args.next();
            scan(arg, null);
            while (args.hasNext()){
                arg = args.next();
                write(", ");
                scan(arg, null);
            };
        }
        write(")");
    }

    @Override
    public Void visitExpressionStatementTree(ExpressionStatementTree statementTree, Void unused) {
        scan(statementTree.getExpression(), null);
        write(";\n");
        return null;
    }

    @Override
    public Void visitReturnTree(ReturnTree returnTree, Void unused) {
        write("return ");
        scan(returnTree.getExpression(), null);
        write(";\n");
        return null;
    }

    @Override
    public Void visitCastTree(CastTree castTree, Void unused) {
        scan(castTree.getOperand(), null);
        return null;
    }

    @Override
    public Void visitNullCheckTree(NullCheckTree nullCheckTree, Void unused) {
        write("if ((");
        scan(nullCheckTree.getOperand(), null);
        write(") == NULL) {\nfprintf(stderr, %s, \"NullPointerException\");\n}\n");
        return null;
    }

    @Override
    public Void visitBinaryOperationTree(BinaryOperationTree operationTree, Void unused) {
        write("(");
        scan(operationTree.getLeftOperand(), null);
        write(" " + operationTree.getOperation().encoding + " ");
        scan(operationTree.getRightOperand(), null);
        write(")");
        return null;
    }

    @Override
    public Void visitNotTree(NotTree notTree, Void unused) {
        write("(!");
        scan(notTree.getOperand(), null);
        write(")");
        return null;
    }

    @Override
    public Void visitNegationTree(NegationTree negationTree, Void unused) {
        write("(");
        write("-");
        scan(negationTree.getOperand(), null);
        write(")");
        return null;
    }

    @Override
    public Void visitIfElseTree(IfElseTree ifElseTree, Void unused) {
        write("if(");
        scan(ifElseTree.getCondition(), null);
        write("){\n");
        scan(ifElseTree.getIfBody(), null);
        write("}\n");

        StatementTree elseBody = ifElseTree.getElseBody();
        if (elseBody != null){
            write("else{\n");
            scan(elseBody, null);
            write("}\n");
        }

        return null;
    }

    @Override
    public Void visitWhileDoTree(WhileDoTree whileDoTree, Void unused) {
        write("while(");
        scan(whileDoTree.getCondition(), null);
        write(") {\n");
        scan(whileDoTree.getBody(), null);
        write("}\n");
        return null;
    }

    @Override
    public Void visitDoWhileTree(DoWhileTree doWhileTree, Void unused) {
        write("do{\n");
        scan(doWhileTree.getCondition(), null);
        write("}\n");
        write("while(");
        scan(doWhileTree.getBody(), null);
        write(");");
        return null;
    }

    @Override
    public Void visitFieldAccessTree(FieldAccessTree accessTree, Void unused) {
        scan(accessTree.getOperand(), null);
        write("->" + accessTree.getFieldName());
        return null;
    }

    @Override
    public Void visitContainerAccessTree(ContainerAccessTree accessTree, Void unused) {
        scan(accessTree.getOperand(), null);
        write("[");
        scan(accessTree.getKey(), null);
        write("]");
        return null;
    }

    @Override
    public Void visitArrayTree(ArrayTree arrayTree, Void unused) {

        return null;
    }

}

package computil.tree;

import computil.util.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Trees {

    private Trees(){}

    private static abstract class AbstractTree implements Tree {
        private final Location location;
        private AbstractTree(Location location) {
            this.location = location;
        }
        public Location getLocation() {
            return location;
        }
    }

    public static class BasicRootTree extends AbstractTree implements RootTree {
        public final List<StructTree> structs = new ArrayList<>();
        public final List<CallableTree> callables = new ArrayList<>();
        public final List<TemplateTree> templates = new ArrayList<>();
        public final List<ImplementationTree> impls = new ArrayList<>();
        public BasicRootTree() {
            super(null);
        }
        public List<StructTree> getStructs() {
            return structs;
        }
        public List<CallableTree> getCallables() {
            return callables;
        }
        public List<TemplateTree> getTemplates() {
            return templates;
        }
        public List<ImplementationTree> getImpls() {
            return impls;
        }
    }

    public static class BasicTypeTree extends AbstractTree implements TypeTree {
        public String name;
        public boolean isNullable = false;
        public final List<TypeTree> generics = new ArrayList<>();
        public BasicTypeTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public String name() {
            return name;
        }
        public boolean isNullable() {
            return isNullable;
        }
        public List<TypeTree> getGenerics() {
            return generics;
        }
    }

    public static class BasicStructTree extends AbstractTree implements StructTree {
        public String name;
        public List<FieldTree> fields = new ArrayList<>();
        public BasicStructTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public List<FieldTree> getFields() {
            return fields;
        }
    }

    public static class BasicFieldTree extends AbstractTree implements FieldTree {
        public boolean isMutable = false;
        public String name;
        public TypeTree type;
        public BasicFieldTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public TypeTree getType() {
            return type;
        }
        public boolean isMutable() {
            return isMutable;
        }
    }

    public static class BasicTemplateTree extends AbstractTree implements TemplateTree {
        public String name;
        public List<TemplateMethodTree> methods = new ArrayList<>();
        public BasicTemplateTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public List<TemplateMethodTree> getMethods() {
            return methods;
        }
    }

    public static class BasicTemplateMethodTree extends AbstractTree implements TemplateMethodTree {
        public String name;
        public List<ParameterTree> parameters = new ArrayList<>();
        public TypeTree returnType;
        public BasicTemplateMethodTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public List<ParameterTree> getParameters() {
            return parameters;
        }
        public TypeTree getReturnType() {
            return returnType;
        }
    }

    public static class BasicImplementationTree extends AbstractTree implements ImplementationTree {
        public String name;
        public String forType;
        public String objectName;
        public final List<CallableTree> implementations = new ArrayList<>();
        public BasicImplementationTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public String getForTypeName() {
            return forType;
        }
        public String getObjectName() {
            return objectName;
        }
        public List<CallableTree> getImplementations() {
            return implementations;
        }
    }

    public static class BasicFunctionTree extends AbstractTree implements FunctionTree {
        public String name;
        public List<ParameterTree> parameters = new ArrayList<>();
        public TypeTree returnType;
        public BlockTree body;
        public BasicFunctionTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public List<ParameterTree> getParameters() {
            return parameters;
        }
        public TypeTree getReturnType() {
            return returnType;
        }
        public BlockTree getBody() {
            return body;
        }
    }

    public static class BasicNativeFunctionTree extends AbstractTree implements NativeFunctionTree {
        public String name;
        public List<ParameterTree> parameters = new ArrayList<>();
        public TypeTree returnType;
        public BasicNativeFunctionTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public List<ParameterTree> getParameters() {
            return parameters;
        }
        public TypeTree getReturnType() {
            return returnType;
        }
    }

    public static class BasicParameterTree extends AbstractTree implements ParameterTree {
        public boolean isMutable = false;
        public String name;
        public TypeTree type;
        public BasicParameterTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public boolean isMutable() {
            return isMutable;
        }

        public String getName() {
            return name;
        }
        public TypeTree getType() {
            return type;
        }
    }

    public static class BasicBlockTree extends AbstractTree implements BlockTree {
        public final List<StatementTree> statements = new ArrayList<>();
        public BasicBlockTree() {
            super(null);
        }
        public List<StatementTree> getStatements() {
            return statements;
        }

        @Override
        public boolean remove(Tree toRemove) {
            return statements.remove((StatementTree) toRemove);
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            return Trees.replace(statements, oldTree, newTree);
        }
    }

    private static abstract class BasicLiteralTree<T> extends AbstractTree implements LiteralTree<T> {
        public T value;
        private BasicLiteralTree(T value, Location location) {
            super(location);
            this.value = value;
        }
        public T get() {
            return value;
        }
    }

    public static class BasicNullTree extends BasicLiteralTree<Void> implements NullTree {
        public BasicNullTree(Location location) {
            super(null, location);
        }
    }

    public static class BasicIntegerTree extends BasicLiteralTree<Integer> implements IntegerTree {
        public BasicIntegerTree(int value, Location location) {
            super(value, location);
        }
    }

    public static class BasicFloatTree extends BasicLiteralTree<Float> implements FloatTree {
        public BasicFloatTree(Float value, Location location) {
            super(value, location);
        }
    }

    public static class BasicStringTree extends BasicLiteralTree<String> implements StringTree {
        public BasicStringTree(String value, Location location) {
            super(value, location);
        }
    }

    public static class BasicBooleanTree extends BasicLiteralTree<Boolean> implements BooleanTree {
        public BasicBooleanTree(boolean value, Location location) {
            super(value, location);
        }
    }

    public static class BasicVariableTree extends AbstractTree implements VariableTree {
        public String name;
        public BasicVariableTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    public static class BasicNotTree extends AbstractTree implements NotTree {
        public ExpressionTree expression;
        public BasicNotTree(ExpressionTree expression, Location location) {
            super(location);
            this.expression = expression;
        }
        public ExpressionTree getOperand() {
            return expression;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (expression == oldTree){
                expression = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }
    }

    public static class BasicNegationTree extends AbstractTree implements NegationTree {
        public ExpressionTree expression;
        public BasicNegationTree(ExpressionTree expression, Location location) {
            super(location);
            this.expression = expression;
        }
        public ExpressionTree getOperand() {
            return expression;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (expression == oldTree){
                expression = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }
    }

    public static class BasicArrayTree extends AbstractTree implements ArrayTree {
        public List<ExpressionTree> content = new ArrayList<>();
        public BasicArrayTree(Location location) {
            super(location);
        }
        public List<ExpressionTree> getContent() {
            return content;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            return Trees.replace(content, oldTree, newTree);
        }
    }

    public static class BasicNullCheckTree extends AbstractTree implements NullCheckTree {
        public ExpressionTree expression;
        public BasicNullCheckTree(ExpressionTree expression, Location location) {
            super(location);
            this.expression = expression;
        }
        public ExpressionTree getOperand() {
            return expression;
        }

        public boolean replace(Tree oldTree, Tree newTree) {
            if (expression == oldTree){
                expression = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }
    }

    public static class BasicContainerAccessTree extends AbstractTree implements ContainerAccessTree {
        public ExpressionTree expression;
        public ExpressionTree key;
        public BasicContainerAccessTree(ExpressionTree expression, Location location) {
            super(location);
            this.expression = expression;
        }
        public ExpressionTree getOperand() {
            return expression;
        }
        public ExpressionTree getKey() {
            return key;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (expression == oldTree){
                expression = (ExpressionTree) newTree;
                return true;
            }
            if (key == oldTree){
                key = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }
    }

    public static class BasicFieldAccessTree extends AbstractTree implements FieldAccessTree {
        public ExpressionTree expression;
        public String fieldName;
        public BasicFieldAccessTree(ExpressionTree expression, Location location) {
            super(location);
            this.expression = expression;
        }
        public ExpressionTree getOperand() {
            return expression;
        }
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (expression == oldTree){
                expression = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }
    }

    private static abstract class BasicBinaryTree extends AbstractTree implements BinaryExpressionTree {
        public ExpressionTree left, right;
        private BasicBinaryTree(Location location) {
            super(location);
        }
        public ExpressionTree getLeftOperand() {
            return left;
        }
        public ExpressionTree getRightOperand() {
            return right;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (left == oldTree){
                left = (ExpressionTree) newTree;
                return true;
            }
            if (right == oldTree){
                right = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }

    }

    public static class BasicAssignTree extends BasicBinaryTree implements AssignTree {
        public BasicAssignTree(Location location) {
            super(location);
        }
    }

    public static class BasicBinaryOperationTree extends BasicBinaryTree implements BinaryOperationTree {
        public Operation operation;
        public BasicBinaryOperationTree(Operation operation, Location location) {
            super(location);
            this.operation = operation;
        }
        public Operation getOperation() {
            return operation;
        }
    }

    public static class BasicGlobalCallTree extends AbstractTree implements CallGlobalTree {
        public String name;
        public List<ExpressionTree> arguments = new ArrayList<>();
        public BasicGlobalCallTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public List<ExpressionTree> getArguments() {
            return arguments;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            return Trees.replace(arguments, oldTree, newTree);
        }
    }

    public static class BasicMethodCallTree extends AbstractTree implements CallMethodTree {
        public String name;
        public ExpressionTree expression;
        public List<ExpressionTree> arguments = new ArrayList<>();
        public BasicMethodCallTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public ExpressionTree getOperand() {
            return expression;
        }
        public String getMethodName() {
            return name;
        }
        public List<ExpressionTree> getArguments() {
            return arguments;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (expression == oldTree){
                expression = (ExpressionTree) newTree;
                return true;
            }
            return Trees.replace(arguments, oldTree, newTree);
        }
    }

    public static class BasicStructInitTree extends AbstractTree implements StructInitTree {
        public TypeTree type;
        public final List<ExpressionTree> arguments = new ArrayList<>();
        public BasicStructInitTree(TypeTree type, Location location) {
            super(location);
            this.type = type;
        }
        public TypeTree getType() {
            return type;
        }
        public List<ExpressionTree> getArguments() {
            return arguments;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            return Trees.replace(arguments, oldTree, newTree);
        }
    }

    public static class BasicCastTree extends AbstractTree implements CastTree {
        public ExpressionTree expression;
        public TypeTree type;
        public BasicCastTree(ExpressionTree expression, Location location) {
            super(location);
            this.expression = expression;
        }
        public TypeTree getCastType() {
            return type;
        }

        public ExpressionTree getOperand() {
            return expression;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (expression == oldTree){
                expression = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }
    }

    public static class BasicReturnTree extends AbstractTree implements ReturnTree {
        ExpressionTree expression;
        public BasicReturnTree(ExpressionTree expression, Location location) {
            super(location);
            this.expression = expression;
        }
        public ExpressionTree getExpression() {
            return expression;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (expression == oldTree){
                expression = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }
    }

    public static class BasicExpressionStatementTree extends AbstractTree implements ExpressionStatementTree {
        public ExpressionTree expression;
        public BasicExpressionStatementTree(ExpressionTree expression, Location location) {
            super(location);
            this.expression = expression;
        }
        public ExpressionTree getExpression() {
            return expression;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (expression == oldTree){
                expression = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }
    }

    public static class BasicVarDecTree extends AbstractTree implements VarDecTree {
        public String name;
        public boolean isMutable;
        public TypeTree type;
        public ExpressionTree initializer;
        public BasicVarDecTree(String name, Location location) {
            super(location);
            this.name = name;
        }
        public boolean isMutable() {
            return isMutable;
        }
        public String getName() {
            return name;
        }
        public TypeTree getType() {
            return type;
        }
        public ExpressionTree getInitializer() {
            return initializer;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (initializer == oldTree){
                initializer = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }
    }

    public static class BasicDoWhileTree extends AbstractTree implements DoWhileTree {
        public StatementTree body;
        public ExpressionTree condition;
        public BasicDoWhileTree(Location location) {
            super(location);
        }
        public StatementTree getBody() {
            return body;
        }
        public ExpressionTree getCondition() {
            return condition;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (condition == oldTree){
                condition = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Tree toRemove) {
            if (body == toRemove){
                body = new BasicBlockTree();
                return true;
            }
            return false;
        }
    }

    public static class BasicWhileDoTree extends AbstractTree implements WhileDoTree {
        public ExpressionTree condition;
        public StatementTree body;
        public BasicWhileDoTree(Location location) {
            super(location);
        }
        public StatementTree getBody() {
            return body;
        }
        public ExpressionTree getCondition() {
            return condition;
        }
        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (condition == oldTree){
                condition = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Tree toRemove) {
            if (body == toRemove){
                body = new BasicBlockTree();
                return true;
            }
            return false;
        }
    }

    public static class BasicIfElseTree extends AbstractTree implements IfElseTree {
        public ExpressionTree condition;
        public StatementTree ifBody;
        public StatementTree elseBody;
        public BasicIfElseTree(Location location) {
            super(location);
        }
        public ExpressionTree getCondition() {
            return condition;
        }
        public StatementTree getIfBody() {
            return ifBody;
        }
        public StatementTree getElseBody() {
            return elseBody;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (condition == oldTree){
                condition = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Tree toRemove) {
            if (ifBody == toRemove){
                ifBody = new BasicBlockTree();
                return true;
            }
            if (elseBody == toRemove){
                elseBody = new BasicBlockTree();
                return true;
            }
            return false;
        }
    }

    public static class BasicFreeTree extends AbstractTree implements FreeTree {
        public ExpressionTree expression;
        public BasicFreeTree(Location location) {
            super(location);
        }
        public ExpressionTree getExpression() {
            return expression;
        }

        @Override
        public boolean replace(Tree oldTree, Tree newTree) {
            if (expression == oldTree){
                expression = (ExpressionTree) newTree;
                return true;
            }
            return false;
        }
    }


    @SuppressWarnings("unchecked")
    private static <T extends Tree> boolean replace(List<T> expressions, Tree oldTree, Tree newTree) {
        AtomicBoolean replaced = new AtomicBoolean(false);
        expressions.replaceAll(statementTree -> {
            if (statementTree == oldTree){
                replaced.set(true);
                return (T) newTree;
            }
            return statementTree;
        });
        return replaced.get();
    }

}

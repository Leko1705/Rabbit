package computil.check;

import computil.diags.Error;
import computil.diags.Errors;
import computil.scope.*;
import computil.tree.*;
import computil.util.ArrayType;
import computil.util.Location;
import computil.util.NullsafeType;

import static computil.util.PrimitiveType.*;

import java.util.*;

public class TypeChecker extends Checker<Scope<TypeChecker.TypeEntry>, TypeTree> {

    private static final TypeFinder typeFinder = new TypeFinder();

    private final Map<String, Set<String>> impls = new HashMap<>();

    private final GlobalScope<TypeEntry> globalScope = new GlobalScope<>();
    private final Map<String, StructScope<TypeEntry>> types = new HashMap<>();

    private TypeTree requiredReturnType = null;

    private boolean returned = false;


    @Override
    public TypeTree visitRootTree(RootTree rootTree, Scope<TypeEntry> unused) {
        registerTypes(rootTree);
        registerFunctions(rootTree, globalScope);
        for (CallableTree callable : rootTree.getCallables())
            scan(callable, globalScope);
        return null;
    }

    private void registerTypes(RootTree rootTree){
        // register structs
        for (StructTree structTree : rootTree.getStructs())
            scan(structTree, null);

        // register templates
        for (TemplateTree template : rootTree.getTemplates())
            scan(template, null);

        // apply implementations
        for (ImplementationTree impl : rootTree.getImpls())
            scan(impl, null);
    }

    private void registerFunctions(RootTree rootTree, Scope<TypeEntry> scope){
        for (CallableTree callable : rootTree.getCallables()) {
            List<TypeTree> paramTypes = new ArrayList<>();
            for (ParameterTree param : callable.getParameters()) paramTypes.add(param.getType());
            scope.add(new TypeEntry(callable.getName(), callable.getReturnType(), false, paramTypes));
        }
    }

    @Override
    public TypeTree visitFunctionTree(FunctionTree functionTree, Scope<TypeEntry> scope) {
        FunctionScope<TypeEntry> functionScope = new FunctionScope<>(scope);
        requiredReturnType = functionTree.getReturnType();
        returned = false;
        super.visitFunctionTree(functionTree, functionScope);
        if (!returned && !typeMatches(requiredReturnType, NULL))
            error(Errors.invalidReturnType(requiredReturnType, NULL, functionTree.getLocation()));
        return null;
    }

    @Override
    public TypeTree visitParameterTree(ParameterTree parameterTree, Scope<TypeEntry> scope) {
        scope.add(new TypeEntry(parameterTree.getName(), parameterTree.getType(), parameterTree.isMutable()));
        return null;
    }

    @Override
    public TypeTree visitStructTree(StructTree structTree, Scope<TypeEntry> scope) {
        impls.put(structTree.getName(), new HashSet<>());
        StructScope<TypeEntry> structScope = new StructScope<>();
        types.put(structTree.getName(), structScope);
        scan(structTree.getFields(), structScope);
        return null;
    }

    @Override
    public TypeTree visitFieldTree(FieldTree fieldTree, Scope<TypeEntry> scope) {
        scope.add(new TypeEntry(fieldTree.getName(), fieldTree.getType(), fieldTree.isMutable()));
        return null;
    }

    @Override
    public TypeTree visitTemplateTree(TemplateTree templateTree, Scope<TypeEntry> scope) {
        StructScope<TypeEntry> structScope = new StructScope<>();
        types.put(templateTree.getName(), structScope);
        scan(templateTree.getMethods(), structScope);
        return null;
    }

    @Override
    public TypeTree visitTemplateMethodTree(TemplateMethodTree methodTree, Scope<TypeEntry> scope) {
        List<TypeTree> paramTypes = new ArrayList<>();
        for (ParameterTree param : methodTree.getParameters()) paramTypes.add(param.getType());
        scope.add(new TypeEntry(methodTree.getName(), methodTree.getReturnType(), false, paramTypes));
        return null;
    }

    @Override
    public TypeTree visitImplementationTree(ImplementationTree implTree, Scope<TypeEntry> scope) {
        Set<String> impls = this.impls.get(implTree.getForTypeName());
        if (impls != null) impls.add(implTree.getName());

        StructScope<TypeEntry> structScope = types.get(implTree.getForTypeName());
        if (structScope == null) return null;
        scan(implTree.getImplementations(), structScope);
        return null;
    }

    @Override
    public TypeTree visitVarDecTree(VarDecTree varDecTree, Scope<TypeEntry> scope) {
        TypeTree required = varDecTree.getType();
        TypeTree got = scan(varDecTree.getInitializer(), scope);

        if (required != null && got != null)
            checkTypesMatches(required, got, varDecTree.getInitializer().getLocation());

        if (required == null) required = got;

        if (!isDefined(varDecTree.getName(), scope))
            scope.add(new TypeEntry(varDecTree.getName(), required, varDecTree.isMutable()));

        return null;
    }

    @Override
    public TypeTree visitReturnTree(ReturnTree returnTree, Scope<TypeEntry> typeEntryScope) {
        TypeTree returnType = scan(returnTree.getExpression(), typeEntryScope);
        if (returnType == null) return null;
        if (!typeMatches(requiredReturnType, returnType))
            error(Errors.invalidReturnType(requiredReturnType, returnType, returnTree.getExpression().getLocation()));
        if (typeEntryScope instanceof FunctionScope<TypeEntry>)
            returned = true;
        return null;
    }

    @Override
    public TypeTree visitFreeTree(FreeTree freeTree, Scope<TypeEntry> scope) {
        TypeTree type = scan(freeTree.getExpression(), scope);
        if (type != NULL && isPrimitive(type))
            error(new Error("ca not release primitive type", freeTree.getLocation()));
        return null;
    }

    @Override
    public TypeTree visitIfElseTree(IfElseTree ifElseTree, Scope<TypeEntry> scope) {
        checkCondition(ifElseTree.getCondition(), scope);
        scan(ifElseTree.getIfBody(), scope);
        scan(ifElseTree.getElseBody(), scope);
        return null;
    }

    @Override
    public TypeTree visitDoWhileTree(DoWhileTree doWhileTree, Scope<TypeEntry> scope) {
        scan(doWhileTree.getBody(), scope);
        checkCondition(doWhileTree.getCondition(), scope);
        return null;
    }

    @Override
    public TypeTree visitWhileDoTree(WhileDoTree whileDoTree, Scope<TypeEntry> scope) {
        checkCondition(whileDoTree.getCondition(), scope);
        scan(whileDoTree.getBody(), scope);
        return null;
    }

    private void checkCondition(ExpressionTree exp, Scope<TypeEntry> scope){
        TypeTree typeTree = scan(exp, scope);
        checkTypesMatches(BOOL, typeTree, exp.getLocation());
        if (typeTree.isNullable())
            error(new Error("branch condition required null safety", exp.getLocation()));
    }

    @Override
    public TypeTree visitNullTree(NullTree nullTree, Scope<TypeEntry> unused) {
        return NULL;
    }

    @Override
    public TypeTree visitIntegerTree(IntegerTree integerTree, Scope<TypeEntry> unused) {
        return INTEGER;
    }

    @Override
    public TypeTree visitFloatTree(FloatTree floatTree, Scope<TypeEntry> unused) {
        return FLOAT;
    }

    @Override
    public TypeTree visitBooleanTree(BooleanTree booleanTree, Scope<TypeEntry> unused) {
        return BOOL;
    }

    @Override
    public TypeTree visitStringTree(StringTree stringTree, Scope<TypeEntry> unused) {
        return STRING;
    }

    @Override
    public TypeTree visitVariableTree(VariableTree variableTree, Scope<TypeEntry> scope) {
        TypeEntry entry = typeFinder.search(variableTree.getName(), scope);
        return entry != null ? entry.type : null;
    }

    @Override
    public TypeTree visitCastTree(CastTree castTree, Scope<TypeEntry> scope) {
        TypeTree required = castTree.getCastType();
        TypeTree got = scan(castTree.getOperand(), scope);
        if (isNumericType(required) && isNumericType(got)) return required;
        if (!typeMatches(required, got) && !isTemplate2StructCast(required, got))
            error(new Error("can not cast " + Errors.getTypeString(got) + " to " + Errors.getTypeString(required), castTree.getLocation()));
        return required;
    }

    private boolean isTemplate2StructCast(TypeTree required, TypeTree template) {
        Set<String> objImpls = impls.get(required.name());
        if (objImpls == null) return false;
        return objImpls.contains(template.name());
    }

    private boolean isNumericType(TypeTree type){
        String name = type.name();
        return Set.of("int", "float").contains(name);
    }

    @Override
    public TypeTree visitNullCheckTree(NullCheckTree nullCheckTree, Scope<TypeEntry> scope) {
        TypeTree type = scan(nullCheckTree.getOperand(), scope);
        if (type != null && (type == NULL || type.name().equals("void")))
            error(new Error("null is never nullsafe by definition", nullCheckTree.getLocation()));
        return type != null ? new NullsafeType(type) : null;
    }

    @Override
    public TypeTree visitNotTree(NotTree notTree, Scope<TypeEntry> scope) {
        TypeTree type = scan(notTree.getOperand(), scope);
        checkTypesMatches(BOOL, type, notTree.getOperand().getLocation());
        return type;
    }

    @Override
    public TypeTree visitNegationTree(NegationTree negationTree, Scope<TypeEntry> scope) {
        TypeTree type = scan(negationTree.getOperand(), scope);
        checkAnyTypeMatches(List.of(INTEGER, FLOAT), type, negationTree.getOperand().getLocation());
        return type;
    }

    @Override
    public TypeTree visitContainerAccessTree(ContainerAccessTree accessTree, Scope<TypeEntry> scope) {
        TypeTree containerType = scan(accessTree.getOperand(), scope);
        if (!containerType.name().equals("arr")){
            error(Errors.arrayExpectedButGot(containerType, accessTree.getLocation()));
            return null;
        }
        TypeTree keyType = scan(accessTree.getKey(), scope);
        checkTypesMatches(INTEGER, keyType, accessTree.getKey().getLocation());
        List<TypeTree> generics = containerType.getGenerics();
        return generics.isEmpty() ? null : generics.get(0);
    }

    @Override
    public TypeTree visitArrayTree(ArrayTree arrayTree, Scope<TypeEntry> scope) {
        TypeTree last = null;
        for (ExpressionTree value : arrayTree.getContent()){
            TypeTree current = scan(value, scope);
            if(last != null && !typeMatches(last, current) && current != NULL)
                error(new Error("inconsistency in array content types", value.getLocation()));

            if (current == NULL && last != null) current = new NullableType(last);

            last = current;
        }
        return new ArrayType(last);
    }

    @Override
    public TypeTree visitCallGlobalTree(CallGlobalTree callGlobalTree, Scope<TypeEntry> scope) {
        TypeEntry entry = globalScope.entries.get(callGlobalTree.getName());
        if (entry == null) return null;
        TypeTree returnType = entry.type;
        if (entry.parameters != null)
            checkCallTypes(callGlobalTree, callGlobalTree.getArguments(), entry.parameters, scope);
        return returnType;
    }

    @Override
    public TypeTree visitCallMethodTree(CallMethodTree callMethodTree, Scope<TypeEntry> scope) {
        TypeTree accessedType = scan(callMethodTree.getOperand(), scope);
        if (accessedType == null) return null;
        if (accessedType.isNullable()) error(new Error("method call requires null safety", callMethodTree.getLocation()));
        StructScope<TypeEntry> accessedStruct = types.get(accessedType.name());
        TypeEntry entry = typeFinder.search(callMethodTree.getMethodName(), accessedStruct);
        if (entry == null) return null;
        if (entry.parameters != null)
            checkCallTypes(callMethodTree, callMethodTree.getArguments(), entry.parameters, scope);
        return entry.type;
    }

    private void checkCallTypes(Tree owner, List<ExpressionTree> args, List<TypeTree> paramTypes, Scope<TypeEntry> scope){
        if (args.size() < paramTypes.size())
            error(new Error("too few parameters given", owner.getLocation()));
        if (args.size() > paramTypes.size())
            error(new Error("too many parameters given", owner.getLocation()));

        Iterator<ExpressionTree> argItr = args.iterator();
        Iterator<TypeTree> typeItr = paramTypes.iterator();

        while (argItr.hasNext()){
            ExpressionTree arg = argItr.next();

            TypeTree givenType = scan(arg, scope);
            TypeTree requiredType = typeItr.next();

            checkTypesMatches(requiredType, givenType, arg.getLocation());
        }
    }

    @Override
    public TypeTree visitFieldAccessTree(FieldAccessTree accessTree, Scope<TypeEntry> scope) {
        TypeTree type = scan(accessTree.getOperand(), scope);
        if (type == null) return null;
        if (isPrimitive(type))
            error(new Error("can not access primitive type " + type.name(), accessTree.getLocation()));
        StructScope<TypeEntry> structScope = types.get(type.name());
        if (structScope == null) return null;
        TypeEntry entry = typeFinder.search(accessTree.getFieldName(), structScope);
        return entry != null ? entry.type : null;
    }

    @Override
    public TypeTree visitAssignTree(AssignTree assignTree, Scope<TypeEntry> scope) {
        if (assignTree.getLeftOperand() instanceof VariableTree variableTree) {
            TypeEntry entry = typeFinder.search(variableTree.getName(), scope);
            if (entry != null && !entry.isMutable())
                error(new Error("'" + variableTree.getName() + "' is immutable and cannot be assigned", variableTree.getLocation()));
        }
        TypeTree expected = scan(assignTree.getLeftOperand(), scope);
        TypeTree given = scan(assignTree.getRightOperand(), scope);
        checkTypesMatches(expected, given, assignTree.getRightOperand().getLocation());
        return expected;
    }

    @Override
    public TypeTree visitStructInitTree(StructInitTree structInitTree, Scope<TypeEntry> scope) {
        TypeTree type = structInitTree.getType();
        StructScope<TypeEntry> structScope = types.get(type.name());
        if (structScope != null){
            List<TypeTree> required = new ArrayList<>();
            for (TypeEntry entry : structScope.getOrdered())
                required.add(entry.type);
            checkCallTypes(structInitTree, structInitTree.getArguments(), required, scope);
        }
        return type;
    }

    @Override
    public TypeTree visitBinaryOperationTree(BinaryOperationTree operationTree, Scope<TypeEntry> scope) {
        TypeTree left = scan(operationTree.getLeftOperand(), scope);
        TypeTree right = scan(operationTree.getRightOperand(), scope);
        Operation op = operationTree.getOperation();
        return switch (op){
            case ADD, DIV, SUB, MUL, AND_BIT, OR_BIT, IDIV
                    -> checkNumericOperation(left, right, op, operationTree);
            case SHIFT_AL, SHIFT_AR -> checkShiftOperation(left, right, operationTree);
            case MOD -> checkIntegerOnlyOperation(left, right, operationTree);
            case AND, OR -> checkBoolOnlyOperation(left, right, operationTree);
            case XOR -> checkBoolOrIntOperation(left, right, operationTree);
            case LESS, GREATER, LESS_EQ, GREATER_EQ
                    -> checkCompareOperation(left, right, operationTree);
            case NOT_EQUALS, EQUALS -> checkEqualityOperation(left, right, operationTree);
        };
    }

    private TypeTree checkCompareOperation(TypeTree left, TypeTree right, BinaryOperationTree tree) {
        if (!isNumeric(left) || !isNumeric(right))
            error(new Error("can not perform numeric operation on non numeric value", tree.getLocation()));
        return BOOL;
    }

    private TypeTree checkBoolOrIntOperation(TypeTree left, TypeTree right, BinaryOperationTree tree){
        if (left.name().equals("bool") && right.name().equals("bool")){
            return BOOL;
        }
        else if (left.name().equals("int") && right.name().equals("int")){
            return INTEGER;
        }
        error(new Error("operation can be performed on int or bool only", tree.getLocation()));
        return null;

    }

    private TypeTree checkEqualityOperation(TypeTree left, TypeTree right, BinaryOperationTree tree) {
        if (isPrimitive(left) ^ isPrimitive(right))
            error(new Error("can not compare primitive value with reference", tree.getLocation()));
        checkTypesMatches(left, right, tree.getLocation());
        return BOOL;
    }

    private TypeTree checkIntegerOnlyOperation(TypeTree left, TypeTree right, BinaryOperationTree tree) {
        if (!left.name().equals("int") || !right.name().equals("int"))
            error(new Error("operation can be performed on int only", tree.getLocation()));
        return INTEGER;
    }

    private TypeTree checkBoolOnlyOperation(TypeTree left, TypeTree right, BinaryOperationTree tree) {
        if (!left.name().equals("bool") || !right.name().equals("bool"))
            error(new Error("operation can be performed on bool only", tree.getLocation()));
        return BOOL;
    }

    private TypeTree checkNumericOperation(TypeTree left, TypeTree right, Operation op, BinaryOperationTree tree){
        if (!isNumeric(left) || !isNumeric(right))
            error(new Error("can not perform numeric operation on non numeric value", tree.getLocation()));
        if (op == Operation.DIV) return FLOAT;
        return left.name().equals("int") && right.name().equals("int") ? INTEGER : FLOAT;
    }

    private TypeTree checkShiftOperation(TypeTree left, TypeTree right, BinaryOperationTree tree){
        if (!isNumeric(left) || !isNumeric(right))
            error(new Error("can not perform numeric operation on non numeric value", tree.getLocation()));
        if (!right.name().equals("int"))
            error(new Error("can not shift by floating point values", tree.getLocation()));
        return left.name().equals("int") ? INTEGER : FLOAT;
    }

    private boolean isPrimitive(TypeTree type){
        String name = type.name();
        return Set.of("int", "float", "bool", "str", "void").contains(name);
    }

    private boolean isNumeric(TypeTree type){
        return type.name().equals("int") || type.name().equals("float");
    }

    private void checkTypesMatches(TypeTree required, TypeTree given, Location location){
        checkAnyTypeMatches(List.of(required), given, location);
    }

    private void checkAnyTypeMatches(List<TypeTree> required, TypeTree given, Location location){
        if (given == null) return;
        if (required.isEmpty()) throw new AssertionError();
        for (TypeTree req : required)
            if (typeMatches(req, given))
                return;
        error(Errors.typeMismatch(required.get(0), given, location));
    }

    private boolean typeMatches(TypeTree required, TypeTree given){
        if (required == null || given == null) return true;

        if (required.isNullable() && given == NULL) return true;

        if (!required.name().equals(given.name()) && !implementsTemplate(given.name(), required.name())) return false;

        if (!required.isNullable() && given.isNullable()) return false;
        if (required.getGenerics().size() != required.getGenerics().size()) return false;

        Iterator<TypeTree> requiredGen = required.getGenerics().iterator();
        Iterator<TypeTree> givenGen = given.getGenerics().iterator();
        while (requiredGen.hasNext())
            if (!typeMatches(requiredGen.next(), givenGen.next()))
                return false;

        return true;
    }

    private boolean implementsTemplate(String given, String templateName){
        Set<String> impls = this.impls.get(given);
        if (impls == null) return false;
        return impls.contains(templateName);
    }

    private static boolean isDefined(String name, Scope<TypeEntry> scope){
        return typeFinder.search(name, scope) != null;
    }



    private static class TypeFinder implements ScopeVisitor<String, TypeEntry, TypeEntry> {

        public TypeEntry search(String name, Scope<TypeEntry> scope){
            return scope.accept(this, name);
        }

        @Override
        public TypeEntry visitGlobalScope(GlobalScope<TypeEntry> globalScope, String name) {
            return globalScope.entries.get(name);
        }

        @Override
        public TypeEntry visitBlockScope(BlockScope<TypeEntry> blockScope, String name) {
            if (blockScope.entries.containsKey(name))
                return blockScope.entries.get(name);
            return blockScope.parent.accept(this, name);
        }

        @Override
        public TypeEntry visitFunctionScope(FunctionScope<TypeEntry> functionScope, String name) {
            return functionScope.entries.get(name);
        }

        @Override
        public TypeEntry visitStructScope(StructScope<TypeEntry> structScope, String name) {
            if (structScope.entries.containsKey(name))
                return structScope.entries.get(name);

            for (Scope<TypeEntry> implScope : structScope.impls) {
                TypeEntry entry = implScope.accept(this, name);
                if (entry != null) return entry;
            }
            return null;
        }
    }


    public record TypeEntry(String name, TypeTree type, boolean isMutable, List<TypeTree> parameters) implements Entry {
        public TypeEntry(String name, TypeTree type, boolean isMutable){
            this(name, type, isMutable, null);
        }

    }


    private record NullableType(TypeTree type) implements TypeTree {
        public Location getLocation() {
            return type.getLocation();
        }
        public String name() {
            return type.name();
        }
        public boolean isNullable() {
            return true;
        }
        public List<TypeTree> getGenerics() {
            return type.getGenerics();
        }
    }

}

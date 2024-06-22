package computil.generation;

import computil.generation.instructions.*;
import computil.generation.pool.Pool;
import computil.generation.targets.rbt.Opcode;
import computil.scope.*;
import computil.tree.*;
import computil.util.ArrayType;
import computil.util.Location;
import computil.util.TreeScanner;
import computil.util.TreeVisitor;

import java.util.*;

import static computil.util.PrimitiveType.*;

public class Generator extends TreeScanner<Scope<Generator.GenEntry>, TypeTree> {

    private static final EntryFinder finder = new EntryFinder();

    private final GlobalScope<GenEntry> globalScope = new GlobalScope<>();
    private final Map<String, StructScope<GenEntry>> types = new HashMap<>();
    private final Map<String, Map<String, TypeTree>> templates = new HashMap<>();

    private GenEntry thisEntry = null;

    private State state;


    public void generate(RootTree tree, Target target){
        Objects.requireNonNull(tree);
        Objects.requireNonNull(target);
        state = new State(new IRMediator());
        scan(tree, null);
        target.write(state.mediator);
    }

    private void newLine(Tree tree){
        Location location = tree.getLocation();
        if (location == null) return;
        if (location.line() > state.currLine)
            state.currentFunction.append(new NewLineNumber(location.line(), state.mediator));
        state.currLine = Math.max(state.currLine, location.line());
    }

    @Override
    public TypeTree visitRootTree(RootTree rootTree, Scope<GenEntry> scope) {
        genStructs(rootTree.getStructs());
        genTemplates(rootTree.getTemplates());

        registerImplementations(rootTree.getImpls());
        registerFunctions(rootTree.getCallables(), globalScope);

        scan(rootTree.getCallables(), globalScope);
        scan(rootTree.getImpls(), scope);
        return null;
    }

    private void genStructs(List<StructTree> structs){
        for (StructTree struct : structs)
            state.registerStruct(struct.getName());
        scan(structs, globalScope);
    }

    private void genTemplates(List<TemplateTree> templates){
        for (TemplateTree template : templates) {
            Map<String, TypeTree> methods = new HashMap<>();
            this.templates.put(template.getName(), methods);
            for (TemplateMethodTree method : template.getMethods())
                methods.put(method.getName(), method.getReturnType());
        }
    }

    private void registerImplementations(List<ImplementationTree> impls){
        for (ImplementationTree implTree : impls) {
            String type = implTree.getForTypeName();
            StructScope<GenEntry> targetStruct = types.get(type);
            List<CallableTree> methods = convertToMethods(type, implTree.getImplementations());
            registerFunctions(methods, targetStruct);
        }
    }

    private void registerFunctions(List<CallableTree> callables, Scope<GenEntry> scope){
        for (CallableTree callable : callables) {
            GenEntry.Kind kind = GenEntry.Kind.NATIVE_FUNCTION;
            if (!(callable instanceof NativeFunctionTree)) {
                state.registerFunction(callable.getName());
                kind = GenEntry.Kind.VIRTUAL_FUNCTION;
            }
            scope.add(new GenEntry(callable.getReturnType(), callable.getName(), kind, -1));
        }
    }

    @Override
    public TypeTree visitStructTree(StructTree structTree, Scope<GenEntry> scope) {
        StructScope<GenEntry> structScope = new StructScope<>();
        types.put(structTree.getName(), structScope);
        state.nextFreeAddress = 0;
        state.enterStruct(structTree.getName());
        scan(structTree.getFields(), structScope);
        return null;
    }

    @Override
    public TypeTree visitFieldTree(FieldTree fieldTree, Scope<GenEntry> struct) {
        int address = state.nextFreeAddress++;
        state.currentStruct.putField(fieldTree.getName());
        struct.add(new GenEntry(fieldTree.getType(), fieldTree.getName(), GenEntry.Kind.FIELD, address));
        return null;
    }

    @Override
    public TypeTree visitFunctionTree(FunctionTree functionTree, Scope<GenEntry> scope) {
        if (isMainFunction(functionTree, scope))
            state.mediator.setEntryPoint(state.pool.putVirtual(functionTree.getName()));
        state.enterFunction(functionTree.getName());
        state.nextFreeAddress = 0;
        state.currLine = -1;
        FunctionScope<GenEntry> functionScope = new FunctionScope<>(scope);

        int initStackSize = 0;
        if (thisEntry != null) initStackSize += 1;
        initStackSize += functionTree.getParameters().size();
        state.currentFunction.stackGrows(initStackSize);

        if (thisEntry != null) new Trees.BasicParameterTree(thisEntry.name, functionTree.getLocation()).accept(this, functionScope);
        scan(functionTree.getParameters(), functionScope);

        scan(functionTree.getBody(), functionScope);
        state.currentFunction.append(new PushNull(state.mediator));
        state.currentFunction.append(new Return(state.mediator));
        return null;
    }

    private boolean isMainFunction(FunctionTree functionTree, Scope<GenEntry> scope){
        return scope == globalScope && functionTree.getName().equals("main");
    }

    @Override
    public TypeTree visitNativeFunctionTree(NativeFunctionTree functionTree, Scope<GenEntry> scope) {
        state.pool.putNative(functionTree.getName());
        return null;
    }

    @Override
    public TypeTree visitParameterTree(ParameterTree parameterTree, Scope<GenEntry> scope) {
        int address = state.nextFreeAddress++;
        state.currentFunction.useLocal(address);
        state.currentFunction.putParameter(parameterTree.getName(), address);
        state.currentFunction.append(new StoreLocal(address, state.mediator));
        state.currentFunction.stackGrows(-1);
        scope.add(new GenEntry(parameterTree.getType(), parameterTree.getName(), GenEntry.Kind.PARAMETER, address));
        return null;
    }

    @Override
    public TypeTree visitImplementationTree(ImplementationTree implTree, Scope<GenEntry> genEntryScope) {
        String type = implTree.getForTypeName();
        StructScope<GenEntry> targetStruct = types.get(type);
        List<CallableTree> methods = convertToMethods(type, implTree.getImplementations());
        Trees.BasicTypeTree typeTree = new Trees.BasicTypeTree(type, null);

        thisEntry = new GenEntry(typeTree, implTree.getObjectName(), GenEntry.Kind.LOCAL, 0);
        Struct writer = state.getStruct(type);
        Iterator<CallableTree> itr = implTree.getImplementations().iterator();

        for (CallableTree method : methods){
            state.getFunction(method.getName()).markAsMethod();
            if (method instanceof FunctionTree)
                writer.putMethod(itr.next().getName(), state.pool.putVirtual(method.getName()));
            else
                writer.putMethod(itr.next().getName(), state.pool.putNative(method.getName()));

            scan(method, targetStruct);
        }

        thisEntry = null;
        return null;
    }

    private List<CallableTree> convertToMethods(String type, List<CallableTree> callables){
        List<CallableTree> methods = new ArrayList<>();
        for (CallableTree impl : callables){
            if (impl instanceof NativeFunctionTree n){
                methods.add(new NativeMethod(type, n));
            }
            else {
                FunctionTree f = (FunctionTree) impl;
                methods.add(new VirtualMethod(type, f));
            }
        }
        return methods;
    }

    @Override
    public TypeTree visitNullTree(NullTree nullTree, Scope<GenEntry> scope) {
        state.currentFunction.append(new PushNull(state.mediator));
        state.currentFunction.stackGrows(1);
        return NULL;
    }

    @Override
    public TypeTree visitIntegerTree(IntegerTree integerTree, Scope<GenEntry> scope) {
        int value = integerTree.get();
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE){
            int poolAddress = state.pool.putInteger(value);
            state.currentFunction.append(new LoadConst(poolAddress, state.mediator));
        }
        else {
            state.currentFunction.append(new PushInt(value, state.mediator));
        }
        state.currentFunction.stackGrows(1);
        return INTEGER;
    }

    @Override
    public TypeTree visitFloatTree(FloatTree floatTree, Scope<GenEntry> scope) {
        int poolAddress = state.pool.putFloat(floatTree.get());
        state.currentFunction.append(new LoadConst(poolAddress, state.mediator));
        state.currentFunction.stackGrows(1);
        return FLOAT;
    }

    @Override
    public TypeTree visitBooleanTree(BooleanTree booleanTree, Scope<GenEntry> scope) {
        state.currentFunction.append(new PushBool(booleanTree.get(), state.mediator));
        state.currentFunction.stackGrows(1);
        return BOOL;
    }

    @Override
    public TypeTree visitStringTree(StringTree stringTree, Scope<GenEntry> scope) {
        int poolAddress = state.pool.putString(stringTree.get());
        state.currentFunction.append(new LoadConst(poolAddress, state.mediator));
        state.currentFunction.stackGrows(1);
        return STRING;
    }

    @Override
    public TypeTree visitCastTree(CastTree castTree, Scope<GenEntry> scope) {
        TypeTree from = scan(castTree.getOperand(), scope);
        TypeTree to = castTree.getCastType();

        if (isType(from, "int") && isType(to, "float")){
            state.currentFunction.append(new Int2Float(state.mediator));
            return to;
        }
        else if (isType(from, "float") && isType(to, "int")){
            state.currentFunction.append(new Float2Int(state.mediator));
            return to;
        }
        else if ((isType(from, "int") && isType(to, "int"))
                ||(isType(from, "float") && isType(to, "float"))){
            return to;
        }

        newLine(castTree);
        int poolAddress = state.pool.putStruct(to.name());
        state.currentFunction.append(new CheckCast(poolAddress, state.mediator));
        return to;
    }

    private boolean isType(TypeTree typeTree, String name){
        return typeTree.name().equals(name);
    }

    @Override
    public TypeTree visitNullCheckTree(NullCheckTree nullCheckTree, Scope<GenEntry> scope) {
        TypeTree type = scan(nullCheckTree.getOperand(), scope);
        newLine(nullCheckTree);
        state.currentFunction.append(new NullCheck(state.mediator));
        return type;
    }

    @Override
    public TypeTree visitNotTree(NotTree notTree, Scope<GenEntry> scope) {
        scan(notTree.getOperand(), scope);
        state.currentFunction.append(new Not(state.mediator));
        return BOOL;
    }

    @Override
    public TypeTree visitNegationTree(NegationTree negationTree, Scope<GenEntry> scope) {
        TypeTree type = scan(negationTree.getOperand(), scope);
        state.currentFunction.append(new Negate(state.mediator));
        return type;
    }

    @Override
    public TypeTree visitArrayTree(ArrayTree arrayTree, Scope<GenEntry> scope) {
        List<ExpressionTree> content = arrayTree.getContent();
        TypeTree type = scan(content, scope);
        state.currentFunction.append(new MakeArray(content.size(), state.mediator));
        state.currentFunction.stackGrows(-content.size());
        return new ArrayType(type);
    }

    @Override
    public TypeTree visitVarDecTree(VarDecTree varDecTree, Scope<GenEntry> scope) {
        TypeTree alternative = scan(varDecTree.getInitializer(), scope);
        int address = state.nextFreeAddress++;
        state.currentFunction.useLocal(address);
        TypeTree type = varDecTree.getType() != null ? varDecTree.getType() : alternative;
        scope.add(new GenEntry(type, varDecTree.getName(), GenEntry.Kind.LOCAL, address));
        state.currentFunction.append(new StoreLocal(address, state.mediator));
        state.currentFunction.stackGrows(-1);
        return null;
    }

    @Override
    public TypeTree visitReturnTree(ReturnTree returnTree, Scope<GenEntry> genEntryScope) {
        scan(returnTree.getExpression(), genEntryScope);
        state.currentFunction.append(new Return(state.mediator));
        state.currentFunction.stackGrows(-1);
        return null;
    }

    @Override
    public TypeTree visitIfElseTree(IfElseTree ifElseTree, Scope<GenEntry> scope) {
        scan(ifElseTree.getCondition(), scope);

        BranchIfFalse branchIf = new BranchIfFalse(0, state.mediator);
        state.currentFunction.append(branchIf);
        state.currentFunction.stackGrows(-1);

        scan(ifElseTree.getIfBody(), scope);

        if (ifElseTree.getElseBody() == null){
            int jmpAddr = state.currentFunction.getStreamSize();
            branchIf.setJumpAddress(jmpAddr);
        }
        else {
            Goto goto_ = new Goto(0, state.mediator);
            state.currentFunction.append(goto_);
            int ifEndIndex = state.currentFunction.getStreamSize();

            scan(ifElseTree.getElseBody(), scope);
            int elseEndIndex = state.currentFunction.getStreamSize();
            branchIf.setJumpAddress(ifEndIndex);
            goto_.setJumpAddress(elseEndIndex);
        }

        return null;
    }

    @Override
    public TypeTree visitDoWhileTree(DoWhileTree doWhileTree, Scope<GenEntry> scope) {
        int to = state.currentFunction.getStreamSize();
        scan(doWhileTree.getBody(), scope);
        scan(doWhileTree.getCondition(), scope);
        state.currentFunction.append(new BranchIfTrue(to, state.mediator));
        state.currentFunction.stackGrows(-1);
        return null;
    }

    @Override
    public TypeTree visitWhileDoTree(WhileDoTree whileDoTree, Scope<GenEntry> scope) {
        Trees.BasicDoWhileTree doWhileTree = new Trees.BasicDoWhileTree(whileDoTree.getLocation());
        doWhileTree.body = whileDoTree.getBody();
        doWhileTree.condition = whileDoTree.getCondition();

        Trees.BasicIfElseTree ifElseTree = new Trees.BasicIfElseTree(whileDoTree.getLocation());
        ifElseTree.condition = whileDoTree.getCondition();
        ifElseTree.ifBody = doWhileTree;

        scan(ifElseTree, scope);
        return null;
    }

    @Override
    public TypeTree visitFreeTree(FreeTree freeTree, Scope<GenEntry> scope) {
        scan(freeTree.getExpression(), scope);
        state.currentFunction.append(new Free(state.mediator));
        return null;
    }

    @Override
    public TypeTree visitCallGlobalTree(CallGlobalTree callGlobalTree, Scope<GenEntry> genEntryScope) {
        List<ExpressionTree> args = callGlobalTree.getArguments();
        genArguments(args, genEntryScope);
        String name = callGlobalTree.getName();
        newLine(callGlobalTree);
        return genCall(name, args.size());
    }

    private TypeTree genCall(String name, int argc){
        GenEntry entry = globalScope.entries.get(name);
        GenEntry.Kind kind = entry.kind;
        switch (kind){
            case NATIVE_FUNCTION -> callNative(name, argc);
            case VIRTUAL_FUNCTION -> callVirtual(name, argc);
            default -> throw new AssertionError();
        }
        state.currentFunction.stackGrows(-argc);
        return entry.type;
    }

    private void callVirtual(String name, int argc){
        int address = state.pool.putVirtual(name);
        state.currentFunction.append(new InvokeVirtual(address, argc, state.mediator));
    }

    private void callNative(String name, int argc){
        int address = state.pool.putNative(name);
        state.currentFunction.append(new InvokeNative(address, argc, state.mediator));
    }

    @Override
    public TypeTree visitCallMethodTree(CallMethodTree callMethodTree, Scope<GenEntry> genEntryScope) {
        String name = callMethodTree.getMethodName();
        List<ExpressionTree> args = callMethodTree.getArguments();

        genArguments(args, genEntryScope);
        TypeTree type = scan(callMethodTree.getOperand(), genEntryScope);

        if (templates.containsKey(type.name())){
            state.currentFunction.append(new Dup(state.mediator));
            int utf8Address = state.pool.putUTF8(name);
            int argc = args.size()+1;
            newLine(callMethodTree);
            state.currentFunction.append(new InvokeTemplate(utf8Address, argc, state.mediator));
            state.currentFunction.stackGrows(-argc);
            return templates.get(type.name()).get(name);
        }
        else {
            StructScope<GenEntry> structScope = types.get(type.name());
            String fullName = type.name() + "$" + name;
            GenEntry entry = structScope.entries.get(fullName);
            if (entry.kind == GenEntry.Kind.VIRTUAL_FUNCTION) {
                newLine(callMethodTree);
                callVirtual(fullName, args.size()+1);
            }
            else
                callNative(name, args.size()+1);

            return entry.type;
        }

    }

    private void genArguments(List<ExpressionTree> args, Scope<GenEntry> scope){
        for(int i = args.size()-1; i >= 0; i--){
            ExpressionTree arg = args.get(i);
            scan(arg, scope);
        }
    }

    @Override
    public TypeTree visitFieldAccessTree(FieldAccessTree accessTree, Scope<GenEntry> genEntryScope) {
        TypeTree type = scan(accessTree.getOperand(), genEntryScope);
        StructScope<GenEntry> scope = types.get(type.name());
        GenEntry entry = scope.entries.get(accessTree.getFieldName());
        state.currentFunction.append(new GetField(entry.address, state.mediator));
        state.currentFunction.stackGrows(1);
        return entry.type;
    }

    @Override
    public TypeTree visitBlockTree(BlockTree blockTree, Scope<GenEntry> scope) {
        scan(blockTree.getStatements(), scope);
        return null;
    }

    @Override
    public TypeTree visitExpressionStatementTree(ExpressionStatementTree statementTree, Scope<GenEntry> scope) {
        ExpressionTree exp = statementTree.getExpression();
        scan(exp, scope);
        if (exp instanceof CallGlobalTree || exp instanceof CallMethodTree) {
            state.currentFunction.append(new Pop(state.mediator));
            state.currentFunction.stackGrows(-1);
        }

        return null;
    }

    @Override
    public TypeTree visitVariableTree(VariableTree variableTree, Scope<GenEntry> scope) {
        state.currentFunction.stackGrows(1);
        if (thisEntry != null) {
            state.currentFunction.useLocal(0);
            state.currentFunction.append(new LoadLocal(0, state.mediator));
            return thisEntry.type;
        }
        GenEntry entry = finder.search(variableTree.getName(), scope);
        state.currentFunction.useLocal(entry.address);
        state.currentFunction.append(new LoadLocal(entry.address, state.mediator));
        return entry.type;
    }

    @Override
    public TypeTree visitContainerAccessTree(ContainerAccessTree accessTree, Scope<GenEntry> scope) {
        TypeTree type = scan(accessTree.getOperand(), scope);
        newLine(accessTree);
        int index = ((IntegerTree) accessTree.getKey()).get();
        state.currentFunction.append(new ReadArray(index, state.mediator));
        state.currentFunction.stackGrows(1);
        return type.getGenerics().get(0);
    }

    @Override
    public TypeTree visitAssignTree(AssignTree assignTree, Scope<GenEntry> scope) {
        scan(assignTree.getRightOperand(), scope);

        ExpressionTree assigned = assignTree.getLeftOperand();

        state.currentFunction.stackGrows(-1);

        if (assigned instanceof VariableTree v)
            return genWriteLocal(v, scope);

        else if (assigned instanceof ContainerAccessTree a)
            return genContainerWrite(a, scope);

        else if (assigned instanceof FieldAccessTree f)
            return genWriteField(f, scope);

        else
            throw new AssertionError();

    }

    private TypeTree genWriteLocal(VariableTree variableTree, Scope<GenEntry> scope){
        GenEntry entry = finder.search(variableTree.getName(), scope);
        state.currentFunction.useLocal(entry.address);
        state.currentFunction.append(new StoreLocal(entry.address, state.mediator));
        return entry.type;
    }

    private TypeTree genContainerWrite(ContainerAccessTree accessTree, Scope<GenEntry> scope){
        TypeTree type = scan(accessTree.getOperand(), scope);
        newLine(accessTree);
        int index = ((IntegerTree) accessTree.getKey()).get();
        state.currentFunction.append(new WriteArray(index, state.mediator));
        state.currentFunction.stackGrows(-1);
        return type.getGenerics().get(0);
    }

    private TypeTree genWriteField(FieldAccessTree accessTree, Scope<GenEntry> genEntryScope){
        TypeTree type = scan(accessTree.getOperand(), genEntryScope);
        StructScope<GenEntry> scope = types.get(type.name());
        GenEntry entry = scope.entries.get(accessTree.getFieldName());
        state.currentFunction.append(new PutField(entry.address, state.mediator));
        state.currentFunction.stackGrows(-1);
        return entry.type;
    }

    @Override
    public TypeTree visitStructInitTree(StructInitTree structInitTree, Scope<GenEntry> scope) {
        TypeTree type = scan(structInitTree.getType(), scope);

        int poolAddress = state.pool.putStruct(type.name());
        state.currentFunction.append(new NewInstance(poolAddress, state.mediator));
        state.currentFunction.stackGrows(1);

        int fieldIndex = 0;
        for (ExpressionTree arg : structInitTree.getArguments()){
            state.currentFunction.append(new Dup(state.mediator));
            state.currentFunction.stackGrows(1);
            scan(arg, scope);
            state.currentFunction.append(new Swap(state.mediator));
            state.currentFunction.append(new PutField(fieldIndex++, state.mediator));
            state.currentFunction.stackGrows(-1);
        }
        return type;
    }

    @Override
    public TypeTree visitBinaryOperationTree(BinaryOperationTree operationTree, Scope<GenEntry> scope) {
        int maybeCastIndex = state.currentFunction.getStreamSize();
        TypeTree left = scan(operationTree.getLeftOperand(), scope);
        TypeTree right = scan(operationTree.getRightOperand(), scope);
        Operation op = operationTree.getOperation();

        if (requireImplicitCast(left, right, op))
            state.currentFunction.insert(maybeCastIndex+1, new Int2Float(state.mediator));

        if (requireImplicitCast(right, left, op))
            state.currentFunction.append(new Int2Float(state.mediator));

        TypeTree result = operationResultType(left, right, op);
        state.currentFunction.append(new BinaryOperation(op, result.name(), state.mediator));
        return result;
    }

    private boolean requireImplicitCast(TypeTree candidate, TypeTree dependentType, Operation op){
        if (!candidate.name().equals("int")) return false;
        return switch (op){
            case ADD, SUB, MUL -> dependentType.name().equals("float");
            default -> false;
        };
    }

    private TypeTree operationResultType(TypeTree left, TypeTree right, Operation op){
        return switch (op){
            case ADD, SUB, MUL, AND_BIT, OR_BIT, XOR, SHIFT_AL, SHIFT_AR
                    -> left.name().equals("float") || right.name().equals("float") ? FLOAT : INTEGER;
            case DIV -> FLOAT;
            case IDIV, MOD -> INTEGER;
            case AND, OR, LESS, GREATER, GREATER_EQ, LESS_EQ, EQUALS, NOT_EQUALS -> BOOL;
        };
    }

    @Override
    public TypeTree visitTypeTree(TypeTree typeTree, Scope<GenEntry> scope) {
        return typeTree;
    }


    private static class State {
        private final IRMediator mediator;
        public int nextFreeAddress = 0;
        public int currLine = -1;
        private Function currentFunction;
        private Struct currentStruct;
        private final Pool pool;
        private State(IRMediator mediator) {
            this.mediator = mediator;
            pool = new Pool(mediator);
        }
        public void enterFunction(String name){
            currentFunction = mediator.getFunction(name);
        }
        public void registerFunction(String name) {
            mediator.registerFunction(name);
        }
        public Function getFunction(String name){
            return mediator.getFunction(name);
        }
        public void enterStruct(String name){
            currentStruct = mediator.getStruct(name);
        }
        public void registerStruct(String name) {
            mediator.registerStruct(name);
        }
        public Struct getStruct(String name) {
            return mediator.getStruct(name);
        }
    }

    public record GenEntry(TypeTree type, String name, Kind kind, int address) implements Entry {
        private enum Kind { LOCAL, FIELD, PARAMETER, VIRTUAL_FUNCTION, NATIVE_FUNCTION}
        @Override
        public String name() {
            return name;
        }
    }

    private record NativeMethod(String type, NativeFunctionTree tree) implements NativeFunctionTree {
        public String getName() {
            return type + "$" + tree.getName();
        }
        public TypeTree getReturnType() {
            return tree.getReturnType();
        }
        public List<ParameterTree> getParameters() {
            return tree.getParameters();
        }

        public Location getLocation() {
            return tree.getLocation();
        }

        public <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
            return visitor.visitNativeFunctionTree(this, p);
        }
    }

    private record VirtualMethod(String type, FunctionTree tree) implements FunctionTree {
        public String getName() {
            return type + "$" + tree.getName();
        }
        public TypeTree getReturnType() {
            return tree.getReturnType();
        }
        public BlockTree getBody() {
            return tree.getBody();
        }
        public List<ParameterTree> getParameters() {
            return tree.getParameters();
        }
        public Location getLocation() {
            return tree.getLocation();
        }

        public <P, R> R accept(TreeVisitor<P, R> visitor, P p) {
            return visitor.visitFunctionTree(this, p);
        }
    }

    private static class EntryFinder implements ScopeVisitor<String, GenEntry, GenEntry> {
        public GenEntry search(String name, Scope<GenEntry> scope){
            return scope.accept(this, name);
        }
        public GenEntry visitGlobalScope(GlobalScope<GenEntry> globalScope, String string) {
            return null;
        }
        public GenEntry visitBlockScope(BlockScope<GenEntry> blockScope, String string) {
            if (blockScope.entries.containsKey(string))
                return blockScope.entries.get(string);
            return blockScope.parent.accept(this, string);
        }
        public GenEntry visitFunctionScope(FunctionScope<GenEntry> functionScope, String string) {
            return functionScope.entries.get(string);
        }
        public GenEntry visitStructScope(StructScope<GenEntry> structScope, String string) {
            return null;
        }
    }

}

package computil.check;

import computil.diags.Error;
import computil.scope.BlockScope;
import computil.scope.Entry;
import computil.scope.FunctionScope;
import computil.scope.Scope;
import computil.tree.*;
import computil.util.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DanglingChecker extends Checker<Scope<DanglingChecker.DangEntry>, DanglingChecker.DangEntry> {

    private final Map<String, List<String>> structMembers = new HashMap<>();

    @Override
    public DangEntry visitRootTree(RootTree rootTree, Scope<DangEntry> scope) {
        scan(rootTree.getStructs(), null);
        scan(rootTree.getCallables(), null);
        return null;
    }

    @Override
    public DangEntry visitStructTree(StructTree structTree, Scope<DangEntry> scope) {
        List<String> members = new ArrayList<>();
        for (FieldTree field : structTree.getFields())
            members.add(field.getName());
        structMembers.put(structTree.getName(), members);
        return null;
    }

    @Override
    public DangEntry visitFunctionTree(FunctionTree functionTree, Scope<DangEntry> scope) {
        return super.visitFunctionTree(functionTree, new FunctionScope<>(null));
    }

    @Override
    public DangEntry visitBlockTree(BlockTree blockTree, Scope<DangEntry> scope) {
        BlockScope<DangEntry> blockScope = new BlockScope<>(scope);
        super.visitBlockTree(blockTree, blockScope);
        for (DangEntry entry : blockScope.entries.values())
            if (entry.ref.state == State.ALIVE && entry.ref.refCount == 1)
                error(new Error("unreleased reference leads to memory leak", entry.ref.lastLocation));
        return null;
    }

    @Override
    public DangEntry visitVarDecTree(VarDecTree varDecTree, Scope<DangEntry> scope) {
        DangEntry entry = scanAndCheckDangling(varDecTree.getInitializer(), scope);

        if (entry == null) {
            scope.add(new DangEntry(varDecTree.getName(), new Reference(State.IMMUTABLE, 1, varDecTree.getLocation())));
        }
        else {
            scope.add(new DangEntry(varDecTree.getName(), entry.ref));
            entry.ref.refCount++;
        }

        return null;
    }

    @Override
    public DangEntry visitParameterTree(ParameterTree parameterTree, Scope<DangEntry> scope) {
        if (scope == null) return null;
        scope.add(new DangEntry(parameterTree.getName(), new Reference(State.IMMUTABLE, 1, parameterTree.getLocation())));
        return null;
    }

    @Override
    public DangEntry visitAssignTree(AssignTree assignTree, Scope<DangEntry> scope) {
        DangEntry left = scan(assignTree.getLeftOperand(), scope);

        left.ref.refCount--;
        if (left.ref.state == State.ALIVE && left.ref.refCount == 0)
            error(new Error("unreleased reference leads to memory leak", left.ref.lastLocation));

        DangEntry right = scanAndCheckDangling(assignTree.getRightOperand(), scope);
        if (right != null)
            left.ref = right.ref;
        left.ref.refCount++;
        left.ref.state = State.ALIVE;
        left.ref.lastLocation = assignTree.getLocation();
        return left;
    }

    @Override
    public DangEntry visitFreeTree(FreeTree freeTree, Scope<DangEntry> scope) {
        ExpressionTree exp = freeTree.getExpression();
        DangEntry entry = scanAndCheckDangling(exp, scope);

        if (entry.ref.state == State.DEAD)
            error(new Error("can not release dangling reference", freeTree.getLocation()));
        else if (entry.ref.state == State.IMMUTABLE)
            error(new Error("can not release reference from outer function", freeTree.getLocation()));

        entry.ref.state = State.DEAD;

        return null;
    }

    @Override
    public DangEntry visitReturnTree(ReturnTree returnTree, Scope<DangEntry> scope) {
        DangEntry entry = scanAndCheckDangling(returnTree.getExpression(), scope);
        entry.ref.state = State.IMMUTABLE;

        if (hasDeepDangler(entry.ref))
            error(new Error("return value contains dangling reference", returnTree.getLocation()));

        for (DangEntry dangEntry : scope)
            if (dangEntry.ref.state == State.ALIVE && dangEntry.ref.refCount == 1)
                error(new Error("unreleased reference leads to memory leak", dangEntry.ref.lastLocation));

        return null;
    }

    @Override
    public DangEntry visitVariableTree(VariableTree variableTree, Scope<DangEntry> scope) {
        for (DangEntry entry : scope) {
            if (entry.name.equals(variableTree.getName())) {
                return entry;
            }
        }
        throw new AssertionError();
    }

    @Override
    public DangEntry visitCallGlobalTree(CallGlobalTree callGlobalTree, Scope<DangEntry> scope) {
        scanAndCheckDanglingArguments(callGlobalTree.getArguments(), scope);
        return new DangEntry(null, new Reference(State.ALIVE, 0, callGlobalTree.getLocation()));
    }

    @Override
    public DangEntry visitCallMethodTree(CallMethodTree callMethodTree, Scope<DangEntry> scope) {
        scanAndCheckDangling(callMethodTree.getOperand(), scope);
        scanAndCheckDanglingArguments(callMethodTree.getArguments(), scope);
        return new DangEntry(null, new Reference(State.ALIVE, 0, callMethodTree.getLocation()));
    }

    @Override
    public DangEntry visitStructInitTree(StructInitTree structInitTree, Scope<DangEntry> scope) {
        scanAndCheckDanglingArguments(structInitTree.getArguments(), scope);
        Reference ref = createReferenceFromType(structInitTree.getType(), structInitTree.getLocation());
        return new DangEntry(null, ref);
    }

    @Override
    public DangEntry visitFieldAccessTree(FieldAccessTree accessTree, Scope<DangEntry> scope) {
        DangEntry entry = scanAndCheckDangling(accessTree.getOperand(), scope);
        if (entry == null) return null;
        if (!entry.ref.members.containsKey(accessTree.getFieldName()))
            entry.ref.members.put(accessTree.getFieldName(), new Reference(State.ALIVE, 1, accessTree.getLocation()));
        return new DangEntry(null, entry.ref.members.get(accessTree.getFieldName()));
    }

    private Reference createReferenceFromType(TypeTree type, Location location){
        Reference reference = new Reference(State.ALIVE, 0, location);
        List<String> members = structMembers.get(type.name());
        for (String member : members)
            reference.members.put(member, new Reference(State.ALIVE, 1, location));
        return reference;
    }

    private DangEntry scanAndCheckDangling(ExpressionTree exp, Scope<DangEntry> scope){
        DangEntry entry = scan(exp, scope);
        if (entry != null && entry.ref.state == State.DEAD)
            error(new Error("dangling reference", exp.getLocation()));
        return entry;
    }

    private void scanAndCheckDanglingArguments(List<ExpressionTree> args, Scope<DangEntry> scope){
        for (ExpressionTree arg : args)
            scanAndCheckDangling(arg, scope);
    }

    private boolean hasDeepDangler(Reference ref){
        if (ref == null) return false;
        if (ref.state == State.DEAD) return true;
        for (Reference member : ref.members.values())
            if (hasDeepDangler(member))
                return true;
        return false;
    }

    public static class DangEntry implements Entry {
        private final String name;
        private Reference ref;
        private DangEntry(String name, Reference ref) {
            this.name = name;
            this.ref = ref;
        }
        public String name() {
            return name;
        }
    }

    private enum State {
        ALIVE,
        DEAD,
        IMMUTABLE
    }

    private static class Reference {
        public Reference(State state, int refCount, Location location){
            this.state = state;
            this.refCount = refCount;
            this.lastLocation = location;
        }
        private State state;
        private int refCount;
        private Location lastLocation;
        private final Map<String, Reference> members = new HashMap<>();
    }

}

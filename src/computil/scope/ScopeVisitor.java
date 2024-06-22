package computil.scope;

public interface ScopeVisitor<P, R, E extends Entry> {

    R visitGlobalScope(GlobalScope<E> globalScope, P p);

    R visitBlockScope(BlockScope<E> blockScope, P p);

    R visitFunctionScope(FunctionScope<E> functionScope, P p);

    R visitStructScope(StructScope<E> structScope, P p);

}

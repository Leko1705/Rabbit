package computil.scope;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FunctionScope<E extends Entry> implements Scope<E> {

    public final Scope<E> parent;
    public final Map<String, E> entries = new HashMap<>();

    public FunctionScope(Scope<E> parent) {
        this.parent = parent;
    }

    @Override
    public void add(E entry) {
        entries.put(entry.name(), entry);
    }

    @Override
    public <P, R> R accept(ScopeVisitor<P, R, E> visitor, P p) {
        return visitor.visitFunctionScope(this, p);
    }

    @Override
    public Iterator<E> iterator() {
        return entries.values().iterator();
    }
}

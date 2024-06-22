package computil.scope;

import java.util.*;

public class StructScope<E extends Entry> implements Scope<E> {

    public final List<Scope<E>> impls = new ArrayList<>();
    public final Map<String, E> entries = new HashMap<>();
    private final List<E> ordered = new ArrayList<>();

    @Override
    public void add(E entry) {
        entries.put(entry.name(), entry);
        ordered.add(entry);
    }

    @Override
    public <P, R> R accept(ScopeVisitor<P, R, E> visitor, P p) {
        return visitor.visitStructScope(this, p);
    }

    public List<E> getOrdered(){
        return ordered;
    }

    @Override
    public Iterator<E> iterator() {
        return ordered.iterator();
    }
}

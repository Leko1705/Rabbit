package computil.scope;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BlockScope<E extends Entry> implements Scope<E> {

    public final Scope<E> parent;
    public final Map<String, E> entries = new HashMap<>();

    public BlockScope(Scope<E> parent) {
        this.parent = parent;
    }

    @Override
    public void add(E entry) {
        entries.put(entry.name(), entry);
    }

    @Override
    public <P, R> R accept(ScopeVisitor<P, R, E> visitor, P p) {
        return visitor.visitBlockScope(this, p);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private final Iterator<E> parentItr = parent.iterator();
            private final Iterator<E> thisItr = entries.values().iterator();
            @Override
            public boolean hasNext() {
                return parentItr.hasNext() || thisItr.hasNext();
            }

            @Override
            public E next() {
                if (parentItr.hasNext())
                    return parentItr.next();
                return thisItr.next();
            }
        };
    }
}

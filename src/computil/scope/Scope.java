package computil.scope;

public interface Scope<E extends Entry> extends Iterable<E> {

    void add(E entry);

    <P, R> R accept(ScopeVisitor<P, R, E> visitor, P p);

}

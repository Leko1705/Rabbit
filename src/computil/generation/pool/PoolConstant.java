package computil.generation.pool;

import computil.generation.BasicIRComponent;
import computil.generation.IRMediator;

import java.util.Objects;

public abstract class PoolConstant<T> extends BasicIRComponent {

    private final T value;

    public PoolConstant(T value, IRMediator mediator){
        super(mediator);
        this.value = value;
    }

    public abstract PoolTag getTag();

    public abstract void accept(PoolConstantVisitor visitor);

    public T get(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoolConstant<?> poolConstant = (PoolConstant<?>) o;
        return Objects.equals(value, poolConstant.value)
                && Objects.equals(getTag(), poolConstant.getTag());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

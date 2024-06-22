package computil.generation.pool;

import computil.generation.IRMediator;
import computil.generation.pool.PoolConstant;
import computil.generation.pool.PoolTag;

public class IntegerConstant extends PoolConstant<Integer> {

    public IntegerConstant(Integer value, IRMediator mediator) {
        super(value, mediator);
    }

    @Override
    public PoolTag getTag() {
        return PoolTag.INTEGER;
    }

    @Override
    public void accept(PoolConstantVisitor visitor) {
        visitor.visitIntegerConstant(this);
    }
}

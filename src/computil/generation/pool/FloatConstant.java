package computil.generation.pool;

import computil.generation.IRMediator;
import computil.generation.pool.PoolConstant;
import computil.generation.pool.PoolTag;

public class FloatConstant extends PoolConstant<Float> {

    public FloatConstant(Float value, IRMediator mediator) {
        super(value, mediator);
    }

    @Override
    public PoolTag getTag() {
        return PoolTag.FLOAT;
    }

    @Override
    public void accept(PoolConstantVisitor visitor) {
        visitor.visitFloatConstant(this);
    }
}

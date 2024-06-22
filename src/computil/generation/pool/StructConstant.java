package computil.generation.pool;

import computil.generation.IRMediator;
import computil.generation.pool.PoolConstant;
import computil.generation.pool.PoolTag;

public class StructConstant extends PoolConstant<String> {
    public StructConstant(String value, IRMediator mediator) {
        super(value, mediator);
    }

    @Override
    public PoolTag getTag() {
        return PoolTag.STRUCT;
    }

    @Override
    public void accept(PoolConstantVisitor visitor) {
        visitor.visitStructConstant(this);
    }
}

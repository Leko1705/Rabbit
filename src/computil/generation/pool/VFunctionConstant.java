package computil.generation.pool;

import computil.generation.IRMediator;
import computil.generation.pool.PoolConstant;
import computil.generation.pool.PoolTag;

public class VFunctionConstant extends PoolConstant<String> {
    public VFunctionConstant(String value, IRMediator mediator) {
        super(value, mediator);
    }

    @Override
    public PoolTag getTag() {
        return PoolTag.VIRTUAL_FUNCTION;
    }

    @Override
    public void accept(PoolConstantVisitor visitor) {
        visitor.visitVFunctionConstant(this);
    }
}

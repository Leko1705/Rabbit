package computil.generation.pool;

import computil.generation.IRMediator;
import computil.generation.pool.PoolConstant;
import computil.generation.pool.PoolTag;

public class NFunctionConstant extends PoolConstant<String> {
    public NFunctionConstant(String value, IRMediator mediator) {
        super(value, mediator);
    }

    @Override
    public PoolTag getTag() {
        return PoolTag.NATIVE_FUNCTION;
    }

    @Override
    public void accept(PoolConstantVisitor visitor) {
        visitor.visitNFunctionConstant(this);
    }
}

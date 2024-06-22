package computil.generation.pool;

import computil.generation.IRMediator;
import computil.generation.pool.PoolConstant;
import computil.generation.pool.PoolTag;

public class UTF8Constant extends PoolConstant<String> {
    public UTF8Constant(String value, IRMediator mediator) {
        super(value, mediator);
    }

    @Override
    public PoolTag getTag() {
        return PoolTag.UTF8;
    }

    @Override
    public void accept(PoolConstantVisitor visitor) {
        visitor.visitUTF8Constant(this);
    }
}

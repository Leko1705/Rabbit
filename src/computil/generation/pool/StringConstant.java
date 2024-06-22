package computil.generation.pool;

import computil.generation.IRMediator;
import computil.generation.pool.PoolConstant;
import computil.generation.pool.PoolTag;

public class StringConstant extends PoolConstant<String> {
    public StringConstant(String value, IRMediator mediator) {
        super(value, mediator);
    }

    @Override
    public PoolTag getTag() {
        return PoolTag.STRING;
    }

    @Override
    public void accept(PoolConstantVisitor visitor) {
        visitor.visitStringConstant(this);
    }
}

package computil.generation.instructions;

import computil.generation.IRMediator;

public class Int2Float extends Instruction {
    public Int2Float(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitInt2Float(this);
    }
}

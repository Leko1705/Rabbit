package computil.generation.instructions;

import computil.generation.IRMediator;

public class NullCheck extends Instruction {
    public NullCheck(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitNullCheck(this);
    }
}

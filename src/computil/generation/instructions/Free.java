package computil.generation.instructions;

import computil.generation.IRMediator;

public class Free extends Instruction {

    public Free(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitFree(this);
    }
}

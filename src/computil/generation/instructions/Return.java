package computil.generation.instructions;

import computil.generation.IRMediator;

public class Return extends Instruction {
    public Return(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitReturn(this);
    }
}

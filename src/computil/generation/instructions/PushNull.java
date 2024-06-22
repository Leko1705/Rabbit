package computil.generation.instructions;

import computil.generation.IRMediator;

public class PushNull extends Instruction {
    public PushNull(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitPushNull(this);
    }
}

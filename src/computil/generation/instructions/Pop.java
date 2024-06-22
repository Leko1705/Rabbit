package computil.generation.instructions;

import computil.generation.IRMediator;

public class Pop extends Instruction {
    public Pop(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitPop(this);
    }
}

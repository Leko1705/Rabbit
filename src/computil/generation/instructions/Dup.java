package computil.generation.instructions;

import computil.generation.IRMediator;

public class Dup extends Instruction {
    public Dup(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitDup(this);
    }
}

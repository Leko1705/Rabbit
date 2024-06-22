package computil.generation.instructions;

import computil.generation.IRMediator;

public class Negate extends Instruction {

    public Negate(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitNegate(this);
    }
}

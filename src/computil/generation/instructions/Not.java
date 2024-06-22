package computil.generation.instructions;

import computil.generation.IRMediator;

public class Not extends Instruction {
    public Not(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitNot(this);
    }
}

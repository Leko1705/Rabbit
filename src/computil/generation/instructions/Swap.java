package computil.generation.instructions;

import computil.generation.IRMediator;

public class Swap extends Instruction {

    public Swap(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitSwap(this);
    }
}

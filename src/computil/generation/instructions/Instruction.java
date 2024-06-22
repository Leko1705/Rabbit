package computil.generation.instructions;

import computil.generation.BasicIRComponent;
import computil.generation.IRMediator;

public abstract class Instruction extends BasicIRComponent {
    protected Instruction(IRMediator mediator) {
        super(mediator);
    }

    public abstract void accept(InstructionVisitor visitor);
}

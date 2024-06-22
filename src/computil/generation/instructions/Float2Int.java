package computil.generation.instructions;

import computil.generation.IRMediator;

public class Float2Int extends Instruction {
    public Float2Int(IRMediator mediator) {
        super(mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitFloat2Int(this);
    }
}

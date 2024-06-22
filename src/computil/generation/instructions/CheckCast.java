package computil.generation.instructions;

import computil.generation.IRMediator;

public class CheckCast extends AddressedBasedInstruction {

    public CheckCast(int address, IRMediator mediator) {
        super(address, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitCheckCast(this);
    }
}

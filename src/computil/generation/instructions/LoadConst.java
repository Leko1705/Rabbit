package computil.generation.instructions;

import computil.generation.IRMediator;

public class LoadConst extends AddressedBasedInstruction {

    public LoadConst(int address, IRMediator mediator) {
        super(address, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitLoadConst(this);
    }
}

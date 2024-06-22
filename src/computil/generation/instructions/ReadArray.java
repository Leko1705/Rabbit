package computil.generation.instructions;

import computil.generation.IRMediator;

public class ReadArray extends AddressedBasedInstruction {
    public ReadArray(int address, IRMediator mediator) {
        super(address, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitReadArray(this);
    }
}

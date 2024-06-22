package computil.generation.instructions;

import computil.generation.IRMediator;

public class WriteArray extends AddressedBasedInstruction {

    public WriteArray(int address, IRMediator mediator) {
        super(address, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitWriteArray(this);
    }
}

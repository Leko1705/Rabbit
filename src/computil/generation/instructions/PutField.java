package computil.generation.instructions;

import computil.generation.IRMediator;

public class PutField extends AddressedBasedInstruction {


    public PutField(int address, IRMediator mediator) {
        super(address, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitPutField(this);
    }
}

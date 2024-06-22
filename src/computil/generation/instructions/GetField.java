package computil.generation.instructions;

import computil.generation.IRMediator;

public class GetField extends AddressedBasedInstruction {
    public GetField(int address, IRMediator mediator) {
        super(address, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitGetField(this);
    }
}

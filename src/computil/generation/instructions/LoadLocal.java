package computil.generation.instructions;

import computil.generation.IRMediator;

public class LoadLocal extends AddressedBasedInstruction {

    public LoadLocal(int address, IRMediator mediator) {
        super(address, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitLoadLocal(this);
    }
}

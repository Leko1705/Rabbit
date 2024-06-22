package computil.generation.instructions;

import computil.generation.IRMediator;

public class StoreLocal extends AddressedBasedInstruction {

    public StoreLocal(int address, IRMediator mediator) {
        super(address, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitStoreLocal(this);
    }
}

package computil.generation.instructions;

import computil.generation.IRMediator;

public class NewInstance extends AddressedBasedInstruction {

    public NewInstance(int address, IRMediator mediator) {
        super(address, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitNewInstance(this);
    }
}

package computil.generation.instructions;

import computil.generation.IRMediator;

public class Goto extends AddressedBasedInstruction {
    public Goto(int address, IRMediator mediator) {
        super(address, mediator);
    }

    public void setJumpAddress(int newAddress){
        address = newAddress;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitGoto(this);
    }
}

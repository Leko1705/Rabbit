package computil.generation.instructions;

import computil.generation.IRMediator;

public class BranchIfFalse extends AddressedBasedInstruction {

    public BranchIfFalse(int address, IRMediator mediator) {
        super(address, mediator);
    }

    public void setJumpAddress(int newAddress){
        address = newAddress;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitBranchIfFalse(this);
    }
}

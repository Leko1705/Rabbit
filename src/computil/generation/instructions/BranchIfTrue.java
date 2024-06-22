package computil.generation.instructions;

import computil.generation.IRMediator;

public class BranchIfTrue extends AddressedBasedInstruction {

    public BranchIfTrue(int address, IRMediator mediator) {
        super(address, mediator);
    }

    void setJumpAddress(int newAddress){
        address = newAddress;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitBranchIfTrue(this);
    }
}

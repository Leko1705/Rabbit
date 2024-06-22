package computil.generation.instructions;

import computil.generation.IRMediator;

public abstract class AddressedBasedInstruction extends Instruction {

    int address;

    protected AddressedBasedInstruction(int address, IRMediator mediator) {
        super(mediator);
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
}

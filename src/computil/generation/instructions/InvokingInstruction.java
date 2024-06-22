package computil.generation.instructions;

import computil.generation.IRMediator;

public abstract class InvokingInstruction extends AddressedBasedInstruction {

    private final int arguments;

    protected InvokingInstruction(int address, int argc, IRMediator mediator) {
        super(address, mediator);
        this.arguments = argc;
    }

    public int getArguments() {
        return arguments;
    }
}

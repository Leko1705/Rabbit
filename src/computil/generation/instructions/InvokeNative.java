package computil.generation.instructions;

import computil.generation.IRMediator;

public class InvokeNative extends InvokingInstruction {
    public InvokeNative(int address, int argc, IRMediator mediator) {
        super(address, argc, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitInvokeNative(this);
    }
}

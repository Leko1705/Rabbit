package computil.generation.instructions;

import computil.generation.IRMediator;

public class InvokeVirtual extends InvokingInstruction {
    public InvokeVirtual(int address, int argc, IRMediator mediator) {
        super(address, argc, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitInvokeVirtual(this);
    }
}

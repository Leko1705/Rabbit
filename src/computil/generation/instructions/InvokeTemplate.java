package computil.generation.instructions;

import computil.generation.IRMediator;

public class InvokeTemplate extends InvokingInstruction {

    public InvokeTemplate(int address, int argc, IRMediator mediator) {
        super(address, argc, mediator);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitInvokeTemplate(this);
    }
}

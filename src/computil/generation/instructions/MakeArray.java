package computil.generation.instructions;

import computil.generation.IRMediator;

public class MakeArray extends Instruction {

    private final int size;

    public MakeArray(int size, IRMediator mediator) {
        super(mediator);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitMakeArray(this);
    }
}

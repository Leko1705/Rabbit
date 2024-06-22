package computil.generation.instructions;

import computil.generation.IRMediator;

public class PushInt extends Instruction {

    private final int value;
    public PushInt(int value, IRMediator mediator) {
        super(mediator);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitPushInt(this);
    }

}

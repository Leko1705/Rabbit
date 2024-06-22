package computil.generation.instructions;

import computil.generation.IRMediator;

public class PushBool extends Instruction {

    private final boolean value;

    public PushBool(boolean value, IRMediator mediator) {
        super(mediator);
        this.value = value;
    }

    public boolean getValue(){
        return value;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitPushBool(this);
    }
}

package computil.generation.instructions;

import computil.generation.IRMediator;

public class NewLineNumber extends Instruction {

    private final int line;

    public NewLineNumber(int line, IRMediator mediator) {
        super(mediator);
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitNewLineNumber(this);
    }
}

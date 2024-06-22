package computil.generation.instructions;

import computil.generation.IRMediator;
import computil.tree.Operation;

public class BinaryOperation extends Instruction {

    private final Operation operation;

    private String type;

    public BinaryOperation(Operation operation, String type, IRMediator mediator) {
        super(mediator);
        this.operation = operation;
        this.type = type;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getType() {
        return type;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitBinaryOperation(this);
    }
}

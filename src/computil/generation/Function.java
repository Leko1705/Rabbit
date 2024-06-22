package computil.generation;

import computil.generation.instructions.Instruction;

import java.util.*;

public class Function extends BasicIRComponent implements Iterable<Instruction> {

    private final String name;

    private int stackSize = 0;

    private int currStackSize = 0;

    private int locals = -1;

    private final Map<String, Integer> parameters = new LinkedHashMap<>();

    private final List<Instruction> instructions = new ArrayList<>();

    private boolean isMethod = false;

    public Function(String name, IRMediator mediator) {
        super(mediator);
        this.name = name;
    }

    public void accept(FunctionVisitor visitor){
        visitor.visitFunction(this);
    }

    public String getName() {
        return name;
    }

    public int getStackSize(){
        return stackSize;
    }

    public int getLocals() {
        return locals+1;
    }

    public int getStreamSize(){
        return instructions.size();
    }

    public boolean isMethod() {
        return isMethod;
    }

    public Map<String, Integer> getParameters() {
        return parameters;
    }


    protected void append(Instruction instruction){
        instructions.add(instruction);
    }
    protected void insert(int index, Instruction instruction){
        instructions.add(index, instruction);
    }


    protected void putParameter(String name, int address) {
        parameters.put(name, address);
    }

    protected void markAsMethod() {
        this.isMethod = true;
    }

    protected void stackGrows(int growth){
        currStackSize += growth;
        stackSize = Math.max(stackSize, currStackSize);
    }

    protected void useLocal(int address){
        locals = Math.max(locals, address);
    }

    @Override
    public Iterator<Instruction> iterator() {
        return instructions.iterator();
    }
}

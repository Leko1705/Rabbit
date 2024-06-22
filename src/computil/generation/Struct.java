package computil.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Struct extends BasicIRComponent {

    private final String name;

    private final List<String> fields = new ArrayList<>();

    private final Map<String, Integer> methodReferences = new HashMap<>();

    public Struct(String name, IRMediator mediator) {
        super(mediator);
        this.name = name;
    }

    public void accept(StructVisitor visitor){
        visitor.visitStruct(this);
    }

    public int getSize() {
        return fields.size();
    }


    public String getName() {
        return name;
    }

    public Map<String, Integer> getMethods() {
        return methodReferences;
    }

    protected void putField(String name){
        fields.add(name);
    }

    protected void putMethod(String name, int poolAddress){
        methodReferences.put(name, poolAddress);
    }
}

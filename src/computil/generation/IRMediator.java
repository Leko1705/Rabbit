package computil.generation;

import computil.generation.pool.Pool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IRMediator implements IRComponent, IRUnit {

    private int entryPoint;
    private Pool pool;
    private final Map<String, Function> functions = new HashMap<>();
    private final Map<String, Struct> structs = new HashMap<>();

    public void registerFunction(String name){
        if (functions.containsKey(name))
            throw new IllegalStateException("can not register a function with the same name twice");
        Function function = new Function(name, this);
        functions.put(name, function);
    }

    public void registerStruct(String name){
        if (structs.containsKey(name))
            throw new IllegalStateException("can not register a struct with the same name twice");
        Struct struct = new Struct(name, this);
        structs.put(name, struct);
    }

    public void registerPool(Pool pool){
        if (this.pool != null)
            throw new IllegalStateException("can not register constant pool twice");
        this.pool = pool;
    }

    public void setEntryPoint(int entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Override
    public Function getFunction(String name) {
        return functions.get(name);
    }

    public Struct getStruct(String name){
        return structs.get(name);
    }

    @Override
    public int getEntryPoint() {
        return entryPoint;
    }

    @Override
    public Pool getPool() {
        return pool;
    }

    @Override
    public Collection<Function> getFunctions() {
        return functions.values();
    }

    @Override
    public Collection<Struct> getStructs() {
        return structs.values();
    }
}

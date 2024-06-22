package computil.generation.pool;

import computil.generation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pool extends BasicIRComponent implements Iterable<PoolConstant<?>> {

    private final List<PoolConstant<?>> entries = new ArrayList<>();

    public Pool(IRMediator mediator) {
        super(mediator);
        mediator.registerPool(this);
    }

    public void accept(PoolVisitor visitor){
        visitor.visitPool(this);
    }

    public int size(){
        return entries.size();
    }

    public PoolConstant<?> get(int index){
        return entries.get(index);
    }

    public int putInteger(int i){
        return putIfAbsent(new IntegerConstant(i, getMediator()));
    }

    public int putFloat(float f){
        return putIfAbsent(new FloatConstant(f, getMediator()));
    }

    public int putString(String s){
        return putIfAbsent(new StringConstant(s, getMediator()));
    }

    public int putVirtual(String name){
        return putIfAbsent(new VFunctionConstant(name, getMediator()));
    }

    public int putNative(String name){
        return putIfAbsent(new NFunctionConstant(name, getMediator()));
    }

    public int putStruct(String name){
        return putIfAbsent(new StructConstant(name, getMediator()));
    }

    public int putUTF8(String uft8){
        return putIfAbsent(new UTF8Constant(uft8, getMediator()));
    }


    private int putIfAbsent(PoolConstant<?> entry){
        int idx = entries.indexOf(entry);
        if (idx != -1) return idx;
        idx = entries.size();
        entries.add(entry);
        return idx;
    }


    @Override
    public Iterator<PoolConstant<?>> iterator() {
        return entries.iterator();
    }
}

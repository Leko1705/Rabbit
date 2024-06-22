package computil.generation;

import computil.generation.pool.Pool;

import java.util.Collection;

public interface IRUnit {

    int getEntryPoint();

    Pool getPool();

    Collection<Function> getFunctions();

    Collection<Struct> getStructs();

}

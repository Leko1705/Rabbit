package computil.check;

import computil.diags.Error;
import computil.diags.Logger;
import computil.tree.Tree;
import computil.util.TreeScanner;

import java.util.Objects;

public abstract class Checker<P, R> extends TreeScanner<P, R> {

    private Logger logger;

    public Checker(){
        super(null);
    }

    public Checker(R defaultValue) {
        super(defaultValue);
    }

    public void check(Tree tree, Logger logger){
        this.logger = Objects.requireNonNull(logger);
        scan(Objects.requireNonNull(tree), null);
    }

    public void error(Error error){
        logger.error(error);
    }

}

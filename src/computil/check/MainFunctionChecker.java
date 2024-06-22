package computil.check;

import computil.diags.Error;
import computil.tree.CallableTree;
import computil.tree.RootTree;

public class MainFunctionChecker extends Checker<Void, Void> {

    @Override
    public Void visitRootTree(RootTree rootTree, Void unused) {
        boolean found = false;
        for (CallableTree callable : rootTree.getCallables()) {
            if (callable.getName().equals("main")) {
                if (!found) {
                    checkMainFunction(callable);
                    return null;
                }
                else error(new Error("can not declare multiple main functions", callable.getLocation()));
            }

        }
        error(new Error("missing main function", null));
        return null;
    }

    private void checkMainFunction(CallableTree main){
        if (!main.getParameters().isEmpty())
            error(new Error("main function must not have parameters", main.getLocation()));
        if (!main.getReturnType().name().equals("void"))
            error(new Error("main function must be type of void", main.getLocation()));
    }
}

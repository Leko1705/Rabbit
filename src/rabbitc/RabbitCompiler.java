package rabbitc;

import computil.check.*;
import computil.diags.Error;
import computil.diags.Logger;
import computil.diags.Warning;
import computil.generation.Generator;
import computil.generation.Target;
import computil.generation.targets.rbt.RabbitBytecodeDisV1;
import computil.generation.targets.rbt.RabbitBytecodeV1;
import computil.optim.ConstantFolder;
import computil.optim.Optimizer;
import computil.parse.Lexer;
import computil.parse.UnicodeReader;
import computil.transpile.CTranspiler;
import computil.tree.RootTree;
import computil.tree.Tree;

import java.io.InputStream;
import java.io.OutputStream;

public class RabbitCompiler {

    private static final int MAX_OPTIMIZATION_PASSES = 100_000;

    public int run(InputStream in, OutputStream out, Logger logger, String... args){
        LoggerAdapter adapter = new LoggerAdapter(logger);
        RootTree tree = parse(in, adapter);
        check(tree, adapter);
        if (adapter.exit != 0) return adapter.exit;
        optimize(tree);
        generate(tree, out, false);
        return 0;
    }

    public int dis(InputStream in, OutputStream out, Logger logger, String... args){
        LoggerAdapter adapter = new LoggerAdapter(logger);
        RootTree tree = parse(in, adapter);
        check(tree, adapter);
        if (adapter.exit != 0) return adapter.exit;
        optimize(tree);
        generate(tree, out, true);
        return 0;
    }

    public int transpile(InputStream in, OutputStream out, Logger logger, String... args){
        LoggerAdapter adapter = new LoggerAdapter(logger);
        RootTree tree = parse(in, adapter);
        check(tree, adapter);
        if (adapter.exit != 0) return adapter.exit;
        CTranspiler transpiler = new CTranspiler(out);
        tree.accept(transpiler, null);
        return 0;
    }

    private RootTree parse(InputStream in, Logger logger){
        Lexer lexer = new Scanner(new UnicodeReader(in, 2), logger);
        RabbitParser parser = new RabbitParser(lexer, logger);
        return parser.parseProgram();
    }

    private void check(Tree tree, Logger logger){
        checkWith(new DefinitionChecker(), tree, logger);
        checkWith(new MainFunctionChecker(), tree, logger);
        checkWith(new TypeChecker(), tree, logger);
        //checkWith(new DanglingChecker(), tree, logger);
    }

    private void optimize(Tree tree){
        boolean optimizationPerformed;
        int pass = 0;
        do {
            optimizationPerformed = false;
            optimizationPerformed |= optimizeWith(new ConstantFolder(), tree);
        }while (optimizationPerformed && (++pass < MAX_OPTIMIZATION_PASSES));
    }

    private boolean optimizeWith(Optimizer<?> optimizer, Tree tree){
        tree.accept(optimizer, null);
        return optimizer.isOptimizationPerformed();
    }

    private void checkWith(Checker<?, ?> checker, Tree tree, Logger logger){
        checker.check(tree, logger);
    }

    private void generate(RootTree tree, OutputStream out, boolean disassembled){
        Generator generator = new Generator();
        Target target = getTarget(out, disassembled);
        generator.generate(tree, target);
    }

    private Target getTarget(OutputStream out, boolean disassembled){
        return disassembled
                ? new RabbitBytecodeDisV1(out)
                : new RabbitBytecodeV1(out);
    }


    private static class LoggerAdapter implements Logger {
        private int exit = 0;
        private final Logger logger;
        private LoggerAdapter(Logger logger) {
            this.logger = logger;
        }
        public void error(Error error) {
            exit = -1;
            logger.error(error);
        }
        public void warning(Warning warning) {
            logger.warning(warning);
        }
    }


}

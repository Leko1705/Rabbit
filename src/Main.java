import computil.diags.StdLogger;
import rabbitc.RabbitCompiler;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        compileTest();
        disTest();
        transpileTest();
    }

    private static void compileTest(){
        compile("test.rbt", "/home/kali/CLionProjects/RabbitVM/cmake-build-debug/test.rbtc");
    }

    private static void disTest(){
        dis("test.rbt", "test.rbti");
    }

    private static void transpileTest(){
        transpile("test.rbt", "test.c");
    }

    private static void compile(String inPath, String outPath, String... args){
        try (InputStream in = new FileInputStream(inPath)){
            RabbitCompiler compiler = new RabbitCompiler();

            OutputStream out = new FileOutputStream(outPath);
            int exitValue = compiler.run(in, out, StdLogger.getLogger(), args);
            if (exitValue == -1)
                System.exit(exitValue);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dis(String inPath, String outPath, String... args){
        try (InputStream in = new FileInputStream(inPath)){
            RabbitCompiler compiler = new RabbitCompiler();

            OutputStream out = new FileOutputStream(outPath);
            int exitValue = compiler.dis(in, out, StdLogger.getLogger(), args);
            if (exitValue == -1)
                System.exit(exitValue);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void transpile(String inPath, String outPath, String... args){
        try(InputStream in = new FileInputStream(inPath)) {

            RabbitCompiler compiler = new RabbitCompiler();
            OutputStream out = new FileOutputStream(outPath);
            int exitValue = compiler.transpile(in, out, StdLogger.getLogger(), args);
            if (exitValue == -1)
                System.exit(exitValue);

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}
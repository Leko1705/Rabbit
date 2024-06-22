package computil.generation.pool;

public interface PoolConstantVisitor {

    void visitIntegerConstant(IntegerConstant constant);
    void visitFloatConstant(FloatConstant constant);
    void visitStringConstant(StringConstant constant);
    void visitVFunctionConstant(VFunctionConstant constant);
    void visitNFunctionConstant(NFunctionConstant constant);
    void visitStructConstant(StructConstant constant);
    void visitUTF8Constant(UTF8Constant constant);

}

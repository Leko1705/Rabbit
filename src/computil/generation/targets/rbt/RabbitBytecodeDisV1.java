package computil.generation.targets.rbt;

import computil.generation.*;
import computil.generation.instructions.*;
import computil.generation.pool.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

public class RabbitBytecodeDisV1
        implements Target, PoolVisitor, PoolConstantVisitor,
        FunctionVisitor, InstructionVisitor, StructVisitor {

    private final OutputStream out;

    private int nextPoolAddress = 0;

    public RabbitBytecodeDisV1(OutputStream out) {
        this.out = out;
    }


    private void write(String s){
        try {
            out.write(s.getBytes(StandardCharsets.UTF_8));
        }catch (IOException ignored){
        }
    }

    @Override
    public void write(IRUnit unit) {
        write("magic number: 0xDEAD\n");
        write("major: " + 1 + "\n");
        write("major: " + 1 + "\n");
        write("entry point: " + unit.getEntryPoint() + "\n");

        Pool pool = unit.getPool();
        pool.accept(this);

        final Collection<Function> functions = unit.getFunctions();
        for (Function function : functions)
            function.accept(this);

        final Collection<Struct> structs = unit.getStructs();
        for (Struct struct : structs)
            struct.accept(this);
    }


    /* ------------ write constant pool ------------ */

    @Override
    public void visitPool(Pool pool) {
        write("\nconstant-pool:\n");
        for (PoolConstant<?> constant : pool)
            constant.accept(this);
    }


    private void writeConstant(PoolConstant<?> constant){
        write((nextPoolAddress++) + "\t" + constant.getTag() + " " + constant.get() + "\n");
    }

    @Override
    public void visitIntegerConstant(IntegerConstant constant) {
        writeConstant(constant);
    }

    @Override
    public void visitFloatConstant(FloatConstant constant) {
        writeConstant(constant);
    }

    @Override
    public void visitStringConstant(StringConstant constant) {
        writeConstant(constant);
    }

    @Override
    public void visitVFunctionConstant(VFunctionConstant constant) {
        writeConstant(constant);
    }

    @Override
    public void visitNFunctionConstant(NFunctionConstant constant) {
        writeConstant(constant);
    }

    @Override
    public void visitStructConstant(StructConstant constant) {
        writeConstant(constant);
    }

    @Override
    public void visitUTF8Constant(UTF8Constant constant) {
        writeConstant(constant);
    }



    /* ------------ write functions with their instructions ------------ */



    @Override
    public void visitFunction(Function function) {
        String s = "\n" + function.getName()
                + "  args=" + function.getParameters()
                + " stack=" + function.getStackSize()
                + " locals=" + function.getLocals() + "\n";
        write(s);
        for (Instruction instruction : function)
            instruction.accept(this);
    }

    private void writeInstruction(Opcode opcode, int... args){
        StringBuilder sb = new StringBuilder("\t");
        sb.append(opcode.toString());
        for (int arg : args)
            sb.append(" ").append(arg);
        sb.append("\n");
        write(sb.toString());
    }

    @Override
    public void visitPushNull(PushNull pushNull) {
        writeInstruction(Opcode.PUSH_NULL);
    }

    @Override
    public void visitPushInt(PushInt pushInt) {
        writeInstruction(Opcode.PUSH_INT, pushInt.getValue());
    }

    @Override
    public void visitPushBool(PushBool pushBool) {
        writeInstruction(Opcode.PUSH_INT, pushBool.getValue() ? 1 : 0);
    }

    @Override
    public void visitLoadConst(LoadConst loadConst) {
        writeInstruction(Opcode.LOAD_CONST, loadConst.getAddress());
    }

    @Override
    public void visitLoadLocal(LoadLocal loadLocal) {
        writeInstruction(Opcode.LOAD_LOCAl, loadLocal.getAddress());
    }

    @Override
    public void visitStoreLocal(StoreLocal storeLocal) {
        writeInstruction(Opcode.STORE_LOCAL, storeLocal.getAddress());
    }

    @Override
    public void visitNullCheck(NullCheck nullCheck) {
        writeInstruction(Opcode.NULL_CHECK);
    }

    @Override
    public void visitCheckCast(CheckCast checkCast) {
        writeInstruction(Opcode.CHECK_CAST, checkCast.getAddress());
    }

    @Override
    public void visitInt2Float(Int2Float int2Float) {
        writeInstruction(Opcode.I2F);
    }

    @Override
    public void visitFloat2Int(Float2Int float2Int) {
        writeInstruction(Opcode.F2I);
    }

    @Override
    public void visitMakeArray(MakeArray makeArray) {
        writeInstruction(Opcode.MAKE_ARRAY, makeArray.getSize());
    }

    @Override
    public void visitReadArray(ReadArray readArray) {
        writeInstruction(Opcode.READ_ARRAY, readArray.getAddress());
    }

    @Override
    public void visitWriteArray(WriteArray writeArray) {
        writeInstruction(Opcode.WRITE_ARRAY, writeArray.getAddress());
    }

    @Override
    public void visitNewInstance(NewInstance newInstance) {
        writeInstruction(Opcode.NEW, newInstance.getAddress());
    }

    @Override
    public void visitFree(Free free) {
        writeInstruction(Opcode.FREE);
    }

    @Override
    public void visitGetField(GetField getField) {
        writeInstruction(Opcode.GET_FIELD, getField.getAddress());
    }

    @Override
    public void visitPutField(PutField putField) {
        writeInstruction(Opcode.PUT_FIELD, putField.getAddress());
    }

    @Override
    public void visitInvokeVirtual(InvokeVirtual invokeVirtual) {
        writeInstruction(Opcode.INVOKE_VIRTUAL, invokeVirtual.getAddress(), invokeVirtual.getArguments());
    }

    @Override
    public void visitInvokeTemplate(InvokeTemplate invokeTemplate) {
        writeInstruction(Opcode.INVOKE_TEMPLATE, invokeTemplate.getAddress(), invokeTemplate.getArguments());
    }

    @Override
    public void visitInvokeNative(InvokeNative invokeNative) {
        writeInstruction(Opcode.INVOKE_NATIVE, invokeNative.getAddress(), invokeNative.getArguments());
    }

    @Override
    public void visitReturn(Return aReturn) {
        writeInstruction(Opcode.RETURN);
    }

    @Override
    public void visitDup(Dup dup) {
        writeInstruction(Opcode.DUP);
    }

    @Override
    public void visitSwap(Swap swap) {
        writeInstruction(Opcode.SWAP);
    }

    @Override
    public void visitPop(Pop pop) {
        writeInstruction(Opcode.POP);
    }

    @Override
    public void visitNot(Not not) {
        writeInstruction(Opcode.NOT);
    }

    @Override
    public void visitNegate(Negate negate) {
        writeInstruction(Opcode.NEG);
    }

    @Override
    public void visitBinaryOperation(BinaryOperation binaryOperation) {
        writeInstruction(RabbitBytecodeV1.byOperation(binaryOperation.getType(), binaryOperation.getOperation()));
    }

    @Override
    public void visitGoto(Goto aGoto) {
        writeInstruction(Opcode.GOTO, aGoto.getAddress());
    }

    @Override
    public void visitBranchIfFalse(BranchIfFalse branchIfFalse) {
        writeInstruction(Opcode.BRANCH_ZERO, branchIfFalse.getAddress());
    }

    @Override
    public void visitBranchIfTrue(BranchIfTrue branchIfTrue) {
        writeInstruction(Opcode.BRANCH_NOT_ZERO, branchIfTrue.getAddress());
    }

    @Override
    public void visitNewLineNumber(NewLineNumber newLineNumber) {
        writeInstruction(Opcode.NEW_LINE, newLineNumber.getLine());
    }



    /* ------------ write structs ------------ */



    @Override
    public void visitStruct(Struct struct) {
        StringBuilder s = new StringBuilder("\nstruct " + struct.getName() + " size=" + struct.getSize());
        for (Map.Entry<String, Integer> method : struct.getMethods().entrySet())
            s.append("\n\t").append(method.getKey()).append(" ").append(method.getValue());
        write(s.toString());
    }

}

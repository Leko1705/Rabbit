package computil.generation.targets.rbt;

import computil.generation.*;
import computil.generation.instructions.*;
import computil.generation.pool.*;
import computil.tree.Operation;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

public class RabbitBytecodeV1
        implements Target, PoolVisitor, PoolConstantVisitor,
        FunctionVisitor, InstructionVisitor, StructVisitor {


    private final OutputStream out;

    public RabbitBytecodeV1(OutputStream out) {
        this.out = out;
    }

    private void write(int b){
        try {
            out.write(b);
        }catch (IOException ignored){}
    }

    private void write(byte[] b){
        try {
            out.write(b);
        }catch (IOException ignored){}
    }

    private void writeInt(int value){
        write(new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value});
    }

    private void writeString(String s){
        writeInt(s.length());
        write(s.getBytes(StandardCharsets.UTF_8));
    }

    public int getMinor(){
        return 1;
    }

    public int getMajor(){
        return 1;
    }

    @Override
    public void write(IRUnit unit) {
        write(0xDE); write(0xAD); // magic number 0xDEAD
        writeInt(getMinor());
        writeInt(getMajor());
        write(unit.getEntryPoint());

        Pool pool = unit.getPool();
        pool.accept(this);

        final Collection<Function> functions = unit.getFunctions();
        write(functions.size());
        for (Function function : functions)
            function.accept(this);

        final Collection<Struct> structs = unit.getStructs();
        write(structs.size());
        for (Struct struct : structs)
            struct.accept(this);
    }


    /* ------------ write constant pool ------------ */

    @Override
    public void visitPool(Pool pool) {
        write(pool.size());
        for (PoolConstant<?> constant : pool)
            constant.accept(this);
    }

    private void writePoolTag(PoolTag tag){
        write(tag.ordinal());
    }

    @Override
    public void visitIntegerConstant(IntegerConstant constant) {
        writePoolTag(constant.getTag());
        writeInt(constant.get());
    }

    @Override
    public void visitFloatConstant(FloatConstant constant) {
        writePoolTag(constant.getTag());
        int asInt = Float.floatToIntBits(constant.get());
        writeInt(asInt);
    }

    @Override
    public void visitStringConstant(StringConstant constant) {
        writePoolTag(constant.getTag());
        writeString(constant.get());
    }

    @Override
    public void visitVFunctionConstant(VFunctionConstant constant) {
        writePoolTag(constant.getTag());
        writeString(constant.get());
    }

    @Override
    public void visitNFunctionConstant(NFunctionConstant constant) {
        writePoolTag(constant.getTag());
        writeString(constant.get());
    }

    @Override
    public void visitStructConstant(StructConstant constant) {
        writePoolTag(constant.getTag());
        writeString(constant.get());
    }

    @Override
    public void visitUTF8Constant(UTF8Constant constant) {
        writePoolTag(constant.getTag());
        writeString(constant.get());
    }



    /* ------------ write functions with their instructions ------------ */


    @Override
    public void visitFunction(Function function) {
        writeString(function.getName());
        write(function.getStackSize());
        write(function.getLocals());

        writeInt(function.getStreamSize());

        for (Instruction instruction : function)
            instruction.accept(this);
    }

    private void writeOpcode(Opcode opcode){
        write(opcode.ordinal());
    }

    @Override
    public void visitPushNull(PushNull pushNull) {
        write(1);
        writeOpcode(Opcode.PUSH_NULL);
    }

    @Override
    public void visitPushInt(PushInt pushInt) {
        write(2);
        writeOpcode(Opcode.PUSH_INT);
        write(pushInt.getValue());
    }

    @Override
    public void visitPushBool(PushBool pushBool) {
        write(2);
        writeOpcode(Opcode.PUSH_INT);
        write(pushBool.getValue() ? 1 : 0);
    }

    @Override
    public void visitLoadConst(LoadConst loadConst) {
        write(2);
        writeOpcode(Opcode.LOAD_CONST);
        write(loadConst.getAddress());
    }

    @Override
    public void visitLoadLocal(LoadLocal loadLocal) {
        write(2);
        writeOpcode(Opcode.LOAD_LOCAl);
        write(loadLocal.getAddress());
    }

    @Override
    public void visitStoreLocal(StoreLocal storeLocal) {
        write(2);
        writeOpcode(Opcode.STORE_LOCAL);
        write(storeLocal.getAddress());
    }

    @Override
    public void visitNullCheck(NullCheck nullCheck) {
        write(1);
        writeOpcode(Opcode.NULL_CHECK);
    }

    @Override
    public void visitCheckCast(CheckCast checkCast) {
        write(2);
        writeOpcode(Opcode.CHECK_CAST);
        write(checkCast.getAddress());
    }

    @Override
    public void visitInt2Float(Int2Float int2Float) {
        write(1);
        writeOpcode(Opcode.I2F);
    }

    @Override
    public void visitFloat2Int(Float2Int float2Int) {
        write(1);
        writeOpcode(Opcode.F2I);
    }

    @Override
    public void visitMakeArray(MakeArray makeArray) {
        write(2);
        writeOpcode(Opcode.MAKE_ARRAY);
        write(makeArray.getSize());
    }

    @Override
    public void visitReadArray(ReadArray readArray) {
        write(2);
        writeOpcode(Opcode.READ_ARRAY);
        write(readArray.getAddress());
    }

    @Override
    public void visitWriteArray(WriteArray writeArray) {
        write(2);
        writeOpcode(Opcode.WRITE_ARRAY);
        write(writeArray.getAddress());
    }

    @Override
    public void visitNewInstance(NewInstance newInstance) {
        write(2);
        writeOpcode(Opcode.NEW);
        write(newInstance.getAddress());
    }

    @Override
    public void visitFree(Free free) {
        write(1);
        writeOpcode(Opcode.FREE);
    }

    @Override
    public void visitGetField(GetField getField) {
        write(2);
        writeOpcode(Opcode.GET_FIELD);
        write(getField.getAddress());
    }

    @Override
    public void visitPutField(PutField putField) {
        write(2);
        writeOpcode(Opcode.PUT_FIELD);
        write(putField.getAddress());
    }

    @Override
    public void visitInvokeVirtual(InvokeVirtual invokeVirtual) {
        write(3);
        writeOpcode(Opcode.INVOKE_VIRTUAL);
        write(invokeVirtual.getAddress());
        write(invokeVirtual.getArguments());
    }

    @Override
    public void visitInvokeTemplate(InvokeTemplate invokeTemplate) {
        write(3);
        writeOpcode(Opcode.INVOKE_TEMPLATE);
        write(invokeTemplate.getAddress());
        write(invokeTemplate.getArguments());
    }

    @Override
    public void visitInvokeNative(InvokeNative invokeNative) {
        write(3);
        writeOpcode(Opcode.INVOKE_NATIVE);
        write(invokeNative.getAddress());
        write(invokeNative.getArguments());
    }

    @Override
    public void visitReturn(Return aReturn) {
        write(1);
        writeOpcode(Opcode.RETURN);
    }

    @Override
    public void visitDup(Dup dup) {
        write(1);
        writeOpcode(Opcode.DUP);
    }

    @Override
    public void visitSwap(Swap swap) {
        write(1);
        writeOpcode(Opcode.SWAP);
    }

    @Override
    public void visitPop(Pop pop) {
        write(1);
        writeOpcode(Opcode.POP);
    }

    @Override
    public void visitNot(Not not) {
        write(1);
        writeOpcode(Opcode.NOT);
    }

    @Override
    public void visitNegate(Negate negate) {
        write(1);
        writeOpcode(Opcode.NEG);
    }

    @Override
    public void visitBinaryOperation(BinaryOperation binaryOperation) {
        write(1);
        writeOpcode(byOperation(binaryOperation.getType(), binaryOperation.getOperation()));
    }

    @Override
    public void visitGoto(Goto aGoto) {
        write(3); // jump-address encoded in 2 bytes
        writeOpcode(Opcode.GOTO);
        write(int2twoBytes(aGoto.getAddress()));
    }

    @Override
    public void visitBranchIfFalse(BranchIfFalse branchIfFalse) {
        write(3); // jump-address encoded in 2 bytes
        writeOpcode(Opcode.BRANCH_ZERO);
        write(int2twoBytes(branchIfFalse.getAddress()));
    }

    @Override
    public void visitBranchIfTrue(BranchIfTrue branchIfTrue) {
        write(3); // jump-address encoded in 2 bytes
        writeOpcode(Opcode.BRANCH_NOT_ZERO);
        write(int2twoBytes(branchIfTrue.getAddress()));
    }

    @Override
    public void visitNewLineNumber(NewLineNumber newLineNumber) {
        write(3); // line number encoded in 2 bytes
        writeOpcode(Opcode.NEW_LINE);
        write(int2twoBytes(newLineNumber.getLine()));
    }

    private byte[] int2twoBytes(int address){
        return new byte[] {
                (byte)((address >> 8) & 0xFF),
                (byte)(address & 0xFF)};
    }



    /* ------------ write structs ------------ */


    @Override
    public void visitStruct(Struct struct) {
        writeString(struct.getName());
        write(struct.getSize());

        final Map<String, Integer> methods = struct.getMethods();
        write(methods.size());

        for (Map.Entry<String, Integer> method : methods.entrySet()){
            writeString(method.getKey());
            write(method.getValue());
        }

    }


    protected static Opcode byOperation(String type, Operation op){

        return switch (op){
            case ADD, SUB, MUL -> numericOpcode(type, op);
            case MOD -> Opcode.MOD;
            case AND -> Opcode.AND;
            case OR -> Opcode.OR;
            case AND_BIT -> Opcode.AND_BIT;
            case OR_BIT -> Opcode.OR_BIT;
            case XOR -> Opcode.XOR;
            case SHIFT_AL -> Opcode.SHIFT_AL;
            case SHIFT_AR -> Opcode.SHIFT_AR;
            case EQUALS -> Opcode.EQUALS;
            case NOT_EQUALS -> Opcode.NOT_EQUALS;
            case LESS -> Opcode.LESS;
            case GREATER -> Opcode.GREATER;
            case LESS_EQ -> Opcode.LESS_EQ;
            case GREATER_EQ -> Opcode.GREATER_EQ;
            case DIV -> Opcode.DIV;

            default -> throw new UnsupportedOperationException(type + " " + op);
        };

    }

    private static Opcode numericOpcode(String type, Operation op){
        if (type.equals("int")){
            return switch (op){
                case ADD -> Opcode.ADD_I;
                case SUB -> Opcode.SUB_I;
                case MUL -> Opcode.MUL_I;
                default -> throw new AssertionError();
            };
        }
        else {
            return switch (op){
                case ADD -> Opcode.ADD_F;
                case SUB -> Opcode.SUB_F;
                case MUL -> Opcode.MUL_F;
                default -> throw new AssertionError();
            };
        }
    }

}

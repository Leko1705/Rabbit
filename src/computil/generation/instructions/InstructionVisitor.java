package computil.generation.instructions;

public interface InstructionVisitor {

    void visitPushNull(PushNull pushNull);

    void visitPushInt(PushInt pushInt);

    void visitPushBool(PushBool pushBool);

    void visitLoadConst(LoadConst loadConst);

    void visitLoadLocal(LoadLocal loadLocal);

    void visitStoreLocal(StoreLocal storeLocal);

    void visitNullCheck(NullCheck nullCheck);

    void visitCheckCast(CheckCast checkCast);

    void visitInt2Float(Int2Float int2Float);

    void visitFloat2Int(Float2Int float2Int);

    void visitMakeArray(MakeArray makeArray);

    void visitReadArray(ReadArray readArray);

    void visitWriteArray(WriteArray writeArray);

    void visitNewInstance(NewInstance newInstance);

    void visitFree(Free free);

    void visitGetField(GetField getField);

    void visitPutField(PutField putField);

    void visitInvokeVirtual(InvokeVirtual invokeVirtual);

    void visitInvokeTemplate(InvokeTemplate invokeTemplate);

    void visitInvokeNative(InvokeNative invokeNative);

    void visitReturn(Return aReturn);

    void visitDup(Dup dup);

    void visitSwap(Swap swap);

    void visitPop(Pop pop);

    void visitNot(Not not);

    void visitNegate(Negate negate);

    void visitBinaryOperation(BinaryOperation binaryOperation);

    void visitGoto(Goto aGoto);

    void visitBranchIfFalse(BranchIfFalse branchIfFalse);

    void visitBranchIfTrue(BranchIfTrue branchIfTrue);


    void visitNewLineNumber(NewLineNumber newLineNumber);

}

package computil.generation.targets.rbt;

import java.util.HashMap;
import java.util.Map;

public enum Opcode {
    PUSH_NULL,
    PUSH_INT,
    LOAD_CONST,

    LOAD_LOCAl,
    STORE_LOCAL,

    NEW,
    FREE,
    NULL_CHECK,
    CHECK_CAST,
    I2F,
    F2I,

    MAKE_ARRAY,
    READ_ARRAY,
    WRITE_ARRAY,

    GET_FIELD,
    PUT_FIELD,

    INVOKE_VIRTUAL,
    INVOKE_TEMPLATE,
    INVOKE_NATIVE,
    RETURN,

    DUP,
    SWAP,
    POP,

    NOT,
    NEG,

    ADD_I,
    SUB_I,
    MUL_I,
    MOD,
    AND,
    OR,
    AND_BIT,
    OR_BIT,
    XOR,
    SHIFT_AL,
    SHIFT_AR,
    ADD_F,
    SUB_F,
    MUL_F,
    DIV,
    EQUALS,
    NOT_EQUALS,
    LESS,
    GREATER,
    LESS_EQ,
    GREATER_EQ,


    GOTO,
    BRANCH_NOT_ZERO,
    BRANCH_ZERO,

    NEW_LINE
    ;


    private static final Map<Integer, Opcode> opcodeMap = new HashMap<>();

    static {
        for (Opcode opcode : values())
            opcodeMap.put(opcode.ordinal(), opcode);
    }

    public static Opcode of(int b) {
       return opcodeMap.get(b);
    }
}

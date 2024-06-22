package rabbitc;

public enum TokenKind {

    INTEGER,
    FLOAT,
    TRUE("true"),
    FALSE("false"),

    STRING,
    NULL("null"),
    FUN("fun"),
    RETURN("return"),
    NAT("nat"),
    NEW("new"),
    DEL("del"),
    STRUCT("struct"),
    TEMPLATE("template"),
    IMPL("impl"),
    FOR("for"),
    AS("as"),
    LET("let"),
    MUT("mut"),
    EQ_ASSIGN("="),
    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    IDIV_ASSIGN("//="),
    MOD_ASSIGN("%="),
    POW_ASSIGN("^="),
    AND_ASSIGN("&="),
    OR_ASSIGN("|="),
    SHIFT_AL_ASSIGN("<<="),
    SHIFT_AR_ASSIGN(">>="),
    SHIFT_LR_ASSIGN(">>>="),
    ARROW("->"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    DO("do"),
    BREAK("break"),
    CONTINUE("continue"),
    PLUS("+"),
    MINUS("-"),
    MUL("*"),
    DIV("/"),
    IDIV("//"),
    MOD("%"),
    AND("&&"),
    OR("||"),
    AND_BIT("&"),
    OR_BIT("|"),
    XOR("^"),
    EX_MARK("!"),
    QU_MARK("?"),
    SHIFT_AL("<<"),
    SHIFT_AR(">>"),
    EQUALS("=="),
    NOT_EQUALS("!="),
    LESS("<"),
    GREATER(">"),
    LESS_EQ("<="),
    GREATER_EQ(">="),
    CURVED_OPEN("{"),
    CURVED_CLOSED("}"),
    BRACKET_OPEN("["),
    BRACKET_CLOSED("]"),
    PARENTHESES_OPEN("("),
    PARENTHESES_CLOSED(")"),
    DOT("."),
    COMMA(","),
    SEMI(";"),
    COLON(":"),
    PLUS_PLUS("++"),
    MINUS_MINUS("--"),
    IMPORT("import"),
    EOF,
    ERROR,
    IDENTIFIER


    ;TokenKind(){
        this(null);
    }
    TokenKind(String name){
        this.name = name;
    }
    public final String name;

    public static TokenKind fromLexem(String lexem){
        for (TokenKind kind : TokenKind.values())
            if (kind.name != null && kind.name.equals(lexem))
                return kind;
        return IDENTIFIER;
    }


}

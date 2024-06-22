package rabbitc;

import computil.diags.Error;
import computil.diags.Logger;
import computil.parse.Lexer;
import computil.parse.Parser;
import computil.parse.Token;
import computil.tree.*;
import computil.util.Location;

import java.util.ArrayList;
import java.util.List;

import static computil.tree.Trees.*;

class RabbitParser implements Parser {

    private final Lexer lexer;
    private final Logger log;

    public RabbitParser(Lexer lexer, Logger logger) {
        this.lexer = lexer;
        this.log = logger;
    }

    private void error(String msg, Token token) {
        log.error(new Error(msg, token.getLocation()));
    }

    @Override
    public RootTree parseProgram() {
        BasicRootTree rootTree = new BasicRootTree();

        Token token = lexer.peek();
        while (!token.hasTag(TokenKind.EOF)){
            switch ((TokenKind)token.getTag()){
                case STRUCT -> rootTree.structs.add(parseStruct());
                case TEMPLATE -> rootTree.templates.add(parseTemplate());
                case IMPL -> rootTree.impls.add(parseImplementation());
                default -> {
                    CallableTree callableTree = parseCallable();
                    if (callableTree != null)
                        rootTree.callables.add(callableTree);
                }
            }
            token = lexer.peek();
        }
        return rootTree;
    }

    private StructTree parseStruct(){
        lexer.consume();
        Token token = lexer.consume();
        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);

        BasicStructTree structTree = new BasicStructTree(token.getLexem(), token.getLocation());

        token = lexer.consume();
        if (!token.hasTag(TokenKind.CURVED_OPEN))
            error("missing '{'", token);

        token = lexer.peek();
        while (!token.hasTag(TokenKind.CURVED_CLOSED, TokenKind.EOF)){
            FieldTree field = parseField();
            structTree.fields.add(field);
            token = lexer.peek();
        }

        token = lexer.consume();
        if (token.hasTag(TokenKind.EOF))
            error("missing '}'", token);

        return structTree;
    }

    private FieldTree parseField(){
        boolean isMutable = false;
        Token token = lexer.consume();
        if (token.hasTag(TokenKind.MUT)){
            isMutable = true;
            token = lexer.consume();
        }

        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);

        BasicFieldTree parameterTree = new BasicFieldTree(token.getLexem(), token.getLocation());
        parameterTree.isMutable = isMutable;

        token = lexer.consume();
        if (!token.hasTag(TokenKind.COLON))
            error("missing ':'", token);

        parameterTree.type = parseType();
        parseEOS();

        return parameterTree;
    }

    private TemplateTree parseTemplate(){
        lexer.consume();
        Token token = lexer.consume();
        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);

        BasicTemplateTree templateTree = new BasicTemplateTree(token.getLexem(), token.getLocation());

        token = lexer.consume();
        if (!token.hasTag(TokenKind.CURVED_OPEN))
            error("missing '{'", token);

        token = lexer.peek();
        while (!token.hasTag(TokenKind.CURVED_CLOSED, TokenKind.EOF)){
            TemplateMethodTree method = parseTemplateMethod();
            templateTree.methods.add(method);
            token = lexer.peek();
        }

        token = lexer.consume();
        if (token.hasTag(TokenKind.EOF))
            error("missing '}'", token);

        return templateTree;
    }

    private TemplateMethodTree parseTemplateMethod(){
        Token token = lexer.consume();
        if (!token.hasTag(TokenKind.FUN))
            error("'fun' expected", token);

        token = lexer.consume();
        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);
        BasicTemplateMethodTree functionTree = new BasicTemplateMethodTree(token.getLexem(), token.getLocation());
        token = lexer.consume();
        if (!token.hasTag(TokenKind.PARENTHESES_OPEN))
            error("missing '('", token);

        token = lexer.peek();
        if (!token.hasTag(TokenKind.PARENTHESES_CLOSED, TokenKind.EOF)){
            do {
                ParameterTree param = parseParam();
                functionTree.parameters.add(param);

                token = lexer.peek();
                if (token.hasTag(TokenKind.COMMA)){
                    lexer.consume();
                    continue;
                }

                break;
            } while (true);
        }

        token = lexer.consume();
        if (token.hasTag(TokenKind.EOF) || !token.hasTag(TokenKind.PARENTHESES_CLOSED))
            error("missing ')'", token);

        token = lexer.consume();
        if (!token.hasTag(TokenKind.ARROW))
            error("missing '->'", token);

        functionTree.returnType = parseType();
        parseEOS();
        return functionTree;
    }

    private ImplementationTree parseImplementation(){
        lexer.consume();
        Token token = lexer.consume();
        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);

        BasicImplementationTree implTree = new BasicImplementationTree(token.getLexem(), token.getLocation());

        token = lexer.consume();
        if (!token.hasTag(TokenKind.FOR))
            error("missing keyword 'for'", token);

        token = lexer.consume();
        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);

        implTree.forType = token.getLexem();

        token = lexer.consume();
        if (token.hasTag(TokenKind.AS)) {
            token = lexer.consume();
            if (!token.hasTag(TokenKind.IDENTIFIER))
                error("identifier expected", token);

            implTree.objectName = token.getLexem();
            token = lexer.consume();
        }

        if (!token.hasTag(TokenKind.CURVED_OPEN))
            error("missing '{'", token);

        token = lexer.peek();

        while (!token.hasTag(TokenKind.CURVED_CLOSED, TokenKind.EOF)){
            CallableTree method = parseCallable();
            if (method == null){
                error("unexpected token '" + token.getLexem() + "'", token);
            }
            else {
                implTree.implementations.add(method);
            }
            token = lexer.peek();
        }

        token = lexer.consume();
        if (token.hasTag(TokenKind.EOF))
            error("missing '}'", token);

        return implTree;
    }

    private CallableTree parseCallable(){
        Token token = lexer.consume();
        return switch ((TokenKind)token.getTag()){
            case FUN -> parseFunction();
            case NAT -> parseNative();
            default -> {
                error("unexpected token '" + token.getLexem() + "'", token);
               yield null;
            }
        };
    }

    private FunctionTree parseFunction(){
        Token token = lexer.consume();
        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);
        BasicFunctionTree functionTree = new BasicFunctionTree(token.getLexem(), token.getLocation());
        token = lexer.consume();
        if (!token.hasTag(TokenKind.PARENTHESES_OPEN))
            error("missing '('", token);

        token = lexer.peek();
        if (!token.hasTag(TokenKind.PARENTHESES_CLOSED, TokenKind.EOF)){
            do {
                ParameterTree param = parseParam();
                functionTree.parameters.add(param);

                token = lexer.peek();
                if (token.hasTag(TokenKind.COMMA)){
                    lexer.consume();
                    continue;
                }

                break;
            } while (true);
        }

        token = lexer.consume();
        if (token.hasTag(TokenKind.EOF) || !token.hasTag(TokenKind.PARENTHESES_CLOSED))
            error("missing ')'", token);

        token = lexer.consume();
        if (!token.hasTag(TokenKind.ARROW))
            error("missing '->'", token);

        functionTree.returnType = parseType();

        functionTree.body = parseBlock();
        return functionTree;
    }

    private NativeFunctionTree parseNative(){

        Token token = lexer.consume();
        if (!token.hasTag(TokenKind.FUN))
            error("'fun' expected", token);

        token = lexer.consume();
        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);
        BasicNativeFunctionTree functionTree = new BasicNativeFunctionTree(token.getLexem(), token.getLocation());
        token = lexer.consume();
        if (!token.hasTag(TokenKind.PARENTHESES_OPEN))
            error("missing '('", token);

        token = lexer.peek();
        if (!token.hasTag(TokenKind.PARENTHESES_CLOSED, TokenKind.EOF)){
            do {
                ParameterTree param = parseParam();
                functionTree.parameters.add(param);

                token = lexer.peek();
                if (token.hasTag(TokenKind.COMMA)){
                    lexer.consume();
                    continue;
                }

                break;
            } while (true);
        }

        token = lexer.consume();
        if (token.hasTag(TokenKind.EOF) || !token.hasTag(TokenKind.PARENTHESES_CLOSED))
            error("missing ')'", token);

        token = lexer.consume();
        if (!token.hasTag(TokenKind.ARROW))
            error("missing '->'", token);

        functionTree.returnType = parseType();
        parseEOS();
        return functionTree;
    }

    private ParameterTree parseParam(){
        boolean isMutable = false;
        Token token = lexer.consume();
        if (token.hasTag(TokenKind.MUT)){
            isMutable = true;
            token = lexer.consume();
        }

        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);

        BasicParameterTree parameterTree = new BasicParameterTree(token.getLexem(), token.getLocation());
        parameterTree.isMutable = isMutable;

        token = lexer.consume();
        if (!token.hasTag(TokenKind.COLON))
            error("missing ':'", token);

        parameterTree.type = parseType();

        return parameterTree;
    }

    private BlockTree parseBlock(){
        BasicBlockTree streamNode = new Trees.BasicBlockTree();

        Token token = lexer.consume();
        if (!token.hasTag(TokenKind.CURVED_OPEN))
            error("missing '{'", token);

        token = lexer.peek();
        while (!token.hasTag(TokenKind.CURVED_CLOSED, TokenKind.EOF)) {
            StatementTree stmtNode = parseStatement();
            if (stmtNode != null) {
                streamNode.statements.add(stmtNode);
            }
            else lexer.consume();
            token = lexer.peek();
        }

        if (!token.hasTag(TokenKind.CURVED_CLOSED))
            error("missing '}'", token);

        lexer.consume();

        return streamNode;
    }

    private TypeTree parseType(){
        Token token = lexer.consume();
        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);
        BasicTypeTree typeTree = new BasicTypeTree(token.getLexem(), token.getLocation());
        token = lexer.peek();
        if (token.hasTag(TokenKind.LESS)){
            lexer.consume();
            token = lexer.peek();
            if (token.hasTag(TokenKind.GREATER)){
                error("type expected", token);
            }
            else {
                do {
                    typeTree.generics.add(parseType());
                    token = lexer.peek();
                } while(token.hasTag(TokenKind.COMMA));
                if (!token.hasTag(TokenKind.GREATER)){
                    error("missing '>'", token);
                }
                lexer.consume();
                token = lexer.peek();
            }
        }

        if (token.hasTag(TokenKind.QU_MARK)) {
            lexer.consume();
            typeTree.isNullable = true;
        }

        return typeTree;
    }

    @Override
    public StatementTree parseStatement() {
        Token token = lexer.peek();
        return switch ((TokenKind) token.getTag()){
            case RETURN -> parseReturn();
            case LET -> parseVarDec();
            case IF -> parseIfElse();
            case WHILE -> parseWhileDo();
            case DO -> parseDoWhile();
            case CURVED_OPEN -> parseBlock();
            case DEL -> parseDelete();
            default -> parseExpressionStatement();
        };
    }

    private ExpressionStatementTree parseExpressionStatement(){
        ExpressionTree exp = parseExpression();
        if (exp == null) {
            error("not a statement", lexer.peek());
            return null;
        }
        parseEOS();
        return new BasicExpressionStatementTree(exp, exp.getLocation());
    }

    private FreeTree parseDelete(){
        BasicFreeTree freeTree = new BasicFreeTree(lexer.consume().getLocation());
        freeTree.expression = unwrap(parseExpression(), lexer.peek());
        parseEOS();
        return freeTree;
    }

    private VarDecTree parseVarDec(){
        lexer.consume();
        Token token = lexer.consume();
        boolean isMutable = false;
        if (token.hasTag(TokenKind.MUT)){
            isMutable = true;
            token = lexer.consume();
        }
        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);
        BasicVarDecTree varDecTree = new BasicVarDecTree(token.getLexem(), token.getLocation());
        varDecTree.isMutable = isMutable;

        token = lexer.consume();
        if (token.hasTag(TokenKind.COLON)){
            varDecTree.type = parseType();
            token = lexer.consume();

            if (token.hasTag(TokenKind.SEMI)){
                varDecTree.initializer = new BasicNullTree(varDecTree.getLocation());
                return varDecTree;
            }
        }

        if (!token.hasTag(TokenKind.EQ_ASSIGN))
            error("missing '='", token);

        varDecTree.initializer = unwrap(parseExpression(), lexer.peek());
        parseEOS();
        return varDecTree;
    }

    private ReturnTree parseReturn(){
        lexer.consume();
        Token token = lexer.peek();
        ExpressionTree returnValue = unwrap(parseExpression(), token);
        parseEOS();
        return new BasicReturnTree(returnValue, token.getLocation());
    }

    private ExpressionTree parseCondition(){
        ExpressionTree cond = unwrap(parseExpression(), lexer.peek());
        Token token = lexer.peek();
        if (!token.hasTag(TokenKind.COLON))
            error("':' expected", token);
        else lexer.consume();
        return cond;
    }

    private IfElseTree parseIfElse(){
        BasicIfElseTree ifElseTree = new BasicIfElseTree(lexer.consume().getLocation());
        ifElseTree.condition = parseCondition();
        ifElseTree.ifBody = parseStatement();

        if (lexer.peek().hasTag(TokenKind.ELSE)){
            lexer.consume();
            ifElseTree.elseBody = parseStatement();
        }

        return ifElseTree;
    }

    private WhileDoTree parseWhileDo(){
        BasicWhileDoTree whileDoTree = new BasicWhileDoTree(lexer.consume().getLocation());
        whileDoTree.condition = parseCondition();
        whileDoTree.body = parseStatement();
        return whileDoTree;
    }

    private DoWhileTree parseDoWhile(){
        BasicDoWhileTree doWhileTree = new BasicDoWhileTree(lexer.consume().getLocation());
        doWhileTree.body = parseStatement();
        Token token = lexer.peek();
        if (!token.hasTag(TokenKind.WHILE))
            error("missing keyword while", token);
        else lexer.consume();
        doWhileTree.condition = parseCondition();
        parseEOS();
        return doWhileTree;
    }

    @Override
    public ExpressionTree parseExpression() {
        return parseExpression(parsePrimaryExpression(), 0);
    }

    private ExpressionTree unwrap(ExpressionTree exp, Token token) {
        if (exp == null)
            error("expression expected", token);
        return exp;
    }

    private ExpressionTree parseExpression(ExpressionTree lhs, int minPrecedence){
        Token lookahead = lexer.peek();

        while (isBinaryOperator(lookahead) && precedenceOf(lookahead) >= minPrecedence){
            final Token op = lexer.consume();
            ExpressionTree rhs = unwrap(parsePrimaryExpression(), op);
            lookahead = lexer.peek();

            while (isBinaryOperator(lookahead)
                    && precedenceOf(lookahead) > precedenceOf(op)){
                int offs = precedenceOf(lookahead) > precedenceOf(op) ? 1 : 0;
                rhs = parseExpression(rhs, precedenceOf(op) + offs);
                lookahead = lexer.peek();
            }

            lhs = PrecedenceCalculator.apply(op, lhs, rhs);
        }
        return lhs;
    }

    private boolean isBinaryOperator(Token token){
        return PrecedenceCalculator.isBinaryOperator(token);
    }

    private int precedenceOf(Token token){
        return PrecedenceCalculator.calculate(token);
    }

    private ExpressionTree parsePrimaryExpression(){
        ExpressionTree expNode = null;

        Token token = lexer.peek();

        if (token.hasTag(TokenKind.INTEGER)) {
            expNode = new Trees.BasicIntegerTree(Integer.parseInt(token.getLexem()), lexer.consume().getLocation());
        }
        if (token.hasTag(TokenKind.FLOAT)) {
            expNode = new Trees.BasicFloatTree(Float.parseFloat(token.getLexem()), lexer.consume().getLocation());
        }
        else if (token.hasTag(TokenKind.STRING)){
            expNode = new Trees.BasicStringTree(token.getLexem(), lexer.consume().getLocation());
        }
        else if (token.hasTag(TokenKind.NULL)){
            expNode = new Trees.BasicNullTree(lexer.consume().getLocation());
        }
        else if (token.hasTag(TokenKind.TRUE, TokenKind.FALSE)){
            expNode = new Trees.BasicBooleanTree(Boolean.parseBoolean(token.getLexem()), lexer.consume().getLocation());
        }
        else if (token.hasTag(TokenKind.IDENTIFIER)){
            token = lexer.consume();
            if (lexer.peek().hasTag(TokenKind.PARENTHESES_OPEN)){
                BasicGlobalCallTree globalCallTree = new BasicGlobalCallTree(token.getLexem(), token.getLocation());
                globalCallTree.arguments = parseCallArguments();
                expNode = globalCallTree;
            }
            else {
                expNode = new Trees.BasicVariableTree(token.getLexem(), token.getLocation());
            }
        }
        else if (token.hasTag(TokenKind.BRACKET_OPEN)){
            expNode = parseArray();
        }
        else if (token.hasTag(TokenKind.QU_MARK)){
            lexer.consume();
            expNode = new Trees.BasicNotTree(unwrap(parseExpression(), token), token.getLocation());
        }
        else if (token.hasTag(TokenKind.MINUS)){
            lexer.consume();
            expNode = new BasicNegationTree(unwrap(parseExpression(), token), token.getLocation());
        }
        else if (token.hasTag(TokenKind.PARENTHESES_OPEN)){
            lexer.consume();
            expNode = parseExpression();
            if (expNode == null)
                error("expression expected", token);
            token = lexer.consume();
            if (!token.hasTag(TokenKind.PARENTHESES_CLOSED))
                error("missing ')'", token);
        }
        else if (token.hasTag(TokenKind.NEW)){
            expNode = parseStructInit();
        }


        while (expNode != null) {
            token = lexer.peek();

            if (token.hasTag(TokenKind.DOT)){
                lexer.consume();
                token = lexer.consume();
                if (!token.hasTag(TokenKind.IDENTIFIER))
                    error("identifier expected", token);
                Token candidate = lexer.peek();
                if (candidate.hasTag(TokenKind.PARENTHESES_OPEN)){
                    BasicMethodCallTree globalCallTree = new BasicMethodCallTree(token.getLexem(), token.getLocation());
                    globalCallTree.expression = expNode;
                    globalCallTree.arguments = parseCallArguments();
                    expNode = globalCallTree;
                }
                else {
                    BasicFieldAccessTree accessTree = new BasicFieldAccessTree(expNode, token.getLocation());
                    accessTree.fieldName = token.getLexem();
                    expNode = accessTree;
                }
                continue;
            }

            else if (token.hasTag(TokenKind.BRACKET_OPEN)) {
                lexer.consume();
                Trees.BasicContainerAccessTree accessNode = new Trees.BasicContainerAccessTree(expNode, token.getLocation());
                accessNode.key = unwrap(parseExpression(), token);
                expNode = accessNode;
                token = lexer.consume();
                if (!token.hasTag(TokenKind.BRACKET_CLOSED))
                    error("missing ']'", token);
                continue;
            }

            else if (token.hasTag(TokenKind.AS)){
                lexer.consume();
                TypeTree type = parseType();
                BasicCastTree castTree = new BasicCastTree(expNode, token.getLocation());
                castTree.type = type;
                expNode = castTree;
            }

            else if (token.hasTag(TokenKind.EX_MARK)){
                lexer.consume();
                expNode = new BasicNullCheckTree(expNode, token.getLocation());
                continue;
            }

            break;
        }

        return expNode;
    }

    private StructInitTree parseStructInit(){
        Token token = lexer.consume();
        Location location = token.getLocation();
        TypeTree type = parseTypeWithoutNullable();
        token = lexer.peek();
        if (!token.hasTag(TokenKind.PARENTHESES_OPEN))
            error("missing '('", token);
        List<ExpressionTree> args = parseCallArguments();
        BasicStructInitTree structTree = new BasicStructInitTree(type, location);
        structTree.arguments.addAll(args);
        return structTree;
    }

    private TypeTree parseTypeWithoutNullable(){
        Token token = lexer.consume();
        if (!token.hasTag(TokenKind.IDENTIFIER))
            error("identifier expected", token);
        BasicTypeTree typeTree = new BasicTypeTree(token.getLexem(), token.getLocation());
        token = lexer.peek();
        if (token.hasTag(TokenKind.LESS)){
            lexer.consume();
            token = lexer.peek();
            if (token.hasTag(TokenKind.GREATER)){
                error("type expected", token);
            }
            else {
                do {
                    typeTree.generics.add(parseType());
                    token = lexer.peek();
                } while(token.hasTag(TokenKind.COMMA));
                if (!token.hasTag(TokenKind.GREATER)){
                    error("missing '>'", token);
                }
                lexer.consume();
            }
        }
        return typeTree;
    }

    private List<ExpressionTree> parseCallArguments(){
        List<ExpressionTree> arguments = new ArrayList<>();

        lexer.consume();
        Token token = lexer.peek();
        if (!token.hasTag(TokenKind.PARENTHESES_CLOSED)){
            do {
                ExpressionTree arg = unwrap(parseExpression(), token);
                arguments.add(arg);

                token = lexer.peek();
                if (token.hasTag(TokenKind.COMMA)){
                    lexer.consume();
                    continue;
                }

                break;
            } while (true);
        }

        token = lexer.consume();
        if (token.hasTag(TokenKind.EOF)|| !token.hasTag(TokenKind.PARENTHESES_CLOSED))
            error("missing ')'", token);

        return arguments;
    }

    private ArrayTree parseArray() {
        ArrayTree arrayNode = new Trees.BasicArrayTree(lexer.consume().getLocation());

        Token token = lexer.peek();

        if (!token.hasTag(TokenKind.BRACKET_CLOSED)){
            do {
                token = lexer.peek();

                ExpressionTree arg = unwrap(parseExpression(), token);
                arrayNode.getContent().add(arg);

                token = lexer.peek();
                if (token.hasTag(TokenKind.COMMA)){
                    lexer.consume();
                    continue;
                }

                break;
            } while (true);
        }

        token = lexer.consume();
        if (token.hasTag(TokenKind.EOF) || !token.hasTag(TokenKind.BRACKET_CLOSED))
            error("missing ']'", token);

        return arrayNode;
    }


    private void parseEOS(){
        Token token = lexer.peek();
        if (!token.hasTag(TokenKind.SEMI))
            error("missing ';'", token);
        else
            lexer.consume();
    }
}

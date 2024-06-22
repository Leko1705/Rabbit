package computil.optim;

import computil.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConstantFolder extends Optimizer<Void> {

    private final Map<String, Tree> locals = new HashMap<>();

    private void forgetValues(){
        locals.replaceAll((i, t) -> null);
    }

    @Override
    public Void visitFunctionTree(FunctionTree functionTree, Tree parent) {
        locals.clear();
        return super.visitFunctionTree(functionTree, parent);
    }

    @Override
    public Void visitNotTree(NotTree notTree, Tree parent) {
        scan(notTree.getOperand(), notTree);
        ExpressionTree operand = notTree.getOperand();

        if (operand instanceof NotTree n){
            replace(parent, notTree, n.getOperand());
        }
        else if (operand instanceof IntegerTree i){
            replace(parent, notTree, new Trees.BasicIntegerTree(~i.get(), i.getLocation()));
        }
        else if (operand instanceof FloatTree f){
            float fVal = f.get();
            int asInt = Float.floatToIntBits(fVal);
            asInt = ~asInt;
            fVal = Float.intBitsToFloat(asInt);
            replace(parent, notTree, new Trees.BasicFloatTree(fVal, f.getLocation()));
        }
        else if (operand instanceof BooleanTree i){
            replace(parent, notTree, new Trees.BasicBooleanTree(!i.get(), i.getLocation()));
        }

        return null;
    }

    @Override
    public Void visitNegationTree(NegationTree negationTree, Tree parent) {
        scan(negationTree.getOperand(), negationTree);
        ExpressionTree operand = negationTree.getOperand();

        if (operand instanceof NegationTree n){
            replace(parent, negationTree, n.getOperand());
        }
        else if (operand instanceof IntegerTree i){
            replace(parent, negationTree, new Trees.BasicIntegerTree(-i.get(), i.getLocation()));
        }
        else if (operand instanceof FloatTree f){
            replace(parent, negationTree, new Trees.BasicFloatTree(-f.get(), f.getLocation()));
        }

        return null;
    }

    @Override
    public Void visitNullCheckTree(NullCheckTree nullCheckTree, Tree parent) {
        scan(nullCheckTree.getOperand(), nullCheckTree);

        if (nullCheckTree.getOperand() instanceof LiteralTree<?>
                && !(nullCheckTree.getOperand() instanceof NullTree))
            replace(parent, nullCheckTree, nullCheckTree.getOperand());

        return null;
    }

    @Override
    public Void visitBinaryOperationTree(BinaryOperationTree operationTree, Tree parent) {
        super.visitBinaryOperationTree(operationTree, parent);

        if (operationTree.getLeftOperand() instanceof LiteralTree<?>
                && operationTree.getRightOperand() instanceof LiteralTree<?>)
            performBinaryOperationFolding(operationTree, parent);

        return null;
    }

    private void performBinaryOperationFolding(BinaryOperationTree binTree, Tree parent){
        Operation op = binTree.getOperation();
        LiteralTree<?> left = (LiteralTree<?>) binTree.getLeftOperand();
        LiteralTree<?> right = (LiteralTree<?>) binTree.getRightOperand();

        switch (op){
            case ADD -> {
                if (left instanceof IntegerTree i1){
                    if (right instanceof IntegerTree i2){
                        replace(parent, binTree, new Trees.BasicIntegerTree(i1.get() + i2.get(), binTree.getLocation()));
                    }
                    else if (right instanceof FloatTree f2){
                        replace(parent, binTree, new Trees.BasicFloatTree(i1.get() + f2.get(), binTree.getLocation()));
                    }
                }
                else if (left instanceof FloatTree f1){
                    float fVal = (float) right.get();
                    replace(parent, binTree, new Trees.BasicFloatTree(f1.get() + fVal, binTree.getLocation()));
                }
            }
            case SUB -> {
                if (left instanceof IntegerTree i1){
                    if (right instanceof IntegerTree i2){
                        replace(parent, binTree, new Trees.BasicIntegerTree(i1.get() - i2.get(), binTree.getLocation()));
                    }
                    else if (right instanceof FloatTree f2){
                        replace(parent, binTree, new Trees.BasicFloatTree(i1.get() - f2.get(), binTree.getLocation()));
                    }
                }
                else if (left instanceof FloatTree f1){
                    float fVal = (float) right.get();
                    replace(parent, binTree, new Trees.BasicFloatTree(f1.get() - fVal, binTree.getLocation()));
                }
            }
            case MUL -> {
                if (left instanceof IntegerTree i1){
                    if (right instanceof IntegerTree i2){
                        replace(parent, binTree, new Trees.BasicIntegerTree(i1.get() * i2.get(), binTree.getLocation()));
                    }
                    else if (right instanceof FloatTree f2){
                        replace(parent, binTree, new Trees.BasicFloatTree(i1.get() * f2.get(), binTree.getLocation()));
                    }
                }
                else if (left instanceof FloatTree f1){
                    float fVal = (float) right.get();
                    replace(parent, binTree, new Trees.BasicFloatTree(f1.get() * fVal, binTree.getLocation()));
                }
            }
            case DIV -> {
                float leftVal = (float) left.get();
                float rightVal = (float) right.get();
                replace(parent, binTree, new Trees.BasicFloatTree(leftVal / rightVal, binTree.getLocation()));
            }
            case MOD -> {
                int leftVal = (int) left.get();
                int rightVal = (int) right.get();
                replace(parent, binTree, new Trees.BasicIntegerTree(leftVal % rightVal, binTree.getLocation()));
            }
            case AND -> {
                boolean leftVal = (boolean) left.get();
                boolean rightVal = (boolean) right.get();
                replace(parent, binTree, new Trees.BasicBooleanTree(leftVal && rightVal, binTree.getLocation()));
            }
            case OR -> {
                boolean leftVal = (boolean) left.get();
                boolean rightVal = (boolean) right.get();
                replace(parent, binTree, new Trees.BasicBooleanTree(leftVal || rightVal, binTree.getLocation()));
            }
            case EQUALS
                    -> replace(parent, binTree, new Trees.BasicBooleanTree(Objects.equals(left.get(), right.get()), binTree.getLocation()));

            case NOT_EQUALS
                    -> replace(parent, binTree, new Trees.BasicBooleanTree(!Objects.equals(left.get(), right.get()), binTree.getLocation()));

            case LESS -> {
                float leftVal = (float) left.get();
                float rightVal = (float) right.get();
                replace(parent, binTree, new Trees.BasicBooleanTree(leftVal < rightVal, binTree.getLocation()));
            }
            case GREATER -> {
                float leftVal = (float) left.get();
                float rightVal = (float) right.get();
                replace(parent, binTree, new Trees.BasicBooleanTree(leftVal > rightVal, binTree.getLocation()));
            }
            case LESS_EQ -> {
                float leftVal = (float) left.get();
                float rightVal = (float) right.get();
                replace(parent, binTree, new Trees.BasicBooleanTree(leftVal <= rightVal, binTree.getLocation()));
            }
            case GREATER_EQ -> {
                float leftVal = (float) left.get();
                float rightVal = (float) right.get();
                replace(parent, binTree, new Trees.BasicBooleanTree(leftVal >= rightVal, binTree.getLocation()));
            }
            case SHIFT_AL -> {
                int shift = (int) right.get();
                if (left instanceof IntegerTree i1){
                    replace(parent, binTree, new Trees.BasicIntegerTree(i1.get() << shift, binTree.getLocation()));
                }
                else if (left instanceof FloatTree f1){
                    float fVal = f1.get();
                    int asInt = Float.floatToIntBits(fVal);
                    asInt <<= shift;
                    fVal = Float.intBitsToFloat(asInt);
                    replace(parent, binTree, new Trees.BasicFloatTree(fVal, binTree.getLocation()));
                }
            }
            case SHIFT_AR -> {
                int shift = (int) right.get();
                if (left instanceof IntegerTree i1){
                    replace(parent, binTree, new Trees.BasicIntegerTree(i1.get() >> shift, binTree.getLocation()));
                }
                else if (left instanceof FloatTree f1){
                    float fVal = f1.get();
                    int asInt = Float.floatToIntBits(fVal);
                    asInt >>= shift;
                    fVal = Float.intBitsToFloat(asInt);
                    replace(parent, binTree, new Trees.BasicFloatTree(fVal, binTree.getLocation()));
                }
            }
        }
    }

    @Override
    public Void visitVarDecTree(VarDecTree varDecTree, Tree parent) {
        scan(varDecTree.getInitializer(), varDecTree);
        locals.put(varDecTree.getName(), varDecTree.getInitializer());
        return null;
    }

    @Override
    public Void visitVariableTree(VariableTree variableTree, Tree parent) {
        String name = variableTree.getName();
        Tree value = locals.get(name);
        if (value != null)
            replace(parent, variableTree, value);
        return null;
    }
}

package org.example.enkel.interpreter.expression;
import org.example.enkel.interpreter.ASTObject;

public class ComparableBool extends ASTObject {
    public ASTObject left;
    public ASTObject right;
    public String operator;
    public ComparableBool(ASTObject left, ASTObject right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public ASTObject getLeft() {
        return left;
    }
    public ASTObject getRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return left.toString() + operator + right.toString();
    }
}

package org.example.enkel.interpreter.expression;

import org.example.enkel.interpreter.ASTObject;

public class Subtraction extends ASTObject {
    public ASTObject left;
    public ASTObject right;
    public Subtraction(ASTObject left, ASTObject right) {
        this.left = left;
        this.right = right;
    }

    public ASTObject getLeft() {
        return left;
    }
    public ASTObject getRight() {
        return right;
    }

    public String getOperator() {
        return "-";
    }

    @Override
    public String toString() {
        return left.toString() + " - " + right.toString();
    }
}

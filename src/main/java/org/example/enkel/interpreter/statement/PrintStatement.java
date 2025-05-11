package org.example.enkel.interpreter.statement;


import org.example.enkel.interpreter.ASTObject;

public class PrintStatement extends ASTObject {
    private ASTObject expression;

    public PrintStatement(ASTObject expression) {
        this.expression = expression;
    }

    public ASTObject getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return expression.toString();
    }
}

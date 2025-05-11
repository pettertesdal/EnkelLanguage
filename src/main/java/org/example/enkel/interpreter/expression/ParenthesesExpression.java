package org.example.enkel.interpreter.expression;

import org.example.enkel.interpreter.ASTObject;

/**
 * Represents a parenthesized expression in the AST
 */
public class ParenthesesExpression extends ASTObject {
    private final ASTObject expression;

    /**
     * Creates a new parenthesized expression
     * @param expression The expression within the parentheses
     */
    public ParenthesesExpression(ASTObject expression) {
        this.expression = expression;
    }

    /**
     * Gets the expression within the parentheses
     * @return The inner expression
     */
    public ASTObject getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "(" + expression.toString() + ")";
    }
}
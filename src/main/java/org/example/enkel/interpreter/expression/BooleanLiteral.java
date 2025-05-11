package org.example.enkel.interpreter.expression;

import org.example.enkel.interpreter.ASTObject;

public class BooleanLiteral extends ASTObject {
    public boolean value;
    public BooleanLiteral(boolean value) {
        this.value = value;
    }
    public String toString() {
        return Boolean.toString(value);
    }
    public boolean getValue() {
        return value;
    }
}

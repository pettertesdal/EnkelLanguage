package org.example.enkel.interpreter.expression;

import org.example.enkel.interpreter.ASTObject;

public class StringLiteral extends ASTObject {
    private String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
